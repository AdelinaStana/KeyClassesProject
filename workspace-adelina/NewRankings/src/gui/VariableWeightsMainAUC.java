package gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import classloader.ClassFileHandler;
import classloader.DirHandler;
import classloader.InputHandler;
import classloader.JarHandler;
import classloader.NoSuchFileException;
import classloader.UnsupportedInputException;
import dependencyfinder.classdependencymodel.DependencyModel;
import dependencyfinder.classdependencymodel.DependencyStrength;
import dependencyfinder.classdependencymodel.DependencyStrengthFactory;
import inputOutput.Reader;
import metrics.AreaUnderCurve;
import metrics.Metric;
import metrics.Recall;
import ranking.ClassRankingProperties;
import ranking.Ranking;
import ranking.propertyValueCalculators.pagerank.PageRankCalculator;
import ranking.strategy.RankingStrategy;
import ranking.strategy.SimpleRankingStrategy;
import sysmodel.DSM;
import sysmodel.SystemModel;

public class VariableWeightsMainAUC {

	public static void main(String[] args) throws Exception {

		
	    String PATH_PREFIX = "refSols//"; // folder where reference
		// solutions are given
		
		// String name = "ApacheJMeter_core.jar";
		// String refSolPath = "ApacheJMeter_core.jar.txt";

		// String name = new String("argouml.jar");
		// String refSolPath = "argouml.jar.txt";

		// String name = new String("jedit.jar");
		// String refSolPath = "jedit.jar.txt";

		 String name = new String("jhotdraw.jar");
		 String refSolPath = "jhotdraw.jar.txt";

		// String name = new String("wro4j-core-1.6.3.jar");
		// String refSolPath = "wro4j-core-1.6.3.jar.txt";

		System.out.println("start...");
		System.out.println("Analyzing: " + name);

		

		// Initialize weights hardcoded, no config file ! 
	
		int inheritance = 1, implementedInterface = 1, memberBase = 1, memberIndex = 1, localBase = 1, localIndex = 1,
				paramBase = 1, paramIndex = 1, staticBase = 1, staticIndex = 1, returnBase = 1, memberAccess = 1,
				typeBinding = 1, instantiated = 1, cast = 1;
		
		DependencyStrength strength = new DependencyStrength(inheritance, implementedInterface, memberBase,
				memberIndex, localBase, localIndex, paramBase, paramIndex, staticBase, staticIndex, returnBase,
				memberAccess, typeBinding, instantiated, cast);

		DependencyStrengthFactory.setDependencyStrengthInstace(strength);

		
		File[] ss = { new File("inputs//" + name) };

		long beginning = System.currentTimeMillis();

		/* 
		 * Initial step: parse bytecode and build a list of all DependencyModel objects
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

		

		for (int repeat = 0; repeat < 3; repeat++) { // repeat 3 times with different values for weights

			if (repeat==0) {
				inheritance = 4; implementedInterface = 4; memberBase = 1; memberIndex = 2; localBase = 1; localIndex = 2;
						paramBase = 1; paramIndex = 2; staticBase = 1; staticIndex = 2; returnBase = 1; memberAccess = 1;
						typeBinding = 1; instantiated = 1; cast = 1;
			}
			else if (repeat==1) {
				inheritance = 1; implementedInterface = 1; memberBase = 1; memberIndex = 1; localBase = 1; localIndex = 1;
				paramBase = 1; paramIndex = 1; staticBase = 1; staticIndex = 1; returnBase = 1; memberAccess = 1;
				typeBinding = 3; instantiated = 3; cast = 3;
				
				
			}
			else  {
				inheritance = 1; implementedInterface = 1; memberBase = 1; memberIndex = 0; localBase = 1; localIndex = 0;
				paramBase = 1; paramIndex = 0; staticBase = 1; staticIndex = 0; returnBase = 1; memberAccess = 1;
				typeBinding = 1; instantiated = 1; cast = 1;
			}
			
			System.out.println("CHANGED WEIGHTS VALUES, repeat ="+repeat);

		
			strength = new DependencyStrength(inheritance, implementedInterface, memberBase,
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

			
			

			Reader r = new Reader();
			List<String> referenceSolution = r.readReferenceSolution(PATH_PREFIX + refSolPath);

			System.out.println("Ref Sol size " + referenceSolution.size());
			
			Ranking ranking = new Ranking(name, referenceSolution);

			System.out.println("Running PageRank  ...");
			
			boolean directed = false; // use directed or undirected graph for
										// pagerank
			int F = 2;

			System.out.println("PR variant: directed=" + directed + " F=" + F);

			
			PageRankCalculator prCalculator = new PageRankCalculator(d, directed, F, 50);
			prCalculator.computeWeightedPageRank();
			ranking.setClassList(prCalculator.getResultAsRankingEntryList());
			
			RankingStrategy rs = new SimpleRankingStrategy(ClassRankingProperties.PAGERANK_UNDIRECTED2_W);
			
			ranking.setStrategy(rs);

			ranking.rank();
			
			Metric auc = new AreaUnderCurve(ranking);
			auc.compute();

			System.out.println("AUC=" + auc.getValue());
			
			
			Metric rec = new Recall(ranking, 30);
			rec.compute();
			System.out.println("Recall in top 30 =" + rec.getValue());
			
			TimeUnit.SECONDS.sleep(3);
		   
		}
	}

	

}
