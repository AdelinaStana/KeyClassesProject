package ranking.strategy;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ranking.ClassRankingProperties;
import ranking.RankingEntry;

public class AverageSortingMinTop extends RankingStrategy {

	List<ClassRankingProperties> props;

	public AverageSortingMinTop(List<ClassRankingProperties> props) {

		this.props = props;
		
		String name="[";
		for (ClassRankingProperties p:props) 
			name=name+" "+p.getDescription();
		name=name+"]";
		setDescription("AverageMinTop "+name);

	}

	public void rank(List<RankingEntry> ranking) {
		if (ranking != null && ranking.size() > 0) {
			// If the first entry has a value set for this property, then all
			// entries must have one.
			if (ranking.get(0).getClassRankingPropertyValue(props.get(0)) != null) {
				Comparator<RankingEntry> comparator = new Comparator<RankingEntry>() {
					@Override
					public int compare(RankingEntry o1, RankingEntry o2) {

						Double rezult1 = 0.0, rezult2 = 0.0;
						for (ClassRankingProperties p : props) {
							Double value = o1.getClassRankingPropertyValue(p);
							rezult1 = rezult1 + value;
						}
						for (ClassRankingProperties p : props) {
							Double value = o2.getClassRankingPropertyValue(p);
							rezult2 = rezult2 + value;
						}

						return rezult1.compareTo(rezult2);
					}
				};
				Collections.sort(ranking, comparator);
			}
		}
	}
}