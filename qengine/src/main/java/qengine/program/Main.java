package qengine.program;

/**
 * Programme simple lisant un fichier de requête et un fichier de données.
 * 
 * <p>
 * Les entrées sont données ici de manière statique,
 * à vous de programmer les entrées par passage d'arguments en ligne de commande comme demandé dans l'énoncé.
 * </p>
 * 
 * <p>
 * Le présent programme se contente de vous montrer la voie pour lire les triples et requêtes
 * depuis les fichiers ; ce sera à vous d'adapter/réécrire le code pour finalement utiliser les requêtes et interroger les données.
 * On ne s'attend pas forcémment à ce que vous gardiez la même structure de code, vous pouvez tout réécrire.
 * </p>
 * 
 * @author Olivier Rodriguez <olivier.rodriguez1@umontpellier.fr>
 */

final class Main {

	//Entrée de notre programme lorsque l'on exécute le projet
	public static void main(String[] args) throws Exception {

		//Appel à la méthode de classe handleArguments pour gérer les arguments que l'utilisateur a saisis en ligne de commande
		CommandLineHandler.handleArguments(args);

		//On déclare notre instance de classe dictionnaire
		Dictionnaire dictionary = new Dictionnaire();

		//On déclare notre instance de classe index
		Index index = new Index();

		//TODO instance de parser ou static méthodes ??

		//On déclare notre instance de Parser
		Parser parser = new Parser();

		//On parse les ressources, on les ajoute au dictionnaire et aux index
		parser.parseData(dictionary, index);

		//On parse les requêtes puis on les exécute une à une
		parser.parseQueries(dictionary, index);

	}
	
}
