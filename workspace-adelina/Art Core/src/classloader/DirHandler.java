package classloader;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import dependencyfinder.classdependencymodel.DependencyModel;

public class DirHandler extends InputHandler {
	private InputHandler fHandler;
	public DirHandler(InputHandler next,InputHandler file) {
		super(next);
		fHandler=file;
	}

	protected boolean canHandle(File input) throws NoSuchFileException {	
		if (!input.exists()) 
			throw new NoSuchFileException();
		return 	input.isDirectory();
	}

	protected List<DependencyModel> specificHandle(File input) 
	{
		return findClasses(input);
	}
	
	private List<DependencyModel> findClasses(File f) 
	{
		if (f.isDirectory())
		{
			List<DependencyModel> list = new LinkedList<DependencyModel>();
			File[] sons = f.listFiles();
			for (File son: sons){
				list.addAll(findClasses(son));
			}
			return list;
		}
		else 
			try {
				return fHandler.handle(f);
			} catch (UnsupportedInputException e) {
				return new LinkedList<DependencyModel>();
			} catch (NoSuchFileException e) {
				e.printStackTrace();
				return null;
			}  
	}

}
