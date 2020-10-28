package ranking.propertyValueCalculators.directWeightTopLinks;

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

public class DirectWeightTopLinksCalculator implements Calculator {

	private DSM dsm;
	private SparceMatrix<Integer> graph;

	private Integer k;

	private Map<Integer, Double> result;

	private Set<Integer> topclasses;

	public DirectWeightTopLinksCalculator(DSM dsm, Set<Integer> top) {
		this.dsm = dsm;
		this.k = k;
		this.topclasses = top;
		setGraphMatrix();
	}

	private void setGraphMatrix() {

		graph = dsm.getDependencyMatrix().createUndirected(1);

	}

	public Map<Integer, Double> getResult() {
		return new HashMap(result);
	}

	public Map<Integer, Double> computeLinksWeight() {

		int N = graph.getNumberOfNodes();
		// System.out.println("Nr of nodes N=" + N);

		List<Integer> nodes = graph.getAllNodes();

		result = new HashMap<Integer, Double>();

		for (Integer p : nodes) {
			double count = 0;

			Set<Integer> neighbors = graph.inboundNeighbors(p);

			for (Integer ip : neighbors)
				if (this.topclasses.contains(ip))
					count = count + graph.getElement(p, ip);

			result.put(p, count);
		}

		return result;
	}

	public List<RankingEntry> getResultAsRankingEntryList() {
		List<RankingEntry> ranking = new ArrayList<>();
		for (Map.Entry<Integer, Double> resultEntry : result.entrySet()) {
			RankingEntry rankingEntry = new RankingEntry();
			rankingEntry.setClassName(dsm.elementAtFull(resultEntry.getKey()).getName());
			rankingEntry.setClassNumber(resultEntry.getKey());

			rankingEntry.setResultValue(ClassRankingProperties.DIRECTTOP_W, resultEntry.getValue());

			ranking.add(rankingEntry);
		}

		return ranking;
	}

	@Override
	public void addResultToRanking(List<RankingEntry> ranking) {
		if (ranking != null && ranking.size() > 0) {
			for (RankingEntry rankingEntry : ranking) {
				rankingEntry.setResultValue(ClassRankingProperties.DIRECTTOP_W, result.get(rankingEntry.getClassNumber()));

			}
		}
	}
}
