package classloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;

import dependencyfinder.CustomVisitor;
import dependencyfinder.classdependencymodel.DependencyModel;
import dependencyfinder.classdependencymodel.JavaClassDependencyModel;
import dependencyfinder.classdependencymodel.JavaClassModelFactory;


public class JarHandler extends InputHandler {

	public JarHandler(InputHandler next) {
		super(next);
	}

	protected boolean canHandle(File input) throws NoSuchFileException {
		if (!input.exists())
			throw new NoSuchFileException();
		return input.getName().endsWith(".jar") && input.isFile(); 
	}

	protected List<DependencyModel> specificHandle(File input) { 
		List<DependencyModel> list = new LinkedList<DependencyModel>(); 
		try {
			JarFile j=new JarFile(input);
			Enumeration<JarEntry> en=j.entries();
			while (en.hasMoreElements())
			{
				JarEntry je=en.nextElement();
				String entryName=je.getName();
				if (entryName.endsWith(".class")){
					JavaClassDependencyModel tm=JavaClassModelFactory.getInstance().getModel();
					ClassReader cr;
					try {
						InputStream is = j.getInputStream(je);
						cr = new ClassReader(is);
						CustomVisitor cv = new CustomVisitor(tm);
						cr.accept(cv, ClassReader.EXPAND_FRAMES);
						list.add(tm);
						is.close();
					} catch (Throwable e) 
					{System.out.println("Encountered problems when extracting dependencies for " + entryName);}
				}
			}
			j.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

}
