package dependencyfinder;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Stack;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.EmptyVisitor;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import dependencyfinder.classdependencymodel.JavaClassDependencyModel;

public class CustomVisitor extends EmptyVisitor implements Opcodes {

	private JavaClassDependencyModel dependencies;
	private LinkedList<String> excluded;
	private int methodParamNo;
	private Stack<VisitorHelper> localVar;
	private String className;
	private int adjustment;
	private boolean isArray;

	public CustomVisitor(JavaClassDependencyModel dependencies) {
		this.dependencies = dependencies;
		this.excluded = new LinkedList<String>();
		localVar = new Stack<VisitorHelper>();
		String[] exStr = { "D", "J", "F", "I", "C", "B", "S", "V", "int", "float", "char", "boolean", "short", "byte",
				"void", "double", "long" };
		for (int i = 0; i < exStr.length; i++)
			excluded.add(exStr[i]);

	}

	public CustomVisitor(JavaClassDependencyModel dependencies, Collection<String> ignored) {
		this(dependencies);
		excluded.addAll(ignored);
	}

	public String toString() {
		return dependencies.toString();
	}

	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		Type t = Type.getObjectType(name);
		className = t.getClassName();
		dependencies.setName(className);
		this.excluded.add(className);

		if (superName != null) {
			t = Type.getObjectType(superName);
			String type = t.getClassName();
			if (!excluded.contains(type))
				dependencies.addSuperClass(type);
		}
		for (int i = 0; i < interfaces.length; i++) {
			t = Type.getObjectType(interfaces[i]);
			String type = t.getClassName();
			if (!excluded.contains(type))
				dependencies.addImplementedInteface(type);
		}
		if (signature != null) {
			SignatureReader classSig = new SignatureReader(signature);
			classSig.accept(new GenericClassVisitor(dependencies, excluded));
		}
	}

	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		this.dependencies.incrementNrFields();
		if ((access & Opcodes.ACC_PUBLIC)!=0) 
			this.dependencies.incrementNrPFields();
		if (signature == null) {
			Type t = Type.getType(desc);
			String type = resolveType(t);
			if (!excluded.contains(type))
				this.dependencies.addField(type);
			isArray = false;
		} else {
			SignatureReader fieldSig = new SignatureReader(signature);
			SignatureVisitor sw = new GenericFieldVisitor(dependencies, excluded);
			fieldSig.acceptType(sw);
		}
		return this;
	}

	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		this.dependencies.incrementNrMethods();
		if ((access & Opcodes.ACC_PUBLIC)!=0) 
			this.dependencies.incrementNrPMethods();
		if (signature == null) {
			if ((access & ACC_STATIC) == 0)
				adjustment = 1;
			else
				adjustment = 0;
			localVar.clear();
			Type returnType = Type.getReturnType(desc);
			String type = resolveType(returnType);
			if (!excluded.contains(type))
				this.dependencies.addReturnType(type);
			isArray = false;
			Type[] params = Type.getArgumentTypes(desc);
			this.methodParamNo = params.length;
			for (int i = 0; i < params.length; i++) {
				type = this.resolveType(params[i]);
				if (!excluded.contains(type))
					this.dependencies.addMethodParameter(type);
				isArray = false;
			}
		} else {
			SignatureReader methodSig = new SignatureReader(signature);
			SignatureVisitor sw = new GenericMethodVisitor(dependencies, excluded);
			methodSig.accept(sw);
		}
		return this;
	}

	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		Type t = Type.getObjectType(owner);
		String type = this.resolveType(t);
		Method m = new Method(name, desc);
		Type[] args = m.getArgumentTypes();
		int paramNo = args.length;
		Type ret = m.getReturnType();
		for (int i = 0; i < paramNo; i++) {
			VisitorHelper vh = localVar.pop();
			if (vh.className != null && !excluded.contains(vh.className))
				this.dependencies.addLocalVar(vh.className);
			if (args[i].equals(Type.DOUBLE_TYPE) || args[i].equals(Type.LONG_TYPE))
				localVar.pop();
		}
		boolean b = this.excluded.contains(type);
		if (opcode == INVOKESPECIAL) {
			localVar.pop();
		} else if (opcode == INVOKESTATIC) {
			if (!b)
				this.dependencies.addStaticInvocation(type, name, desc);
		} else {
			char kind = localVar.pop().type;
			if (!b && !isArray) {
				this.dependencies.addMethodInvocation(type, kind, name, desc);

			}
		}
		if (!ret.equals(Type.VOID_TYPE)) {
			String retName = this.resolveType(ret);
			VisitorHelper vh = new VisitorHelper(retName, JavaClassDependencyModel.LOCAL_VARIABLE);
			localVar.push(vh);
			if (ret.equals(Type.DOUBLE_TYPE) || ret.equals(Type.LONG_TYPE))
				localVar.push(vh);
		}
		isArray = false;

	}

	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		Type t = Type.getObjectType(owner);
		Type t1 = Type.getType(desc);
		String type = this.resolveType(t);
		if (opcode == GETFIELD || opcode == GETSTATIC) {
			char aux = JavaClassDependencyModel.MEMBER;
			if (!excluded.contains(type) && !isArray) {
				aux = JavaClassDependencyModel.LOCAL_VARIABLE;
				this.dependencies.addMemberAccess(type);
			}
			if (opcode == GETFIELD)
				localVar.pop();
			VisitorHelper vh = new VisitorHelper(null, aux);
			localVar.push(vh);
			if (t1.equals(Type.DOUBLE_TYPE) || t1.equals(Type.LONG_TYPE))
				localVar.push(vh);
		} else {
			if (opcode == PUTFIELD)
				localVar.pop();
			localVar.pop();
			if (t1.equals(Type.DOUBLE_TYPE) || t1.equals(Type.LONG_TYPE))
				localVar.pop();
			if (!excluded.contains(type)) {
				this.dependencies.addMemberAccess(type);
			}
		}

	}

	public void visitVarInsn(int opcode, int var) {
		if ((opcode == ALOAD) || (opcode == ILOAD) || (opcode == FLOAD) || (opcode == DLOAD) || (opcode == LLOAD)) {
			char aux;
			if (this.methodParamNo <= var - this.adjustment)
				aux = (JavaClassDependencyModel.LOCAL_VARIABLE);
			else
				aux = (JavaClassDependencyModel.METHOD_PARAMETER);
			VisitorHelper vh = new VisitorHelper(null, aux);
			localVar.push(vh);
			if (opcode == DLOAD || opcode == LLOAD)
				localVar.push(vh);
		}
		if ((opcode == ASTORE && !localVar.empty()) || (opcode == ISTORE) || (opcode == FSTORE) || opcode == DSTORE
				|| opcode == LSTORE) {
			localVar.pop();
			if (opcode == DSTORE || opcode == LSTORE)
				localVar.pop();
			return;
		}
	}

	public void visitInsn(int opcode) {
		if ((opcode == IALOAD) || (opcode == AALOAD) || (opcode == FALOAD) || (opcode == DALOAD) || (opcode == LALOAD)
				|| opcode == BALOAD || opcode == CALOAD || opcode == SALOAD) {
			localVar.pop();
			if (opcode == DALOAD || opcode == LALOAD)
				localVar.push(localVar.peek());
			return;
		}
		if ((opcode == IASTORE) || (opcode == AASTORE) || (opcode == FASTORE) || opcode == BASTORE || opcode == CASTORE
				|| opcode == SASTORE || (opcode == DASTORE) || (opcode == LASTORE) || opcode == DCMPL || opcode == DCMPG
				|| opcode == LCMP) {
			localVar.pop();
			localVar.pop();
			localVar.pop();
			if (opcode == DASTORE || opcode == LASTORE)
				localVar.pop();
			return;
		}
		if ((opcode == POP || opcode == ATHROW || opcode == FADD || opcode == IADD || opcode == FMUL || opcode == IMUL
				|| opcode == IREM || opcode == FREM || opcode == IDIV || opcode == FDIV || opcode == FCMPL
				|| opcode == FCMPG || opcode == ISHL || opcode == LSHL || opcode == ISHR || opcode == LSHR
				|| opcode == IUSHR || opcode == LUSHR || opcode == IAND || opcode == IOR || opcode == IXOR
				|| opcode == L2I || opcode == L2F || opcode == D2I || opcode == D2F)) {
			if (!localVar.empty())
				localVar.pop();
			return;
		}
		if (opcode == I2L || opcode == F2L || opcode == I2D || opcode == F2D) {
			localVar.push(localVar.peek());
			return;
		}
		if (opcode == POP2 || opcode == DADD || opcode == LADD || opcode == DSUB || opcode == LSUB || opcode == DMUL
				|| opcode == LMUL || opcode == DREM || opcode == LREM || opcode == DDIV || opcode == LDIV
				|| opcode == LAND || opcode == LOR || opcode == LXOR) {
			localVar.pop();
			localVar.pop();
			return;
		}
		if (opcode == SWAP) {
			VisitorHelper t1 = localVar.pop();
			VisitorHelper t2 = localVar.pop();
			localVar.push(t1);
			localVar.push(t2);
			return;
		}
		if (opcode == DUP) {
			VisitorHelper t1 = localVar.peek();
			localVar.push(t1);
			return;
		}
		if (opcode == DUP2) {
			VisitorHelper t1 = localVar.pop();
			VisitorHelper t2 = localVar.peek();
			localVar.push(t1);
			localVar.push(t2);
			localVar.push(t1);
			return;
		}

		if (opcode == DUP_X1) {
			VisitorHelper t1 = localVar.pop();
			VisitorHelper t2 = localVar.pop();
			localVar.push(t1);
			localVar.push(t2);
			localVar.push(t1);
			return;
		}
		if (opcode == DUP2_X1) {
			VisitorHelper t1 = localVar.pop();
			VisitorHelper t2 = localVar.pop();
			VisitorHelper t3 = localVar.pop();
			localVar.push(t2);
			localVar.push(t1);
			localVar.push(t3);
			localVar.push(t2);
			localVar.push(t1);
			return;
		}

		if (opcode == DUP_X2) {
			VisitorHelper t1 = localVar.pop();
			VisitorHelper t2 = localVar.pop();
			VisitorHelper t3 = localVar.pop();
			localVar.push(t1);
			localVar.push(t3);
			localVar.push(t2);
			localVar.push(t1);
			return;
		}
		if (opcode == DUP2_X2) {
			VisitorHelper t1 = localVar.pop();
			VisitorHelper t2 = localVar.pop();
			VisitorHelper t3 = localVar.pop();
			VisitorHelper t4 = localVar.pop();
			localVar.push(t2);
			localVar.push(t1);
			localVar.push(t4);
			localVar.push(t3);
			localVar.push(t2);
			localVar.push(t1);
			return;
		}

		if (opcode == IRETURN || opcode == FRETURN || opcode == ARETURN || opcode == MONITORENTER
				|| opcode == MONITOREXIT || opcode == LRETURN || opcode == DRETURN) {
			localVar.pop();
			if (opcode == LRETURN || opcode == DRETURN)
				localVar.pop();
			return;
		}
		if (opcode != RETURN && opcode != NOP && opcode != L2D && opcode != F2I && opcode != I2F && opcode != D2L) {
			VisitorHelper vh = new VisitorHelper(null, JavaClassDependencyModel.LOCAL_VARIABLE);
			localVar.push(vh);
			if (opcode == LCONST_0 || opcode == LCONST_1 || opcode == DCONST_0 || opcode == DCONST_1) {
				localVar.push(vh);
			}
		}
	}

	public void visitTypeInsn(int opcode, String type) {
		VisitorHelper vh = new VisitorHelper(null, JavaClassDependencyModel.LOCAL_VARIABLE);
		if (opcode == ANEWARRAY) {
			localVar.pop();
			localVar.push(vh);
			return;
		}
		if (opcode == NEW) {
			Type t = Type.getObjectType(type);
			String name = this.resolveType(t);
			if (!this.excluded.contains(name))
				this.dependencies.addCreated(name);
			vh.className = name;
			localVar.push(vh);
		} else {
			Type t = Type.getObjectType(type);
			String name = this.resolveType(t);
			if (!this.excluded.contains(name))
				this.dependencies.addCast(name);
		}
	}

	public void visitLdcInsn(Object cst) {
		Class<?> c = cst.getClass();
		String name = c.getCanonicalName();
		VisitorHelper vh = new VisitorHelper(name, JavaClassDependencyModel.LOCAL_VARIABLE);
		localVar.push(vh);
		if (cst instanceof Long || cst instanceof Double)
			localVar.push(vh);
	}

	public void visitIntInsn(int opcode, int operand) {
		if (opcode == BIPUSH || opcode == SIPUSH)
			localVar.push(new VisitorHelper(null, JavaClassDependencyModel.LOCAL_VARIABLE));
	}

	public void visitMultiANewArrayInsn(String desc, int dims) {
		for (int i = 0; i < dims; i++)
			localVar.pop();
		localVar.push(new VisitorHelper(null, JavaClassDependencyModel.LOCAL_VARIABLE));
	}

	public void visitJumpInsn(int opcode, Label label) {
		if (opcode == IFEQ || opcode == IFNE || opcode == IFLT || opcode == IFGE || opcode == IFGT || opcode == IFLE
				|| opcode == IFNONNULL || opcode == IFNULL) {
			localVar.pop();
			return;
		}
		if (opcode == IF_ICMPEQ || opcode == IF_ICMPNE || opcode == IF_ICMPLT || opcode == IF_ICMPGE
				|| opcode == IF_ICMPGT || opcode == IF_ICMPLE || opcode == IF_ACMPEQ || opcode == IF_ACMPNE) {
			localVar.pop();
			localVar.pop();
			return;
		}
		if (opcode == JSR)
			localVar.push(new VisitorHelper(null, JavaClassDependencyModel.LOCAL_VARIABLE));
	}

	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		localVar.pop();
	}

	public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
		localVar.pop();
	}

	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
		if (signature == null) {
			Type t = Type.getType(desc);
			String className = resolveType(t);
			isArray = false;
			if (!excluded.contains(className))
				this.dependencies.addLocalVar(className);
		} else {
			SignatureReader varSig = new SignatureReader(signature);
			varSig.acceptType(new GenericLocalVarVisitor(dependencies, excluded));
		}
	}

	protected void finalize() throws Throwable {
		super.finalize();
		this.dependencies = null;
		this.excluded.clear();
		this.excluded = null;
		this.localVar.clear();
		this.localVar = null;
	}

	private String resolveType(Type t) {
		String typeAsString = t.getClassName();
		if (typeAsString.endsWith("[]")) {
			int index = typeAsString.indexOf("[]");
			typeAsString = typeAsString.substring(0, index);
			isArray = true;
		}
		return typeAsString;
	}

	private class VisitorHelper {
		String className;
		char type;

		public VisitorHelper(String className, char type) {
			this.className = className;
			this.type = type;
		}
	}
}