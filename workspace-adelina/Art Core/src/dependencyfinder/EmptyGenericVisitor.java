package dependencyfinder;

import org.objectweb.asm.signature.SignatureVisitor;


public class EmptyGenericVisitor implements SignatureVisitor{

	

	public SignatureVisitor visitArrayType() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void visitBaseType(char arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SignatureVisitor visitClassBound() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void visitClassType(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitEnd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SignatureVisitor visitExceptionType() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void visitFormalTypeParameter(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitInnerClassType(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SignatureVisitor visitInterface() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public SignatureVisitor visitInterfaceBound() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public SignatureVisitor visitParameterType() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public SignatureVisitor visitReturnType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SignatureVisitor visitSuperclass() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void visitTypeArgument() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SignatureVisitor visitTypeArgument(char arg0) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void visitTypeVariable(String arg0) {
		// TODO Auto-generated method stub
		
	}

}
