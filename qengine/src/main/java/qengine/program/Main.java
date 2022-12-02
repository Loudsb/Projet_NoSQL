package qengine.program;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.jena.ext.com.google.common.base.Stopwatch;

final class Main {

	// Entrée de notre programme lorsque l'on exécute le projet
	public static void main(String[] args) throws Exception {

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

		// On parse les requêtes puis on les exécute une à une et on récupère une
		// ArrayList contenant tous les résultats
		ArrayList<String> results = parser.parseQueries(dictionary, index);

		// Mettre les résultats dans un fichier CSV
		CSV.putResultInCSV(results);

		// Variable pour calculer le temps total
		long totalTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);

		CSV.timeEvaluationInCSV(totalTime);
	}

}
