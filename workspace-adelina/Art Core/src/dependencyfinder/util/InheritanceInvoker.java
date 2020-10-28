package dependencyfinder.util;

import dependencyfinder.classdependencymodel.DependencyStrengthFactory;
import dependencyfinder.classdependencymodel.JavaClassModelFactory;

public class InheritanceInvoker implements MethodInvoker {
	@Override
	public void invoke(int i) {
		DependencyStrengthFactory.setInheritance(i);
	}

	@Override
	public boolean canInvoke(int i) {
		if (i == 0){
			JavaClassModelFactory.getInstance().ignoreInherintance();
			return false;
		}
		if (i > 0)
			return true;
		return false;
	}

}
