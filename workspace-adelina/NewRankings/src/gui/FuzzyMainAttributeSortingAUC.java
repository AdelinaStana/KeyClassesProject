package gui;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;

import classloader.ClassFileHandler;
import classloader.DirHandler;
import classloader.InputHandler;
import classloader.JarHandler;
import classloader.NoSuchFileException;
import classloader.UnsupportedInputException;
import dependencyfinder.classdependencymodel.DependencyModel;
import dependencyfinder.classdependencymodel.DependencyStrengthFactory;
import dependencyfinder.util.*;
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
import sysmodel.SparceMatrix;
import sysmodel.SystemModel;
import metrics.AreaUnderCurve;
import metrics.Metric;
import metrics.Precision;
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



public class FuzzyMainAttributeSortingAUC {
	private static String PATH_PREFIX = "refSols//"; // folder where reference
	// solutions are given

	private static double recallSum = 0;

	private static PrintWriter writer;

	private static double resultsArray[][][] = new double[100][100][100];
	private static double aucArray[][] = new double[100][100];

	private static int indMethod, indSystem;
	private static List<String> nameMethods;

	private static int numMethods, numSystems;

	private static void displayPrecRec(Ranking ranking, int threshold) {

		System.out.println("THRESHOLD=" + threshold);
		Precision precisionPR2 = new Precision(ranking, threshold);
		precisionPR2.compute();
		Recall recallPR2 = new Recall(ranking, threshold);
		recallPR2.compute();
		recallSum = recallSum + recallPR2.getValue();
		System.out.println("Prec=" + precisionPR2.getValue() + " Recall=" + recallPR2.getValue());

		writer.print("" + recallPR2.getValue() + ", ");

		resultsArray[threshold][indMethod][indSystem] = recallPR2.getValue();

	}

	private static void applyStrategy(RankingStrategy rs, Ranking ranking) {
		ranking.setStrategy(rs);
		ranking.rank();
		System.out.println("\n\nDid ranking with strategy " + rs.getDescription());

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
	}

	public static void doOneExternalSystem(String name, String rankingName, String refSolPath) {
		Reader r = new Reader();
		List<String> referenceSolution = r.readReferenceSolution(PATH_PREFIX + refSolPath);
		Ranking ranking = new Ranking(name, referenceSolution);

		ExternalRankingReader reader = new ExternalRankingReader();
		List<RankingEntry> rl = reader.readRanking("inputs//" + rankingName);
		ranking.setClassList(rl);
		System.out.println("\n\nLoaded external ranking  " + name);

		writer.print(name + ", ");

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

		writer.println("" + auc.getValue() + ", " + recallSum / 6.00);
		aucArray[indMethod][indSystem] = auc.getValue();
	}

