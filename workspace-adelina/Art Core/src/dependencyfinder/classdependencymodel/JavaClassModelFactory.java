package dependencyfinder.classdependencymodel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import sysmodel.StringBinSearch;

public class JavaClassModelFactory
{
	private int ignored = 0;// bitvetor of flags
	/*
	 * b1 - inheritance (de la cel mai putin semnificativ bit) 
	 * b2 - interface implementation
	 * b3 - member declaration
	 * b4 - parameter
	 * b5 - local variable 
	 * b6 - member access 
	 * b7 - return 
	 * b8 - type parameter binding 
	 * b9 - static invocation 
	 * b10 - invocation 
	 * b11 - instantiation
	 * b12 - cast
	 */
	private static JavaClassModelFactory instance = null;

	private JavaClassModelFactory()
	{
	}

	public static JavaClassModelFactory getInstance()
	{
		if (instance == null)
			instance = new JavaClassModelFactory();
		return instance;
	}

	public JavaClassDependencyModel getModel()
	{
		return new BetterDependencyModel(null, ignored);
	}

	public void ignoreInherintance()
	{
		ignored |= 1;
	}

	public void ignoreInterfaceImplementation()
	{
		ignored |= 2;
	}

	public void ignoreIsA()
	{
		this.ignoreInherintance();
		this.ignoreInterfaceImplementation();
	}

	public void ignoreHasA()
	{
		ignored |= 4;
	}

	public void ignoreParameter()
	{
		ignored |= 8;
	}

	public void ignoreLocalVar()
	{
		ignored |= 16;
	}

	public void ignoreMemberAccess()
	{
		ignored |= 32;
	}

	public void ignoreUses()
	{
		this.ignoreLocalVar();
		this.ignoreParameter();
		this.ignoreReturn();
		this.ignoreMemberAccess();
	}

	public void ignoreTypeBinding()
	{
		ignored |= 128;
	}

	public void ignoreReturn()
	{
		ignored |= 64;
	}

	public void ignoreStaticInv()
	{
		ignored |= 256;
	}

	public void ignoreInstantiation()
	{
		ignored |= 512;
	}

	public void ignoreCast()
	{
		ignored |= 1024;
	}
}

class BetterDependencyModel implements JavaClassDependencyModel
{
	private String clazz;
	private Set<String> superClass;
	private Set<String> interfaces;
	private Set<String> currentReturn;
	private Set<String> bindings;
	private Set<String> params;
	private Set<String> instantiated;
	private Map<String, Set<MethodInfo>> staticInv;
	private Set<String> memberAccessed;
	private Map<String, Set<MethodInfo>> invocationp;
	private Map<String, Set<MethodInfo>> invocationl;
	private Map<String, Set<MethodInfo>> invocationf;
	private Set<String> fields;
	private Set<String> casts;
	private int ignored;
	private Builder[] compute;
	private int nrMethods;
	private int nrFields;
	private int nrPMethods;
	private int nrPFields;


	private Set<String> allClassDeps;

	public BetterDependencyModel(String clazz, int ignored)
	{
		nrMethods = 0;
		nrFields = 0;
		nrPMethods = 0;
		nrPFields = 0;
		
		interfaces = new HashSet<String>();
		superClass = new HashSet<String>();
		currentReturn = new HashSet<String>();
		bindings = new HashSet<String>();
		params = new HashSet<String>();
		invocationp = new HashMap<String, Set<MethodInfo>>();
		invocationl = new HashMap<String, Set<MethodInfo>>();
		invocationf = new HashMap<String, Set<MethodInfo>>();
		fields = new HashSet<String>();
		staticInv = new HashMap<String, Set<MethodInfo>>();
		instantiated = new HashSet<String>();
		memberAccessed = new HashSet<String>();
		casts = new HashSet<String>();
		
		this.clazz = clazz;
		this.ignored = ignored;
		allClassDeps = new HashSet<String>();

		initCompute();
	}

	public Set<DependenciesOnAClass> giveAllDetails()
	{
		Set<DependenciesOnAClass> rez = new LinkedHashSet<DependenciesOnAClass>();
		Iterator<String> it = allClassDeps.iterator();
		while (it.hasNext())
			rez.add(giveDetailsOn(it.next()));
		return rez;
	}

