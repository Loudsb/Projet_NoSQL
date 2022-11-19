package qengine.program;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
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
	static final String dataFile = workingDir + "sample_data.nt";

	// ========================================================================

	/**
	 * Méthode utilisée ici lors du parsing de requête sparql pour agir sur l'objet obtenu.
	 */
	public static void processAQuery(ParsedQuery query) {
		List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());

		/*Pour pouvoir traiter les n branches de notre étoile
		for(int i=0; i<patterns.size(); i++){
			System.out.println("Pattern number " + (i+1) + ": " + patterns.get(i));
			//TODO vérifier que c'est que dans ce sens
			System.out.println(patterns.get(i).getSubjectVar());
			System.out.println(patterns.get(i).getPredicateVar());
			System.out.println(patterns.get(i).getObjectVar());

			//ON prend chaque requete puis on fait l'intersection, on fait l'intersection à chaque fois (n fois)
			//ON fait que ce type de requête
			
			//System.out.println("Object of the pattern : " + patterns.get(i).getObjectVar().getValue());

			//TODO recherche patterns 0 puis 1 puis n (filtre au fur et à mesure)

			//On a besoin de note dictionnaire et de nos 6 index (les obtenir en paramètre et les déclarer dans le main)
		}

		System.out.println("Variables to project : ");

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

		parseData(dictionary, index);
		parseQueries();

	}

	// ========================================================================

	/**
	 * Traite chaque requête lue dans {@link #queryFile} avec {@link #processAQuery(ParsedQuery)}.
	 */
	private static void parseQueries() throws FileNotFoundException, IOException {
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
					processAQuery(query); // Traitement de la requête, à adapter/réécrire pour votre programme

					queryString.setLength(0); // Reset le buffer de la requête en chaine vide
				}
			}
		}
	}

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
