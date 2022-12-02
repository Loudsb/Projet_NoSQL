package qengine.program;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.jena.ext.com.google.common.base.Stopwatch;

/**
 * Programme simple lisant un fichier de requête et un fichier de données.
 * 
 * <p>
 * Les entrées sont données ici de manière statique,
 * à vous de programmer les entrées par passage d'arguments en ligne de commande
 * comme demandé dans l'énoncé.
 * </p>
 * 
 * <p>
 * Le présent programme se contente de vous montrer la voie pour lire les
 * triples et requêtes
 * depuis les fichiers ; ce sera à vous d'adapter/réécrire le code pour
 * finalement utiliser les requêtes et interroger les données.
 * On ne s'attend pas forcémment à ce que vous gardiez la même structure de
 * code, vous pouvez tout réécrire.
 * </p>
 * 
 * @author Olivier Rodriguez <olivier.rodriguez1@umontpellier.fr>
 */

final class Main {

	// Entrée de notre programme lorsque l'on exécute le projet
	public static void main(String[] args) throws Exception {
		// Temps total du début à la fin du programme
		// Crée et démarre un nouveau chronomètre
        //Stopwatch stopwatch = Stopwatch.createStarted();

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

		//System.out.println(dictionary.getDictionaryIntegerToString());
		//System.out.println(dictionary.getDictionaryStringToInteger());
		//System.out.println(index.getIndexPOS());

		Thread.sleep(10000);

		// On parse les requêtes puis on les exécute une à une et on récupère une ArrayList contenant tous les résultats
		ArrayList<String> results = parser.parseQueries(dictionary, index);
		
		System.out.println("***********"+Parser.nb);
		// Mettre les résultats dans un fichier CSV
		//CSV.putResultInCSV(results);
		
		// Variable pour calculer le temps total
		//long totalTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);

		//CSV.timeEvaluationInCSV(totalTime);
	}

}
