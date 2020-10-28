package inputOutput;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ranking.RankingEntry;


public class ExternalRankingReader {

    /**
     * Format of file: each line contains the fully qualified name of a class in raking order
     * @return List of classes as RankingEntry.
     */
    public List<RankingEntry> readRanking(String filePath){
        List<RankingEntry> ranking = null;
        try{
            Scanner classScanner = new Scanner(new File(filePath));
            ranking = new ArrayList<>();
            while(classScanner.hasNextLine()){
            	RankingEntry re;
            	re=new RankingEntry();
            	re.setClassName(classScanner.nextLine());
                ranking.add(re);
            }
            classScanner.close();

            if(ranking.isEmpty()){
                throw new RuntimeException("Exteernal ranking file is empty!");
            }

            return ranking;
        }catch(FileNotFoundException e){
            throw new RuntimeException("External ranking file not found !"+filePath);
        }
        /*finally {
            if(referenceSolution == null || referenceSolution.size() == 0){
                return null;
            }
            return referenceSolution;
        }*/
    }
}
