package sysmodel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import dependencyfinder.classdependencymodel.DependenciesOnAClass;
import dependencyfinder.classdependencymodel.DependencyModel;

public class SystemModel
{
	private String name;
	private Set<String> elements;
	private Map<String, ClassAttributesEntry> infoMap;
	private Map<String, Map<String, Integer>> depMap;
	private String[] indexMap = null;

	private Set<DependenciesOnAClass> allPairsDepDetails;

	public SystemModel(String name)
	{
		this.name = name;
		elements = new TreeSet<String>();
		infoMap = new HashMap<String, ClassAttributesEntry>();
		depMap = new HashMap<String, Map<String, Integer>>();
		allPairsDepDetails = new LinkedHashSet<DependenciesOnAClass>();
	}

	public String getName()
	{
		return name;
	}

	public void addElement(DependencyModel model)
	{

		allPairsDepDetails.addAll(model.giveAllDetails());

		String name = model.getClassName();
		elements.add(name);

		// System.out.println("***** addElement "+name);

		infoMap.put(name, new ClassAttributesEntry(name, model.getNrMethods(), model.getNrFields(), model.getNrPublicMethods(), model.getNrPublicFields()));

		Map<String, Integer> classDeps = model.computeModel();

		// System.out.println(classDeps);

		depMap.put(name, classDeps);
		this.indexMap = null;

	}

	public Set<DependenciesOnAClass> getAllPairsExternalDepDetails()
	{

		Set<DependenciesOnAClass> result = new HashSet<DependenciesOnAClass>();

		Iterator<DependenciesOnAClass> it = allPairsDepDetails.iterator();
		while (it.hasNext())
		{
			DependenciesOnAClass o = it.next();

			if (!elements.contains(o.dependencyClassName))
				o.isExternalDep = true;

			if (!o.dependencyClassName.startsWith("java."))
				result.add(o);
		}

		return result;

	}

	public Set<DependenciesOnAClass> getAllPairsInternalDepDetails()
	{

		Set<DependenciesOnAClass> result = new HashSet<DependenciesOnAClass>();

		Iterator<DependenciesOnAClass> it = allPairsDepDetails.iterator();
		while (it.hasNext())
		{
			DependenciesOnAClass o = it.next();
			if (!elements.contains(o.dependencyClassName))
				o.isExternalDep = true;
			if (!o.isExternalDep)
				result.add(o);

		}
		return result;

	}

	public Set<DependenciesOnAClass> getAllPairsDepDetails(Set<String> excluded)
	{

		Set<DependenciesOnAClass> result = new HashSet<DependenciesOnAClass>();

		Iterator<DependenciesOnAClass> it = allPairsDepDetails.iterator();
		while (it.hasNext())
		{
			DependenciesOnAClass o = it.next();
			if (!elements.contains(o.dependencyClassName))
				o.isExternalDep = true;
			Iterator<String> itt = excluded.iterator();
			boolean isExcluded = false;
			while (itt.hasNext())
			{
				String e = itt.next();
				if (o.dependencyClassName.startsWith(e))
					isExcluded = true;
			}
			if (!isExcluded)
				result.add(o);

		}
		return result;

	}

	public DSM computeDSM()
	{
		buildIndexMap();
		int added = elements.size();
		SparceMatrix<Integer> toRet = new SparceMatrix<Integer>(added, added);
		int i = 0;
		Iterator<String> it = elements.iterator();
		while (it.hasNext())
		{
			String currentElement = it.next();
			Map<String, Integer> m = this.depMap.get(currentElement);
			Set<Entry<String, Integer>> entries = m.entrySet();
			Iterator<Entry<String, Integer>> itt = entries.iterator();
			StringBinSearch alg = new StringBinSearch();
			while (itt.hasNext())
			{
				Entry<String, Integer> currentDep = itt.next();
				int j = alg.indexOf(indexMap, currentDep.getKey());
				if (j >= 0)
				{
					Integer dep = toRet.getElement(j, i);
					if (dep != null)
						dep += currentDep.getValue();
					else
						dep = currentDep.getValue();
					toRet.putElement(j, i, dep);
				}
			}
			i++;
		}
		return new DSM(toRet, indexMap, infoMap);
	}

	private void buildIndexMap()
	{
		int len = elements.size();
		this.indexMap = new String[len];
		elements.toArray(this.indexMap);
		System.out.println("elements " + elements);

	}

	/*
	 * public String elementAt(int i) { if (this.indexMap == null) throw new
	 * IllegalStateException(); return this.indexMap[i]; }
	 */

	/*
	 * public String elementAtFull(int i) { if (this.indexMap == null) throw new
	 * IllegalStateException(); return this.indexMap[i] + " " +
	 * this.infoMap.get(indexMap[i]); }
	 */
}
