package gui;

import java.util.Arrays;
import java.util.List;

import ranking.ClassRankingProperties;
import ranking.strategy.AverageIgnoreWorstSorting;
import ranking.strategy.AverageSorting;
import ranking.strategy.FuzzyRulesRankingStrategy;
import ranking.strategy.RankingStrategy;
import ranking.strategy.SimpleRankingStrategy;

public class DefineStrategyList {
	
	public static void addBestStrategies(List<RankingStrategy> strategies) {	
		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.PAGERANK_UNDIRECTED2_W));
        strategies.add(new SimpleRankingStrategy(ClassRankingProperties.PAGERANK_DIRECTED)); 
		
		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.PAGERANK_UNDIRECTED1));
		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.WEIGHT_CONN_TOTAL));
	    strategies.add(new SimpleRankingStrategy(ClassRankingProperties.CONN_TOTAL));
	    
	}
	    
	
	public static void addSimpleStrategies(List<RankingStrategy> strategies) {
	
	    strategies.add(new SimpleRankingStrategy(ClassRankingProperties.FIELD_NR));
		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.METHOD_NR));
		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.PFIELD_NR));
		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.PMETHOD_NR));
		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.SIZE));

		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.CONN_TO_COL));
		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.CONN_TO_ROW));
		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.CONN_TOTAL));
		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.WEIGHT_IN));
		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.WEIGHT_OUT));

		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.WEIGHT_CONN_TOTAL));

		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.PAGERANK_DIRECTED)); 
		
		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.PAGERANK_UNDIRECTED1));
		
		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.PAGERANK_DIRECTED_W));

		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.PAGERANK_UNDIRECTED2_W));

		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.PAGERANK_UNDIRECTED1_W));

		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.BTW_UNDIRECTED));
		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.BTW_UNDIRECTED_W));
		//strategies.add(new SimpleRankingStrategy(ClassRankingProperties.BTW_UNDIRECTED_W2));

		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.HITS_AUTHORITY));
		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.HITS_HUB));

		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.HITS_AUTHORITY_W));
		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.HITS_HUB_W));

		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.KCORE10));
		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.W_KCORE10));

		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.DIRECTTOP));

		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.DIRECTTOP_IN));

		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.DIRECTTOP_OUT));

		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.DIRECTTOP_W));

		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.DIRECTTOP_W_IN));

		strategies.add(new SimpleRankingStrategy(ClassRankingProperties.DIRECTTOP_W_OUT));

	}

	public static void addMultiplePropertiesStrategies(List<RankingStrategy> strategies) {
		// Normalizer.normalize(ranking, 10000);

		List<ClassRankingProperties> props1 = Arrays.asList(ClassRankingProperties.PAGERANK_UNDIRECTED2_W,
				ClassRankingProperties.WEIGHT_CONN_TOTAL);

		List<ClassRankingProperties> props2 = Arrays.asList(ClassRankingProperties.PAGERANK_UNDIRECTED2_W,
				ClassRankingProperties.WEIGHT_CONN_TOTAL, ClassRankingProperties.DIRECTTOP_W);

		/*
		 * 
		 * 
		 * List<ClassRankingProperties> props3 =
		 * Arrays.asList(ClassRankingProperties.PAGERANK_UNDIRECTED2_W,
		 * ClassRankingProperties.WEIGHT_CONN_TOTAL,
		 * ClassRankingProperties.DIRECTTOP_W,
		 * ClassRankingProperties.PAGERANK_UNDIRECTED1_W);
		 * 
		 * List<ClassRankingProperties> props4 =
		 * Arrays.asList(ClassRankingProperties.PAGERANK_UNDIRECTED2_W,
		 * ClassRankingProperties.WEIGHT_CONN_TOTAL,
		 * ClassRankingProperties.DIRECTTOP_W,
		 * ClassRankingProperties.PAGERANK_UNDIRECTED1_W,
		 * ClassRankingProperties.DIRECTTOP_W_IN);
		 * 
		 * List<ClassRankingProperties> props5 =
		 * Arrays.asList(ClassRankingProperties.PAGERANK_UNDIRECTED2_W,
		 * ClassRankingProperties.WEIGHT_CONN_TOTAL,
		 * ClassRankingProperties.DIRECTTOP_W,
		 * ClassRankingProperties.PAGERANK_UNDIRECTED1_W,
		 * ClassRankingProperties.DIRECTTOP_W_IN,
		 * ClassRankingProperties.WEIGHT_IN);
		 * 
		 * List<ClassRankingProperties> props6 =
		 * Arrays.asList(ClassRankingProperties.PAGERANK_UNDIRECTED2_W,
		 * ClassRankingProperties.WEIGHT_CONN_TOTAL,
		 * ClassRankingProperties.DIRECTTOP_W,
		 * ClassRankingProperties.PAGERANK_UNDIRECTED1_W,
		 * ClassRankingProperties.DIRECTTOP_W_IN,
		 * ClassRankingProperties.WEIGHT_IN,
		 * ClassRankingProperties.HITS_AUTHORITY_W);
		 * 
		 * List<ClassRankingProperties> props7 =
		 * Arrays.asList(ClassRankingProperties.PAGERANK_UNDIRECTED2_W,
		 * ClassRankingProperties.WEIGHT_CONN_TOTAL,
		 * ClassRankingProperties.DIRECTTOP_W,
		 * ClassRankingProperties.PAGERANK_UNDIRECTED1_W,
		 * ClassRankingProperties.DIRECTTOP_W_IN,
		 * ClassRankingProperties.WEIGHT_IN,
		 * ClassRankingProperties.HITS_AUTHORITY_W,
		 * ClassRankingProperties.DIRECTTOP);
		 * 
		 * List<ClassRankingProperties> props8 =
		 * Arrays.asList(ClassRankingProperties.PAGERANK_UNDIRECTED2_W,
		 * ClassRankingProperties.WEIGHT_CONN_TOTAL,
		 * ClassRankingProperties.DIRECTTOP_W,
		 * ClassRankingProperties.PAGERANK_UNDIRECTED1_W,
		 * ClassRankingProperties.DIRECTTOP_W_IN,
		 * ClassRankingProperties.WEIGHT_IN,
		 * ClassRankingProperties.HITS_AUTHORITY_W,
		 * ClassRankingProperties.DIRECTTOP,
		 * ClassRankingProperties.DIRECTTOP_IN);
		 * 
		 * List<ClassRankingProperties> props9 =
		 * Arrays.asList(ClassRankingProperties.PAGERANK_UNDIRECTED2_W,
		 * ClassRankingProperties.WEIGHT_CONN_TOTAL,
		 * ClassRankingProperties.DIRECTTOP_W,
		 * ClassRankingProperties.PAGERANK_UNDIRECTED1_W,
		 * ClassRankingProperties.DIRECTTOP_W_IN,
		 * ClassRankingProperties.WEIGHT_IN,
		 * ClassRankingProperties.HITS_AUTHORITY_W,
		 * ClassRankingProperties.DIRECTTOP,
		 * ClassRankingProperties.DIRECTTOP_IN,
		 * ClassRankingProperties.PAGERANK_DIRECTED_W);
		 * 
		 * List<ClassRankingProperties> props10 =
		 * Arrays.asList(ClassRankingProperties.PAGERANK_UNDIRECTED2_W,
		 * ClassRankingProperties.WEIGHT_CONN_TOTAL,
		 * ClassRankingProperties.DIRECTTOP_W,
		 * ClassRankingProperties.PAGERANK_UNDIRECTED1_W,
		 * ClassRankingProperties.DIRECTTOP_W_IN,
		 * ClassRankingProperties.WEIGHT_IN,
		 * ClassRankingProperties.HITS_AUTHORITY_W,
		 * ClassRankingProperties.DIRECTTOP,
		 * ClassRankingProperties.DIRECTTOP_IN,
		 * ClassRankingProperties.PAGERANK_DIRECTED_W,
		 * ClassRankingProperties.CONN_TOTAL);
		 * 
		 * List<ClassRankingProperties> props11 =
		 * Arrays.asList(ClassRankingProperties.PAGERANK_UNDIRECTED2_W,
		 * ClassRankingProperties.WEIGHT_CONN_TOTAL,
		 * ClassRankingProperties.DIRECTTOP_W,
		 * ClassRankingProperties.PAGERANK_UNDIRECTED1_W,
		 * ClassRankingProperties.DIRECTTOP_W_IN,
		 * ClassRankingProperties.WEIGHT_IN,
		 * ClassRankingProperties.HITS_AUTHORITY_W,
		 * ClassRankingProperties.DIRECTTOP,
		 * ClassRankingProperties.DIRECTTOP_IN,
		 * ClassRankingProperties.PAGERANK_DIRECTED_W,
		 * ClassRankingProperties.CONN_TOTAL,
		 * ClassRankingProperties.PAGERANK_UNDIRECTED2 );
		 * 
		 * List<ClassRankingProperties> props12 =
		 * Arrays.asList(ClassRankingProperties.PAGERANK_UNDIRECTED2_W,
		 * ClassRankingProperties.WEIGHT_CONN_TOTAL,
		 * ClassRankingProperties.DIRECTTOP_W,
		 * ClassRankingProperties.PAGERANK_UNDIRECTED1_W,
		 * ClassRankingProperties.DIRECTTOP_W_IN,
		 * ClassRankingProperties.WEIGHT_IN,
		 * ClassRankingProperties.HITS_AUTHORITY_W,
		 * ClassRankingProperties.DIRECTTOP,
		 * ClassRankingProperties.DIRECTTOP_IN,
		 * ClassRankingProperties.PAGERANK_DIRECTED_W,
		 * ClassRankingProperties.CONN_TOTAL,
		 * ClassRankingProperties.PAGERANK_UNDIRECTED2,
		 * ClassRankingProperties.HITS_AUTHORITY);
		 * 
		 * List<ClassRankingProperties> props13 =
		 * Arrays.asList(ClassRankingProperties.PAGERANK_UNDIRECTED2_W,
		 * ClassRankingProperties.WEIGHT_CONN_TOTAL,
		 * ClassRankingProperties.DIRECTTOP_W,
		 * ClassRankingProperties.PAGERANK_UNDIRECTED1_W,
		 * ClassRankingProperties.DIRECTTOP_W_IN,
		 * ClassRankingProperties.WEIGHT_IN,
		 * ClassRankingProperties.HITS_AUTHORITY_W,
		 * ClassRankingProperties.DIRECTTOP,
		 * ClassRankingProperties.DIRECTTOP_IN,
		 * ClassRankingProperties.PAGERANK_DIRECTED_W,
		 * ClassRankingProperties.CONN_TOTAL,
		 * ClassRankingProperties.PAGERANK_UNDIRECTED2,
		 * ClassRankingProperties.HITS_AUTHORITY,
		 * ClassRankingProperties.CONN_TO_ROW);
		 * 
		 */

		List<ClassRankingProperties> props14 = Arrays.asList(ClassRankingProperties.PAGERANK_UNDIRECTED2_W,
				ClassRankingProperties.WEIGHT_CONN_TOTAL, ClassRankingProperties.DIRECTTOP_W,
				ClassRankingProperties.PAGERANK_UNDIRECTED1_W, ClassRankingProperties.DIRECTTOP_W_IN,
				ClassRankingProperties.WEIGHT_IN, ClassRankingProperties.HITS_AUTHORITY_W,
				ClassRankingProperties.DIRECTTOP, ClassRankingProperties.DIRECTTOP_IN,
				ClassRankingProperties.PAGERANK_DIRECTED_W, ClassRankingProperties.CONN_TOTAL,
				ClassRankingProperties.PAGERANK_UNDIRECTED1, ClassRankingProperties.HITS_AUTHORITY,
				ClassRankingProperties.CONN_TO_ROW, ClassRankingProperties.PAGERANK_DIRECTED);

		List<ClassRankingProperties> propsall = Arrays.asList(ClassRankingProperties.PAGERANK_UNDIRECTED2_W,
				ClassRankingProperties.WEIGHT_CONN_TOTAL, ClassRankingProperties.DIRECTTOP_W,
				ClassRankingProperties.PAGERANK_UNDIRECTED1_W, ClassRankingProperties.DIRECTTOP_W_IN,
				ClassRankingProperties.WEIGHT_IN, ClassRankingProperties.HITS_AUTHORITY_W,
				ClassRankingProperties.DIRECTTOP, ClassRankingProperties.DIRECTTOP_IN,
				ClassRankingProperties.PAGERANK_DIRECTED_W, ClassRankingProperties.CONN_TOTAL,
				ClassRankingProperties.PAGERANK_UNDIRECTED1, ClassRankingProperties.HITS_AUTHORITY,
				ClassRankingProperties.CONN_TO_ROW, ClassRankingProperties.PAGERANK_DIRECTED,
				ClassRankingProperties.WEIGHT_OUT, ClassRankingProperties.HITS_HUB_W, ClassRankingProperties.KCORE10,
				ClassRankingProperties.DIRECTTOP_W_OUT, ClassRankingProperties.METHOD_NR,
				ClassRankingProperties.DIRECTTOP_OUT, ClassRankingProperties.SIZE, ClassRankingProperties.CONN_TO_COL,
				ClassRankingProperties.FIELD_NR, ClassRankingProperties.HITS_HUB);

		/*
		 * List<ClassRankingProperties> props10 =
		 * Arrays.asList(ClassRankingProperties.PAGERANK_DIRECTED_W,
		 * ClassRankingProperties.PAGERANK_UNDIRECTED2_W,
		 * ClassRankingProperties.HITS_AUTHORITY_W,
		 * ClassRankingProperties.HITS_HUB_W);
		 * 
		 * List<ClassRankingProperties> props11 =
		 * Arrays.asList(ClassRankingProperties.PAGERANK_DIRECTED_W,
		 * ClassRankingProperties.HITS_AUTHORITY_W,
		 * ClassRankingProperties.HITS_HUB_W);
		 * 
		 * List<ClassRankingProperties> props12 = Arrays.asList( //
		 * ClassRankingProperties.PAGERANK_UNDIRECTED2_W,
		 * ClassRankingProperties.WEIGHT_CONN_TOTAL,
		 * ClassRankingProperties.DIRECTTOP_W,
		 * ClassRankingProperties.PAGERANK_DIRECTED_W,
		 * ClassRankingProperties.HITS_AUTHORITY_W,
		 * ClassRankingProperties.HITS_HUB_W);
		 * 
		 * List<ClassRankingProperties> props13 =
		 * Arrays.asList(ClassRankingProperties.PAGERANK_UNDIRECTED2_W,
		 * ClassRankingProperties.WEIGHT_CONN_TOTAL,
		 * ClassRankingProperties.DIRECTTOP_W,
		 * ClassRankingProperties.PAGERANK_DIRECTED_W,
		 * ClassRankingProperties.HITS_AUTHORITY_W,
		 * ClassRankingProperties.HITS_HUB_W);
		 * 
		 * List<ClassRankingProperties> props14 =
		 * Arrays.asList(ClassRankingProperties.PAGERANK_UNDIRECTED2_W,
		 * ClassRankingProperties.WEIGHT_CONN_TOTAL,
		 * ClassRankingProperties.DIRECTTOP_W,
		 * ClassRankingProperties.PAGERANK_DIRECTED_W,
		 * ClassRankingProperties.HITS_AUTHORITY_W);
		 */
		List<ClassRankingProperties> propsgood = Arrays.asList(ClassRankingProperties.PAGERANK_UNDIRECTED2_W,
				ClassRankingProperties.WEIGHT_CONN_TOTAL, ClassRankingProperties.DIRECTTOP_W,
				ClassRankingProperties.PAGERANK_UNDIRECTED1_W, ClassRankingProperties.PAGERANK_DIRECTED_W,
				ClassRankingProperties.HITS_AUTHORITY_W);

		List<ClassRankingProperties> propsgood2 = Arrays.asList(ClassRankingProperties.W_KCORE10,
				ClassRankingProperties.PAGERANK_UNDIRECTED2_W, ClassRankingProperties.WEIGHT_CONN_TOTAL,
				ClassRankingProperties.DIRECTTOP_W, ClassRankingProperties.PAGERANK_UNDIRECTED1_W,
				ClassRankingProperties.PAGERANK_DIRECTED_W, ClassRankingProperties.HITS_AUTHORITY_W);

		List<ClassRankingProperties> propsgood3 = Arrays.asList(ClassRankingProperties.W_KCORE10,
				ClassRankingProperties.PAGERANK_UNDIRECTED2_W, ClassRankingProperties.WEIGHT_CONN_TOTAL,
				ClassRankingProperties.DIRECTTOP_W);

		List<ClassRankingProperties> props_all_network = Arrays.asList(ClassRankingProperties.PAGERANK_UNDIRECTED2_W,
				ClassRankingProperties.PAGERANK_UNDIRECTED1_W, ClassRankingProperties.HITS_AUTHORITY_W,
				ClassRankingProperties.PAGERANK_DIRECTED_W, ClassRankingProperties.PAGERANK_UNDIRECTED1,
				ClassRankingProperties.HITS_AUTHORITY, ClassRankingProperties.PAGERANK_DIRECTED,
				ClassRankingProperties.HITS_HUB_W, ClassRankingProperties.KCORE10, ClassRankingProperties.HITS_HUB);

		List<ClassRankingProperties> props_good_network = Arrays.asList(ClassRankingProperties.PAGERANK_UNDIRECTED2_W,
				ClassRankingProperties.PAGERANK_UNDIRECTED1_W, ClassRankingProperties.HITS_AUTHORITY_W,
				ClassRankingProperties.PAGERANK_DIRECTED_W, ClassRankingProperties.PAGERANK_UNDIRECTED1,
				ClassRankingProperties.HITS_AUTHORITY, ClassRankingProperties.PAGERANK_DIRECTED);

		List<ClassRankingProperties> props_all_conn = Arrays.asList(ClassRankingProperties.WEIGHT_CONN_TOTAL,

				ClassRankingProperties.WEIGHT_IN,

				ClassRankingProperties.CONN_TO_ROW, ClassRankingProperties.WEIGHT_OUT,
				ClassRankingProperties.CONN_TO_COL

		);

		List<ClassRankingProperties> props_good_conn = Arrays.asList(ClassRankingProperties.WEIGHT_CONN_TOTAL,

				ClassRankingProperties.WEIGHT_IN,

				ClassRankingProperties.CONN_TO_ROW

		);

		List<ClassRankingProperties> props_all_directtop = Arrays.asList(ClassRankingProperties.DIRECTTOP_W,
				ClassRankingProperties.DIRECTTOP_W_IN, ClassRankingProperties.DIRECTTOP,
				ClassRankingProperties.DIRECTTOP_IN, ClassRankingProperties.DIRECTTOP_W_OUT,
				ClassRankingProperties.DIRECTTOP_OUT);

		List<ClassRankingProperties> props_good_directtop = Arrays.asList(ClassRankingProperties.DIRECTTOP_W,
				ClassRankingProperties.DIRECTTOP_W_IN, ClassRankingProperties.DIRECTTOP,
				ClassRankingProperties.DIRECTTOP_IN

		);

		List<List<ClassRankingProperties>> ll = Arrays.asList(
				/*
				 * props1, props2,*
				 */

				/*
				 * props3, props4, props5, props6, props7, props8, props9,
				 * props10, props11, props12, props13,
				 */
				/* props14, propsall, */
				propsgood, propsgood2, propsgood3
		/*
		 * , props_all_network, props_good_network, props_all_conn,
		 * props_good_conn, props_all_directtop, props_good_directtop
		 */ );

		for (List<ClassRankingProperties> p : ll) {

			strategies.add(new AverageSorting(p));

			strategies.add(new AverageIgnoreWorstSorting(p));
		}

	}
	
	public static void addFuzzyStrategies(List<RankingStrategy> strategies) {
	// RankingPositionNormalizer should be used before
			
			List<ClassRankingProperties> variables = Arrays.asList(ClassRankingProperties.PAGERANK_UNDIRECTED2_W,
					ClassRankingProperties.WEIGHT_CONN_TOTAL, ClassRankingProperties.DIRECTTOP_W);

			String path1 = "newranking3in.fcl";

			strategies.add(new FuzzyRulesRankingStrategy(variables, path1));

	}	
}
