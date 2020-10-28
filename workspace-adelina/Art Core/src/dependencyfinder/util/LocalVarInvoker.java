package dependencyfinder.util;

import dependencyfinder.classdependencymodel.DependencyStrengthFactory;
import dependencyfinder.classdependencymodel.JavaClassModelFactory;

public class LocalVarInvoker implements DoubleMethodInvoker {

	@Override
	public void invokeSecond(int i) {
		DependencyStrengthFactory.setLocalIndex(i);
	}

	@Override
	public boolean canInvoke(int i) {
		if (i == 0){
			JavaClassModelFactory.getInstance().ignoreLocalVar();
			return false;
		}
		if (i > 0)
			return true;
		return false;
	}	

	@Override
	public void invoke(int i) {
		DependencyStrengthFactory.setLocalBase(i);
	}

}
