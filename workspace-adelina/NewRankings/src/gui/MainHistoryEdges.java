package gui;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

import facade.ArtCoreFacade;
import inputOutput.ExternalRankingReader;
import inputOutput.OutputWriter;
import inputOutput.Reader;
import ranking.Ranking;
import ranking.RankingEntry;
import ranking.RankingPositionNormalizer;
import ranking.strategy.AverageIgnoreWorstSorting;
import ranking.strategy.AverageIgnoreWorstSortingMinTop2;
import ranking.strategy.AverageSorting;
import ranking.strategy.AverageSortingMinTop;
import ranking.strategy.CorrectionStrategy;
import ranking.strategy.CorrectionStrategy2;
import ranking.strategy.FuzzyRulesRankingStrategy;
import ranking.strategy.MIN2Strategy;
import ranking.strategy.RankingStrategy;
import ranking.strategy.SimpleRankingStrategy;
import sysmodel.DSM;

import metrics.AreaUnderCurve;
import metrics.Metric;
import metrics.Precision;
import metrics.RankingInfo;
import metrics.RankingPositions;
import metrics.Recall;
import ranking.propertyValueCalculators.DefaultClassRankingPropertiesAdder;
import ranking.propertyValueCalculators.betweenness.BetweennessCalc;

import ranking.propertyValueCalculators.directTopLinks.DirectTopInOutLinksCalculator;
import ranking.propertyValueCalculators.directTopLinks.DirectTopLinksCalculator;
import ranking.propertyValueCalculators.directWeightTopLinks.DirectWeightTopInOutLinksCalculator;
import ranking.propertyValueCalculators.directWeightTopLinks.DirectWeightTopLinksCalculator;
import ranking.propertyValueCalculators.hits.HitsCalculator;
import ranking.propertyValueCalculators.hits.HitsValue;
import ranking.propertyValueCalculators.kcore.KcoreCalculator;
import ranking.propertyValueCalculators.pagerank.PageRankCalculator;
import ranking.ClassRankingProperties;
import ranking.Normalizer;


public class MainHistoryEdges {
	private static String PATH_PREFIX = "refSols//"; // folder where reference
	// solutions are given

	private static double recallSum = 0;

	private static PrintWriter writer;   

	private static PrintWriter positionWriter;

	private static double resultsArray[][][] = new double[100][100][100];
	private static double aucArray[][] = new double[100][100];

	private static int indMethod, indSystem;
	private static List<String> nameMethods;

	private static int numMethods, numSystems;

	static List<String> weightsVariants = Arrays.asList(new String("ist-submitted.txt")
	// new String("w_allequal.txt")
	// new String("w_callsonly.txt")
	// new String("w_callsmostly.txt")
	// new String("w_interfonly.txt")
	// new String("w_interfmostly.txt")
	);

	private static String crtCfg;

