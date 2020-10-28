package ranking;

import java.util.HashMap;
import java.util.Map;

public class RankingEntry{

    private String className;
    private int classNumber;
    private Map<ClassRankingProperties, Double> classRankingPropertiesValues;
    // normalized values in the range 0-100
    private Map<ClassRankingProperties, Integer> normalizedPropertiesValues;

    public RankingEntry() {
        classRankingPropertiesValues = new HashMap<>();
    }

    public void setResultValue(ClassRankingProperties algorithm, Double value){
        classRankingPropertiesValues.put(algorithm, value);
    }

    public Double getClassRankingPropertyValue(ClassRankingProperties property){
        return classRankingPropertiesValues.get(property);
    }

    public void setNormalizedPropertyValue(ClassRankingProperties property, Integer value){
        if(normalizedPropertiesValues == null){
            normalizedPropertiesValues = new HashMap<>();
        }

        normalizedPropertiesValues.put(property, value);
    }

    public Integer getNormalizedPropertyValue(ClassRankingProperties property){
        return normalizedPropertiesValues.get(property);
    }

    public int getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(int classNumber) {
        this.classNumber = classNumber;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Map<ClassRankingProperties, Double> getClassRankingPropertiesValues() {
        return classRankingPropertiesValues;
    }

    public Map<ClassRankingProperties, Integer> getNormalizedPropertiesValues() {
        return normalizedPropertiesValues;
    }
}
