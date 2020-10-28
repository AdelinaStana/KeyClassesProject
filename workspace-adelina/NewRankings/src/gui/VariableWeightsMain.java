package gui;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import classloader.ClassFileHandler;
import classloader.DirHandler;
import classloader.InputHandler;
import classloader.JarHandler;
import classloader.NoSuchFileException;
import classloader.UnsupportedInputException;
import dependencyfinder.classdependencymodel.DependencyModel;
import dependencyfinder.classdependencymodel.DependencyStrength;
import dependencyfinder.classdependencymodel.DependencyStrengthFactory;
import dependencyfinder.util.*;

import inputOutput.OutputWriter;
import inputOutput.Reader;
import ranking.Ranking;
import ranking.strategy.FuzzyRulesRankingStrategy;
import ranking.strategy.SimpleRankingStrategy;
import sysmodel.DSM;
import sysmodel.SparceMatrix;
import sysmodel.SystemModel;
import metrics.Metric;
import metrics.Precision;
import metrics.Recall;
import ranking.propertyValueCalculators.DefaultClassRankingPropertiesAdder;
import ranking.propertyValueCalculators.hits.HitsCalculator;
import ranking.propertyValueCalculators.hits.HitsValue;
import ranking.propertyValueCalculators.pagerank.PageRankCalculator;
import ranking.ClassRankingProperties;

public class VariableWeightsMain {

