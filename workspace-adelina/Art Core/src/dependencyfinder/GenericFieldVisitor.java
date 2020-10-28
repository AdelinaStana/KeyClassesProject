package dependencyfinder;

import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureVisitor;

import dependencyfinder.classdependencymodel.JavaClassDependencyModel;

public class GenericFieldVisitor extends EmptyGenericVisitor
{
	private List<String> params;
	private JavaClassDependencyModel dependencies;

	public GenericFieldVisitor(JavaClassDependencyModel dependencies,List<String> formalParams)
	{
		params=formalParams;
		this.dependencies=dependencies;
	}
	
	public SignatureVisitor visitTypeArgument(char wildcard)
	{
		return new TypeParameterVisitor(dependencies,params);
	}
	public void visitClassType(String name)
	{
		Type classType = Type.getObjectType(name);
		String t= classType.getClassName();
		if (!params.contains(t))
		{
			dependencies.addField(t);
		}
	}
}