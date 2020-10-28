package dependencyfinder.util;

public abstract class Initializer {
	private Initializer next;
	public Initializer(Initializer next) {
		this.next = next;
	}
	public boolean handle(String depType, String value){
		if (canHandle(depType)){
			doIt(depType,value);
			return true;
		}
		if (next != null)
			return next.handle(depType, value);
		return false;
	}
	public abstract boolean canHandle(String depType);
	public abstract void doIt(String depType,String value);
}
