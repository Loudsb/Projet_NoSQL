package qengine.program;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
		//Temps total du début à la fin du programme
		long debutTime = System.currentTimeMillis();

		// Appel à la méthode de classe handleArguments pour gérer les arguments que
		// l'utilisateur a saisis en ligne de commande
		CommandLineHandler.handleArguments(args);

		// On déclare notre instance de classe dictionnaire
		Dictionnaire dictionary = new Dictionnaire();

		// On déclare notre instance de classe index
		Index index = new Index();

		// TODO instance de parser ou static méthodes ??

		// On déclare notre instance de Parser
		Parser parser = new Parser();

		// On parse les ressources, on les ajoute au dictionnaire et aux index
		parser.parseData(dictionary, index);

		// On parse les requêtes puis on les exécute une à une
		parser.parseQueries(dictionary, index);

		//variables pour calculer le temps total (du début à la fin du programme)
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - debutTime;





	}


	//Fonction qui affiche d'abord les résulats dans le terminal puis les enregistres dans un fichier CSV
	public void timeEvaluationInCSV(int nbTriplet, int nbRequest, int nbIndex, long totalTime) throws IOException{
		//D'abord on affiche les temps d’évaluation des requêtes et le temps total d’évaluation du workload dans le terminal
		//nom du fichier de données - TODO mettre le nom du fichier pas en dur
		String dataFile = "data/100K.nt";
		System.out.println("Nom du fichier de données" + dataFile);
		
		//nom du dossier des requêtes - TODO mettre le nom du dossier pas en dur
		String requestRepositary = "data";
		System.out.println("Nom du dossier des requêtes : " + requestRepositary);

		//nombre de triplets RDF
		System.out.println("Nombre de triplets RDF : " + nbTriplet);

		//Nombre de requêtes
		System.out.println("nombre de requêtes : " + nbRequest);

		//Nombre de requêtes
		System.out.println(nbRequest);

		//temps de lecture des données (ms)
		
		//temps de lecture des requêtes (ms)
		
		//temps création dico (ms),
		
		//Nombre d’index
		System.out.println(nbIndex);

		//temps de création des index (ms)

		//temps total d’évaluation du workload (ms)
		
		//temps total (du début à la fin du programme) (ms)


		// On met tous les résultats dans un CSV

		// Délimiteurs qui doivent être dans le fichier CSV
		final String DELIMITER = ",";
		final String SEPARATOR = "\n";

		// En-tête de fichier
		final String HEADER = "nom du fichier de données,nom du dossier des requêtes,nombre de triplets RDF,nombre de requêtes,temps de lecture des données (ms),temps de lecture des requêtes (ms),temps création dico (ms),nombre d’index,temps de création des index (ms),temps total d’évaluation du workload (ms),temps total (du début à la fin du programme) (ms)";

		//TODO Mettre le bon path et nom de fichier
		String filePath = "jesaispas.csv";
		//TODO Finir de remplir le fichier avec les résulats
		String fileContent = dataFile + "," + requestRepositary + "," + nbTriplet + "," + nbRequest;

		File outFile = new File(filePath);
		FileWriter fileCSV = new FileWriter(outFile);

		// Ajouter l'en-tête
		fileCSV.append(HEADER);
		// Ajouter une nouvelle ligne après l'en-tête
		fileCSV.append(SEPARATOR);

		fileCSV.write(fileContent);

		fileCSV.close();

		System.out.println("Fichier csv créé");
	}


}
