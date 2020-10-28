package ranking;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ranking.strategy.SimpleRankingStrategy;

public class RankingPositionNormalizer {

	public static void normalize(Ranking r) {
		List<RankingEntry> ranking = r.getClassList();
		
		// Assume that all RankingEntries in the list have the same properties
		// computed.
		Set<ClassRankingProperties> setProperties = ranking.get(0).getClassRankingPropertiesValues().keySet();

		// initialise
		
		for (ClassRankingProperties property : setProperties) {

			r.setStrategy(new SimpleRankingStrategy(property));
			r.rank();
			ranking = r.getClassList();
			double rank=0.0;
			Double previousValue=Double.POSITIVE_INFINITY;
			
			int count=1;
			for (RankingEntry rankingEntry : ranking) {
				
				Double value = rankingEntry.getClassRankingPropertyValue(property);
				
				if (value != null) {
					if (value<previousValue) {
					   rank=rank+count;
					   count=1;
					}
					else count++;
					previousValue=value;
					rankingEntry.setResultValue(property,ranking.size()-rank);				
				}
			}
		}

		
	}

}
