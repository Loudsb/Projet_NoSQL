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
        Stopwatch stopwatch = Stopwatch.createStarted();

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
		ArrayList<String> results = parser.parseQueries(dictionary, index);

		// Mettre les résultats dans un fichier CSV
		putResultInCSV(results);
		
		// Variable pour calculer le temps total
		long totalTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);

		timeEvaluationInCSV(totalTime);
	}
	

	//Fonction qui affiche d'abord les résulats dans le terminal puis les enregistres dans un fichier CSV
	public static void timeEvaluationInCSV(long totalTime) throws IOException{
		
		//D'abord on affiche les temps d’évaluation des requêtes et le temps total d’évaluation du workload dans le terminal
		
		//nom du fichier de données
		String dataFile = Parser.dataFile;
		String decoupageDataFile[] = dataFile.split("/");
		System.out.println("\nNom du fichier qui contient les données : " + decoupageDataFile[decoupageDataFile.length-1]);
		
		//nom du fichier de requêtes
		String requestFile = Parser.queryFile;
		String decoupageRequestFile[] = requestFile.split("/");
		System.out.println("Nom du fichier qui contient les requêtes : " + decoupageRequestFile[decoupageRequestFile.length-1]);

		//nombre de triplets RDF - OK
		int nbTriplet = MainRDFHandler.getCptTriplet();
		System.out.println("Nombre de triplets RDF : " + nbTriplet);

		//Nombre de requêtes - OK (dans parseQueries)
		int nbRequest = Parser.getnbRequest();
		System.out.println("Nombre de requêtes : " + nbRequest);

		//temps de lecture des données (ms) - TODO à faire !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		long timeData = Parser.getTotalTimeData();
		String conversion = String.valueOf(timeData);
		String unite = conversion.substring(0,1);
		String virgule = conversion.substring(1, 4);

		System.out.println("Temps de lecture des données : " + unite + "," + virgule +" ms");
		
		//temps de lecture des requêtes (ms) - OK - temps exact que met le programme à lire les lignes des requêtes dans le fichier qui les contients (dans parseQueries)
		long timeRequest = Parser.getTotalTimeR();
		System.out.println("Temps de lecture des requêtes : " + timeRequest + " ms");

		//temps création dico (ms) - OK
		long timeDicoCreation = MainRDFHandler.getTotalTimeDico();
		System.out.println("Temps de création du dictionnaire : " + timeDicoCreation + " ms");
		
		//Nombre d’index - OK
		int nbIndex = 6;
		System.out.println("Nombre d'index : " + nbIndex);

		//temps de création des index (ms) - OK
		long timeIndexCreation = MainRDFHandler.getTotalTimeIndex();
		System.out.println("Temps de création des indexs : " + timeIndexCreation + " ms");

		//temps total d’évaluation du workload (ms) - OK - Temps qu'on met à trouver les réponses (à évaluer les requêtes)
		long workloadTime = Parser.getStopWatchQuery();
		System.out.println("Temps d'évaluation du workload : " + workloadTime + " ms");

		//temps total (du début à la fin du programme) (ms) - OK
		System.out.println("Temps total du programme : " + totalTime + " ms");

		

		// Maintenant on met tous les résultats dans un CSV

		// Délimiteurs qui doivent être dans le fichier CSV
		//final String DELIMITER = ","; à utiliser que si on utilise plus fileContent et qu'on fait que des append
		final String SEPARATOR = "\n";

		// En-tête de fichier
		final String HEADER = "nom du fichier de données,nom du dossier des requêtes,nombre de triplets RDF,nombre de requêtes,temps de lecture des données (ms),temps de lecture des requêtes (ms),temps création dico (ms),nombre d’index,temps de création des index (ms),temps total d’évaluation du workload (ms),temps total (du début à la fin du programme) (ms)";

		//TODO Mettre le bon path (à donner en argument) et nom de fichier
		String filePath = "data/evaluationTime.csv";
		//TODO Finir de remplir le fichier avec les résulats
		String fileContent = dataFile + "," + requestFile + "," + nbTriplet + "," + nbRequest +  "," + timeData + "," + timeRequest + "," + timeDicoCreation + "," + nbIndex + "," + timeIndexCreation + "," + workloadTime + "," + totalTime;

		File outFile = new File(filePath);
		FileWriter fileCSV = new FileWriter(outFile);

		// Ajouter l'en-tête
		fileCSV.append(HEADER);
		// Ajouter une nouvelle ligne après l'en-tête
		fileCSV.append(SEPARATOR);
		// Ajouter le 
		fileCSV.write(fileContent);

		fileCSV.close();

		System.out.println("Fichier csv créé");
	}

	// Fonction qui met les résulats des requêtes dans un fichier CSV
	public static void putResultInCSV(ArrayList<String> results) throws IOException{
		// Délimiteurs qui doivent être dans le fichier CSV
		final String DELIMITER = ", "; // à utiliser que si on utilise plus fileContent et qu'on fait que des append
		final String SEPARATOR = "\n";

		// En-tête de fichier
		final String HEADER = "Requêtes, Résultats";

		//TODO Mettre le bon path
		String filePath = "data/resultFile.csv";

		File outFile = new File(filePath);
		FileWriter fileCSV = new FileWriter(outFile);

		// Ajouter l'en-tête
		fileCSV.append(HEADER);
		// Ajouter une nouvelle ligne après l'en-tête
		fileCSV.append(SEPARATOR);
		int numRequete = 1;
		fileCSV.append("Requête " + numRequete);
		fileCSV.append(DELIMITER);
		numRequete++;

		for(int i = 0 ; i < results.size()-1; i++){
			if (results.get(i) == "") {
				fileCSV.append(SEPARATOR);
				fileCSV.append("Requête " + numRequete);
				numRequete++;
				fileCSV.append(DELIMITER);
			} else if(results.get(i).equals("empty")){
				fileCSV.append("NO RESULT");
				fileCSV.append(SEPARATOR);
				fileCSV.append("Requête " + numRequete);
				numRequete++;
				fileCSV.append(DELIMITER);
			} else {
				fileCSV.append(results.get(i));
				fileCSV.append(DELIMITER);
			}

		}

		if(results.get(results.size()-1).equals("empty")){
			fileCSV.append("NO RESULT");
		}else{
			fileCSV.append(results.get(results.size()-1));
		}

		fileCSV.close();

		System.out.println("Fichier csv qui contient les résultats créé");
	}

}
