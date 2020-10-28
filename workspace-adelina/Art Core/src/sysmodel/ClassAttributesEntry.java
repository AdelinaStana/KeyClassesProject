package sysmodel;

import java.util.Set;

public class ClassAttributesEntry {
	private int nrMethods;
	private int nrFields;
	private int nrPublicMethods;
	private int nrPublicFields;
	private String name;
	

	public ClassAttributesEntry(String n, int nrM, int nrF, int nrPM, int nrPF) {
		name=n;
		nrMethods = nrM;
		nrFields = nrF;
		nrPublicMethods = nrPM;
		nrPublicFields = nrPF;
	}

	public String getName(){
		return name;
	}
	
	public int getNrMethods() {
		return nrMethods;
	}

	public int getNrFields() {
		return nrFields;
	}
	
	public int getNrPublicMethods() {
		return nrPublicMethods;
	}

	public int getNrPublicFields() {
		return nrPublicFields;
	}
}
