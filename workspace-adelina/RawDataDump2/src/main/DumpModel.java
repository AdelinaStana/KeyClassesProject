package main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import classloader.ClassFileHandler;
import classloader.DirHandler;
import classloader.InputHandler;
import classloader.JarHandler;
import classloader.NoSuchFileException;
import classloader.UnsupportedInputException;
import dependencyfinder.classdependencymodel.DependenciesOnAClass;
import dependencyfinder.classdependencymodel.DependencyModel;
import dependencyfinder.classdependencymodel.DependencyStrengthFactory;
import dependencyfinder.util.*;
import inputOutput.OutputWriter;
import ranking.ClassRankingProperties;
import ranking.Ranking;
import ranking.propertyValueCalculators.DefaultClassRankingPropertiesAdder;
import ranking.propertyValueCalculators.hits.HitsCalculator;
import ranking.propertyValueCalculators.levels.LevelCalculator;
import ranking.propertyValueCalculators.pagerank.PageRankCalculator;
import ranking.strategy.SimpleRankingStrategy;
import sysmodel.DSM;
import sysmodel.SparceMatrix;
import sysmodel.SystemModel;

public class DumpModel
{
	public static void main(String[] args) throws Exception
	{
		System.out.println("Starting...");
		
		System.out.println(new File(".").getAbsolutePath());
		//pack("../TestProject01/bin","./inputs/TestProject01.jar");
		
		String names[] = {
			//new String("ant.jar"),
			//new String("ApacheJMeter_core.jar"),
			//new String("argouml.jar"),
			//new String("jedit.jar"),
			//new String("jhotdraw.jar"),
			//new String("maze.jar"),
			//new String("wro4j-core-1.6.3.jar")
			//new String("TestProject01.jar")
				new String("Cards.jar"),
				new String("TestProject01.jar")
		};

		for(String name : names)
		{
			SystemModel sm = buildSystemModel(name);
			
			dumpRawEdgeData(sm);
			dumpRawNodesData(sm);
		}
		
		System.out.println("Stopping...");
	}

	private static SystemModel buildSystemModel(String name) throws Exception
	{
		System.out.println("Analyzing: " + name);

		buildConfig("equal.txt"); // initializes all dependencies strengths with value 1

		File[] ss = { new File("inputs//" + name) };

		long beginning = System.currentTimeMillis();

		InputHandler ih = new JarHandler(
				new DirHandler(new ClassFileHandler(null), new JarHandler(new ClassFileHandler(null))));

		SystemModel sm = new SystemModel(name);

		int i = 0;
		for (File f : ss)
		{
			List<DependencyModel> deps;
			try
			{
				deps = ih.handle(f);
				i = deps.size();
				for (DependencyModel dm : deps)
					sm.addElement(dm);
			} 
			catch (UnsupportedInputException e)
			{
			} 
			catch (NoSuchFileException e)
			{
			}

		}
		if (i == 0)
			throw new Exception();

		System.out.println("input read and system model built in " + ((double) System.currentTimeMillis() - beginning) / 1000);
		
		return sm;
	}

	private static void buildConfig(String file) throws Exception
	{
		// initialize dependency strengths with values from the configuration file
		BufferedReader in = new BufferedReader(new FileReader(file));
		Initializer i = new SingleValueInitializer(new SingleValueInitializer(
				new SingleValueInitializer(
						new TwoValueInitializer(new TwoValueInitializer(
								new TwoValueInitializer(
										new TwoValueInitializer(
												new SingleValueInitializer(new SingleValueInitializer(
														new SingleValueInitializer(
																new SingleValueInitializer(null, "instantiation",
																		new InstantiatedInvoker()),
																"member_access", new MemberAccessInvoker()),
														"type_binding", new TypeBindingInvoker()), "return",
														new ReturnInvoker()),
												"static_invocation", new StaticInvoker()),
										"parameter", new ParamInvoker()),
								"local_variable", new LocalVarInvoker()), "member", new MemberInvoker()),
						"implementation", new ImplementationInvoker()),
				"inheritance", new InheritanceInvoker()), "cast", new CastInvoker());
		String line = null;
		while ((line = in.readLine()) != null)
		{
			String[] parts = line.split("  *= *");
			if (parts.length == 2)
			{
				System.out.println("Input handle parts " + parts[0] + " " + parts[1]);
				i.handle(parts[0], parts[1]);
			}
		}
		in.close();
	}

