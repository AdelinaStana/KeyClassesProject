package dependencyfinder;

import java.util.List;

import org.objectweb.asm.Type;

import dependencyfinder.classdependencymodel.JavaClassDependencyModel;

public class TypeParameterVisitor extends EmptyGenericVisitor
{
	private List<String> params;
	private JavaClassDependencyModel dependencies;

	public TypeParameterVisitor(JavaClassDependencyModel dependencies,List<String> formalParams)
	{
		params=formalParams;
		this.dependencies=dependencies;
	}
	
	public void visitClassType(String name)
	{
		Type classType = Type.getObjectType(name);
		String t= classType.getClassName();
		if (!params.contains(t))
		{
			dependencies.addParameter(t);
		}
	}
}