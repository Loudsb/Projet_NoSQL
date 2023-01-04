package qengine.program;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CSV {

	public static String directoryPathOutPut;
	public static String directoryPathQueryResults;

	// Délimiteurs qui doivent être dans le fichier CSV
	public static final String DELIMITER = ",";
	public static final String SEPARATOR = "\n";

	//Variable qui stocke le nom du fichier dans lequel on va mettre nos données résultats
	public static FileWriter fileCSV;

	// Fonction qui affiche d'abord les résulats dans le terminal puis les
	// enregistres dans un fichier CSV
	public static void timeEvaluationInCSV(long totalTime) throws IOException {

		// D'abord on affiche les temps d’évaluation des requêtes et le temps total
		// d’évaluation du workload dans le terminal

		// nom du fichier de données
		String dataFile = Parser.dataFile;
		String decoupageDataFile[] = dataFile.split("/");
		System.out.println(
				"\nNom du fichier qui contient les données : " + decoupageDataFile[decoupageDataFile.length - 1]);

		// nom du fichier de requêtes
		String requestFile = Parser.queryFile;
		String decoupageRequestFile[] = requestFile.split("/");
		System.out.println(
				"Nom du dossier qui contient les requêtes : " + decoupageRequestFile[decoupageRequestFile.length - 1]);

		// nombre de triplets RDF - OK
		int nbTriplet = MainRDFHandler.getCptTriplet();
		System.out.println("Nombre de triplets RDF : " + nbTriplet);

		// Nombre de requêtes - OK (dans parseQueries)
		int nbRequest = Parser.getnbRequest();
		System.out.println("Nombre de requêtes : " + nbRequest);

		// Temps de lecture des données (ms) - OK
		long timeData = Parser.getTotalTimeData();

		// Pour mettre les chiffres après la virgule
		// String conversion = String.valueOf(timeData);
		// String unite = conversion.substring(0,1);
		// String virgule = conversion.substring(1, 4);
		// System.out.println("Temps de lecture des données : " + unite + "," + virgule
		// +" ms");

		System.out.println("Temps de lecture des données : " + timeData + " ms");

		// temps de lecture des requêtes (ms) - OK - temps exact que met le programme à
		// lire les lignes des requêtes dans le fichier qui les contients (dans
		// parseQueries)
		long timeRequest = Parser.getTotalTimeReadQuery();
		System.out.println("Temps de lecture des requêtes : " + timeRequest + " ms");

		// temps création dico (ms) - OK
		long timeDicoCreation = MainRDFHandler.getTotalTimeDico();
		System.out.println("Temps de création du dictionnaire : " + timeDicoCreation + " ms");

		// Nombre d’index - OK
		int nbIndex = 6;
		System.out.println("Nombre d'index : " + nbIndex);

		// temps de création des index (ms)
		long timeIndexCreation = MainRDFHandler.getTotalTimeIndex();
		System.out.println("Temps de création des indexs : " + timeIndexCreation + " ms");

		// temps total d’évaluation du workload (ms) - Temps qu'on met à trouver les
		// réponses (à évaluer les requêtes)
		long workloadTime = Parser.getTotalTimeEvaluateQuery();
		System.out.println("Temps d'évaluation du workload : " + workloadTime + " ms");

		// temps total (du début à la fin du programme) (ms)
		System.out.println("Temps total du programme : " + totalTime + " ms");

		// Maintenant on met tous les résultats dans un CSV

		// Délimiteurs qui doivent être dans le fichier CSV
		// final String DELIMITER = ","; à utiliser que si on utilise plus fileContent
		// et qu'on fait que des append
		//final String SEPARATOR = "\n";

		// En-tête de fichier
		final String HEADER = "nom du fichier de données,nom du dossier des requêtes,nombre de triplets RDF,nombre de requêtes,temps de lecture des données (ms),temps de lecture des requêtes (ms),temps création dico (ms),nombre d’index,temps de création des index (ms),temps total d’évaluation du workload (ms),temps total (du début à la fin du programme) (ms)";

		String fileName = "evaluationTime.csv";
		String filePath = directoryPathOutPut + fileName;
		String fileContent = dataFile + "," + requestFile + "," + nbTriplet + "," + nbRequest + "," + timeData + ","
				+ timeRequest + "," + timeDicoCreation + "," + nbIndex + "," + timeIndexCreation + "," + workloadTime
				+ "," + totalTime;

		File outFile = new File(filePath);
		FileWriter fileCSV = new FileWriter(outFile);

		// Ajouter l'en-tête
		fileCSV.append(HEADER);
		// Ajouter une nouvelle ligne après l'en-tête
		fileCSV.append(SEPARATOR);
		// Ajouter le
		fileCSV.write(fileContent);

		fileCSV.close();

		System.out.println("Fichier csv créé et placé là où indiqué, ou au même niveau que le .jar dans l'arborescence de fichiers");

	}

	// Fonction qui met les résulats des requêtes dans un fichier CSV
	public static void putResultInCSV(ArrayList<String> results) throws IOException {

		// En-tête de fichier
		final String HEADER = "Requêtes, Résultats";

		String fileName = "resultFile.csv";
		String filePath = directoryPathQueryResults + fileName;

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

		for(int i=0; i<results.size(); i++){
			System.out.println(results.get(i));
		}

		for (int i = 0; i < results.size() - 1; i++) {
			if (results.get(i) == "") {
				fileCSV.append(SEPARATOR);
				fileCSV.append("Requête " + numRequete);
				numRequete++;
				fileCSV.append(DELIMITER);
			} else if (results.get(i).equals("empty")) {
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

		if (!results.isEmpty()) {
			if (results.get(results.size() - 1).equals("empty")) {
				fileCSV.append("NO RESULT");

			} else {
				fileCSV.append(results.get(results.size() - 1));
			}
		}

		fileCSV.close();

		System.out.println("\nFichier csv qui contient les résultats créé et placé là où indiqué, ou au même niveau que le .jar dans l'arborescence de fichiers");
	}

	//Fonction qui initialise
	public static void initialiseSortRequest() throws IOException{
		
		// En-tête de fichier
		final String HEADER = "Requêtes, nombre de résultats, nombre de conditions, doublons?";

		String fileName = "resultFileToSortRequests.csv";
		String filePath = "data/" + fileName;

		File outFile = new File(filePath);
		fileCSV = new FileWriter(outFile);

		// Ajouter l'en-tête
		fileCSV.append(HEADER);
		// Ajouter une nouvelle ligne après l'en-tête
		fileCSV.append(SEPARATOR);

		//fileCSV.close();

	}

	//Fonction qui récupère chaque requête, le nombre de résultats qu'elle retourne, le nombre de conditions
	//On va chercher ensuite à trier ce .csv pour : 
	//Garder un certain % de requête avec 0 résultats
	//Garder des % équivalents de requêtes avec 2,3,4.. conditions
	//Enlever les doublons
	public static void sortRequests(HashMap<String, String> param) throws IOException {

		String contenuLigne = param.get("req") + "," + param.get("nbResults") + "," + param.get("nbCond") + "," + param.get("doublon");

		fileCSV.write(contenuLigne);

		fileCSV.append(SEPARATOR);

		//fileCSV.close();
	}

}
