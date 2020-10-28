package ranking.propertyValueCalculators;

import sysmodel.DSM;
import sysmodel.SparceMatrix;
import ranking.ClassRankingProperties;
import ranking.RankingEntry;

import java.util.List;

public class DefaultClassRankingPropertiesAdder implements Calculator {

    private DSM dsm;
    private SparceMatrix<Integer> mat;

    public DefaultClassRankingPropertiesAdder(DSM dsm, boolean directed, Integer fraction) {
        this.dsm = dsm;
        if (!directed && fraction != null) {
            mat = dsm.getDependencyMatrix().createUndirected(fraction);
        } else {
            mat = dsm.getDependencyMatrix();
        }
    }

    @Override
    public void addResultToRanking(List<RankingEntry> ranking) {
        for(RankingEntry rankingEntry : ranking){
            int classi = rankingEntry.getClassNumber();

            rankingEntry.setResultValue(ClassRankingProperties.FIELD_NR, (double)(dsm.elementAtFull(classi).getNrFields()));
            rankingEntry.setResultValue(ClassRankingProperties.METHOD_NR, (double)(dsm.elementAtFull(classi).getNrMethods()));
            
            rankingEntry.setResultValue(ClassRankingProperties.PFIELD_NR, (double)(dsm.elementAtFull(classi).getNrPublicFields()));
            rankingEntry.setResultValue(ClassRankingProperties.PMETHOD_NR, (double)(dsm.elementAtFull(classi).getNrPublicMethods()));
            
            double size = (double)(dsm.elementAtFull(classi).getNrFields()) + (double)(dsm.elementAtFull(classi).getNrMethods());
            rankingEntry.setResultValue(ClassRankingProperties.SIZE, size);

            int ToR = 0;
            if (mat.getConnectedToRow(classi) != null){
                ToR = mat.getConnectedToRow(classi).size();
            }
            int ToC = 0;
            if (mat.getConnectedToColumns(classi) != null){
                ToC = mat.getConnectedToColumns(classi).size();
            }
            rankingEntry.setResultValue(ClassRankingProperties.CONN_TO_ROW, (double)ToR);
            rankingEntry.setResultValue(ClassRankingProperties.CONN_TO_COL, (double)ToC);
            rankingEntry.setResultValue(ClassRankingProperties.CONN_TOTAL, (double)(ToC+ToR));

            rankingEntry.setResultValue(ClassRankingProperties.WEIGHT_IN, (double)(mat.inWeight(classi)));
            rankingEntry.setResultValue(ClassRankingProperties.WEIGHT_OUT, (double)(mat.outWeight(classi)));
            rankingEntry.setResultValue(ClassRankingProperties.WEIGHT_CONN_TOTAL, (double)(mat.inWeight(classi)+mat.outWeight(classi)));
        }
    }
}
