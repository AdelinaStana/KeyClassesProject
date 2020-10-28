package ranking.strategy;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import ranking.ClassRankingProperties;
import ranking.RankingEntry;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public class FuzzyRulesRankingStrategy extends RankingStrategy{

    private List<ClassRankingProperties> variables;
    private String rulesFilePath;

    public FuzzyRulesRankingStrategy(List<ClassRankingProperties> variables, String rulesFilePath) {
        this.variables = variables;
        this.rulesFilePath = rulesFilePath;
        
        setDescription(ClassRankingProperties.FUZZY_DECISION.getName()+" "+rulesFilePath);
    }

    @Override
    public void rank(List<RankingEntry> ranking) {
        if(variables != null && !variables.isEmpty() && rulesFilePath != null && !rulesFilePath.isEmpty()){
            normalize(ranking);

            FIS fis = FIS.load(rulesFilePath, true);

            if (fis == null) {
                System.err.println("Can't load file: '" + rulesFilePath + "'");
            }

            // Get default function block
            FunctionBlock fb = fis.getFunctionBlock(null);

            // Compute fuzzy decision for each entry in the ranking
            for(RankingEntry rankingEntry : ranking){
                for(ClassRankingProperties property : variables){
                    Integer normalizedPropValue = rankingEntry.getNormalizedPropertyValue(property);
                    normalizedPropValue = normalizedPropValue == null ? 0 : normalizedPropValue;
                    
                    //System.out.println(property.getDescription());
                    fb.setVariable(property.getDescription(), normalizedPropValue);
                }
                fb.evaluate();
                rankingEntry.setResultValue(ClassRankingProperties.FUZZY_DECISION,
                        fb.getVariable("decision").defuzzify());
            }

            sort(ranking);
        }
    }

    private void normalize(List<RankingEntry> ranking){
        Map<ClassRankingProperties, Double> meanValues = new HashMap<>();
        Map<ClassRankingProperties, Double> standardDeviationValues = new HashMap<>();

        // The ClassRankingProperties which have a value. We compute normalized values only for these properties.
        // Assume that all RankingEntries in the list have the same properties computed.
        Set<ClassRankingProperties> setProperties = ranking.get(0).getClassRankingPropertiesValues().keySet();

        // initialise
        for(ClassRankingProperties property : setProperties){
            meanValues.put(property, 0.0);
            standardDeviationValues.put(property, 0.0);
        }

        // compute mean values
        for(RankingEntry rankingEntry : ranking){
            for(ClassRankingProperties property : setProperties){
                Double value = rankingEntry.getClassRankingPropertyValue(property);
                if(value != null){
                    meanValues.put(property, value + meanValues.get(property));
                }
            }
        }

        for(ClassRankingProperties property : setProperties){
            meanValues.put(property, meanValues.get(property) / ranking.size());
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
        }

        // normalize the values in the range 0...100
        for(ClassRankingProperties property : setProperties){
            Double minValue = meanValues.get(property) - 2 * standardDeviationValues.get(property);
            Double maxValue = meanValues.get(property) + 2 * standardDeviationValues.get(property);

            for(RankingEntry rankingEntry : ranking){
                Double normalizedValue = (rankingEntry.getClassRankingPropertyValue(property) - minValue)
                        / (maxValue - minValue)
                        * 100;

                normalizedValue = normalizedValue > 100 ? 100 :
                                 (normalizedValue < 0 ? 0 : normalizedValue);

                DecimalFormat df = new DecimalFormat("#");
                df.setRoundingMode(RoundingMode.HALF_UP);
                Integer roundedNormalizedValue = Integer.valueOf(df.format(normalizedValue));

                rankingEntry.setNormalizedPropertyValue(property, roundedNormalizedValue);
            }
        }
    }

    private void sort(List<RankingEntry> ranking){
        // Sort list according to fuzzy decision
        Comparator<RankingEntry> comparator = new Comparator<RankingEntry>(){
            @Override
            public int compare(RankingEntry o1, RankingEntry o2) {
                return o2.getClassRankingPropertyValue(ClassRankingProperties.FUZZY_DECISION)
                        .compareTo(o1.getClassRankingPropertyValue(ClassRankingProperties.FUZZY_DECISION));
            }
        };
        Collections.sort(ranking, comparator);
    }
}
