package inputOutput;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Mihaela Ilin on 8/17/2016.
 */
public class Reader {

    /**
     * Format of file: each line contains the fully qualified name of a class that is part of the reference solution.
     * @return List of classes as strings.
     */
    public List<String> readReferenceSolution(String filePath){
        List<String> referenceSolution = null;
        try{
            Scanner classScanner = new Scanner(new File(filePath));
            referenceSolution = new ArrayList<>();
            while(classScanner.hasNextLine()){
                referenceSolution.add(classScanner.nextLine());
            }
            classScanner.close();

            if(referenceSolution.isEmpty()){
                throw new RuntimeException("Reference solution is empty!");
            }

            return referenceSolution;
        }catch(FileNotFoundException e){
            throw new RuntimeException("Reference solution file not found!");
        }
        /*finally {
            if(referenceSolution == null || referenceSolution.size() == 0){
                return null;
            }
            return referenceSolution;
        }*/
    }
}
