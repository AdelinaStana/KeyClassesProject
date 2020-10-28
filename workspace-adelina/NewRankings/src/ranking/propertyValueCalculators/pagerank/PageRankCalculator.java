package ranking.propertyValueCalculators.pagerank;

import ranking.propertyValueCalculators.Calculator;
import sysmodel.DSM;
import sysmodel.SparceMatrix;
import ranking.ClassRankingProperties;
import ranking.RankingEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageRankCalculator implements Calculator {

	private DSM dsm;
	private SparceMatrix<Integer> graph;
	boolean directed;
	private Integer F;
	private int iterations;
	private Map<Integer, Double> result;
	private boolean weighted;

	public PageRankCalculator(DSM dsm, boolean directed, Integer fraction, int iterations) {
		this.dsm = dsm;
		this.directed = directed;
		this.F = fraction;
		this.iterations = iterations;
		setGraphMatrix();
	}

	private void setGraphMatrix() {
		if (!directed) {
			graph = dsm.getDependencyMatrix().createUndirected(F);
		} else {
			graph = dsm.getDependencyMatrix();
		}
	}

	public Map<Integer, Double> getResult() {
		return new HashMap(result);
	}

	private boolean convergent(Map<Integer, Double> m1, Map<Integer, Double> m2) {
		for (Integer v : m1.keySet())
			if (Math.abs(m2.get(v) - m1.get(v)) > 0.000000000000000001)
				return false;
		return true;
	}

	public Map<Integer, Double> computePageRank() {
		double d = 0.85;
		int N = graph.getNumberOfNodes();
		System.out.println("Nr of nodes N=" + N);

		weighted = false;

		Map<Integer, Double> opr = new HashMap<Integer, Double>();
		Map<Integer, Double> npr = new HashMap<Integer, Double>();

		List<Integer> nodes = graph.getAllNodes();

		List<Integer> nodesWithoutOutlinks = graph.getNodesWithoutOutlinks();

		for (Integer n : nodes)
			npr.put(n, 1.0);
		for (Integer n : nodes)
			opr.put(n, 1.0 / N);

		while (iterations > 0) {
			double dp = 0;
			// System.out.println("PR iteration " + iterations);
			for (Integer p : nodesWithoutOutlinks) {
				// System.out.println("Node without outlinks "+p);
				dp = dp + d * opr.get(p) / N; // added d factor here !!!
			}

			for (Integer p : nodes) {
				double nprp;
				nprp = dp + (1 - d) / N;

				for (Integer ip : graph.inboundNeighbors(p)) {
					// System.out.println(p+"gets something from "+ip);
					// System.out.println("Outdegree of "+ip+" is
					// "+g.outDegree(ip));

					nprp = nprp + d * opr.get(ip) / graph.outDegree(ip);
				}
				npr.put(p, nprp);

			}
			Map<Integer, Double> temp;
			temp = opr;
			opr = npr;
			npr = temp;

		//	System.out.println(npr);

			iterations = iterations - 1;
		}

		result = npr;
		return result;
	}

	public Map<Integer, Double> computeWeightedPageRank() {
		double d = 0.85;
		int N = graph.getNumberOfNodes();

		weighted = true;

		Map<Integer, Double> opr = new HashMap<Integer, Double>();
		Map<Integer, Double> npr = new HashMap<Integer, Double>();

		Map<Integer, Double> temp;

		List<Integer> nodes = graph.getAllNodes();

		List<Integer> nodesWithoutOutlinks = graph.getNodesWithoutOutlinks();

		for (Integer n : nodes)
			npr.put(n, 1.0);
		for (Integer n : nodes)
			opr.put(n, 1.0 / N);

		while (iterations > 0) {
			double dp = 0;
			// System.out.println("> PR iteration " + iterations);
			for (Integer p : nodesWithoutOutlinks) {
				// System.out.println("Node without outlinks "+p);
				dp = dp + d * opr.get(p) / N; // added d factor !!!
			}
			// System.out.println("Done nodes without outlinks");
			for (Integer p : nodes) {

				double nprp;
				nprp = dp + (1 - d) / N;
				// if (p%1000==0)
				// System.out.println("PR iter "+iterations+": "+p+" ");
				for (Integer ip : graph.inboundNeighbors(p)) {
					// System.out.println(p+"gets "+g.Weight(ip,p) +" out of
					// "+g.outWeight(ip)+" from "+ip);
					// System.out.println("Outdegree of "+ip+" is
					// "+g.outDegree(ip));

					// ip -> p

					nprp = nprp + d * opr.get(ip) * graph.Weight(ip, p) / graph.outWeight(ip);
				}
				npr.put(p, nprp);
			}

			temp = opr;
			opr = npr;
			npr = temp;

			// System.out.println(npr);

			iterations = iterations - 1;
		}

		result = npr;
		return result;
	}

	public List<RankingEntry> getResultAsRankingEntryList() {
		List<RankingEntry> ranking = new ArrayList<>();
		for (Map.Entry<Integer, Double> resultEntry : result.entrySet()) {
			RankingEntry rankingEntry = new RankingEntry();
			rankingEntry.setClassName(dsm.elementAtFull(resultEntry.getKey()).getName());
			rankingEntry.setClassNumber(resultEntry.getKey());
	
			if (weighted) {
			if (directed) {
				rankingEntry.setResultValue(ClassRankingProperties.PAGERANK_DIRECTED_W, resultEntry.getValue());
			} else {
				if (F == 1)
					rankingEntry.setResultValue(ClassRankingProperties.PAGERANK_UNDIRECTED1_W, resultEntry.getValue());
				else if (F == 2)
					rankingEntry.setResultValue(ClassRankingProperties.PAGERANK_UNDIRECTED2_W, resultEntry.getValue());
				else if (F == 4)
					rankingEntry.setResultValue(ClassRankingProperties.PAGERANK_UNDIRECTED4_W, resultEntry.getValue());

			}
			}
			else {
				if (directed) {
					rankingEntry.setResultValue(ClassRankingProperties.PAGERANK_DIRECTED, resultEntry.getValue());
				} else {
					if (F == 1)
						rankingEntry.setResultValue(ClassRankingProperties.PAGERANK_UNDIRECTED1, resultEntry.getValue());
					//else if (F == 2)
					//	rankingEntry.setResultValue(ClassRankingProperties.PAGERANK_UNDIRECTED2, resultEntry.getValue());
					//else if (F == 4)
					//	rankingEntry.setResultValue(ClassRankingProperties.PAGERANK_UNDIRECTED4, resultEntry.getValue());

				}
			}
			ranking.add(rankingEntry);
		}

		return ranking;
	}

	@Override
	public void addResultToRanking(List<RankingEntry> ranking) {
		if (ranking != null && ranking.size() > 0) {
			for (RankingEntry rankingEntry : ranking) {
				if (weighted) {
					if (directed) {
						rankingEntry.setResultValue(ClassRankingProperties.PAGERANK_DIRECTED_W,
								result.get(rankingEntry.getClassNumber()));
					} else {
						if (F == 1)
							rankingEntry.setResultValue(ClassRankingProperties.PAGERANK_UNDIRECTED1_W,
									result.get(rankingEntry.getClassNumber()));
						else if (F == 2)
							rankingEntry.setResultValue(ClassRankingProperties.PAGERANK_UNDIRECTED2_W,
									result.get(rankingEntry.getClassNumber()));
						else if (F == 4)
							rankingEntry.setResultValue(ClassRankingProperties.PAGERANK_UNDIRECTED4_W,
									result.get(rankingEntry.getClassNumber()));
					}
				} else {
					if (directed) {
						rankingEntry.setResultValue(ClassRankingProperties.PAGERANK_DIRECTED,
								result.get(rankingEntry.getClassNumber()));
					} else {
						if (F == 1)
							rankingEntry.setResultValue(ClassRankingProperties.PAGERANK_UNDIRECTED1,
									result.get(rankingEntry.getClassNumber()));
					//	else if (F == 2)
					//		rankingEntry.setResultValue(ClassRankingProperties.PAGERANK_UNDIRECTED2,
					//				result.get(rankingEntry.getClassNumber()));
					//	else if (F == 4)
					//		rankingEntry.setResultValue(ClassRankingProperties.PAGERANK_UNDIRECTED4,
					//				result.get(rankingEntry.getClassNumber()));
					}

				}
			}
		}
	}
}
