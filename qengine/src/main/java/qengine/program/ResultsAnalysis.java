package qengine.program;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.jena.tdb.store.Hash;


public class ResultsAnalysis {

    // Délimiteurs qui doivent être dans le fichier CSV
	public static final String DELIMITER = ",";
	public static final String SEPARATOR = "\n";

    public static void analyse() throws IOException{
        Reader dataReader = new FileReader("data/resultFileToSortRequests.csv");

        BufferedReader CSVFile = new BufferedReader(dataReader);
        String dataRow = CSVFile.readLine();
        dataRow = CSVFile.readLine();

        HashMap<Integer, Integer> hashMapNbResults = new HashMap<>();
        HashMap<Integer, Integer> hashMapNbCond = new HashMap<>();
        int nbDoubles = 0;
        int inc=0;
        int nbRequest = 0;

        //Valeur maximum nbResultats à changer en fonction du nombre de résultats maximum que l'on veut tester
        for(int nbResultats=0; nbResultats<10000; nbResultats++){
            hashMapNbResults.put(nbResultats, 0);
        }

        //Valeur maximum nbConditions à changer en fonction du nombre de conditions maximum que l'on veut tester
        for(int nbConditions=0; nbConditions<5; nbConditions++){
            hashMapNbCond.put(nbConditions, 0);
        }

        while (dataRow != null){
            String[] dataArray = dataRow.split(",");

            //On compte le nombre de requêtes pour ensuite faire le pourcentage de 0 maximum à avoir
            nbRequest++;

            //On récupère le nombre de résultats de la requête (clé) et on incrémente la valeur correspondante
            
            if(hashMapNbResults.get(Integer.parseInt(dataArray[1])) == null){
                hashMapNbResults.put(Integer.parseInt(dataArray[1]), 1);
            }else{
                inc = hashMapNbResults.get(Integer.parseInt(dataArray[1]));
                hashMapNbResults.put(Integer.parseInt(dataArray[1]), inc+1);
            }

            if(hashMapNbCond.get(Integer.parseInt(dataArray[2])) == null){
                hashMapNbCond.put(Integer.parseInt(dataArray[2]), 1);
            }else{
                inc = hashMapNbCond.get(Integer.parseInt(dataArray[2]));
                hashMapNbCond.put(Integer.parseInt(dataArray[2]), inc+1);
            }

            //On compte le nombre de doublons (0= n'est pas un doublon mais peut en avoir, 1=est le doublon de quelqu'un)
            if(Integer.parseInt(dataArray[3]) == 1){
                nbDoubles++;
            }
            dataRow = CSVFile.readLine();
        }

        /*for(int i=0; i<hashMapNbResults.size(); i++){
            if(hashMapNbResults.get(i)!=0){
                System.out.println("Le nombre de requêtes ayant "+i+" résultats est de"+hashMapNbResults.get(i));
            }
        }*/
        
        HashMap<Integer, Integer> histogramme = new HashMap<>();
        int intervalle = 1;
        int stock = 0;
        for(int i=1; i<=hashMapNbResults.size(); i++){
            if(i < intervalle * 50){
                if(histogramme.containsKey(intervalle)){
                    stock = histogramme.get(intervalle);
                    histogramme.put(intervalle, stock+hashMapNbResults.get(i));
                }else{
                    histogramme.put(intervalle, hashMapNbResults.get(i));
                }
            }else{
                intervalle++;
                histogramme.put(intervalle, hashMapNbResults.get(i));
            }  
        } 

        /*for(int j = 1 ; j < histogramme.size(); j++){
            System.out.println(histogramme.get(j));
        }*/


        //Quelques affichages pour expliquer le fichier que l'on vient d'analyser
        System.out.println("\nNombre de requêtes qui renvoient 0 résultats est de : "+hashMapNbResults.get(0));

        System.out.println("\nNombre de requêtes qui sont des doublons (sans compter l'originale) : "+nbDoubles);

        System.out.println("\nNombre de requêtes total : "+nbRequest);

        
        dataReader.close();


    }

