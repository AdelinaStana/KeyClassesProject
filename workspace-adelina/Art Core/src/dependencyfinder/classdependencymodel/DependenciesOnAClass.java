package dependencyfinder.classdependencymodel;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DependenciesOnAClass
{
	public String className;
	public String dependencyClassName;

	public boolean isSuperClass;
	public boolean isImplementedInterface;
	public boolean hasReturns;
	public boolean hasParameters;
	public boolean hasBindings;
	public boolean instantiates;
	public boolean hasFields;
	public boolean hasLocalVariables;
	public boolean hasCast;
	
	public Set<String> calledStaticMethods;
	public Set<String> calledMethodsOnLocalVars;
	public Set<String> calledMethodsOnParams;
	public Set<String> calledMethodsOnFields;

	public Set<String> calledMethods;
	public int numberOfCalledMethods;

	public boolean isExternalDep;

	DependenciesOnAClass(String name, String dependencyName)
	{
		className = name;
		dependencyClassName = dependencyName;
		calledStaticMethods = new HashSet<String>();
		calledMethodsOnLocalVars = new HashSet<String>();
		calledMethodsOnParams = new HashSet<String>();
		calledMethodsOnFields = new HashSet<String>();

		calledMethods = new HashSet<String>();
		numberOfCalledMethods = 0;

		isExternalDep = false;
	}

	private String b2S(boolean b)
	{
		if (b)
			return "1";
		else
			return "0";
	}

	public String toString()
	{
		// non static called members
		Iterator<String> it = calledMethods.iterator();
		String r = "[ ";
		while (it.hasNext())
		{
			r = r + " " + it.next();
		}
		r = r + " ]";

		// static called members
		it = calledStaticMethods.iterator();
		String rs = "[ ";
		while (it.hasNext())
		{
			rs = rs + " " + it.next();
		}
		rs = rs + " ]";

		return 
			className + "," + 
			dependencyClassName + "," + 
			b2S(isExternalDep) + "," + 
			b2S(isSuperClass) + "," + 
			b2S(isImplementedInterface) + "," + 
			b2S(hasReturns) + "," + 
			b2S(hasParameters) + "," + 
			b2S(hasBindings) + "," + 
			b2S(instantiates) + "," + 
			b2S(hasFields) + "," +
			b2S(hasLocalVariables) + "," +			
			b2S(hasCast) + "," + 
			calledStaticMethods.size() + "," + 
			rs
			// +","+calledStaticMethods.toString()
			// +","+calledMethodsOnLocalvar.toString()
			// +","+calledMethodsOnParams.toString()
			// +","+calledMethodsOnFields.toString()	
			+ "," + 
			this.numberOfCalledMethods + "," + 
			r;
	}
}