	static List<SystNames> systems = Arrays.asList(
			/*new SystNames("ant.jar", "ant.jar-zaidman-nocoll.txt", "ant_git_strength_10.csv"),
			new SystNames("ant.jar", "ant.jar-zaidman-nocoll.txt", "ant_git_strength_20.csv"),
			new SystNames("ant.jar", "ant.jar-zaidman-nocoll.txt", "ant_git_strength_30.csv"),
			new SystNames("ant.jar", "ant.jar-zaidman-nocoll.txt", "ant_git_strength_40.csv"), 
			new SystNames("ant.jar", "ant.jar-zaidman-nocoll.txt", "ant_git_strength_50.csv"),
			new SystNames("ant.jar", "ant.jar-zaidman-nocoll.txt", "ant_git_strength_60.csv"),
			new SystNames("ant.jar", "ant.jar-zaidman-nocoll.txt", "ant_git_strength_70.csv"),
			new SystNames("ant.jar", "ant.jar-zaidman-nocoll.txt", "ant_git_strength_80.csv"), 
			new SystNames("ant.jar", "ant.jar-zaidman-nocoll.txt", "ant_git_strength_90.csv"),
			new SystNames("ant.jar", "ant.jar-zaidman-nocoll.txt", "ant_git_strength_100.csv")*/
	
			new SystNames("hibernate-core-5.2.12.Final.jar", "hibernate5.2.txt", "hibernate_git_strength_10.csv"),
			new SystNames("hibernate-core-5.2.12.Final.jar", "hibernate5.2.txt", "hibernate_git_strength_20.csv"),
			new SystNames("hibernate-core-5.2.12.Final.jar", "hibernate5.2.txt", "hibernate_git_strength_30.csv"),
			new SystNames("hibernate-core-5.2.12.Final.jar", "hibernate5.2.txt", "hibernate_git_strength_40.csv"),
			new SystNames("hibernate-core-5.2.12.Final.jar", "hibernate5.2.txt", "hibernate_git_strength_50.csv"),
			new SystNames("hibernate-core-5.2.12.Final.jar", "hibernate5.2.txt", "hibernate_git_strength_60.csv"),
			new SystNames("hibernate-core-5.2.12.Final.jar", "hibernate5.2.txt", "hibernate_git_strength_70.csv"),
			new SystNames("hibernate-core-5.2.12.Final.jar", "hibernate5.2.txt", "hibernate_git_strength_80.csv"),
			new SystNames("hibernate-core-5.2.12.Final.jar", "hibernate5.2.txt", "hibernate_git_strength_90.csv"),
			new SystNames("hibernate-core-5.2.12.Final.jar", "hibernate5.2.txt", "hibernate_git_strength_100.csv")
			
			/*new SystNames("tomcat-catalina-9.0.4.jar", "catalina.txt", "catalina_git_strength_10.csv"),
			new SystNames("tomcat-catalina-9.0.4.jar", "catalina.txt", "catalina_git_strength_20.csv"),
		    new SystNames("tomcat-catalina-9.0.4.jar", "catalina.txt", "catalina_git_strength_30.csv"),
		    new SystNames("tomcat-catalina-9.0.4.jar", "catalina.txt", "catalina_git_strength_40.csv"),
		    new SystNames("tomcat-catalina-9.0.4.jar", "catalina.txt", "catalina_git_strength_50.csv"),
			new SystNames("tomcat-catalina-9.0.4.jar", "catalina.txt", "catalina_git_strength_60.csv"),
			new SystNames("tomcat-catalina-9.0.4.jar", "catalina.txt", "catalina_git_strength_70.csv"),
		    new SystNames("tomcat-catalina-9.0.4.jar", "catalina.txt", "catalina_git_strength_80.csv"),
		    new SystNames("tomcat-catalina-9.0.4.jar", "catalina.txt", "catalina_git_strength_90.csv"),
			new SystNames("tomcat-catalina-9.0.4.jar", "catalina.txt", "catalina_git_strength_100.csv")*/
		   
	);

	public static void main(String[] args) throws Exception {

		
		numSystems = systems.size();
		indSystem = -1; // global variable representing index of last processed test system
		
		for (String weightscfg : weightsVariants) { // for all variants of weighting schemes
			crtCfg = weightscfg;

			resultsArray = new double[100][100][100];
			aucArray = new double[100][100];
			indSystem = -1;
			for (SystNames s : systems) {
				doOneSystemWithHistory(s.jarName, s.refName, crtCfg, s.histName, false); 
				//doOneSystemWithHistory: OnlyHist - overwrite=true , AddHistoryToStruct - overwrite=false
				
				//doOneSystem(s.jarName, s.refName, crtCfg);
				// doOneSystem: old version, takes only structural deps, does not need history deps
	
			}

			// doComparison(k) compares the results (precision, recall) if threshold is set to n 
			// doComparison(5);
			// doComparison(10);
			// doComparison(15);
			doComparison(20);
			// doComparison(25);
			doComparison(30);
			// doComparison(35);
			// doComparison(40);
			// doComparison(50);
			doAUCComparison();
		}

	}

