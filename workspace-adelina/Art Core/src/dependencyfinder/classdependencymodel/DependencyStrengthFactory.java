package dependencyfinder.classdependencymodel;

public final class DependencyStrengthFactory
{
	static private int inheritance = 250;
	static private int implementedInterface = 150;
	static private int memberBase = 75;
	static private int memberIndex = 5;
	static private int localBase = 50;
	static private int localIndex = 3;
	static private int paramBase = 50;
	static private int paramIndex = 3;
	static private int staticBase = 50;
	static private int staticIndex = 3;
	static private int returnBase = 35;
	static private int memberAccess = 150;
	static private int typeBinding = 15;
	static private int instantiated = 30;
	static private int cast = 50;
	private static DependencyStrength strength = null;

	public static void setInheritance(int inheritance)
	{
		DependencyStrengthFactory.inheritance = inheritance;
		strength = null;
	}

	public static void setImplementedInterface(int implementedInterface)
	{
		DependencyStrengthFactory.implementedInterface = implementedInterface;
		strength = null;
	}

	public static void setMemberBase(int memberBase)
	{
		DependencyStrengthFactory.memberBase = memberBase;
		strength = null;
	}

	public static void setMemberIndex(int memberIndex)
	{
		DependencyStrengthFactory.memberIndex = memberIndex;
		strength = null;
	}

	public static void setLocalBase(int localBase)
	{
		DependencyStrengthFactory.localBase = localBase;
		strength = null;
	}

	public static void setLocalIndex(int localIndex)
	{
		DependencyStrengthFactory.localIndex = localIndex;
		strength = null;
	}

	public static void setParamBase(int paramBase)
	{
		DependencyStrengthFactory.paramBase = paramBase;
		strength = null;
	}

	public static void setParamIndex(int paramIndex)
	{
		DependencyStrengthFactory.paramIndex = paramIndex;
		strength = null;
	}

	public static void setStaticBase(int staticBase)
	{
		DependencyStrengthFactory.staticBase = staticBase;
		strength = null;
	}

	public static void setStaticIndex(int staticIndex)
	{
		DependencyStrengthFactory.staticIndex = staticIndex;
		strength = null;
	}

	public static void setReturnBase(int returnBase)
	{
		DependencyStrengthFactory.returnBase = returnBase;
		strength = null;
	}

	public static void setMemberAccess(int memberAccess)
	{
		DependencyStrengthFactory.memberAccess = memberAccess;
		strength = null;
	}

	public static void setTypeBinding(int typeBinding)
	{
		DependencyStrengthFactory.typeBinding = typeBinding;
		strength = null;
	}

	public static void setInstantiated(int instantiated)
	{
		DependencyStrengthFactory.instantiated = instantiated;
		strength = null;
	}

	public static void setCast(int cast)
	{
		DependencyStrengthFactory.cast = cast;
		strength = null;
	}

	public static DependencyStrength getDependencyStrengthInstace()
	{
		if (strength == null)
			build();
		return strength;
	}

	
	public static void setDependencyStrengthInstace(DependencyStrength ds)
	{
		strength = ds;
	}
	
	private static void build()
	{
		strength = new DependencyStrength(inheritance, implementedInterface, memberBase, memberIndex, localBase,
				localIndex, paramBase, paramIndex, staticBase, staticIndex, returnBase, memberAccess, typeBinding,
				instantiated, cast);

	}
}
