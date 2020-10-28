package inputOutput;

import metrics.Metric;
import ranking.Ranking;
import ranking.ClassRankingProperties;
import ranking.RankingEntry;
import util.wagu.Block;
import util.wagu.Board;
import util.wagu.Table;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Mihaela Ilin on 8/27/2016.
 */
public class OutputWriter {

    private static final String outputDirectory = "outputs//";

    /**
     * @param ranking - list of classes that has been previously sorted
     */
    public static void outputRanking(Ranking ranking, boolean normalized){

        List<RankingEntry> classList = ranking.getClassList();

        if(classList.size() > 0){

            String fileName = outputDirectory + ranking.getJarName() + "_"
                    + ranking.getStrategyDescription() + ".csv";
            try{
                PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
                writeHeader(writer, classList.get(0));

                DecimalFormat df = new DecimalFormat("#.#####");
                df.setRoundingMode(RoundingMode.HALF_UP);

                for(RankingEntry rankingEntry : classList){
                    String s = rankingEntry.getClassName() + ",";

                    if(normalized){
                        Map<ClassRankingProperties, Integer> propertyValues = rankingEntry.getNormalizedPropertiesValues();
                        if(propertyValues != null){
                            for(ClassRankingProperties property : ClassRankingProperties.getRankingPropertiesOrderForPrinting()){
                               
                            	Integer value = propertyValues.get(property);
                                if(value != null){
                                    s += value + ",";
                                }
                            }
                        }
                    }else{
                        Map<ClassRankingProperties, Double> propertyValues = rankingEntry.getClassRankingPropertiesValues();
                        for(ClassRankingProperties property : ClassRankingProperties.getRankingPropertiesOrderForPrinting()){
                        	
                            Double value = propertyValues.get(property);
                            if(value != null){
                                if(value.equals(value.intValue())){
                                    s += value.intValue() + ",";
                                }else{
                                    s += df.format(value) + ",";
                                }
                            }
                            
                        }
                    }
                    writer.println(s);
                }

                writer.close();
            }catch(IOException e){
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    public static void outputRanking(Ranking ranking){
        outputRanking(ranking, false);
    }

    public static void outputMetrics(String jarName, Metric... metrics){
        String fileName = outputDirectory + jarName + "_metrics.txt";
        try{
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));

            for(Metric m : metrics){
                String s = m.getStrategyDescription();
                s += ":   " + m.getName() + " = " + m.getValue();
                writer.println(s);
            }

            writer.close();
        }catch(IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @param jarName Name of the jar that has been analyzed.
     * @param metrics
     *              String - the name of the metric having the computed values;
     *              List<Metric> - the computed metrics of that type.
     */
    //todo Ar fi mai bine sa se primeasca Ranking-urile ca parametri, voi stii din start cate randuri sunt.

    /*
        Idee refactorizare: adaugat in Ranking un Map<ranking_method_description, Map<metric_name, metric_value>>
     */

    //todo calculat direct aici metricile (specificat ce metrici trebuiesc calculate)
    public static void outputMetrics2(String jarName, Map<String, List<Metric>> metrics){
        // Make sure that we have at least one row for the table/at least one ranking.
        if(jarName != null && metrics != null && metrics.size() != 0){
            /* && metrics.get(0) != null
                && metrics.get(0).size() != 0*/

            String fileName = outputDirectory + jarName + "_metrics.txt";

            String fileHeader = jarName.toUpperCase() + " METRICS";

            List<String> tableHeaders = new ArrayList<>();
            tableHeaders.add("");
            for(String metricName : metrics.keySet()){
                tableHeaders.add(metricName);
            }

            List<List<String>> tableRows = new ArrayList<>();
            int nrOfRows = metrics.get("Precision").size();
            for(int i = 0; i < nrOfRows; i++){
                List<String> rowi = new ArrayList<>();
                rowi.add(metrics.get("Precision").get(i).getStrategyDescription());

                for(List<Metric> computedMetrics : metrics.values()){
                    rowi.add(computedMetrics.get(i).getValue().toString());
                }
                tableRows.add(rowi);
            }

            Board b = new Board(75);
            b.setInitialBlock(new Block(b, 25, 3, fileHeader).allowGrid(false).setBlockAlign(Block.BLOCK_CENTRE).setDataAlign(Block.DATA_CENTER));
            Table table = new Table(b, 75, tableHeaders, tableRows);

            // Center all columns' content
            Integer[] colAlignArray = new Integer[tableHeaders.size()];
            Arrays.fill(colAlignArray, Block.DATA_CENTER);
            table.setColAlignsList(Arrays.asList(colAlignArray));

            b.appendTableTo(0, Board.APPEND_BELOW, table);

            String tableString = b.build().getPreview();

            try{
                PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));

                writer.print(tableString);

                writer.close();
            }catch(IOException e){
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    /**
     * We assume that each entry of the ranking will have the same parameters set (e.g. nr of fields, methods, etc),
     * so we can use the first element from the ranking to decide what the header will look like.
     */
    private static void writeHeader(PrintWriter writer, RankingEntry rankingEntry){
        if(writer != null){
            String s = "Classname,";
            Map<ClassRankingProperties, Double> setAlgos = rankingEntry.getClassRankingPropertiesValues();
            for(ClassRankingProperties algorithm : ClassRankingProperties.getRankingPropertiesOrderForPrinting()){
                if(setAlgos.get(algorithm) != null){
                    s += algorithm.getDescription();
                    s += ",";
                }
            }
            writer.println(s);
        }
    }

}