	public static void doOneSystem(String name, String refSolPath, String weightscfg) throws IOException {
		//final String PATH_PREFIX = "refSols//"; // folder where reference
												// solutions are given

		indSystem++;

		System.out.println("start...");
		System.out.println("Analyzing: " + name);

		ArtCoreFacade artcore = null;
		try {
			artcore = new ArtCoreFacade(weightscfg, "inputs//" + name);
		} catch (Exception e) {
			System.out.println("Something went wrong " + e.toString());
		}

		DSM d = artcore.getDSM();
		if (!name.equals("ant.jar")) {
			d.collapseInnerClasses(); // inner classes are considered part of
										// the containing class, except Ant
										// where Zaidman gives an inner class in
										// the reference solution
			System.out.println("collapsed inner for " + name);
		}

		Reader r = new Reader();
		List<String> referenceSolution = r.readReferenceSolution(PATH_PREFIX + refSolPath);

		System.out.println("Reference Solution Size " + referenceSolution.size());

		Ranking ranking = createBestRanking(d, name, referenceSolution);

		writer = new PrintWriter(new BufferedWriter(new FileWriter("outputs//" + name + "_metrics.csv")));
		
		new File("outputs//POS//"+weightscfg).mkdirs();
		
		new File("outputs//Attributes//"+weightscfg).mkdirs();		

		positionWriter = new PrintWriter(new BufferedWriter(new FileWriter("outputs//POS//"+weightscfg+"//POS_" + name + "_positions.csv")));

		List<RankingStrategy> strategies = new ArrayList<RankingStrategy>();

		indMethod = -1;

		//DefineStrategyList.addSimpleStrategies(strategies);

		DefineStrategyList.addBestStrategies(strategies);

		
		numMethods = strategies.size();// + 1;
		nameMethods = new ArrayList<String>();
		for (RankingStrategy rs : strategies) {
			nameMethods.add(rs.getDescription());
			applyStrategy(rs, ranking);
			// OutputWriter.outputRanking(ranking);
		}

		OutputWriter.outputRanking(ranking); 
		

		writer.close();
		positionWriter.close();
		
		System.out.println("THE END  for System " + name);
	}

	
	public static void doOneSystemWithHistory(String name, String refSolPath, String weightscfg, String historyEdges, boolean overwrite) throws IOException {
		//final String PATH_PREFIX = "refSols//"; // folder where reference
												// solutions are given

		indSystem++;

		System.out.println("start...");
		System.out.println("Analyzing: " + name);

		ArtCoreFacade artcore = null;
		try {
			artcore = new ArtCoreFacade(weightscfg, "inputs//" + name);
		} catch (Exception e) {
			System.out.println("Something went wrong " + e.toString());
		}

		DSM d = artcore.getDSM();
		
		System.out.println("Processing history from file "+historyEdges);
		try {
			if (overwrite) 
				d.replaceMatrixFromHistory("history//" +historyEdges);
			else
				d.addHistoryToMatrix("history//" +historyEdges);
		} catch (Exception e) {
			System.out.println("Something went wrong when reading history edges " + e.toString());
			e.printStackTrace();
		}
		
		if (!name.equals("ant.jar")) {
			d.collapseInnerClasses(); // inner classes are considered part of
										// the containing class, except Ant
										// where Zaidman gives an inner class in
										// the reference solution
			System.out.println("collapsed inner for " + name);
		}

		Reader r = new Reader();
		List<String> referenceSolution = r.readReferenceSolution(PATH_PREFIX + refSolPath);

		System.out.println("Reference Solution Size " + referenceSolution.size());

		Ranking ranking = createBestRanking(d, name, referenceSolution);

		writer = new PrintWriter(new BufferedWriter(new FileWriter("outputs//" + name + "_metrics.csv")));
		
		new File("outputs//POS//"+weightscfg).mkdirs();
		
		new File("outputs//Attributes//"+weightscfg).mkdirs();		

		positionWriter = new PrintWriter(new BufferedWriter(new FileWriter("outputs//POS//"+weightscfg+"//POS_" + name + "_positions.csv")));

		List<RankingStrategy> strategies = new ArrayList<RankingStrategy>();

		indMethod = -1;

		//DefineStrategyList.addSimpleStrategies(strategies);

		DefineStrategyList.addBestStrategies(strategies);

		numMethods = strategies.size();// + 1;
		nameMethods = new ArrayList<String>();
		for (RankingStrategy rs : strategies) {
			nameMethods.add(rs.getDescription());
			applyStrategy(rs, ranking);
			// OutputWriter.outputRanking(ranking);
		}

		OutputWriter.outputRanking(ranking); 
		
		

		writer.close();
		positionWriter.close();
		
		System.out.println("THE END  for System " + name);
	}
	
	
	
	
	private static Ranking createBestRanking(DSM d, String name, List<String> referenceSolution) {
		Ranking ranking = new Ranking(name, referenceSolution);

		
		System.out.println("Running PageRankCalculators for PR VALUES ...");

		boolean directed = false;
		int F = 2;
		System.out.println("PR variant: directed=" + directed + " F=" + F); // PR-U2-W
		PageRankCalculator prCalculator = new PageRankCalculator(d, directed, F, 50);
		long beginning = System.currentTimeMillis();

		prCalculator.computeWeightedPageRank();

		System.out.println("PR done in  " + ((double) System.currentTimeMillis() - beginning) / 1000);
			
		ranking.setClassList(prCalculator.getResultAsRankingEntryList());

		F = 1;
		System.out.println("PR variant : directed=" + directed + " F=" + F); // PR-U
		prCalculator = new PageRankCalculator(d, directed, F, 50);
		prCalculator.computePageRank();
		prCalculator.addResultToRanking(ranking.getClassList());

		directed=true;
		System.out.println("PR variant : directed=" + directed + " F=" + F); // PR
		prCalculator = new PageRankCalculator(d, directed, F, 50);
		prCalculator.computePageRank();
		prCalculator.addResultToRanking(ranking.getClassList());
		
		DefaultClassRankingPropertiesAdder defaultCalculator = new DefaultClassRankingPropertiesAdder(d, directed,
				null);
		defaultCalculator.addResultToRanking(ranking.getClassList());
		return ranking;
	}

	
	private static void applyStrategy(RankingStrategy rs, Ranking ranking) {
		ranking.setStrategy(rs);

		long beginning = System.currentTimeMillis();
		ranking.rank();
		System.out.println("\n\nDid ranking with strategy " + rs.getDescription() + " in seconds "
				+ ((double) System.currentTimeMillis() - beginning) / 1000);

		writer.print(rs.getDescription() + ", ");

		indMethod++;

		recallSum = 0;

		displayPrecRec(ranking, 5);
		displayPrecRec(ranking, 10);
		displayPrecRec(ranking, 15);
		displayPrecRec(ranking, 20);
		displayPrecRec(ranking, 25);

		displayPrecRec(ranking, 30);
		displayPrecRec(ranking, 35);
		displayPrecRec(ranking, 40);

		displayPrecRec(ranking, 50);

		Metric auc = new AreaUnderCurve(ranking);
		auc.compute();

		writer.println("" + auc.getValue() + ", " + recallSum / 8.00);
		aucArray[indMethod][indSystem] = auc.getValue();

		RankingPositions rpos = new RankingPositions(ranking);
		rpos.compute();
		List<RankingInfo> lri = rpos.getPositions();

		positionWriter.println(ranking.getJarName() + ", " + ranking.getStrategyDescription());

		for (int i = 0; i < lri.size(); i++) {
			int p = lri.get(i).pos + 1;
			positionWriter.println("" + lri.get(i).name + ", " + p);
			// System.out.println("POSITION "+lri.get(i).name + ", " + p);
		}

	}

