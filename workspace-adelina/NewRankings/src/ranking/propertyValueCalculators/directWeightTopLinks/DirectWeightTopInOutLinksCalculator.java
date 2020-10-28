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

public class DirectWeightTopInOutLinksCalculator implements Calculator {

	private DSM dsm;
	private SparceMatrix<Integer> graph;

	private Integer k;

	private Map<Integer, Double> resultIn;
	private Map<Integer, Double> resultOut;

	
	private Set<Integer> topclasses;

	public DirectWeightTopInOutLinksCalculator(DSM dsm, Set<Integer> top) {
		this.dsm = dsm;
		this.k = k;
		this.topclasses = top;
		setGraphMatrix();
	}

	private void setGraphMatrix() {

		graph = dsm.getDependencyMatrix(); //.createUndirected(1);

	}

	public Map<Integer, Double> getResultIn() {
		return new HashMap(resultIn);
	}

	
	public Map<Integer, Double> getResultOut() {
		return new HashMap(resultOut);
	}

	
	public void computeLinksWeight() {

		int N = graph.getNumberOfNodes();
		// System.out.println("Nr of nodes N=" + N);

		List<Integer> nodes = graph.getAllNodes();

		resultIn = new HashMap<Integer, Double>();

		for (Integer p : nodes) {
			double count = 0;

			Set<Integer> neighbors = graph.inboundNeighbors(p);

			for (Integer ip : neighbors)
				if (this.topclasses.contains(ip))
					count = count + graph.Weight(ip, p);

			resultIn.put(p, count);
		}

		
		resultOut = new HashMap<Integer, Double>();

		for (Integer p : nodes) {
			double count = 0;

			List<Integer> neighbors = graph.outboundNeighbors(p);

			for (Integer op : neighbors)
				if (this.topclasses.contains(op))
					count = count + graph.Weight(p, op);

			resultOut.put(p, count);
		}

		
	}

	public List<RankingEntry> getResultAsRankingEntryList() {
		List<RankingEntry> ranking = new ArrayList<>();
		for (Map.Entry<Integer, Double> resultEntry : resultIn.entrySet()) {
			RankingEntry rankingEntry = new RankingEntry();
			rankingEntry.setClassName(dsm.elementAtFull(resultEntry.getKey()).getName());
			rankingEntry.setClassNumber(resultEntry.getKey());

			rankingEntry.setResultValue(ClassRankingProperties.DIRECTTOP_W_IN, resultEntry.getValue());
			rankingEntry.setResultValue(ClassRankingProperties.DIRECTTOP_W_OUT, resultOut.get(resultEntry.getKey()));

			ranking.add(rankingEntry);
		}

		return ranking;
	}

	@Override
	public void addResultToRanking(List<RankingEntry> ranking) {
		if (ranking != null && ranking.size() > 0) {
			for (RankingEntry rankingEntry : ranking) {
				rankingEntry.setResultValue(ClassRankingProperties.DIRECTTOP_W_IN, resultIn.get(rankingEntry.getClassNumber()));
				rankingEntry.setResultValue(ClassRankingProperties.DIRECTTOP_W_OUT, resultOut.get(rankingEntry.getClassNumber()));

			}
		}
	}
}
