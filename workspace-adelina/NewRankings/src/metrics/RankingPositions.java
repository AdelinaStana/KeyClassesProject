package metrics;

import java.util.ArrayList;
import java.util.List;

import ranking.Ranking;
import ranking.RankingEntry;




public class RankingPositions extends Metric {
	private static final String name = "RankingPositions";
    
	private List<RankingInfo> positions =new ArrayList<RankingInfo>();
	
	public RankingPositions(Ranking ranking) {
		super(ranking, -1);
	}

	
	public List<RankingInfo> getPositions() {
		return positions;
	}
	
	public void compute() {

		List<String> referenceSolution = ranking.getReferenceSolution();

		if (ranking != null && referenceSolution != null) {
			List<RankingEntry> classList = ranking.getClassList();

			for (int i = 0; i < referenceSolution.size(); i++) {
				for (int j = 0; j < classList.size(); j++)

					if (referenceSolution.get(i).equals(classList.get(j).getClassName())) {
						//System.out.println("Found " + classList.get(j).getClassName() + " at position " + j);
positions.add(new RankingInfo(j, classList.get(j).getClassName()));
					}
			}
		}
	}

	
	
	
	@Override
	protected Double doCalculations(int foundClasses) {
		// not used in this class
		return null;
	}

	@Override
	public String getName() {
		return name;
	}
}
