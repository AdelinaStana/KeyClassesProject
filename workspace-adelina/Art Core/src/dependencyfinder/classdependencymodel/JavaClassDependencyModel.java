package dependencyfinder.classdependencymodel;

public interface JavaClassDependencyModel extends DependencyModel
{
	public static char LOCAL_VARIABLE = 0;
	public static char MEMBER = 1;
	public static char METHOD_PARAMETER = 2;

	void addSuperClass(String clazz);

	void addImplementedInteface(String clazz);

	void addReturnType(String clazz);

	void addMethodParameter(String clazz);

	void addCreated(String clazz);// for new objects

	void addStaticInvocation(String clazz, String name, String desc);

	void addMethodInvocation(String clazz, char type, String name, String desc);

	void addParameter(String clazz);// pt clasele parametrizate

	void addField(String clazz);

	void addMemberAccess(String clazz);

	void addLocalVar(String clazz);

	void addCast(String clazz);

	void incrementNrFields();

	void incrementNrMethods();
	
	void incrementNrPFields();

	void incrementNrPMethods();
}
