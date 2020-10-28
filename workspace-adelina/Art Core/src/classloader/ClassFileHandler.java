package classloader;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.objectweb.asm.ClassReader;

import dependencyfinder.CustomVisitor;
import dependencyfinder.classdependencymodel.DependencyModel;
import dependencyfinder.classdependencymodel.JavaClassDependencyModel;
import dependencyfinder.classdependencymodel.JavaClassModelFactory;

public class ClassFileHandler extends InputHandler {
	public ClassFileHandler(InputHandler next) {
		super(next);
	}

	@Override
	protected boolean canHandle(File input) throws NoSuchFileException {
		if (!input.exists())
			throw new NoSuchFileException();
		if (!input.getName().endsWith(".class"))
			return false;
		return true;
	}

	
	@Override
	protected List<DependencyModel> specificHandle(File input) {
		List<DependencyModel> list=new LinkedList<DependencyModel>();
		try {
			InputStream stream = new BufferedInputStream(new FileInputStream(input));
			JavaClassDependencyModel tm=JavaClassModelFactory.getInstance().getModel();
			ClassReader cr;
			try {
				cr = new ClassReader(stream);
				CustomVisitor cv = new CustomVisitor(tm);
		        cr.accept(cv, ClassReader.EXPAND_FRAMES);
		        list.add(tm);
			} catch (Throwable e)
			{
				System.out.println("Encountered problems when extracting dependencies for" + tm.getClassName());
				}
		} catch (FileNotFoundException e) {}
		return list;
	}

}
