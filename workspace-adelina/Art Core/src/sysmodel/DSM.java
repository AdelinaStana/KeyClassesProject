package sysmodel;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DSM {

	private final SparceMatrix<Integer> dependencyMatrix;
	private final String[] indexMap;
	private final Map<String, ClassAttributesEntry> infoIndexMap;

	public DSM(SparceMatrix<Integer> dependencyMatrix, String[] indexMap, Map<String, ClassAttributesEntry> infoMap) {
		this.dependencyMatrix = dependencyMatrix;
		this.indexMap = indexMap;
		this.infoIndexMap = infoMap;

		//System.out.println("Dependency matrix is:");
		//System.out.println(dependencyMatrix.toString());
		System.out.println("Built DSM with number of classes=" + dependencyMatrix.getNumberOfNodes());

	}

	public void replaceMatrixFromHistory(String historyFile) throws Exception {
		dependencyMatrix.initEmptyMatrix();

		BufferedReader in = new BufferedReader(new FileReader(historyFile));

		// for (int i = 0; i < dependencyMatrix.getNumberOfNodes(); i++)
		// System.out.println(elementAt(i));

		String line = null;
		while ((line = in.readLine()) != null) {
			String[] parts = line.split("\\s*,\\s*");
			if (parts.length == 2) {
				// System.out.println("history dependency " + parts[0] + " - " +
				// parts[1]);
				int ia = -1, ib = -1;
				int historyWeight = 1;


				for (int i = 0; i < dependencyMatrix.getNumberOfNodes(); i++) {
					
					if (elementAt(i).equals(parts[0])) {
						// System.out.println("FOUND "+parts[0]+" at "+i);
						ia = i;
					}
					if (elementAt(i).equals(parts[1])) {
						// System.out.println("FOUND "+parts[1]+" at "+i);
						ib = i;
					}

					//System.out.println("ia=" + ia + " ib=" + "" + ib);
					if ((ia >= 0) && (ib >= 0)) {
						//System.out.println("ADDED Hist");
						dependencyMatrix.putElement(ib, ia, historyWeight);
						dependencyMatrix.putElement(ia, ib, historyWeight);
					}
				}
			}
		}
		in.close();

	}

	
	public void addHistoryToMatrix(String historyFile) throws Exception {
		

		BufferedReader in = new BufferedReader(new FileReader(historyFile));

		String line = null;
		while ((line = in.readLine()) != null) {
			String[] parts = line.split("\\s*,\\s*");
			if (parts.length == 2) {
				// System.out.println("history dependency " + parts[0] + " - " +
				// parts[1]);
				int ia = -1, ib = -1;
				int historyWeight = 1;


				for (int i = 0; i < dependencyMatrix.getNumberOfNodes(); i++) {
					
					if (elementAt(i).equals(parts[0])) {
						// System.out.println("FOUND "+parts[0]+" at "+i);
						ia = i;
					}
					if (elementAt(i).equals(parts[1])) {
						// System.out.println("FOUND "+parts[1]+" at "+i);
						ib = i;
					}

					//System.out.println("ia=" + ia + " ib=" + "" + ib);
					if ((ia >= 0) && (ib >= 0)) {
						//System.out.println("ADDED Hist");
						Integer oldValue1 = dependencyMatrix.getElement(ib, ia);
						if (oldValue1!=null)
							dependencyMatrix.putElement(ib, ia, oldValue1+historyWeight);
						else dependencyMatrix.putElement(ib, ia, historyWeight);
						
						Integer oldValue2 = dependencyMatrix.getElement(ia, ib);
						if (oldValue2!=null)
							dependencyMatrix.putElement(ia, ib, oldValue2+historyWeight);
						else dependencyMatrix.putElement(ia, ib, historyWeight);
					}
				}
			}
		}
		in.close();

	}

	
	
	public void collapseInnerClasses() {
		for (int i = 0; i < dependencyMatrix.getNumberOfNodes() * 1; i++) {
			int dolarindex = elementAt(i).indexOf('$');
			if (dolarindex >= 0) {
				// System.out.print("$$$$$$$" +elementAt(i));
				String containerName = elementAt(i).substring(0, dolarindex);
				for (int j = 0; j < dependencyMatrix.getNumberOfNodes(); j++) {
					if (elementAt(j).equals(containerName)) {
						// System.out.println("found container");
						/*
						 * if (containerName.equals(
						 * "org.apache.tools.ant.helper.ProjectHelperImpl")
						 * ||containerName.equals(
						 * "org.apache.tools.ant.helper.ProjectHelper2"))
						 */
						dependencyMatrix.mergeIntoContainer(i, j);
					}
				}
			}
		}

		// System.out.println("Dependency matrix is:");
		// System.out.println(dependencyMatrix.toString());

	}

	public String toString() {
		return dependencyMatrix.toString();
	}

	public String elementAt(int i) {
		return indexMap[i]; // + infoIndexMap.get(indexMap[i]);
	}

	public ClassAttributesEntry elementAtFull(int i) {
		return infoIndexMap.get(indexMap[i]);
	}

	public SparceMatrix<Integer> getDependencyMatrix() {
		return this.dependencyMatrix;
	}

	public PackageNames getPackageNames() {
		List<String> packageNames = new ArrayList<>();
		String packageName;

		for (int i = 0; i < indexMap.length; i++) {
			packageName = indexMap[i].substring(0, indexMap[i].lastIndexOf('.'));
			if (!packageNames.contains(packageName)) {
				packageNames.add(packageName);
			}
		}

		return new PackageNames(packageNames);
	}
}
