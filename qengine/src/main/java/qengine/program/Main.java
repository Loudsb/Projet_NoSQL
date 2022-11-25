package qengine.program;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.apache.jena.sparql.function.library.leviathan.tan;
import org.eclipse.rdf4j.query.algebra.Projection;
import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.helpers.AbstractQueryModelVisitor;
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;

import riotcmd.infer;

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
	static final String baseURI = null;

	/**
	 * Votre répertoire de travail où vont se trouver les fichiers à lire
	 */
	static final String workingDir = "data/";

	/**
	 * Fichier contenant les requêtes sparql
	 */
	static final String queryFile = workingDir + "sample_query.queryset";

	/**
	 * Fichier contenant des données rdf
	 */
	static final String dataFile = workingDir + "100K.nt";

	// ========================================================================

	/**
	 * Méthode utilisée ici lors du parsing de requête sparql pour agir sur l'objet obtenu.
	 */
	public static void processAQuery(ParsedQuery query, Dictionnaire dictionnaire, Index index) {

		System.out.println("\nRequête lancée");

		List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());

		//TODO on renvoie des entiers mais faudrait des string + faudrait utiliser la projection (donnée par le prof)

		//ArrayList qui contient le résultat de la requête, elle est mise à jour à chaque étape
		ArrayList<Integer> queryResult = new ArrayList<>();

		//Pour pouvoir traiter les n branches de notre étoile on itère sur les n patterns
		for(int i=0; i<patterns.size(); i++){
			
			//On récupère dans notre dictionnaire les entiers correspondant au P et au O
			ArrayList<Integer> listOfPredicatAndObject = dictionnaire.queryStringToInt(patterns.get(i).getPredicateVar().getValue().toString(), patterns.get(i).getObjectVar().getValue().toString());
			//System.out.println("Dictionnaire, prédicat : "+listOfPredicatAndObject.get(0));
			//System.out.println("Dictionnaire, objet : "+listOfPredicatAndObject.get(1));

			//Si notre dictionnaire connaît toutes les ressources de la requête, on lance la recherche dans les index
			if(listOfPredicatAndObject.size() != 0){
				//ArrayList qui récupère la recherche pour la branche i de notre étoile
				//TODO changer ce nom pourri
				ArrayList<Integer> listOfSubjects = index.findSubjectWithPOSindex(listOfPredicatAndObject);

 				//Dans le cas où c'est la première branche
				if(i==0){

					if(listOfSubjects == null){
						System.out.println("Branche numéro 0 : Aucun résultat trouvé dans l'index pour cette branche. On arrête les recherches");
						//queryResult est vide à ce stade
						break;
					}else{
						//On remplit notre liste de résultat, qui était jusque là vide et on affiche le résultat pour la branche 0
						queryResult = listOfSubjects;
						//TODO tester si pas vide + affichages
						System.out.println("Résultat branche numéro "+ i +" : "+ queryResult);
					}
					
				}else{ //C'est une branche autre que la branche 0, je dois faire l'intersection entre la branche précédente et la branche courante de mon étoile
					if(listOfSubjects == null){ //L'index n'a retourné aucun résultat pour la branche i
						System.out.println("Branche numéro "+i+" : Aucun résultat trouvé dans l'index pour cette branche. On arrête les recherches");
						//On vide notre arrayList vide pour ensuite afficher dans le résultat de la requête, que cela n'a rien donné
						queryResult = new ArrayList<>();
						break;
					}else{
						//L'index a retourné au moins un résultat pour la branche i
						//On fait l'intersection en retenant dans queryResult, uniquement les éléments qui sont aussi dans listOfSubjects
						queryResult.retainAll(listOfSubjects);

						
						if(queryResult.size() !=0){
							System.out.println("Intersection branches "+(i-1)+" et "+i+" donne : "+queryResult);
						}else{//Si la liste est vide suite à retainAll, alors cette requête ne donnera aucun résultat (queryResult est vidée à ce stade)
							System.out.println("Intersection branches "+(i-1)+" et "+i+" ne donne aucun résultat");
							break;
						}
					}	
				}

			}else{
				//Dans le cas où une ressource de la requête n'est pas enregsitrée dans notre dictionnaire, on peut être sûrs que cela ne donnera aucun résultat dans les indexs
				System.out.println("Requête échouée, une des ressources de la requête n'est pas présente dans notre dictionnaire.");
				break;
			}

		}

		//TODO quand c'est vide afficher qq chose
		if(queryResult.size()!=0){
			System.out.println("Résultat de la requête : "+queryResult);
		}else{
			System.out.println("Résultat de la requête : AUCUN RESULTAT");
		}

		//System.out.println("\nVariables to project : ");

		/*TODO
		// Utilisation d'une classe anonyme
		query.getTupleExpr().visit(new AbstractQueryModelVisitor<RuntimeException>() {

			public void meet(Projection projection) {
				//On récupère uniquement la variable (v0 par exemple) sur laquelle on va projeter
				//On fait uniquement get(0) car on suppose que l'on a des requêtes avec comme variable le sujet
				System.out.println(projection.getProjectionElemList().getElements().get(0).getTargetName());

				//TODO on prend l'ensemble des triplets trouvés et on projete uniquement sur les TargetName que l'on veut (v0)
			}
		});*/
	
	}

	/**
	 * Entrée du programme
	 */
	public static void main(String[] args) throws Exception {

		//On déclare notre instance de classe dictionnaire
		Dictionnaire dictionary = new Dictionnaire();

		//On déclarer notre instance de classe index
		Index index = new Index();



		long start = System.nanoTime();
		parseData(dictionary, index);
		long stop = System.nanoTime();
		long duree = (stop - start)/1000000;
		System.out.println("Durée de l'exécution de notre fonction parseData() : "+duree);

		parseQueries(dictionary, index);

	}

	// ========================================================================

	//TODO rajouter String queryFile et String dataFile dans les paramètre de la fonction pour pouvoir les passer en ligne de commande
	/**
	 * Traite chaque requête lue dans {@link #queryFile} avec {@link #processAQuery(ParsedQuery)}.
	 */
	private static void parseQueries(Dictionnaire dictionnaire, Index index) throws FileNotFoundException, IOException {
		/**
		 * Try-with-resources
		 * 
		 * @see <a href="https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html">Try-with-resources</a>
		 */
		/*
		 * On utilise un stream pour lire les lignes une par une, sans avoir à toutes les stocker
		 * entièrement dans une collection.
		 */
		try (Stream<String> lineStream = Files.lines(Paths.get(queryFile))) {
			SPARQLParser sparqlParser = new SPARQLParser();
			Iterator<String> lineIterator = lineStream.iterator();
			StringBuilder queryString = new StringBuilder();

			while (lineIterator.hasNext())
			/*
			 * On stocke plusieurs lignes jusqu'à ce que l'une d'entre elles se termine par un '}'
			 * On considère alors que c'est la fin d'une requête
			 */
			{
				String line = lineIterator.next();
				queryString.append(line);

				if (line.trim().endsWith("}")) {
					ParsedQuery query = sparqlParser.parseQuery(queryString.toString(), baseURI);
					processAQuery(query, dictionnaire, index); // Traitement de la requête, à adapter/réécrire pour votre programme

					queryString.setLength(0); // Reset le buffer de la requête en chaine vide
				}
			}
		}
	}

	//TODO rajouter String dataFile dans les paramètre de la fonction pour pouvoir les passer en ligne de commande
	/**
	 * Traite chaque triple lu dans {@link #dataFile} avec {@link MainRDFHandler}.
	 */
	private static void parseData(Dictionnaire dictionnaire, Index index) throws FileNotFoundException, IOException {

		try (Reader dataReader = new FileReader(dataFile)) {
			// On va parser des données au format ntriples
			RDFParser rdfParser = Rio.createParser(RDFFormat.NTRIPLES);

			//On crée notre classe handler
			MainRDFHandler handler = new MainRDFHandler();
			
			//A laquelle on affecte notre instance de dictionnaire et notre instance d'index pour qu'elle les remplisse
			handler.setDictionnaire(dictionnaire);
			handler.setIndex(index);

			// On utilise notre implémentation de handler
			rdfParser.setRDFHandler(handler);

			// Parsing et traitement de chaque triple par le handler
			rdfParser.parse(dataReader, baseURI);
		}
	}
}
