package dependencyfinder.util;

import dependencyfinder.classdependencymodel.DependencyStrengthFactory;
import dependencyfinder.classdependencymodel.JavaClassModelFactory;

public class InstantiatedInvoker implements MethodInvoker {

	@Override
	public boolean canInvoke(int i) {
		if (i == 0){
			JavaClassModelFactory.getInstance().ignoreInstantiation();
			return false;
		}
		if (i > 0)
			return true;
		return false;
	}

	@Override
	public void invoke(int i) {
		DependencyStrengthFactory.setInstantiated(i);
	}

}
