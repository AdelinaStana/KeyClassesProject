package dependencyfinder;

import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureVisitor;

import dependencyfinder.classdependencymodel.JavaClassDependencyModel;


public class GenericClassVisitor extends EmptyGenericVisitor
{

	private List<String> formals;
	private JavaClassDependencyModel model;
	public GenericClassVisitor(JavaClassDependencyModel model,List<String> formals)
	{
		this.formals=formals;
		this.model=model;
	}
	
	

	public SignatureVisitor visitClassBound(){ 
		return this;
	}

	@Override
	public void visitClassType(String arg0) 
	{
		Type classType = Type.getObjectType(arg0);
		String t= classType.getClassName();
		if (!formals.contains(t))
		{
			model.addParameter(t);
		}
	}

	public void visitFormalTypeParameter(String arg0) 
	{
		formals.add(arg0);
	}

	@Override
	public SignatureVisitor visitInterface() {
		return new EmptyGenericVisitor();
	}

	@Override
	public SignatureVisitor visitInterfaceBound() {
		return this;
	}

	@Override
	public SignatureVisitor visitSuperclass() {
		return new EmptyGenericVisitor();
	}


	public SignatureVisitor visitTypeArgument(char arg0) {
		return this;
	}


}