	private DependenciesOnAClass giveDetailsOn(String dependencyClassName)
	{
		DependenciesOnAClass deps = new DependenciesOnAClass(clazz, dependencyClassName);

		if (interfaces.contains(dependencyClassName))
			deps.isImplementedInterface = true;
		else
			deps.isImplementedInterface = false;

		if (superClass.contains(dependencyClassName))
			deps.isSuperClass = true;
		else
			deps.isSuperClass = false;

		if (currentReturn.contains(dependencyClassName))
			deps.hasReturns = true;
		else
			deps.hasReturns = false;

		if (bindings.contains(dependencyClassName))
			deps.hasBindings = true;
		else
			deps.hasBindings = false;

		if (params.contains(dependencyClassName))
			deps.hasParameters = true;
		else
			deps.hasParameters = false;
		
		if(fields.contains(dependencyClassName))
			deps.hasFields=true;
		else
			deps.hasFields=false;

		if (instantiated.contains(dependencyClassName))
			deps.instantiates = true;
		else
			deps.instantiates = false;

		if (casts.contains(dependencyClassName))
			deps.hasCast = true;
		else
			deps.hasCast = false;

		// invocation of static members
		if (staticInv.containsKey(dependencyClassName))
		{
			Set<MethodInfo> sm = staticInv.get(dependencyClassName);
			Iterator<MethodInfo> itt = sm.iterator();
			while (itt.hasNext())
			{
				deps.calledStaticMethods.add(itt.next().name);
			}
		}

		// invocation of parameters
		if (invocationp.containsKey(dependencyClassName))
		{
			Set<MethodInfo> sm = invocationp.get(dependencyClassName);
			Iterator<MethodInfo> itt = sm.iterator();
			while (itt.hasNext())
			{
				deps.calledMethodsOnParams.add(itt.next().name);
			}
		}

		// invocation of local variables
		if (invocationl.containsKey(dependencyClassName))
		{
			//System.out.println(invocationl);
			deps.hasLocalVariables=true;
			
			Set<MethodInfo> sm = invocationl.get(dependencyClassName);
			
			Iterator<MethodInfo> itt = sm.iterator();
			while (itt.hasNext())
			{
				deps.calledMethodsOnLocalVars.add(itt.next().name);
			}
		}

		// invocation of fields
		if (invocationf.containsKey(dependencyClassName))
		{
			Set<MethodInfo> sm = invocationf.get(dependencyClassName);
			Iterator<MethodInfo> itt = sm.iterator();
			while (itt.hasNext())
			{
				deps.calledMethodsOnFields.add(itt.next().name);
			}
		}

		deps.calledMethods.addAll(deps.calledMethodsOnFields);
		deps.calledMethods.addAll(deps.calledMethodsOnLocalVars);
		deps.calledMethods.addAll(deps.calledMethodsOnParams);

		deps.numberOfCalledMethods = deps.calledMethods.size();
		
		//System.out.println(clazz+"->"+deps.calledMethods);

		return deps;
	}

	private void initCompute()
	{
		compute = new Builder[11];
		compute[0] = new SuperBuilder(superClass);
		compute[1] = new InterfaceBuilder(interfaces);
		compute[2] = new FieldBuilder(invocationf, fields);
		compute[3] = new ParameterBuilder(invocationp, params);
		compute[4] = new LocalVarBuilder(invocationl);
		compute[5] = new MemberAccessedBuilder(memberAccessed);
		compute[6] = new ReturnBuilder(currentReturn);
		compute[7] = new BindingBuilder(bindings);
		compute[8] = new StaticInvBuilder(staticInv);
		compute[9] = new InstantiationBuilder(instantiated);
		compute[10] = new CastBuilder(casts);
	}

	public void addCreated(String clazz)
	{
		allClassDeps.add(clazz);
		this.instantiated.add(clazz);
	}

	@Override
	public void addField(String clazz)
	{
		allClassDeps.add(clazz);
		fields.add(clazz);
	}

	@Override
	public void addImplementedInteface(String clazz)
	{
		allClassDeps.add(clazz);
		interfaces.add(clazz);
	}

	@Override
	public void addMemberAccess(String clazz)
	{
		allClassDeps.add(clazz);
		this.memberAccessed.add(clazz);
	}

	@Override
	public void addMethodInvocation(String clazz, char type, String name, String desc)
	{
		allClassDeps.add(clazz);
		switch (type)
		{
		case LOCAL_VARIABLE:
			this.addMethodInvocationHelper(invocationl, clazz, name, desc);
			break;
		case MEMBER:
			this.addMethodInvocationHelper(invocationf, clazz, name, desc);
			break;
		case METHOD_PARAMETER:
			this.addMethodInvocationHelper(invocationp, clazz, name, desc);
		}
	}