	public static void main(String[] args) throws Exception {

		// the name of the jar to be analyzed - it is in the inputs folder
		String name = new String("ant.jar");

		// the name of the reference solution file from the reference solutions
		// folder
		// String refSolPath = "ant.jar.txt"; NOT USED IN CODE HERE

		// OTHER POSSIBLE INPUTS:
		// String name = "ApacheJMeter_core.jar";
		// String refSolPath = "ApacheJMeter_core.jar.txt";

		// String name = new String("argouml.jar");
		// String refSolPath = "argouml.jar.txt";

		// String name = new String("jedit.jar");
		// String refSolPath = "jedit.jar.txt";

		// String name = new String("jhotdraw.jar");
		// String refSolPath = "jhotdraw.jar.txt";

		// String name = new String("wro4j-core-1.6.3.jar");
		// String refSolPath = "wro4j-core-1.6.3.jar.txt";

		System.out.println("start...");
		System.out.println("Analyzing: " + name);

		// enase.txt is a good config for dependency strengths values
		buildConfig("enase.txt"); // initializes dep strengths with values used
									// in ENASE paper experiments

		File[] ss = { new File("inputs//" + name) };

		long beginning = System.currentTimeMillis();

		/* 
		 * Initial step: parse bytecode and build a list of DependencyModel objects
		 * Each DependencyModel describes the dependencies for one class
		 */
		
		InputHandler ih = new JarHandler(
				new DirHandler(new ClassFileHandler(null), new JarHandler(new ClassFileHandler(null))));

		

		List<DependencyModel> alldeps = new ArrayList<DependencyModel>();

		int i = 0;
		for (File f : ss) {
			List<DependencyModel> deps;
			try {
				deps = ih.handle(f);
				i = deps.size();
				for (DependencyModel dm : deps)
					alldeps.add(dm);

			} catch (UnsupportedInputException e) {
			} catch (NoSuchFileException e) {
			}

		}
		if (i == 0)
			throw new Exception();

		System.out.println("input read in " + ((double) System.currentTimeMillis() - beginning) / 1000);

		int inheritance = 1, implementedInterface = 1, memberBase = 1, memberIndex = 1, localBase = 1, localIndex = 1,
				paramBase = 1, paramIndex = 1, staticBase = 1, staticIndex = 1, returnBase = 1, memberAccess = 1,
				typeBinding = 1, instantiated = 1, cast = 1;

		for (int repeat = 0; repeat < 5; repeat++) {

			System.out.println("CHANGE WEIGHTS VALUES ");

			inheritance++;
			implementedInterface++;
			memberBase++;
			memberIndex++;
			localBase++;
			localIndex++;
			paramBase++;
			paramIndex++;
			staticBase++;
			staticIndex++;
			returnBase++;
			memberAccess++;
			typeBinding++;
			instantiated++;
			cast++;

			DependencyStrength strength = new DependencyStrength(inheritance, implementedInterface, memberBase,
					memberIndex, localBase, localIndex, paramBase, paramIndex, staticBase, staticIndex, returnBase,
					memberAccess, typeBinding, instantiated, cast);

			DependencyStrengthFactory.setDependencyStrengthInstace(strength);

			SystemModel sm = new SystemModel(name);
			
			for (DependencyModel dm : alldeps)
				sm.addElement(dm);

			beginning = System.currentTimeMillis();

			DSM d = sm.computeDSM();
			d.collapseInnerClasses();

			System.out.println("DSM built in " + ((double) System.currentTimeMillis() - beginning) / 1000);

			System.out.println("Running PageRankCalculators for PR VALUES ...");

			boolean directed = false; // use directed or undirected graph for
										// pagerank
			int F = 2;

			System.out.println("PR variant: directed=" + directed + " F=" + F);

			PageRankCalculator prCalculator = new PageRankCalculator(d, directed, F, 50);
			prCalculator.computeWeightedPageRank();

			Map<Integer, Double> m2 = prCalculator.getResult();

			List<Entry<Integer, Double>> sortedEntries = new ArrayList<Entry<Integer, Double>>(m2.entrySet());

			Collections.sort(sortedEntries, new Comparator<Entry<Integer, Double>>() {
				@Override
				public int compare(Entry<Integer, Double> e1, Entry<Integer, Double> e2) {
					return e2.getValue().compareTo(e1.getValue());
				}
			});

			System.out.println("\n\n Top decreasing PageRank : \n\n");
			System.out.printf("%-55s %s\t%s\t%s\t%s\t%s \n", "Classname", "NrF ", "NrM ", "Win ", "Wout ", "PR  ");

			for (i = 0; i < 20; i++) {

				int classi = sortedEntries.get(i).getKey();
				System.out.printf("%-55s  %5d  %5d  %7d  %7d   %f \n", d.elementAtFull(classi).getName(), // classname
						d.elementAtFull(classi).getNrFields(), // number of
																// fields
						d.elementAtFull(classi).getNrMethods(), // number of
																// methods
						d.getDependencyMatrix().inWeight(classi), // weight
																	// incoming
																	// deps
						d.getDependencyMatrix().outWeight(classi), // weight
																	// outgoing
																	// deps
						sortedEntries.get(i).getValue()); // pagerank value

			}
		}
	}

	private static void buildConfig(String file) throws Exception {
		// initialize dependency strengths with values from config file
		BufferedReader in = new BufferedReader(new FileReader(file));
		Initializer i = new SingleValueInitializer(
				new SingleValueInitializer(
						new SingleValueInitializer(
								new TwoValueInitializer(
										new TwoValueInitializer(
												new TwoValueInitializer(new TwoValueInitializer(
														new SingleValueInitializer(
																new SingleValueInitializer(
																		new SingleValueInitializer(
																				new SingleValueInitializer(null,
																						"instantiation",
																						new InstantiatedInvoker()),
																				"member_access",
																				new MemberAccessInvoker()),
																		"type_binding", new TypeBindingInvoker()),
																"return", new ReturnInvoker()),
														"static_invocation", new StaticInvoker()), "parameter",
														new ParamInvoker()),
												"local_variable", new LocalVarInvoker()),
										"member", new MemberInvoker()),
								"implementation", new ImplementationInvoker()),
						"inheritance", new InheritanceInvoker()),
				"cast", new CastInvoker());
		String line = null;
		while ((line = in.readLine()) != null) {
			String[] parts = line.split("  *= *");
			if (parts.length == 2) {
				System.out.println("Input handle parts " + parts[0] + " " + parts[1]);
				i.handle(parts[0], parts[1]);
			}
		}
		in.close();

	}

}