    public static void createRequestFileForBenchmarkEraseDouble() throws IOException{

        Reader dataReader = new FileReader("data/resultFileToSortRequests.csv");

        BufferedReader CSVFile = new BufferedReader(dataReader);
        String dataRow = CSVFile.readLine();
        dataRow = CSVFile.readLine();

        //On crée le nouveau .csv qui va contenir nos requêtes (et leur résultats sans doublons)
        // En-tête de fichier
		final String HEADER = "Requêtes, nombre de résultats, nombre de conditions, doublons?";

		String fileName = "resultFileToSortRequestsWithoutDouble.csv";
		String filePath = "data/" + fileName;

		File outFile = new File(filePath);
		FileWriter fileCSV = new FileWriter(outFile);

		// Ajouter l'en-tête
		fileCSV.append(HEADER);
		// Ajouter une nouvelle ligne après l'en-tête
		fileCSV.append(SEPARATOR);

        while (dataRow != null){
            String[] dataArray = dataRow.split(",");
            if(Integer.parseInt(dataArray[3]) == 0){
                fileCSV.write(dataRow);
                fileCSV.append(SEPARATOR);
            }
            dataRow = CSVFile.readLine();
        }

        CSVFile.close();
        fileCSV.close();

        System.out.println("\nCréation nouveau fichier de requêtes : doublons supprimés");
    }

    public static void createRequestFileForBenchmarkEraseSomeResultsZero() throws IOException{

        Reader dataReader = new FileReader("data/resultFileToSortRequestsWithoutDouble.csv");

        BufferedReader CSVFile = new BufferedReader(dataReader);
        String dataRow = CSVFile.readLine();
        dataRow = CSVFile.readLine();

        //On crée le nouveau .csv qui va contenir nos requêtes (et leur résultats sans doublons)
        // En-tête de fichier
		final String HEADER = "Requêtes, nombre de résultats, nombre de conditions, doublons?";

		String fileName = "resultFileToSortRequestsWithoutDoubleAndLessZeroResults.csv";
		String filePath = "data/" + fileName;

		File outFile = new File(filePath);
		FileWriter fileCSV = new FileWriter(outFile);

		// Ajouter l'en-tête
		fileCSV.append(HEADER);
		// Ajouter une nouvelle ligne après l'en-tête
		fileCSV.append(SEPARATOR);

        //On espère avoir 6000 requêtes
        int nbReqZeroResMax = 600;
        
        //On met des coefficients pour essayer d'avoir plus de requêtes avec 4 conditions
        int div = nbReqZeroResMax/8;

        int nbMaxCondUn = div;
        int nbMaxCondDeux = div*3;
        int nbMaxCondTrois = div;
        int nbMaxCondQuatre = div*3;


        while (dataRow != null){
            String[] dataArray = dataRow.split(",");
            if(Integer.parseInt(dataArray[1]) == 0){
                if(nbReqZeroResMax>=0){
                    if(nbMaxCondUn>=0 && Integer.parseInt(dataArray[2])==1){
                        fileCSV.write(dataRow);
                        fileCSV.append(SEPARATOR);
                        nbMaxCondUn--;
                        nbReqZeroResMax--;
                    }
                    if(nbMaxCondDeux>=0 && Integer.parseInt(dataArray[2])==2){
                        fileCSV.write(dataRow);
                        fileCSV.append(SEPARATOR);
                        nbMaxCondDeux--;
                        nbReqZeroResMax--;
                    }
                    if(nbMaxCondTrois>=0 && Integer.parseInt(dataArray[2])==3){
                        fileCSV.write(dataRow);
                        fileCSV.append(SEPARATOR);
                        nbMaxCondTrois--;
                        nbReqZeroResMax--;
                    }
                    if(nbMaxCondQuatre>=0 && Integer.parseInt(dataArray[2])==4){
                        fileCSV.write(dataRow);
                        fileCSV.append(SEPARATOR);
                        nbMaxCondQuatre--;
                        nbReqZeroResMax--;
                    }
                }
            }else{
                fileCSV.write(dataRow);
                fileCSV.append(SEPARATOR);
            }
            dataRow = CSVFile.readLine();
        }

        CSVFile.close();
        fileCSV.close();

        System.out.println("Création nouveau fichier de requêtes : nombre de requêtes renvoyant 0 résultats diminué");

    }

