package ranking.propertyValueCalculators.directTopLinks;

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

public class DirectTopInOutLinksCalculator implements Calculator {

	private DSM dsm;
	private SparceMatrix<Integer> graph;

	private Integer k;

	private Map<Integer, Double> resultIn;
	private Map<Integer, Double> resultOut;

	private Set<Integer> topclasses;

	public DirectTopInOutLinksCalculator(DSM dsm, Set<Integer> top) {
		this.dsm = dsm;
		this.k = k;
		this.topclasses = top;
		setGraphMatrix();
	}

	private void setGraphMatrix() {

		graph = dsm.getDependencyMatrix(); //.createUndirected(1);

	}

	public Map<Integer, Double> getInResult() {
		computeLinksNumber();
		return new HashMap(resultIn);
		}
	

	public Map<Integer, Double> getOutResult() {
		
		computeLinksNumber();
		return new HashMap(resultOut);
		
	}

	public void computeLinksNumber() {

		int N = graph.getNumberOfNodes();
		// System.out.println("Nr of nodes N=" + N);

		List<Integer> nodes = graph.getAllNodes();

		resultIn = new HashMap<Integer, Double>();
		resultOut = new HashMap<Integer, Double>();
		
		for (Integer p : nodes) {
			double count = 0;

			Set<Integer> neighborsIn = graph.inboundNeighbors(p);
			List<Integer> neighborsOut = graph.outboundNeighbors(p);

			for (Integer ip : neighborsIn)
				if (this.topclasses.contains(ip))
					count = count + 1;

			resultIn.put(p, count);
			
			count=0;
			for (Integer ip : neighborsOut)
				if (this.topclasses.contains(ip))
					count = count + 1;

			resultOut.put(p, count);
		}

	
	}

	public List<RankingEntry> getResultAsRankingEntryList() {
		List<RankingEntry> ranking = new ArrayList<>();
		for (Map.Entry<Integer, Double> resultEntry : resultIn.entrySet()) {
			RankingEntry rankingEntry = new RankingEntry();
			rankingEntry.setClassName(dsm.elementAtFull(resultEntry.getKey()).getName());
			rankingEntry.setClassNumber(resultEntry.getKey());

			rankingEntry.setResultValue(ClassRankingProperties.DIRECTTOP_IN, resultEntry.getValue());
			rankingEntry.setResultValue(ClassRankingProperties.DIRECTTOP_OUT, resultOut.get(resultEntry.getKey()));

			
			ranking.add(rankingEntry);
		}

		return ranking;
	}

	@Override
	public void addResultToRanking(List<RankingEntry> ranking) {
		if (ranking != null && ranking.size() > 0) {
			for (RankingEntry rankingEntry : ranking) {
				rankingEntry.setResultValue(ClassRankingProperties.DIRECTTOP_IN, resultIn.get(rankingEntry.getClassNumber()));
				rankingEntry.setResultValue(ClassRankingProperties.DIRECTTOP_OUT, resultOut.get(rankingEntry.getClassNumber()));
 
			}
		}
	}
}
