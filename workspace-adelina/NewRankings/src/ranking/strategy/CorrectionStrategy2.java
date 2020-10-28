package ranking.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ranking.ClassRankingProperties;
import ranking.RankingEntry;

public class CorrectionStrategy2 extends RankingStrategy {

	Map<Integer, Double> meanValuesDir;
	Map<Integer, Double> minValuesDir;
	Map<Integer, Double> maxValuesDir;
	Map<Integer, Double> meanValuesW;

	public CorrectionStrategy2() {

		setDescription("Correction");
	}

	private Double getMeanValue(ClassRankingProperties property, List<RankingEntry> ranking, int start, int stop) {
		int i;
		Double rezult = 0.0;
		for (i = start; i <= stop; i++) {
			RankingEntry re = ranking.get(i);
			Double value = re.getClassRankingPropertyValue(property);
			rezult = rezult + value;
		}
		rezult = rezult / (stop - start + 1);
		return rezult;
	}
	
	
	private Double getMinValue(ClassRankingProperties property, List<RankingEntry> ranking, int start, int stop) {
		int i;
		RankingEntry re = ranking.get(start);
		Double rezult = re.getClassRankingPropertyValue(property);
		for (i = start; i <= stop; i++) {
			re = ranking.get(i);
			Double value = re.getClassRankingPropertyValue(property);
			if (value<rezult) rezult=value;
		
		}
		
		return rezult;
	}
	
	private Double getMaxValue(ClassRankingProperties property, List<RankingEntry> ranking, int start, int stop) {
		int i;
		RankingEntry re = ranking.get(start);
		Double rezult = re.getClassRankingPropertyValue(property);
		for (i = start; i <= stop; i++) {
			re = ranking.get(i);
			Double value = re.getClassRankingPropertyValue(property);
			if (value>rezult) rezult=value;
		}
		
		return rezult;
	}
	

	private void moveup(List<RankingEntry> ranking, int i, int step) {
		if (i - step < 0)
			step = i;

		RankingEntry re = ranking.remove(i);
		ranking.add(i - step, re);
		swapMeanValues(i, i - step);
		if (i < 100)
			System.out.println(" MOVE UP: with " + step + " " + re.getClassName());
		;

	}

	private void movedown(List<RankingEntry> ranking, int i, int step) {
		if (i + step >= ranking.size())
			step = ranking.size() - 1 - i;

		RankingEntry re = ranking.remove(i);
		ranking.add(i + step, re);
		swapMeanValues(i, i + step);
		if (i < 100)
			System.out.println(" MOVE DOWN: with " + step + " " + re.getClassName());
		;

	}

	private void swapMeanValues(int i, int j) {
		Double vi, vj;
		vi = meanValuesDir.get(i);
		vj = meanValuesDir.get(j);
		meanValuesDir.put(j, vi);
		meanValuesDir.put(i, vj);

		vi = meanValuesW.get(i);
		vj = meanValuesW.get(j);
		meanValuesW.put(j, vi);
		meanValuesW.put(i, vj);
		
		vi = minValuesDir.get(i);
		vj = minValuesDir.get(j);
		minValuesDir.put(j, vi);
		minValuesDir.put(i, vj);
	
		
		vi = maxValuesDir.get(i);
		vj = maxValuesDir.get(j);
		maxValuesDir.put(j, vi);
		maxValuesDir.put(i, vj);
		
	}

	public void rank(List<RankingEntry> ranking) {
		if (ranking != null && ranking.size() > 0) {

			meanValuesDir = new HashMap<>();
			meanValuesW = new HashMap<>();
			minValuesDir = new HashMap<>();
			maxValuesDir = new HashMap<>();
			
			// initialise
			for (RankingEntry re : ranking) {
				int index = ranking.indexOf(re);
				meanValuesDir.put(index, 0.0);
				meanValuesW.put(index, 0.0);
				minValuesDir.put(index, 0.0);
				maxValuesDir.put(index, 0.0);
			}

			for (int i = 0; i < 10; i++) {
				meanValuesDir.put(i, getMeanValue(ClassRankingProperties.DIRECTTOP, ranking, 0, 9));
				meanValuesW.put(i, getMeanValue(ClassRankingProperties.DIRECTTOP_W, ranking, 0, 9));
				minValuesDir.put(i, getMinValue(ClassRankingProperties.DIRECTTOP, ranking, 0, 9));
				maxValuesDir.put(i, getMaxValue(ClassRankingProperties.DIRECTTOP, ranking, 0, 9));
			}

			for (int i = 10; i < ranking.size() - 10; i++) {
				meanValuesDir.put(i, getMeanValue(ClassRankingProperties.DIRECTTOP, ranking, i - 5, i + 5));
				meanValuesW.put(i, getMeanValue(ClassRankingProperties.DIRECTTOP_W, ranking, i - 5, i + 5));
				minValuesDir.put(i, getMinValue(ClassRankingProperties.DIRECTTOP, ranking, i-5, i+5));
				maxValuesDir.put(i, getMaxValue(ClassRankingProperties.DIRECTTOP, ranking, i-5, i+5));
			}

			for (int i = ranking.size() - 10; i < ranking.size(); i++) {
				meanValuesDir.put(i, getMeanValue(ClassRankingProperties.DIRECTTOP, ranking, ranking.size() - 10,
						ranking.size() - 1));
				meanValuesW.put(i, getMeanValue(ClassRankingProperties.DIRECTTOP_W, ranking, ranking.size() - 10,
						ranking.size() - 1));
				minValuesDir.put(i, getMinValue(ClassRankingProperties.DIRECTTOP, ranking, ranking.size() - 10,
						ranking.size() - 1));
				maxValuesDir.put(i, getMaxValue(ClassRankingProperties.DIRECTTOP, ranking, ranking.size() - 10,
						ranking.size() - 1));
				
			}

			int step = 10;

			for (int i = 0; i < ranking.size(); i++) {
				RankingEntry re = ranking.get(i);

				Double valueDirect = re.getClassRankingPropertyValue(ClassRankingProperties.DIRECTTOP);
				Double valueDirectW = re.getClassRankingPropertyValue(ClassRankingProperties.DIRECTTOP_W);

				if ((valueDirect > ((meanValuesDir.get(i) + 3*maxValuesDir.get(i)) / 4)))
					moveup(ranking, i, step);
/*				else if ((valueDirect > meanValuesDir.get(i) * 1.4) && (valueDirectW > meanValuesW.get(i) * 0.8))
					moveup(ranking, i, step / 2);
*/
				// else if (valueDirect ==0)
				// movedown(ranking, i, 4 * step);
				else if (valueDirect < (meanValuesDir.get(i) +5*minValuesDir.get(i))/6) 
					movedown(ranking, i, step );
				// else if ((valueDirect < meanValuesDir.get(i) *3/4) &&
				// (valueDirectW < meanValuesW.get(i)*1/2)) {
				// System.out.println(" valueDirect="+valueDirect+"
				// "+meanValuesDir.get(i)+ " valueDirect="+valueDirectW+"
				// "+meanValuesW.get(i));
				// movedown(ranking, i, step / 2);
			}

		}
	}
}
