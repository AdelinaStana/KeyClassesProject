package ranking;

import ranking.strategy.RankingStrategy;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Ranking {

	private List<RankingEntry> classList;
	private List<String> referenceSolution;

	// name of jar file that has been analyzed
	private String jarName;

	// refers to the entire graph as being directed or not;
	// not the graph used for Pagerank
	private boolean undirected;
	private Integer fraction;

	// the strategy by which the list has been sorted
	private RankingStrategy strategy;

	public Ranking(String jarName, List<String> referenceSolution) {
		this.jarName = jarName;
		this.referenceSolution = referenceSolution;
	}

	public void rank() {
		if (classList != null && classList.size() > 0) {
			strategy.rank(classList);
		}
	}

	public String getStrategyDescription() {
		if (strategy == null)
			return "nodes";
		String description = strategy.getDescription();
		if (undirected == true) {
			description += "_F" + fraction;
		}
		return description;
	}

	public List<RankingEntry> getClassList() {
		return classList;
	}

	public String getJarName() {
		return jarName;
	}

	public void setClassList(List<RankingEntry> classList) {
		this.classList = classList;
	}

	public void setStrategy(RankingStrategy strategy) {
		this.strategy = strategy;
	}

	public List<String> getReferenceSolution() {
		return referenceSolution;
	}

	public void setUndirected(boolean undirected) {
		this.undirected = undirected;
	}

	public void setFraction(Integer fraction) {
		this.fraction = fraction;
	}

	public Set<Integer> getTopClassNumbers(int threshold) {
		Set<Integer> topresult = new TreeSet<Integer>();

		for (int index = 0; (index < classList.size()) && (index < threshold); index++) {
			RankingEntry re = classList.get(index);

			topresult.add(re.getClassNumber());
		}

		return topresult;
	}
}
