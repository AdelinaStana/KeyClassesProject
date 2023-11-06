package facade;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import classloader.ClassFileHandler;
import classloader.DirHandler;
import classloader.InputHandler;
import classloader.JarHandler;
import classloader.NoSuchFileException;
import classloader.UnsupportedInputException;
import dependencyfinder.classdependencymodel.DependencyModel;
import dependencyfinder.util.CastInvoker;
import dependencyfinder.util.ImplementationInvoker;
import dependencyfinder.util.InheritanceInvoker;
import dependencyfinder.util.Initializer;
import dependencyfinder.util.InstantiatedInvoker;
import dependencyfinder.util.LocalVarInvoker;
import dependencyfinder.util.MemberAccessInvoker;
import dependencyfinder.util.MemberInvoker;
import dependencyfinder.util.ParamInvoker;
import dependencyfinder.util.ReturnInvoker;
import dependencyfinder.util.SingleValueInitializer;
import dependencyfinder.util.StaticInvoker;
import dependencyfinder.util.TwoValueInitializer;
import dependencyfinder.util.TypeBindingInvoker;
import sysmodel.DSM;
import sysmodel.SystemModel;

public class ArtCoreFacade
{
	// configuration text file with empirical values for relation between classes:
	// class implements interface 900
	// class inherits class 500
	// class has a member of another class 600 400
	// in a class method we find a local variable instance of another class 50 400
	// in a class method we find a parameter type of another class 600 400
	// in a class method we find a static invocation of another's class attribute or method 50 400
	// in a class method we find a return instance of another class 600
	// in a class method we find an access to another's class member 400
	// in a class method we find a type binding of another class 100
	// in a class method we find an instantiation of another class 300
	// in a class method we find a cast of an instance to a different class 100
	private String configFile;

	// project jar file
	private String inputFile;

	// ???
	private SystemModel s;

	// dependency sparse matrix
	private DSM dsm;

	public ArtCoreFacade(String configFile, String inputFile) throws Exception
	{
		this.inputFile = inputFile;
		this.configFile = configFile;
		s = null;
		dsm = null;
		extractInfo();
	}

	public SystemModel getSystemModel()
	{
		return s;
	}

	public DSM getDSM()
	{
		return dsm;
	}

	private void extractInfo() throws Exception
	{

		buildConfig(configFile);

		// the list of input files: one jar file or multiple class files
		File[] ss = { new File(inputFile) };

		long beginning = System.currentTimeMillis();

		InputHandler ih = new JarHandler(
				new DirHandler(new ClassFileHandler(null), new JarHandler(new ClassFileHandler(null))));

		// the system model sm is a list of dependency models
		SystemModel sm = new SystemModel(inputFile);

		int i = 0;
		for (File f : ss)
		{
			List<DependencyModel> deps;

			deps = ih.handle(f);
			
			
			
			i = deps.size();
			for (DependencyModel dm : deps)
				sm.addElement(dm);
		}
		if (i == 0)
		{
			throw new Exception();
		}

		System.out.println("input read in " + ((double) System.currentTimeMillis() - beginning) / 1000);

		beginning = System.currentTimeMillis();

		dsm = sm.computeDSM();
		dsm.collapseInnerClasses();

		System.out.println("DSM built in " + ((double) System.currentTimeMillis() - beginning) / 1000);

	}

	// affects the DependencyStrengthFactory static object
	private void buildConfig(String file) throws Exception
	{
		// initialize dependency strengths with values from the configuration file

		// to create a linked list of initializers
		BufferedReader in = new BufferedReader(new FileReader(file));
		Initializer i = new SingleValueInitializer(
				new SingleValueInitializer(
						new SingleValueInitializer(
								new TwoValueInitializer(
										new TwoValueInitializer(
												new TwoValueInitializer(
													new TwoValueInitializer(
														new SingleValueInitializer(
																new SingleValueInitializer(
																		new SingleValueInitializer(
																				new SingleValueInitializer(null,
																						"instantiation", new InstantiatedInvoker()),
																				"member_access", new MemberAccessInvoker()),
																		"type_binding", new TypeBindingInvoker()),
																"return", new ReturnInvoker()),
														"static_invocation", new StaticInvoker()), 
													"parameter", new ParamInvoker()),												
												"local_variable", new LocalVarInvoker()),
										"member", new MemberInvoker()),
								"implementation", new ImplementationInvoker()),
						"inheritance", new InheritanceInvoker()),
				"cast", new CastInvoker());

		String line = null;
		while ((line = in.readLine()) != null)
		{
			String[] parts = line.split("  *= *");
			if (parts.length == 2)
			{
				System.out.println("Input handle parts " + parts[0] + " " + parts[1]);
				i.handle(parts[0], parts[1]);
			}
		}
		in.close();
	}
}