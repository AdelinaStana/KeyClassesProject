package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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


public class MainTestARTcore {

	/*
	 * the name of the file(s) to be analysed and used to build the system
	 * model; it is expected to be a *.jar, *.class or folder
	 */
	private static String sourceFileName;

	/* the reverse engineered system model representation */
	private static SystemModel theSystemModel;

	/* the DSM built from the system model */
	private static DSM theDSM;

	
	/* the Main program that does everything */
	public static void main(final String[] args) throws Exception {

		/*
		 * Select a sourceFileName = the name of the input to be analysed - it
		 * is searched in the inputs folder.
		 * 
		 * Following files are in the inputs folder - uncomment one:
		 */

		// sourceFileName=new String("ant.jar");
		// sourceFileName=new String("argouml.jar");
		// sourceFileName = new String("jhotdraw.jar");
		// sourceFileName=new String("jedit.jar");
		// sourceFileName = new String("wro4j-core-1.6.3.jar");
		sourceFileName=new String("minimalART.jar");
		
		buildSystemModel();

		computeDSM();

		}

	private static void buildSystemModel() throws Exception {
		System.out.println("start reading input ...");

		File[] ss = { new File("inputs//" + sourceFileName) };

		long beginning = System.currentTimeMillis();
		InputHandler ih = new JarHandler(
				new DirHandler(new ClassFileHandler(null), new JarHandler(new ClassFileHandler(null))));

		theSystemModel = new SystemModel(sourceFileName);

		int i = 0;
		for (File f : ss) {
			List<DependencyModel> deps;
			try {
				deps = ih.handle(f);
				i = deps.size();
				for (DependencyModel dm : deps)
					theSystemModel.addElement(dm);
			} catch (UnsupportedInputException e) {
			} catch (NoSuchFileException e) {
			}

		}
		if (i == 0)
			throw new Exception();

		System.out.println(
				"input read and system model built in " + ((double) System.currentTimeMillis() - beginning) / 1000);

	}

	private static void buildConfig(String string) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(string));
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
																							new SingleValueInitializer(
																									null,
																									"instantiation",
																									new InstantiatedInvoker()),
																							"member_access",
																							new MemberAccessInvoker()),
																					"type_binding",
																					new TypeBindingInvoker()),
																			"return", new ReturnInvoker()),
																	"static_invocation", new StaticInvoker()),
															"parameter", new ParamInvoker()),
													"local_variable", new LocalVarInvoker()),
											"member", new MemberInvoker()),
									"implementation", new ImplementationInvoker()),
							"inheritance", new InheritanceInvoker()),
					"cast", new CastInvoker());
			String line = null;
			while ((line = in.readLine()) != null) {
				String[] parts = line.split("  *= *");
				if (parts.length == 2)
					i.handle(parts[0], parts[1]);
			}
			in.close();
		} catch (IOException e) {
		}
	}

	private static void computeDSM() {

		// buildConfig("cfg.txt"); // this is optional, use it only if you want
		// to change some of the default values for dependency strengths

		long beginning = System.currentTimeMillis();

		theDSM = theSystemModel.computeDSM();

		System.out.println("DSM created in " + ((double) System.currentTimeMillis() - beginning) / 1000);

	}

	

}