	private void addMethodInvocationHelper(Map<String, Set<MethodInfo>> m, String clazz, String name, String desc)
	{
		Set<MethodInfo> set = m.get(clazz);
		if (set == null)
		{
			set = new HashSet<MethodInfo>();
			m.put(clazz, set);
		}
		MethodInfo mi = new MethodInfo(name, desc);
		set.add(mi);
	}

	@Override
	public void addMethodParameter(String clazz)
	{
		allClassDeps.add(clazz);
		params.add(clazz);
	}

	@Override
	public void addParameter(String clazz)
	{
		allClassDeps.add(clazz);
		bindings.add(clazz);

	}

	@Override
	public void addReturnType(String clazz)
	{
		allClassDeps.add(clazz);
		this.currentReturn.add(clazz);
	}

	@Override
	public void addStaticInvocation(String clazz, String name, String desc)
	{
		allClassDeps.add(clazz);
		this.addMethodInvocationHelper(staticInv, clazz, name, desc);

	}

	@Override
	public void addSuperClass(String clazz)
	{
		allClassDeps.add(clazz);
		this.superClass.add(clazz);
	}

	@Override
	public String[] getClassFullName()
	{
		return clazz.split("\\.");
	}

	@Override
	public String getClassName()
	{
		return String.valueOf(this.clazz);
	}

	@Override
	public void addLocalVar(String clazz)
	{
		allClassDeps.add(clazz);
		Set<MethodInfo> set = invocationl.get(clazz);
		if (set == null)
		{
			set = new HashSet<MethodInfo>();
			invocationl.put(clazz, set);
		}

	}

	public Map<String, Integer> computeModel()
	{
		Map<String, Integer> toRet = new HashMap<String, Integer>();
		DependencyStrength ds = DependencyStrengthFactory.getDependencyStrengthInstace();
		for (int i = 0, p = 1; i < this.compute.length; i++, p *= 2)
		{
			if ((ignored & p) == 0)
				this.compute[i].compute(toRet, ds);
		}
		return toRet;
	}

	@Override
	public void addCast(String clazz)
	{
		allClassDeps.add(clazz);
		casts.add(clazz);
	}

	@Override
	public void setName(String name)
	{
		this.clazz = name;
	}

	@Override
	public int getNrFields()
	{
		return this.nrFields;
	}

	@Override
	public int getNrPublicMethods()
	{
		return this.nrPMethods;
	}

	@Override
	public int getNrPublicFields()
	{
		return this.nrPFields;
	}

	@Override
	public int getNrMethods()
	{
		return this.nrMethods;
	}

	
	@Override
	public void incrementNrFields()
	{
		nrFields++;
	}

	@Override
	public void incrementNrMethods()
	{
		nrMethods++;
	}
	
	
	@Override
	public void incrementNrPFields()
	{
		nrPFields++;
	}

	@Override
	public void incrementNrPMethods()
	{
		nrPMethods++;
	}
	
}

interface Builder
{
	void compute(Map<String, Integer> m, DependencyStrength ds);
}

class SuperBuilder implements Builder
{
	private Set<String> set;

	public SuperBuilder(Set<String> set)
	{
		super();
		this.set = set;
	}

	@Override
	public void compute(Map<String, Integer> m, DependencyStrength ds)
	{
		Iterator<String> it = set.iterator();
		while (it.hasNext())
			m.put(it.next(), getStrength(ds));
	}

	protected int getStrength(DependencyStrength ds)
	{
		return ds.getInheritance();
	}
}

class InterfaceBuilder extends SuperBuilder
{

	public InterfaceBuilder(Set<String> set)
	{
		super(set);
	}

	protected int getStrength(DependencyStrength ds)
	{
		return ds.getInheritance();
	}
}

abstract class ApparitionCheckBuilder implements Builder
{

	@Override
	public void compute(Map<String, Integer> m, DependencyStrength ds)
	{
		Iterator<String> it = this.getIterator();
		while (it.hasNext())
		{
			String s = it.next();
			int j = getStrength(ds, s);
			compute(s, m, j);
		}
	}

	protected abstract Iterator<String> getIterator();

	protected abstract int getStrength(DependencyStrength ds, String s);

