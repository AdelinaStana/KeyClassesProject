package metrics;

import ranking.Ranking;


public class Recall extends Metric{

    private static final String name = "Recall";

    public Recall(Ranking ranking, int threshold) {
        super(ranking, threshold);
    }

    @Override
    protected Double doCalculations(int foundClasses) {
        return (double) foundClasses / getReferenceSolutionSize() * 100;
    }

    @Override
    public String getName() {
        return name;
    }

    public static String getNameStatic(){
        return name;
    }

    /*@Override
    public Metrics getEnumValue() {
        return Metrics.RECALL;
    }*/
}
