package ranking.propertyValueCalculators.hits;

import ranking.propertyValueCalculators.Calculator;
import sysmodel.DSM;
import sysmodel.SparceMatrix;
import ranking.ClassRankingProperties;
import ranking.RankingEntry;

import java.util.*;

public class HitsCalculator implements Calculator {

    // DSM needed in order to get class names
    private DSM dsm;
    private SparceMatrix<Integer> graph;
    private int nrOfNodes;
    private int iterations;
    private Map<Integer, HitsValue> result;
    private boolean weighted;

    public HitsCalculator(DSM dsm, int iterations) {
        this.dsm = dsm;
        this.iterations = iterations;
        graph = dsm.getDependencyMatrix();
        nrOfNodes = graph.getNumberOfNodes();
        result = new HashMap<>(nrOfNodes);
        for(int i=0; i<nrOfNodes; i++){
            result.put(i, new HitsValue());
        }
    }

    public Map<Integer, HitsValue> computeHubsAndAuthorities() {

    	weighted=false;
    	
        Double norm;
        List<Integer> nodes = graph.getAllNodes();

        for(int i = 0; i < iterations; i++){
            norm = 0.0;
//        	System.out.println("HITS unweighted - Iteration "+i);
            // update all authority values first
            for(Integer node : nodes){
                Double auth = 0.0;
                Set<Integer> inboundNeighbors = graph.inboundNeighbors(node);
                for(Integer n : inboundNeighbors){
                    auth += result.get(n).getHub();
                }
                result.get(node).setAuthority(auth);
                norm += auth*auth;
            }

            norm = Math.sqrt(norm);
            // normalise authority values
            for(HitsValue hv : result.values()){
                hv.setAuthority(hv.getAuthority() / norm);
            }

            norm = 0.0;
            //update all hub values
            for(Integer node : nodes){
                Double hub = 0.0;
                List<Integer> outboundNeighbors = graph.outboundNeighbors(node);
                for(Integer n : outboundNeighbors){
                    hub += result.get(n).getAuthority();
                }
                result.get(node).setHub(hub);
                norm += hub*hub;
            }

            norm = Math.sqrt(norm);
            for(HitsValue hv : result.values()){
                hv.setHub(hv.getHub() / norm);
            }
        }

        return result;
    }

    public Map<Integer, HitsValue> computeWeightedHubsAndAuthorities() {

    	weighted=true;
    	
        Double norm;
        List<Integer> nodes = graph.getAllNodes();

        for(int i = 0; i < iterations; i++){
        	//System.out.println("HITS weighted - Iteration "+i);
            norm = 0.0;

            // update all authority values first
            for(Integer node : nodes){
                Double auth = 0.0;
                Set<Integer> inboundNeighbors = graph.inboundNeighbors(node);
                for(Integer n : inboundNeighbors){
                    auth += result.get(n).getHub() * graph.Weight(n, node)/graph.outWeight(n);
                }
                result.get(node).setAuthority(auth);
                norm += auth*auth;
            }

            norm = Math.sqrt(norm);
            // normalise authority values
            for(HitsValue hv : result.values()){
                hv.setAuthority(hv.getAuthority() / norm);
            }

            norm = 0.0;
            //update all hub values
            for(Integer node : nodes){
                Double hub = 0.0;
                List<Integer> outboundNeighbors = graph.outboundNeighbors(node);
                for(Integer n : outboundNeighbors){
                    hub += result.get(n).getAuthority() * graph.Weight(node, n)/graph.inWeight(n);
                }
                result.get(node).setHub(hub);
                norm += hub*hub;
            }

            norm = Math.sqrt(norm);
            // normalise hub values
            for(HitsValue hv : result.values()){
                hv.setHub(hv.getHub() / norm);
            }
        }

        return result;
    }

    public List<RankingEntry> getResultAsRankingEntryList(){
        List<RankingEntry> ranking = new ArrayList<>();
        for(Map.Entry<Integer, HitsValue> resultEntry : result.entrySet()){
            RankingEntry rankingEntry = new RankingEntry();
            rankingEntry.setClassName(dsm.elementAtFull(resultEntry.getKey()).getName());
            rankingEntry.setClassNumber(resultEntry.getKey());
        if(weighted) {
            rankingEntry.setResultValue(ClassRankingProperties.HITS_AUTHORITY_W, resultEntry.getValue().getAuthority());
            rankingEntry.setResultValue(ClassRankingProperties.HITS_HUB_W, resultEntry.getValue().getHub());
        }
        else {
        	 rankingEntry.setResultValue(ClassRankingProperties.HITS_AUTHORITY, resultEntry.getValue().getAuthority());
             rankingEntry.setResultValue(ClassRankingProperties.HITS_HUB, resultEntry.getValue().getHub());
        }
            ranking.add(rankingEntry);
        }

        return ranking;
    }

    /**
     * @param ranking - We assume that the ranking list already contains RankingEntries and we only add some newly computed
     *                  information to those entries.
     */
    public void addResultToRanking(List <RankingEntry> ranking){
        if(ranking != null && ranking.size() > 0){
            for(RankingEntry rankingEntry : ranking){
            	if (weighted) {
                rankingEntry.setResultValue(ClassRankingProperties.HITS_AUTHORITY_W, result.get(rankingEntry.getClassNumber()).getAuthority());
                rankingEntry.setResultValue(ClassRankingProperties.HITS_HUB_W, result.get(rankingEntry.getClassNumber()).getHub());
            	}
            	else {
            		rankingEntry.setResultValue(ClassRankingProperties.HITS_AUTHORITY, result.get(rankingEntry.getClassNumber()).getAuthority());
                    rankingEntry.setResultValue(ClassRankingProperties.HITS_HUB, result.get(rankingEntry.getClassNumber()).getHub());
            	}
            }
        }
    }

    public int getIterations() {
        return iterations;
    }
}