	private void compute(String s, Map<String, Integer> m, int j)
	{
		Integer i = (Integer) m.get(s);
		if (i == null)
			i = Integer.valueOf(j);
		else
			i = Integer.valueOf(i.intValue() + j);
		m.put(s, i);
	}
}

abstract class SetBuilder extends ApparitionCheckBuilder
{
	private Set<String> set;

	protected SetBuilder(Set<String> set)
	{
		this.set = set;
	}

	@Override
	protected Iterator<String> getIterator()
	{
		return set.iterator();
	}
}

class BindingBuilder extends SetBuilder
{

	protected BindingBuilder(Set<String> set)
	{
		super(set);
	}

	protected int getStrength(DependencyStrength ds, String s)
	{
		return ds.getTypeBinding();
	}
}

class ReturnBuilder extends SetBuilder
{

	protected ReturnBuilder(Set<String> set)
	{
		super(set);
	}

	protected int getStrength(DependencyStrength ds, String s)
	{
		return ds.getReturned();
	}
}

class MemberAccessedBuilder extends SetBuilder
{

	protected MemberAccessedBuilder(Set<String> set)
	{
		super(set);
	}

	@Override
	protected int getStrength(DependencyStrength ds, String s)
	{
		return ds.getMemberAccessed();
	}

}

class InstantiationBuilder extends SetBuilder
{

	protected InstantiationBuilder(Set<String> set)
	{
		super(set);
	}

	protected int getStrength(DependencyStrength ds, String s)
	{
		return ds.getInstantiated();
	}
}

class CastBuilder extends SetBuilder
{

	protected CastBuilder(Set<String> set)
	{
		super(set);
	}

	protected int getStrength(DependencyStrength ds, String s)
	{
		return ds.getCast();
	}

}

abstract class MapBuilder extends ApparitionCheckBuilder
{
	private Map<String, Set<MethodInfo>> map;

	protected MapBuilder(Map<String, Set<MethodInfo>> map)
	{
		this.map = map;
	}

	protected Iterator<String> getIterator()
	{
		return map.keySet().iterator();
	}

	protected int getStrength(DependencyStrength ds, String s)
	{
		Set<MethodInfo> methods = map.get(s);
		char noMethodsUsed;
		if (methods != null)
			noMethodsUsed = (char) methods.size();
		else
			noMethodsUsed = 0;
		return getStrength(ds, noMethodsUsed);
	}

	protected abstract int getStrength(DependencyStrength ds, char noMethodsUsed);
}

class StaticInvBuilder extends MapBuilder
{

	protected StaticInvBuilder(Map<String, Set<MethodInfo>> map)
	{
		super(map);
	}

	@Override
	protected int getStrength(DependencyStrength ds, char noMethodsUsed)
	{
		return ds.getStaticInvocation(noMethodsUsed);
	}
}

class LocalVarBuilder extends MapBuilder
{
	protected LocalVarBuilder(Map<String, Set<MethodInfo>> map)
	{
		super(map);
	}

	@Override
	protected int getStrength(DependencyStrength ds, char noMethodsUsed)
	{
		return ds.getLocalVariable(noMethodsUsed);
	}
}

class FieldBuilder extends MapBuilder
{
	private Set<String> fields;

	protected FieldBuilder(Map<String, Set<MethodInfo>> map, Set<String> fields)
	{
		super(map);
		this.fields = fields;
	}

	protected int getStrength(DependencyStrength ds, char noMethodsUsed)
	{
		return ds.getMember(noMethodsUsed);
	}

	protected Iterator<String> getIterator()
	{
		return fields.iterator();
	}
}

class ParameterBuilder extends MapBuilder
{
	private Set<String> params;

	protected ParameterBuilder(Map<String, Set<MethodInfo>> map, Set<String> params)
	{
		super(map);
		this.params = params;
	}

	protected int getStrength(DependencyStrength ds, char noMethodsUsed)
	{
		return ds.getMember(noMethodsUsed);
	}

	protected Iterator<String> getIterator()
	{
		return params.iterator();
	}
}

class MethodInfo
{
	String name;
	String desc;

	public MethodInfo(String name, String desc)
	{
		this.name = name;
		this.desc = desc;
	}

	public int hashCode()
	{
		return name.hashCode() + desc.hashCode();
	}

	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o instanceof MethodInfo)
		{
			MethodInfo oo = (MethodInfo) o;
			return oo.desc.equals(this.desc) && this.name.equals(oo.name);
		}
		return false;
	}
}
