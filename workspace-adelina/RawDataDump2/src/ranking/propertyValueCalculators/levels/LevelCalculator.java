package ranking.propertyValueCalculators.levels;

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

import cycleFinder.BinaryCycleFinder;
import cycleFinder.CycleFinder;

public class LevelCalculator implements Calculator
{
	private DSM dsm;
	private SparceMatrix<Integer> graph;

	private Map<Integer, Double> result;

	public LevelCalculator(DSM dsm)
	{
		this.dsm = dsm;
		graph = dsm.getDependencyMatrix();
	}

	public Map<Integer, Double> getResult()
	{
		return new HashMap(result);
	}

	public Map<Integer, Double> computeLevel()
	{

		Map<Integer, Double> npr = new HashMap<Integer, Double>();

		// System.out.println("intra");
		CycleFinder b = new BinaryCycleFinder();
		List<? extends Set<Integer>> partitions = b.find(graph);

		int size = partitions.size();
		int[] levels = computeLevels(partitions, graph.getRows());

		List<Integer> nodes = graph.getAllNodes();

		for (Integer n : nodes)
		{
			npr.put(n, ((double) levels[n]) / size);
			// System.out.println("level ["+n+"] ="+levels[n]+" "+npr.get(n));
		}

		result = npr;
		return result;
	}

	private int[] computeLevels(List<? extends Set<Integer>> partitions, int n)
	{
		int[] levels = new int[n];
		int j = 0;
		for (Set<Integer> s : partitions)
		{
			for (Integer i : s)
			{
				levels[i] = j;
			}

			j++;
		}

		return levels;
	}

	public List<RankingEntry> getResultAsRankingEntryList()
	{
		List<RankingEntry> ranking = new ArrayList<>();
		for (Map.Entry<Integer, Double> resultEntry : result.entrySet())
		{
			RankingEntry rankingEntry = new RankingEntry();
			rankingEntry.setClassName(dsm.elementAtFull(resultEntry.getKey()).getName());
			rankingEntry.setClassNumber(resultEntry.getKey());

			rankingEntry.setResultValue(ClassRankingProperties.LEVEL, resultEntry.getValue());

			ranking.add(rankingEntry);
		}

		return ranking;
	}

	@Override
	public void addResultToRanking(List<RankingEntry> ranking)
	{
		if (ranking != null && ranking.size() > 0)
		{
			for (RankingEntry rankingEntry : ranking)
			{
				//System.out.println("classnumber " + rankingEntry.getClassNumber());
				
				rankingEntry.setResultValue(ClassRankingProperties.LEVEL, result.get(rankingEntry.getClassNumber()));

				//System.out.println("level value " + rankingEntry.getClassRankingPropertiesValues().get(ClassRankingProperties.LEVEL));
			}
		}
	}
}
