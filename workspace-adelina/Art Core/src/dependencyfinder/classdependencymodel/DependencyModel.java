package dependencyfinder.classdependencymodel;

import java.util.Map;
import java.util.Set;

public interface DependencyModel
{
	String[] getClassFullName();

	String getClassName();

	int getNrFields(); // added for size

	int getNrMethods(); // added for size
	
	int getNrPublicFields(); // added for size

	int getNrPublicMethods(); // added for size


	void setName(String name);

	Map<String, Integer> computeModel();

	Set<DependenciesOnAClass> giveAllDetails();
}
