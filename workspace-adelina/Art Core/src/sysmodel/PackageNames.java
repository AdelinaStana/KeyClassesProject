package sysmodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class PackageNames 
{
	private static final String SRC = "src.";
	
	private Collection<String> packageNames;
	
	public PackageNames(Collection<String> packageNames) 
	{
		super();
		this.packageNames = packageNames;
	}
	
	public Map<String, Double> getMatchValueMap(String className,
			double referenceValue)
	{
		if(className.lastIndexOf('.') == -1)
		{
			className = SRC.concat(className);
		}
		
		String packageName = className.substring(0, className.lastIndexOf('.'));
		Map<String, Double> result = new HashMap<String, Double>();
		Iterator<String> itPNs = packageNames.iterator();
		String currentPN;
		Double currentMatchValue;
		
		while(itPNs.hasNext())
		{
			currentPN = itPNs.next();
			currentMatchValue = getMatchValue(currentPN, packageName);
			if(currentMatchValue > 0)
			{
				result.put(currentPN, currentMatchValue * referenceValue);
			}
		}
		
		return result;
	}
	
	private double getMatchValue(String firstName, String secondName)
	{
		String[] firstNameArray = firstName.split("\\.");
		String[] secondNameArray = secondName.split("\\.");
		boolean isMatching = true;
		int matches = 0;
		
		if(firstNameArray.length < secondNameArray.length)
		{
			String[] auxNameArray = firstNameArray;
			firstNameArray = secondNameArray;
			secondNameArray = auxNameArray;
		}
		
		while(isMatching)
		{
			if((matches < secondNameArray.length) &&
					(firstNameArray[matches].equals(secondNameArray[matches])))
			{
				matches += 1;
			}
			else
			{
				isMatching = false;
			}
		}
		
		return (double)matches/firstNameArray.length;
	}
}
