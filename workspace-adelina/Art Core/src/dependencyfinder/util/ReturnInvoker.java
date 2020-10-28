package dependencyfinder.util;

import dependencyfinder.classdependencymodel.DependencyStrengthFactory;
import dependencyfinder.classdependencymodel.JavaClassModelFactory;

public class ReturnInvoker implements MethodInvoker {

	@Override
	public boolean canInvoke(int i) {
		if (i == 0){
			JavaClassModelFactory.getInstance().ignoreReturn();
			return false;
		}
		if (i > 0)
			return true;
		return false;
	}

	@Override
	public void invoke(int i) {
		DependencyStrengthFactory.setReturnBase(i);
	}

}
