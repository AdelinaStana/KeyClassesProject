package dependencyfinder.util;

public class SingleValueInitializer extends Initializer{
	private String option;
	private MethodInvoker mi;
	public SingleValueInitializer(Initializer next,String option, MethodInvoker mi) {
		super(next);
		this.option = option;
		this.mi = mi;
	}

	@Override
	public boolean canHandle(String depType) {
		return option.equalsIgnoreCase(depType);
	}

	@Override
	public void doIt(String depType, String value) {
		try{
			int i =  Integer.parseInt(value);
			if (mi.canInvoke(i))
				mi.invoke(i);
		}catch(NumberFormatException e){}
	}
}
