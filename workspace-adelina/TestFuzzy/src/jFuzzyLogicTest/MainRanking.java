package jFuzzyLogicTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Variable;

public class MainRanking {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {

		String filename = "ranking3in.fcl";
	
		FIS fis = FIS.load(filename, true);

		if (fis == null) {
			System.err.println("Can't load file: '" + filename + "'");
			System.exit(1);
		}
		// Get default function block
		FunctionBlock fb = fis.getFunctionBlock(null);

		// Show
//		JFuzzyChart.get().chart(fb);

		// read file with input values
		String file = "JMeter-3in.txt";
		File input = new File(file);

		PrintWriter writer = new PrintWriter("JMeter-3in-ranking.txt");
		
		Scanner is = new Scanner(input);

		int N = is.nextInt();

		System.out.println("Reading  " + N + " lines from file " + file
				+ " ...");

		int size, wid, wod, pr;
        String name;
		
              
		while (is.hasNext()) {

			name = is.next();
			size = is.nextInt();
			wid = is.nextInt();
			wod = is.nextInt();
//			pr = is.nextInt();
			
			// Set inputs
			fb.setVariable("Size", size);
			fb.setVariable("Win", wid);
			fb.setVariable("Wout", wod);
//			fb.setVariable("pr", pr);
			// Evaluate
			fb.evaluate();

			// Show output variable's chart
			Variable tip = fb.getVariable("decision");
			tip.defuzzify();

			//System.out
			writer.println(name+"\t" +size+"\t"+wid+" "+wod+"\t"+ fb.getVariable("decision").getValue());
//			+pr+"\t"
			
		//	JFuzzyChart.get().chart(tip, tip.getDefuzzifier(), true);
		}

		writer.close();
		// Print ruleSet
		System.out.println(fb);
		

	}

}