	public static void doOneSystem(String name, String refSolPath) throws IOException {
		final String PATH_PREFIX = "refSols//"; // folder where reference
		// solutions are given
		indSystem++;

		System.out.println("start...");
		System.out.println("Analyzing: " + name);

		ArtCoreFacade artcore = null;
		try {
			artcore = new ArtCoreFacade("enase2.txt", "inputs//" + name);
		} catch (Exception e) {
			System.out.println("Something went wrong " + e.toString());
		}

		DSM d = artcore.getDSM();
		d.collapseInnerClasses();

		Reader r = new Reader();
		List<String> referenceSolution = r.readReferenceSolution(PATH_PREFIX + refSolPath);

		Ranking ranking = createRanking(d, name, referenceSolution);

		updateRankingTopLinks(ranking, d);

		writer = new PrintWriter(new BufferedWriter(new FileWriter("outputs//" + name + "_metrics.csv")));

		List<RankingStrategy> strategies = new ArrayList<RankingStrategy>();

		indMethod = -1;
	 
		   
		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.PAGERANK_UNDIRECTED2_W));

		
		
		//RankingPositionNormalizer n=new RankingPositionNormalizer();
				RankingPositionNormalizer.normalize(ranking);
				Normalizer.normalize(ranking,100);
			
				
				List <ClassRankingProperties> variables1 = Arrays.asList(
		                ClassRankingProperties.PAGERANK_UNDIRECTED2_W, ClassRankingProperties.WEIGHT_CONN_TOTAL,
		                ClassRankingProperties.DIRECTTOP_W);
				
				
				
		                // ClassRankingProperties.HITS_AUTHORITY, ClassRankingProperties.HITS_HUB
		        String path1 = "newranking3in1.fcl";
				
		        strategies.add(new FuzzyRulesRankingStrategy(variables1, path1));  
		        
		        List <ClassRankingProperties> variables2 = Arrays.asList(
		                ClassRankingProperties.PAGERANK_UNDIRECTED2_W, ClassRankingProperties.WEIGHT_CONN_TOTAL,
		                ClassRankingProperties.DIRECTTOP_W, ClassRankingProperties.W_KCORE10, ClassRankingProperties.HITS_HUB_W);
				
				
				
		                // ClassRankingProperties.HITS_AUTHORITY, ClassRankingProperties.HITS_HUB
		        String path2 = "newranking3in2.fcl";
				
		        //strategies.add(new FuzzyRulesRankingStrategy(variables2, path2));  
		        
		        
				//ranking.setStrategy(new FuzzyRulesRankingStrategy(variables, path));
				//ranking.rank();
				//System.out.println("Did ranking with fuzzy rules");
				//OutputWriter.outputRanking(ranking);
				

		
		
		numMethods = strategies.size();// + 1;
		nameMethods = new ArrayList<String>();
		for (RankingStrategy rs : strategies) {
			nameMethods.add(rs.getDescription());
			applyStrategy(rs, ranking);
			OutputWriter.outputRanking(ranking);
		}
		
		writer.close();

		System.out.println("THE END - look for results in the output folder");

		// System.out.println("Nodes details dumped in " + ((double)
		// System.currentTimeMillis() - beginning) / 1000);
		System.out.println("THE END ");
	}

	
	static List<SystNames> systems = Arrays.asList(
		  //  new SystNames("ant.jar", "ant.jar.txt"),
			
		   // new SystNames("xuml-compiler-0.4.6.jar", "xuml_thung.txt"),  rau
		    
			//new SystNames("jpmc.jar", "jpmc_cipri.txt"), rau
			
	//		new SystNames("jpmc.jar", "jpmc_thung.txt"), rau
			

		    new SystNames("gwtportlets-0.95.jar", "gwt-portlets_cipri.txt"),
		    
		    		    
		    new SystNames("javaclient-2.jar", "javaclient-2_thung.txt"),
		    
			
		     new SystNames("ant.jar", "ant.jar-zaidman.txt"),
		
		     
	   	new SystNames("argouml.jar", "argouml.jar.txt"),

			new SystNames("jedit.jar", "jedit.jar.txt"),

			new SystNames("jhotdraw.jar", "jhotdraw.jar.txt"),

			//new SystNames("ApacheJMeter_core.jar", "ApacheJMeter_core.jar.txt"),
			new SystNames("ApacheJMeter_core.jar", "ApacheJMeter_core.jar-zaidman.txt"),
			
			 new SystNames("mars_3.06.jar", "mars_thung_ioana.txt"),
			// new SystNames("mars_3.06.jar", "mars_thung.txt"),
			
			new SystNames("maze.jar", "maze_thung.txt"),

			new SystNames("wro4j-core-1.6.3.jar", "wro4j-core-1.6.3.jar.txt"),
			
			new SystNames("neuroph.jar", "neuroph_thung.txt"),
			
			//new SystNames("jgap_3.4.4.jar", "jgap_thung.txt"),
			new SystNames("jgap_3.6.3.jar", "jgap_thung.txt"),
		
			new SystNames("hibernate3.5.0-final.jar", "hibernate.txt" ), 
	    	//new SystNames("hibernate-core-5.2.12.Final.jar", "hibernate5.2.txt" ), 
		
			//new SystNames("Azureus3.0.2.0.jar", "hibernate5.2.txt" ),
		
			new SystNames("log4j-core-2.10.0.jar", "log4j.txt" ),
		
		    new SystNames("tomcat-catalina-9.0.4.jar", "catalina.txt" )
			//new SystNames("catalina7.jar", "catalina.txt" )
			
		
		
			);
	
	public static void main(String[] args) throws Exception {

		
		
		
		indSystem = -1;
		numSystems = systems.size();
		
		for (SystNames s:systems) {
			doOneSystem(s.jarName, s.refName);
		}
		
		

		doComparison(5);
		doComparison(10);
		doComparison(15);
		doComparison(20);
		doComparison(25);
		doComparison(30);
		doComparison(35);
		doComparison(40);
		doComparison(50);
		doAUCComparison();

		// doOneSystem("hibernate3.5.0-final.jar", "hibernate.txt" );

		// doOneSystem("catalina7.jar", "catalina.txt" );

		// doOneExternalSystem("ant", "ant_PR_PR2_Auth_Hub.csv", "ant.jar.txt");

	}

	private static void doComparison(int comparisonthresh) throws IOException {

		PrintWriter comparisonWriter;

		comparisonWriter = new PrintWriter(
				new BufferedWriter(new FileWriter("outputs//COMBINATII_comparison_Top" + comparisonthresh + ".csv")));
		
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
				new BufferedWriter(new FileWriter("outputs//COMBINATII_AUC_comparison_Top.csv")));
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

	private static Ranking updateRankingTopLinks(Ranking ranking, DSM d) {
		ranking.setStrategy(new SimpleRankingStrategy(ClassRankingProperties.PAGERANK_UNDIRECTED2_W));
		ranking.rank();

		int linkthreshold = 30;

		Set<Integer> topclasses = ranking.getTopClassNumbers(linkthreshold);
		System.out.println("Direct Top ");
		DirectTopLinksCalculator directTopCalculator = new DirectTopLinksCalculator(d, topclasses);
		directTopCalculator.computeLinksNumber();
		directTopCalculator.addResultToRanking(ranking.getClassList());
		// OutputWriter.outputRanking(ranking);

		System.out.println("Direct Top IN/Out");
		DirectTopInOutLinksCalculator directTopInOutCalculator = new DirectTopInOutLinksCalculator(d, topclasses);
		directTopInOutCalculator.computeLinksNumber();
		directTopInOutCalculator.addResultToRanking(ranking.getClassList());

		System.out.println("Direct Top Weight");
		DirectWeightTopLinksCalculator directWTopCalculator = new DirectWeightTopLinksCalculator(d, topclasses);
		directWTopCalculator.computeLinksWeight();
		directWTopCalculator.addResultToRanking(ranking.getClassList());

		System.out.println("Direct Top Weight IN");
		DirectWeightTopInOutLinksCalculator directWTopInOutCalculator = new DirectWeightTopInOutLinksCalculator(d,
				topclasses);
		directWTopInOutCalculator.computeLinksWeight();
		directWTopInOutCalculator.addResultToRanking(ranking.getClassList());

		return ranking;
	}

	private static Ranking createRanking(DSM d, String name, List<String> referenceSolution) {
		Ranking ranking = new Ranking(name, referenceSolution);

		System.out.println("Running HitsCalculator for HUB and AUTHORITY VALUES - Weighted ...");

		HitsCalculator hitsCalculator = new HitsCalculator(d, 20);
		hitsCalculator.computeWeightedHubsAndAuthorities();
		ranking.setClassList(hitsCalculator.getResultAsRankingEntryList());

		
		System.out.println("Running HitsCalculator for HUB and AUTHORITY VALUES - Unweighted ...");

		// HitsCalculator hitsCalculator = new HitsCalculator(d, 20);
		hitsCalculator.computeHubsAndAuthorities();
		hitsCalculator.addResultToRanking(ranking.getClassList());

		
		System.out.println("Running PageRankCalculators for PR VALUES ...");
		boolean directed = false; // use directed or undirected graph for
									// pagerank
		int F = 2;
		System.out.println("PR variant: directed=" + directed + " F=" + F);
		PageRankCalculator prCalculator = new PageRankCalculator(d, directed, F, 50);
		prCalculator.computeWeightedPageRank();
		prCalculator.addResultToRanking(ranking.getClassList());

		
		F = 1;
		System.out.println("PR variant: directed=" + directed + " F=" + F);
		prCalculator = new PageRankCalculator(d, directed, F, 50);
		prCalculator.computeWeightedPageRank();
		prCalculator.addResultToRanking(ranking.getClassList());

		System.out.println("Running PageRankCalculators for PR VALUES ...");
		directed = true; // use directed or undirected graph for
							// pagerank
		F = 0;
		System.out.println("PR variant: directed=" + directed + " F=" + F);
		prCalculator = new PageRankCalculator(d, directed, F, 50);
		prCalculator.computeWeightedPageRank();
		prCalculator.addResultToRanking(ranking.getClassList());

		System.out.println("Running PageRankCalculators for PR VALUES ...");
		directed = false; // use directed or undirected graph for
							// pagerank
		F = 2;
		System.out.println("PR variant NOT weighted: directed=" + directed + " F=" + F);
		prCalculator = new PageRankCalculator(d, directed, F, 50);
		prCalculator.computePageRank();
		prCalculator.addResultToRanking(ranking.getClassList());

		F = 1;
		System.out.println("PR variant: directed=" + directed + " F=" + F);
		prCalculator = new PageRankCalculator(d, directed, F, 50);
		prCalculator.computeWeightedPageRank();
		prCalculator.addResultToRanking(ranking.getClassList());

		System.out.println("Running PageRankCalculators for PR VALUES ...");
		directed = true; // use directed or undirected graph for
							// pagerank
		F = 0;
		System.out.println("PR variant NOT weighted: directed=" + directed + " F=" + F);
		prCalculator = new PageRankCalculator(d, directed, F, 50);
		prCalculator.computePageRank();
		prCalculator.addResultToRanking(ranking.getClassList());

		
		
		System.out.println("Running Betweenness  ...");
		directed = false; // use directed or undirected graph for
									
	
		System.out.println("BW variant: directed=" + directed + " F=" + F);
		BetweennessCalc bwCalculator = new BetweennessCalc(d, directed, 1);
	    bwCalculator.computeWeightedBetweennes();
		bwCalculator.addResultToRanking(ranking.getClassList());

		bwCalculator.computeBetweennes();
		bwCalculator.addResultToRanking(ranking.getClassList());
		
		
		bwCalculator = new BetweennessCalc(d, directed, 2);
	    bwCalculator.computeWeightedBetweennes();
		bwCalculator.addResultToRanking(ranking.getClassList());

		bwCalculator.computeBetweennes();
		bwCalculator.addResultToRanking(ranking.getClassList());
		
	
		
		
		
	
		
		
		System.out.println("K Core ");
		KcoreCalculator kcoreCalculator = new KcoreCalculator(d);
		kcoreCalculator.computeKcore();
		kcoreCalculator.addResultToRanking(ranking.getClassList());
		kcoreCalculator.computeWeightedKcore();
		kcoreCalculator.addResultToRanking(ranking.getClassList());

		
		DefaultClassRankingPropertiesAdder defaultCalculator = new DefaultClassRankingPropertiesAdder(d, directed,
				null);
		defaultCalculator.addResultToRanking(ranking.getClassList());
		return ranking;
	}

}