    public static void createRequestFileForBenchmarkProportionnalNumberConditions() throws IOException{

        Reader dataReader = new FileReader("data/resultFileToSortRequestsWithoutDoubleAndLessZeroResults.csv");

        BufferedReader CSVFile = new BufferedReader(dataReader);
        String dataRow = CSVFile.readLine();
        dataRow = CSVFile.readLine();

        //On crée le nouveau .csv qui va contenir nos requêtes (et leur résultats sans doublons)
        // En-tête de fichier
		final String HEADER = "Requêtes, nombre de résultats, nombre de conditions, doublons?";

		String fileName = "resultFileToSortRequestsWithoutDoubleAndLessZeroResultsAndProportionnalCond.csv";
		String filePath = "data/" + fileName;

		File outFile = new File(filePath);
		FileWriter fileCSV = new FileWriter(outFile);

		// Ajouter l'en-tête
		fileCSV.append(HEADER);
		// Ajouter une nouvelle ligne après l'en-tête
		fileCSV.append(SEPARATOR);

        //Ici on met en dur le nombre max de requêtes qui renvoient zéro résultat (en fonction de l'affichage de la fonction d'avant)
        int conditionUn = 3000;
        int conditionDeux = 3000;
        int conditionTrois = 3000;
        int conditionQuatre = 3000;


        while (dataRow != null){
            String[] dataArray = dataRow.split(",");
            
            //On change le code en dur en fonction du nombre de conditions que l'on a sur les requêtes (rajouter autant de if que nécessaire)
            if(Integer.parseInt(dataArray[2]) == 1){
                if(conditionUn>=0){
                    fileCSV.write(dataRow);
                    fileCSV.append(SEPARATOR);
                    conditionUn--;
                }
            }
            if(Integer.parseInt(dataArray[2]) == 2){
                if(conditionDeux>=0){
                    fileCSV.write(dataRow);
                    fileCSV.append(SEPARATOR);
                    conditionDeux--;
                }
            }
            if(Integer.parseInt(dataArray[2]) == 3){
                if(conditionTrois>=0){
                    fileCSV.write(dataRow);
                    fileCSV.append(SEPARATOR);
                    conditionTrois--;
                }
            }
            if(Integer.parseInt(dataArray[2]) == 4){
                if(conditionQuatre>=0){
                    fileCSV.write(dataRow);
                    fileCSV.append(SEPARATOR);
                    conditionQuatre--;
                }
            }
            
            dataRow = CSVFile.readLine();
        }

        CSVFile.close();
        fileCSV.close();

        System.out.println("Création nouveau fichier de requêtes : nombre de conditions proportionnel");

    }

    public static void createRequestFileForBenchmark() throws IOException{

        Reader dataReader = new FileReader("data/resultFileToSortRequestsWithoutDoubleAndLessZeroResultsAndProportionnalCond.csv");

        BufferedReader CSVFile = new BufferedReader(dataReader);
        String dataRow = CSVFile.readLine();
        dataRow = CSVFile.readLine();
        
        
        String fileName = "querySetForBenchMark.queryset";
		String filePath = "data/new_data/" + fileName;

		File outFile = new File(filePath);
		FileWriter fileQueryset = new FileWriter(outFile);
        
        while (dataRow != null){
            String[] dataArray = dataRow.split(",");
            fileQueryset.write(dataArray[0]);
            fileQueryset.append(SEPARATOR);
            dataRow = CSVFile.readLine();
        }

        CSVFile.close();
        fileQueryset.close();

    }
    
}
