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



		//Ajoute les options à l'objet Options
		options.addOption(queries);
		options.addOption(data);
		options.addOption(output);
		options.addOption(Jena);
		options.addOption(warm);
		options.addOption(shuffle);

		return options;
	}
    
	//Méthode qui agit en fonction des arguments donné en ligne de commande par l'utilisateur
	public static void usingArgs(CommandLine line){

		//TODO soit on met ça et on passe arg puis depuis les if else on appelle les méthode sappropriée des autres classes
		//TODO soit on fait que if else modifie juste des variables et ensuite dans le main on affecte
		

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
    		// print the date and time
		}
		else {
    		// print the date
		}

		if(line.hasOption("Jena")) {
			//On active la vérification avec Jena
			Parser.JenaVerification = true;
		}
		else {
			//On n'active pas la vérification avec Jena
    		Parser.JenaVerification = false;
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

	}

}
