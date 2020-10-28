package metrics;

import ranking.Ranking;


public class Precision extends Metric {

    private static final String name = "Precision";

    public Precision(Ranking ranking, int threshold) {
        super(ranking, threshold);
    }

    @Override
    protected Double doCalculations(int foundClasses) {
        return (double) foundClasses / getThreshold() * 100;
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
        return Metrics.PRECISION;
    }*/
}
