package dependencyfinder;

import java.util.List;

import org.objectweb.asm.signature.SignatureVisitor;

import dependencyfinder.classdependencymodel.JavaClassDependencyModel;


public class GenericMethodVisitor extends EmptyGenericVisitor {

	private GenericClassVisitor paramVisitor;
	public GenericMethodVisitor(JavaClassDependencyModel dependencies,List<String> ignored) 
	{
		this.paramVisitor=new GenericClassVisitor(dependencies,ignored);
	}
	public SignatureVisitor visitParameterType() 
	{
		return paramVisitor;
	}
	public SignatureVisitor visitReturnType() 
	{
		return paramVisitor;
	}
}
