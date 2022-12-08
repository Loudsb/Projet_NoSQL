package qengine.program;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.jena.ext.com.google.common.base.Stopwatch;

final class Main {

	// Entrée de notre programme lorsque l'on exécute le projet
	public static void main(String[] args) throws Exception {

		System.out.println("\nLancement de notre projet !");
		System.out.println("Nous n'afficherons pas les résultats de chaque requête mais vous pouvez les retrouver dans le fichier .csv de résultat");

	
		//On initialise les tableaux qui vont contenir nos résultats
		//TODO déplacer tout ce qui est en rapport dans la classe CSV
		Parser.initializeArrays();

		// Temps total du début à la fin du programme
		// Crée et démarre un nouveau chronomètre
		Stopwatch stopwatch = Stopwatch.createStarted();

		// Appel à la méthode de classe handleArguments pour gérer les arguments que
		// l'utilisateur a saisis en ligne de commande
		CommandLineHandler.handleArguments(args);

		// On déclare notre instance de classe dictionnaire
		Dictionnaire dictionary = new Dictionnaire();

		// On déclare notre instance de classe index
		Index index = new Index();

		// On déclare notre instance de Parser
		Parser parser = new Parser();

		// On parse les ressources, on les ajoute au dictionnaire et aux index
		parser.parseData(dictionary, index);

		System.out.println("\nDictionnaire et index créés !");

		System.out.println("\n\n\n\nOn lance l'exécution des requêtes... Patientez s'il vous plaît.\n");

		// On parse les requêtes puis on les exécute une à une et on récupère une
		// ArrayList contenant tous les résultats
		ArrayList<String> results = parser.parseQueries(dictionary, index);

		// Mettre les résultats dans un fichier CSV
		CSV.putResultInCSV(results);

		// Variable pour calculer le temps total
		long totalTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);

		CSV.timeEvaluationInCSV(totalTime);

		System.out.println("Résultats pour construire notre histogramme");
		//TODO faut arriver à exploiter tout ça pour faire un histogramme
		//TODO faut pas afficher ce tableau comme ça c'est horrible
		System.out.println(Parser.nombreResultatsRequete.toString());
		System.out.println(Parser.nombreConditionsRequete);
	}

}
