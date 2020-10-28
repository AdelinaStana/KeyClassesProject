package dependencyfinder.util;

import dependencyfinder.classdependencymodel.DependencyStrengthFactory;
import dependencyfinder.classdependencymodel.JavaClassModelFactory;

public class MemberInvoker implements DoubleMethodInvoker {

	@Override
	public void invokeSecond(int i) {
		DependencyStrengthFactory.setMemberIndex(i);
	}

	@Override
	public boolean canInvoke(int i) {
		if (i == 0){
			JavaClassModelFactory.getInstance().ignoreHasA();
			return false;
		}
		if (i > 0)
			return true;
		return false;
	}

	@Override
	public void invoke(int i) {
		DependencyStrengthFactory.setMemberBase(i);
	}

}
