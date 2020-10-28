package dependencyfinder.util;

public class TwoValueInitializer extends Initializer {
	private String option;
	private DoubleMethodInvoker mi;
	
	public TwoValueInitializer(Initializer next, String option,
			DoubleMethodInvoker mi) {
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
			String[] vals = value.split(" +");
			if (vals.length>0){
				int i =  Integer.parseInt(vals[0]);
				if (mi.canInvoke(i)){
					mi.invoke(i);
					if (vals.length>1){
						int j = Integer.parseInt(vals[1]);
						mi.invokeSecond(j);
					}
				}
			}
		}catch(NumberFormatException e){}

	}

}
