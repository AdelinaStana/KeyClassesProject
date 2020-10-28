package ranking;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Normalizer {
	
	
        
    public static void normalize(Ranking r, int maxdomain){
    	List<RankingEntry> ranking = r.getClassList();
        Map<ClassRankingProperties, Double> meanValues = new HashMap<>();
        Map<ClassRankingProperties, Double> standardDeviationValues = new HashMap<>();

        Map<ClassRankingProperties, Double> maxValues = new HashMap<>();
        Map<ClassRankingProperties, Double> minValues = new HashMap<>();

        
        // The ClassRankingProperties which have a value. We compute normalized values only for these properties.
        // Assume that all RankingEntries in the list have the same properties computed.
        Set<ClassRankingProperties> setProperties = ranking.get(0).getClassRankingPropertiesValues().keySet();

        // initialise
        for(ClassRankingProperties property : setProperties){
        //	System.out.println(property.getName());
            meanValues.put(property, 0.0);
            standardDeviationValues.put(property, 0.0);
            maxValues.put(property, -1000000.0);
            minValues.put(property, 10000000.0);
        }

        // compute mean values
        for(RankingEntry rankingEntry : ranking){
            for(ClassRankingProperties property : setProperties){
                Double value = rankingEntry.getClassRankingPropertyValue(property);
                if(value != null){
                    meanValues.put(property, value + meanValues.get(property));
                    if (value>maxValues.get(property))
                    	maxValues.put(property, value);
                    if (value<minValues.get(property))
                    	minValues.put(property, value);
                }
            }
        }

        for(ClassRankingProperties property : setProperties){
            meanValues.put(property, meanValues.get(property) / ranking.size());
          //  System.out.println("Mean value for "+property.getName()+" ="+meanValues.get(property));
        }

        // compute standard deviation values
        for(ClassRankingProperties property : setProperties){
            Double standardDeviation = 0.0;

            for(RankingEntry rankingEntry : ranking){
                Double value = rankingEntry.getClassRankingPropertyValue(property);
                if(value != null){
                    Double v = value - meanValues.get(property);
                    standardDeviation += v * v;
                }
            }

            standardDeviation /= ranking.size();
            standardDeviation = Math.sqrt(standardDeviation);
            standardDeviationValues.put(property, standardDeviation);
            //System.out.println("Std dev value for "+property.getName()+" ="+standardDeviationValues.get(property));
        }

        // normalize the values in the range 0...maxdomain
        for(ClassRankingProperties property : setProperties){
            Double minValue = meanValues.get(property) - 3 * standardDeviationValues.get(property);
            //if (minValue<minValues.get(property))
            	minValue=minValues.get(property);
            Double maxValue = meanValues.get(property) + 3 * standardDeviationValues.get(property);
            //if (maxValue>maxValues.get(property))
            	maxValue=maxValues.get(property);
            
            for(RankingEntry rankingEntry : ranking){
                Double normalizedValue = (rankingEntry.getClassRankingPropertyValue(property) - minValue)
                        / (maxValue - minValue)
                        * maxdomain;

                normalizedValue = normalizedValue > maxdomain ? maxdomain :
                                 (normalizedValue < 0 ? 0 : normalizedValue);

                DecimalFormat df = new DecimalFormat("#");
                df.setRoundingMode(RoundingMode.HALF_UP);
                Integer roundedNormalizedValue = Integer.valueOf(df.format(normalizedValue)); 

                //rankingEntry.setNormalizedPropertyValue(property, roundedNormalizedValue);
                rankingEntry.setResultValue(property, roundedNormalizedValue.doubleValue());
            }
        }
    }

}
