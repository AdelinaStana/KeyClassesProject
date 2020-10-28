package dependencyfinder.classdependencymodel;

public class DependencyStrength
{
	final private int inheritance;
	final private int implementedInterface;
	final private int memberBase;
	final private int memberIndex;
	final private int localBase;
	final private int localIndex;
	final private int paramBase;
	final private int paramIndex;
	final private int staticBase;
	final private int staticIndex;
	final private int returnBase;
	final private int memberAccess;
	final private int typeBinding;
	final private int instantiated;
	final private int cast;

	public DependencyStrength(int inheritance, int implementedInterface, int memberBase, int memberIndex, int localBase,
			int localIndex, int paramBase, int paramIndex, int staticBase, int staticIndex, int returnBase,
			int memberAccess, int typeBinding, int instantiated, int cast)
	{
		this.inheritance = inheritance;
		this.implementedInterface = implementedInterface;
		this.memberBase = memberBase;
		this.memberIndex = memberIndex;
		this.localBase = localBase;
		this.localIndex = localIndex;
		this.paramBase = paramBase;
		this.paramIndex = paramIndex;
		this.staticBase = staticBase;
		this.staticIndex = staticIndex;
		this.returnBase = returnBase;
		this.memberAccess = memberAccess;
		this.typeBinding = typeBinding;
		this.instantiated = instantiated;
		this.cast = cast;
	}

	public int getInstantiated()
	{
		return this.instantiated;
	}

	public int getInheritance()
	{
		return this.inheritance;
	}

	public int getImplementedInterface()
	{
		return this.implementedInterface;
	}

	public int getMember(char noMethodsUsed)
	{
		int i = this.memberBase + this.memberIndex * noMethodsUsed;
		return i;
	}

	public int getLocalVariable(char noMethodsUsed)
	{
		int i = this.localBase + this.localIndex * noMethodsUsed;
		return i;
	}

	public int getParameter(char noMethodsUsed)
	{
		return this.paramBase + this.paramIndex * noMethodsUsed;
	}

	public int getStaticInvocation(char noMethodsUsed)
	{
		return this.staticBase + this.staticIndex * noMethodsUsed;
	}

	public int getMemberAccessed()
	{
		return this.memberAccess;
	}

	public int getTypeBinding()
	{
		return this.typeBinding;
	}

	public int getReturned()
	{
		return this.returnBase;
	}

	public int getCast()
	{
		return cast;
	}
}
