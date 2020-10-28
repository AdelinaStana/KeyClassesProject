package ranking.propertyValueCalculators.kcore;

import ranking.propertyValueCalculators.Calculator;
import sysmodel.DSM;
import sysmodel.SparceMatrix;
import ranking.ClassRankingProperties;
import ranking.RankingEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class KcoreCalculator implements Calculator {

	private DSM dsm;
	private SparceMatrix<Integer> graph;

	private Integer k;

	private Map<Integer, Double> result;

	private boolean weighted;

	public KcoreCalculator(DSM dsm) {
		this.dsm = dsm;
		this.k = 1;

		setGraphMatrix();
	}

	private void setGraphMatrix() {

		graph = dsm.getDependencyMatrix().createUndirected(1);

	}

	public Map<Integer, Double> getResult() {
		return new HashMap(result);
	}

	public Map<Integer, Double> computeKcore() {

		weighted = false;

		setGraphMatrix();

		int N = graph.getNumberOfNodes();
		List<Integer> nodes = graph.getAllNodes();

		result = new HashMap<Integer, Double>();
		for (Integer p : nodes)
			result.put(p, 0.0);

		// System.out.println("Nr of nodes N=" + N);
		boolean continuam = true;
		for (k = 1; (continuam) && (k < N); k++) {
	//	System.out.println("k=" + k);
			setGraphMatrix();
			// Map<Integer, Double> opr = new HashMap<Integer, Double>();
			// Map<Integer, Double> npr = new HashMap<Integer, Double>();

			// nodes = graph.getAllNodes();
			Set<Integer> deletedNodes = new TreeSet<Integer>();

			boolean action = true;
			while (action) {
				action = false;
				for (Integer p : nodes) {

					if ((!deletedNodes.contains(p)) && (graph.inboundNeighbors(p).size() < k)) {
						graph.deleteNode(p);
						deletedNodes.add(p);
						action = true;
					}

				}

			}
			continuam = false;

			for (Integer p : nodes)
				if ((!deletedNodes.contains(p))) {
					double dk = k;
					result.put(p, dk);
					continuam = true;
				}
		}

		return result;
	}


	
	public Map<Integer, Double> computeWeightedKcore() {

		weighted = true;

		setGraphMatrix();

		int N = graph.getNumberOfNodes();
		List<Integer> nodes = graph.getAllNodes();

		result = new HashMap<Integer, Double>();
		for (Integer p : nodes)
			result.put(p, 0.0);

		// System.out.println("Nr of nodes N=" + N);
		boolean continuam = true;
		for (k = 1; (continuam) && (k < N); k++) {
			//System.out.println("k=" + k);
			setGraphMatrix();
			// Map<Integer, Double> opr = new HashMap<Integer, Double>();
			// Map<Integer, Double> npr = new HashMap<Integer, Double>();

			// nodes = graph.getAllNodes();
			Set<Integer> deletedNodes = new TreeSet<Integer>();

			boolean action = true;
			while (action) {
				action = false;
				for (Integer p : nodes) {
					double degree=Math.sqrt(graph.inboundNeighbors(p).size()*graph.inWeight(p));
					if ((!deletedNodes.contains(p)) && (degree < k)) {
						graph.deleteNode(p);
						deletedNodes.add(p);
						action = true;
					}

				}

			}
			continuam = false;

			for (Integer p : nodes)
				if ((!deletedNodes.contains(p))) {
					double dk = Math.sqrt(graph.inboundNeighbors(p).size()*graph.inWeight(p));
					result.put(p, dk);
					continuam = true;
				}
		}

		return result;
	}

	
	
	
	
	public List<RankingEntry> getResultAsRankingEntryList() {
		List<RankingEntry> ranking = new ArrayList<>();
		for (Map.Entry<Integer, Double> resultEntry : result.entrySet()) {
			RankingEntry rankingEntry = new RankingEntry();
			rankingEntry.setClassName(dsm.elementAtFull(resultEntry.getKey()).getName());
			rankingEntry.setClassNumber(resultEntry.getKey());
			if(weighted) {
			rankingEntry.setResultValue(ClassRankingProperties.W_KCORE10, resultEntry.getValue());
			}
			else {
				rankingEntry.setResultValue(ClassRankingProperties.KCORE10, resultEntry.getValue());
			}

			ranking.add(rankingEntry);
		}

		return ranking;
	}

	@Override
	public void addResultToRanking(List<RankingEntry> ranking) {
		if (ranking != null && ranking.size() > 0) {
			for (RankingEntry rankingEntry : ranking) {
				// System.out.println(rankingEntry.getClassName()+"
				// "+result.get(rankingEntry.getClassNumber()));
				if(weighted) {
				rankingEntry.setResultValue(ClassRankingProperties.W_KCORE10, result.get(rankingEntry.getClassNumber()));
				}
				else {
					rankingEntry.setResultValue(ClassRankingProperties.KCORE10, result.get(rankingEntry.getClassNumber()));
					}

			}
		}
	}
}