	private static void displayPrecRec(Ranking ranking, int threshold) {

		// System.out.println("THRESHOLD=" + threshold);
		Precision precisionPR2 = new Precision(ranking, threshold);
		precisionPR2.compute();
		Recall recallPR2 = new Recall(ranking, threshold);
		recallPR2.compute();
		recallSum = recallSum + recallPR2.getValue();
		// System.out.println("Prec=" + precisionPR2.getValue() + " Recall=" +
		// recallPR2.getValue());

		writer.print("" + recallPR2.getValue() + ", ");

		resultsArray[threshold][indMethod][indSystem] = recallPR2.getValue();

	}

	private static void doComparison(int comparisonthresh) throws IOException {

		PrintWriter comparisonWriter;

		comparisonWriter = new PrintWriter(new BufferedWriter(
				new FileWriter("outputs//" + crtCfg + "_COMBINATII_comparison_Top" + comparisonthresh + ".csv")));

		comparisonWriter.print(",");
		for (int j = 0; j < numSystems; j++) {
			comparisonWriter.print(systems.get(j).jarName + ", ");
		}
		comparisonWriter.println("");
		for (int i = 0; i < numMethods; i++) {
			Double sum = 0.0, min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
			comparisonWriter.print(nameMethods.get(i) + ", ");
			for (int j = 0; j < numSystems; j++) {
				Double crt = resultsArray[comparisonthresh][i][j] / 100;
				sum = sum + crt;
				if (crt < min)
					min = crt;
				if (crt > max)
					max = crt;
				comparisonWriter.print(String.format("%.2f", resultsArray[comparisonthresh][i][j] / 100) + ", ");
				// DecimalFormat df = new DecimalFormat("#.00");
				// comparisonWriter.format("%.3",
				// resultsArray[comparisonthresh][i][j]/100);
				// comparisonWriter.print(", ");
			}
			comparisonWriter.print(String.format("%.2f", sum / numSystems) + ", ");
			comparisonWriter.print(String.format("%.2f", (sum - max - min) / (numSystems - 2)));
			comparisonWriter.println("");

		}
		comparisonWriter.close();
	}

	private static void doAUCComparison() throws IOException {

		PrintWriter comparisonWriter;
		comparisonWriter = new PrintWriter(
				new BufferedWriter(new FileWriter("outputs//" + crtCfg + "COMBINATII_AUC_comparison_Top.csv")));
		comparisonWriter.print(",");
		for (int j = 0; j < numSystems; j++) {
			comparisonWriter.print(systems.get(j).jarName + ", ");
		}
		comparisonWriter.println("");

		for (int i = 0; i < numMethods; i++) {
			comparisonWriter.print(nameMethods.get(i) + ", ");
			for (int j = 0; j < numSystems; j++) {

				comparisonWriter.print(aucArray[i][j] + ", ");
			}
			comparisonWriter.println("");
		}
		comparisonWriter.close();
	}

}
