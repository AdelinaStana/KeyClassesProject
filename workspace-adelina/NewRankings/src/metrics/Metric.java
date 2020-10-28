package metrics;

import ranking.Ranking;
import ranking.RankingEntry;

import java.util.TreeMap;
import java.util.List;
import java.util.Map;

public abstract class Metric {

	protected Map<Integer, String> found;
	
    protected Ranking ranking;
    private int threshold;

    protected Double value;
    private String strategyDescription;

    public Metric(Ranking ranking, int threshold) {
    	found=new TreeMap<Integer, String>();
    	
        this.ranking = ranking;
        this.threshold = threshold;
        strategyDescription = ranking.getStrategyDescription();
    }

    public void compute(){
    	
        List<String> referenceSolution = ranking.getReferenceSolution();
        if(ranking != null && referenceSolution != null){
            List<RankingEntry> classList = ranking.getClassList();
            if(threshold <= 0){
                throw new RuntimeException("Threshold must be positive!");
            } else if(threshold >= classList.size()){
                threshold = classList.size() - 1;
            }

            int foundClasses = 0;
            //System.out.println("THRESHOLD= "+threshold);
            for(int i=0; i<threshold; i++){
                if(referenceSolution.contains(classList.get(i).getClassName())){
                	{
                		//System.out.println("Found "+classList.get(i).getClassName());
                		foundClasses++;
                		found.put(i, classList.get(i).getClassName());
                }
                }
            }
           
            //System.out.println("foundClasses="+foundClasses);
          // System.out.println(found.toString());
           
          
            value = doCalculations(foundClasses);
        }
    }

//    public abstract Metrics getEnumValue();

    protected abstract Double doCalculations(int foundClasses);

    public abstract String getName();

    protected int getThreshold() {
        return threshold;
    }

    public Double getValue() {
        return value;
    }

    public Ranking getRanking() {
        return ranking;
    }

    protected int getReferenceSolutionSize(){
        if(ranking.getReferenceSolution() != null){
            return ranking.getReferenceSolution().size();
        }
        return 0;
    }

    public String getStrategyDescription() {
        return strategyDescription;
    }
}
