package ranking.strategy;

import ranking.ClassRankingProperties;
import ranking.RankingEntry;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SimpleRankingStrategy extends RankingStrategy{

    private ClassRankingProperties property;

    public SimpleRankingStrategy(ClassRankingProperties property){
        this.property = property;
        setDescription(property.getName());
    }

    @Override
    public void rank(List<RankingEntry> ranking) {
        if(ranking != null && ranking.size() > 0){
            // If the first entry has a value set for this property, then all entries must have one.
            if(ranking.get(0).getClassRankingPropertyValue(property) != null){
                Comparator<RankingEntry> comparator = new Comparator<RankingEntry>(){
                    @Override
                    public int compare(RankingEntry o1, RankingEntry o2) {
                        return o2.getClassRankingPropertyValue(property).compareTo(o1.getClassRankingPropertyValue(property));
                    }
                };
                Collections.sort(ranking, comparator);
            }
            else 
            	System.out.println("!!!!!!Ranking on unexisting property "+property.getName());
        }
    }
}
