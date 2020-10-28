package metrics;

import java.util.List;

import ranking.Ranking;
import ranking.RankingEntry;

public class PlotAreaUnderCurve extends Metric 
{
    private static final String name = "PlotAreaUnderCurve";
	
	public PlotAreaUnderCurve(Ranking ranking) 
	{
		super(ranking, -1);
	}

    public void compute()
    {
    	int threshold;
    	
        List<String> referenceSolution = ranking.getReferenceSolution();
        if(ranking != null && referenceSolution != null)
        {
            List<RankingEntry> classList = ranking.getClassList();

            int tp = 0;
            int fp = 0;
            int fn = 0;
            int tn = 0;

            // current values
            double tpr=0.0;
            double fpr=0.0;
            
            // old values
            double tprOld=0.0;
            double fprOld=0.0;
            double auc=0.0;
            
            for(threshold=0;threshold<classList.size();threshold++)
            {
	//            System.out.println("THRESHOLD= "+threshold);
	            
	            // hits
	            tp = 0;	         
	            for(int i=0; i<threshold; i++)
	            {
	                if(referenceSolution.contains(classList.get(i).getClassName()))
	                {
	                		//System.out.println("Found "+classList.get(i).getClassName());
	                		tp++;
	                }
	            }
	            
	            // threshold - hits
	            fp = threshold - tp;
	            // refsize - hits
	            fn = referenceSolution.size() - tp;
	            // size - threshold - false negatives
	            tn = classList.size() - threshold - fn;

	            tpr=((double)tp)/(tp+fn);
	            fpr=((double)fp)/(fp+tn);
	            
	            
	            System.out.println(threshold+", "+fpr+", "+tpr);
	            
	            if((threshold>0) && (fpr-fprOld!=0.0))
	            {
	            	auc+=(tpr+tprOld)*(fpr-fprOld)/2;
	            }
	            
	  //          System.out.println("tp="+tp+" fp="+fp+" fn="+fn+" tn="+tn+" tpr1="+tpr+" fpr1="+fpr+" auc="+auc);
	            
	            tprOld=tpr;
	            fprOld=fpr;
            }
            
            System.out.println("auc="+auc);
            value=auc;
        }
    }
	
	@Override
	protected Double doCalculations(int foundClasses) 
	{
		// not used in this class
		return null;
	}

	@Override
	public String getName() 
	{
		return name;
	}
}