	private static void dumpRawEdgeData(SystemModel sm) throws Exception
	{
		long beginning = System.currentTimeMillis();

		Set<String> excluded = new HashSet<String>();
		excluded.add("java.lang");
		excluded.add("java.util");
		excluded.add("java.io");

		Set<DependenciesOnAClass> deps = sm.getAllPairsDepDetails(excluded);

		Iterator<DependenciesOnAClass> it = deps.iterator();

		PrintWriter writer = new PrintWriter("outputs//" + sm.getName() + "_edges.csv");
		writer.printf(
			"Class1(From),"+
			"Class2(To),"+
			"isExternalDep,"+
			"isSuperClass,"+
			"isImplementingInterface,"+
			
			"hasReturn,"+
			"hasParameter,"+
			"hasBinding,"+
			"instantiates,"+
			
			"hasField,"+
			"hasLocalVariable,"+
			"hasCast,"+
			
			"numberOfStaticMethodsCalled,"+
			"StaticMethodsCalled,"+
			
			"numberOfMethodsCalled,"+
			"MethodsCalled\n");
		while(it.hasNext())
		{
			DependenciesOnAClass o = it.next();
			// System.out.println(o);
			writer.printf(o.toString() + "\n");
		}
		writer.close();
		
		System.out.println("Dependency details edges dumped in " + ((double) System.currentTimeMillis() - beginning) / 1000);
		
		System.out.println("Look for file " + "outputs//" + sm.getName() + "_edges.csv");
	}

	private static void dumpRawNodesData(SystemModel sm) throws Exception
	{
		long beginning = System.currentTimeMillis();

		DSM d = sm.computeDSM();
		// d.collapseInnerClasses();

		Ranking ranking = new Ranking(sm.getName(), null);

		System.out.println("Running HitsCalculator for HUB and AUTHORITY VALUES ...");

		HitsCalculator hitsCalculator = new HitsCalculator(d, 20);
		hitsCalculator.computeWeightedHubsAndAuthorities();
		ranking.setClassList(hitsCalculator.getResultAsRankingEntryList());

		System.out.println("Calculating class Levels");

		LevelCalculator levelCalculator = new LevelCalculator(d);
		levelCalculator.computeLevel();
		levelCalculator.addResultToRanking(ranking.getClassList());

		System.out.println("Running PageRankCalculators for PR VALUES ...");
		
		boolean directed = false; // use directed or undirected graph for pagerank
		int F = 2;
		System.out.println("PR variant: directed=" + directed + " F=" + F);
		PageRankCalculator prCalculator = new PageRankCalculator(d, directed, F, 50);
		prCalculator.computeWeightedPageRank();
		prCalculator.addResultToRanking(ranking.getClassList());

		directed = true;// use directed or undirected graph for pagerank
		F = 0;
		System.out.println("PR variant: directed=" + directed + " F=" + F);
		prCalculator = new PageRankCalculator(d, directed, F, 50);
		prCalculator.computeWeightedPageRank();
		prCalculator.addResultToRanking(ranking.getClassList());

		DefaultClassRankingPropertiesAdder defaultCalculator = new DefaultClassRankingPropertiesAdder(d, directed,
				null);
		defaultCalculator.addResultToRanking(ranking.getClassList());

		/*
		 * ranking.setStrategy(new
		 * SimpleRankingStrategy(ClassRankingProperties.PAGERANK_UNDIRECTED2));
		 * ranking.rank(); System.out.println( "Did ranking after IMPORTANCE values");
		 */

		OutputWriter.outputRanking(ranking);

		System.out.println("THE END - look for results in the output folder");

		System.out.println("Nodes details dumped in " + ((double) System.currentTimeMillis() - beginning) / 1000);
	}
	
	public static void pack(String sourceDirPath, String zipFilePath) throws IOException 
	{
		if(new File(zipFilePath).exists())
		{
			Files.delete(Paths.get(zipFilePath));
		}
	    Path p = Files.createFile(Paths.get(zipFilePath));
	    try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) 
	    {
	        Path pp = Paths.get(sourceDirPath);
	        Files.walk(pp)
	          .filter(path -> !Files.isDirectory(path))
	          .forEach(path -> {
	              ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
	              try 
	              {
	                  zs.putNextEntry(zipEntry);
	                  Files.copy(path, zs);
	                  zs.closeEntry();
	              } 
	              catch (IOException e) 
	              {
	                System.err.println(e);
	              }
	          	});
	    }
	}	 
}
