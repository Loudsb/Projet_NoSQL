package qengine.program;

import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;


public class CommandLineHandler {

    public CommandLineHandler(){}

    public static void handleArguments(String[] args) throws ParseException{

		//On crée nos options
		Options options = createOptions();

		//On parse les arguments passés en ligne de commande
		CommandLineParser parser = new DefaultParser();
		CommandLine line = parser.parse(options, args);

		//On utilise ce que l'utilisateur en donné en ligne de commande
		usingArgs(line);

    }  

	//Méthode qui crée nos options une à une et les ajoute dans un objet Options qui est retourné
	public static Options createOptions(){

		//Crée l'objet Options
		Options options = new Options();

		//On crée les options sans argument
		Option Jena = new Option("Jena", "active la vérification de la correction et complétude du système en utilisant Jena comme un oracle");
		Option shuffle = new Option("shuffle", "considère une permutation aléatoire des requêtes en entrée");
		
		//On crée les options avec argument
		Option queries   = Option.builder("queries")
                         .argName("/chemin/vers/dossier/requetes")
                         .hasArg()
                         .desc("Donner le chemin vers le fichier de requêtes")
                         .build();
						 //Fichier et non dossier vers requête !
						 
		Option data   = Option.builder("data")
                         .argName("/chemin/vers/dossier/requetes")
                         .hasArg()
                         .desc("Donner le chemin vers le fichier de données")
                         .build();

		Option output   = Option.builder("output")
                         .argName("/chemin/vers/dossier/sortie")
                         .hasArg()
                         .desc("Donner le chemin vers le dossier de sortie")
                         .build();		 
						 
		Option warm   = Option.builder("warm")
                         .argName("X")
                         .hasArg()
						 .desc("utilise un échantillon des requêtes en entrée (prises au hasard) correspondant au pourcentage 'X' pour chauffer le système")
                         .build();
		
		Option export_query_results = Option.builder("export_query_results")
                         .argName("/chemin/vers/dossier/resultat")
                         .hasArg()
						 .desc("exporte les résultats des requêtes dans un fichier csv séparé")
                         .build();



		//Ajoute les options à l'objet Options
		options.addOption(queries);
		options.addOption(data);
		options.addOption(output);
		options.addOption(Jena);
		options.addOption(warm);
		options.addOption(shuffle);
		options.addOption(export_query_results);

		return options;
	}
    
	//Méthode qui agit en fonction des arguments donné en ligne de commande par l'utilisateur
	public static void usingArgs(CommandLine line){

		if(line.hasOption("queries")) {
			//queryFile = workingDir + "sample_query.queryset";
			Parser.queryFile = line.getOptionValue("queries");
		}
		else {
			/*System.out.println("Veuillez entrer le chemin absolu vers votre fichier de requêtes");
			Scanner scanner = new Scanner(System.in);
			String chemin = scanner.nextLine();
    		Parser.queryFile = chemin;*/
			Parser.queryFile = "/home/garcialea/Bureau/Projet_NoSQL/qengine/data/STAR_ALL_workload.queryset";
		}

		if(line.hasOption("data")) {
    		//dataFile = workingDir + "sample_data.nt";
			Parser.dataFile = line.getOptionValue("data");
		}
		else {
    		/*System.out.println("Veuillez entrer le chemin absolu vers votre fichier de ressources");
			Scanner scanner = new Scanner(System.in);
			String chemin = scanner.nextLine();
    		Parser.dataFile = chemin;*/
			Parser.dataFile = "/home/garcialea/Bureau/Projet_NoSQL/qengine/data/100K.nt";
		}

		if(line.hasOption("output")) {
    		CSV.directoryPathOutPut = line.getOptionValue("output");
		}
		else {
    		CSV.directoryPathOutPut = "/home/garcialea/Bureau/Projet_NoSQL/qengine/data/";
		}

		if(line.hasOption("Jena")) {
			//On active la vérification avec Jena
			Parser.JenaVerification = true;
		}
		else {
			//On n'active pas la vérification avec Jena
    		Parser.JenaVerification = true;
		}

		if(line.hasOption("warm")) {
    		int pourcentage = Integer.parseInt(line.getOptionValue("X"));
		}
		else {
    		// print the date
		}

		if(line.hasOption("shuffle")) {
    		// print the date and time
		}
		else {
    		// print the date
		}

		if(line.hasOption("export_query_results")) {
    		CSV.directoryPathQueryResults = line.getOptionValue("export_query_results");
		}
		else {
    		/*System.out.println("Veuillez entrer le chemin absolu vers le dossier où vous voulez mettre le csv contenant les résultats");
			Scanner scanner = new Scanner(System.in);
			String chemin = scanner.nextLine();
    		Parser.dataFile = chemin;*/
			CSV.directoryPathQueryResults = "/home/garcialea/Bureau/Projet_NoSQL/qengine/data/";
		}

	}

}
