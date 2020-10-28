package classloader;

import java.io.File;
import java.util.List;

import dependencyfinder.classdependencymodel.DependencyModel;

public abstract class InputHandler {
	private InputHandler next; 
	protected InputHandler(InputHandler next)
	{
		this.next=next;
	}
	public List<DependencyModel> handle(File input) throws UnsupportedInputException,NoSuchFileException
	{
		if (canHandle(input))
			return specificHandle(input);
		else 
			if (next!=null)
				return next.handle(input);
			else
				throw new UnsupportedInputException();
	}
	protected abstract boolean canHandle(File input) throws NoSuchFileException;
	protected abstract List<DependencyModel> specificHandle(File Input);
}
