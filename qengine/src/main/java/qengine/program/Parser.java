package qengine.program;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.apache.jena.ext.com.google.common.base.Stopwatch;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class Parser {

	static final String baseURI = null;

	// Variable qui va contenir le chemin vers le fichier contenant les requêtes
	// sparql
	static String queryFile;

	// Variable qui va contenir le chemin vers le fichier contenant des données rdf
	static String dataFile;

	// Variable booléenne qui permet d'activer ou non la vérification de la
	// correction et complétude du système en utilisant Jena comme un oracle
	static boolean JenaVerification;

	// Variable qui compte le nombre de requête
	static int nbRequest = 0;

	// Variable qui stock le temps qu'on a mis à lire les requêtes
	static long totalTimeR = 0;

	// variable qui stock le temps qu'on a mis à évaluer nos requêtes
	static long stopwatchQuery = 0;

	// Variable qui stock combien de temps on a mis à lire notre fichier de données
	static long totalTimeData = 0;

	// Méthode qui traite chaque requête lue dans {@link #queryFile} avec {@link
	// #processAQuery(ParsedQuery)}.
	public ArrayList<String> parseQueries(Dictionnaire dictionnaire, Index index)
			throws FileNotFoundException, IOException {
		/**
		 * Try-with-resources
		 * 
		 * @see <a href=
		 *      "https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html">Try-with-resources</a>
		 */

		/*
		 * On utilise un stream pour lire les lignes une par une, sans avoir à toutes
		 * les stocker
		 * entièrement dans une collection.
		 */
		// Chrono pour calculer le temps qu'on met à lire notre fichier de données
		Stopwatch stopwatchData = Stopwatch.createStarted();

		// Les deux ArrayList qui vont contenir nos résultats et ceux de Jena, pour que
		// l'on compare
		ArrayList<ArrayList<String>> listResultprocessAQuery = new ArrayList<>();
		ArrayList<String> ourResultsForCSV = new ArrayList<>();
		ArrayList<String> ourResults = new ArrayList<>();
		ArrayList<String> jenaResults = new ArrayList<>();

		try (Stream<String> lineStream = Files.lines(Paths.get(queryFile))) {

			long endTimeData = stopwatchData.elapsed(TimeUnit.MICROSECONDS);
			totalTimeData += endTimeData;

			SPARQLParser sparqlParser = new SPARQLParser();
			Iterator<String> lineIterator = lineStream.iterator();
			StringBuilder queryString = new StringBuilder();

			// Chrono pour calculer le temps qu'on met à remplir les requêtes
			Stopwatch stopwatchQueryFill = Stopwatch.createStarted();

			while (lineIterator.hasNext())
			/*
			 * On stocke plusieurs lignes jusqu'à ce que l'une d'entre elles se termine par
			 * un '}'
			 * On considère alors que c'est la fin d'une requête
			 */
			{
				String line = lineIterator.next();
				queryString.append(line);

				if (line.trim().endsWith("}")) {
					ParsedQuery query = sparqlParser.parseQuery(queryString.toString(), baseURI);

					// Chrono pour calculer le temps qu'on met à évaluer les requêtes
					Stopwatch startStopwatchQuery = Stopwatch.createStarted();

					// Traitement de la requête
					listResultprocessAQuery = processAQuery(query, dictionnaire, index);
					ourResultsForCSV.addAll(listResultprocessAQuery.get(0));
					ourResults = listResultprocessAQuery.get(1);

					long endStopwatchQuery = startStopwatchQuery.elapsed(TimeUnit.MILLISECONDS);
					stopwatchQuery += endStopwatchQuery;

					// Si l'option Jena est activée on vérifie l'exactitude des résultats renvoyés
					// par notre système d'évaluation par rapport à ceux renvoyés par Jena
					if (JenaVerification) {
						jenaResults = processAQueryWithJena(queryString.toString());

						if(jenaResults.size()==0 && ourResults.size()!=0){
							System.out.println("NOUS AVONS TROUVE DES RESULTATS ET JENA AUCUN WTF");
						}

						else if(jenaResults.size()!=0 && ourResults.size()==0){
							System.out.println("JENA A TROUVE ET NOUS 0 WTF");
						}

						else if(jenaResults.size()==0 && ourResults.size()==0){
							System.out.println("ACCORDDDDDDDDDDDDDDD 0 RESULTAT");
						}

						else if (ourResults.containsAll(jenaResults) && jenaResults.containsAll(ourResults)) {
							System.out.println("Résultats de notre système validés !\n\n");
						} else {
							System.out.println("Résultats de notre système : FAUX\n\n");
						}
					}

					queryString.setLength(0); // Reset le buffer de la requête en chaine vide

					// Pour compter combien il y a de requêtes au total:
					nbRequest++;
				}
			}

			long endTimeRequest = stopwatchQueryFill.elapsed(TimeUnit.MILLISECONDS);
			totalTimeR += endTimeRequest;
		}

		return ourResultsForCSV;

	}

	/**
	 * Traite chaque triple lu dans {@link #dataFile} avec {@link MainRDFHandler}.
	 */
	public void parseData(Dictionnaire dictionnaire, Index index) throws FileNotFoundException, IOException {

		try (Reader dataReader = new FileReader(dataFile)) {
			// On va parser des données au format ntriples
			RDFParser rdfParser = Rio.createParser(RDFFormat.NTRIPLES);

			// On crée notre classe handler
			MainRDFHandler handler = new MainRDFHandler();

			// A laquelle on affecte notre instance de dictionnaire et notre instance
			// d'index pour qu'elle les remplisse
			handler.setDictionnaire(dictionnaire);
			handler.setIndex(index);

			// On utilise notre implémentation de handler
			rdfParser.setRDFHandler(handler);

			// Parsing et traitement de chaque triple par le handler
			rdfParser.parse(dataReader, baseURI);
		}
	}

	/**
	 * Méthode utilisée ici lors du parsing de requête sparql pour agir sur l'objet
	 * obtenu.
	 * Elle retourne une liste qui contient tous les résulats des requêtes pour
	 * pouvoir les mettre dans le fichier CSV
	 */
	public static ArrayList<ArrayList<String>> processAQuery(ParsedQuery query, Dictionnaire dictionnaire, Index index) {

		//ArrayList pour les résultats
		ArrayList<ArrayList<String>> twoLists = new ArrayList<>();
		ArrayList<String> OurSystemResults = new ArrayList<>();
		ArrayList<String> OurSystemResultsForCSV = new ArrayList<>();

		System.out.println("Requête : "+query);

		List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());

		//Liste qui va contenir le résultat final
		ArrayList<Integer> queryResult = new ArrayList<>();

		// Pour pouvoir traiter les n branches de notre étoile on itère sur les n
		// patterns
		for (int i = 0; i < patterns.size(); i++) {

			// On récupère dans notre dictionnaire les entiers correspondant au P et au O
			ArrayList<Integer> listOfPredicatAndObject = dictionnaire.queryStringToInt(
					patterns.get(i).getPredicateVar().getValue().toString(),
					patterns.get(i).getObjectVar().getValue().toString());

			// Si notre dictionnaire connaît toutes les ressources de la requête, on lance
			// la recherche dans les index, sinon on indique qu'un élément de la requête n'est pas dans les ressources
			if (listOfPredicatAndObject.size() != 0) {

				// ArrayList qui récupère la recherche pour la branche i de notre étoile
				ArrayList<Integer> listOfSubjects = index.findSubjectWithPOSindex(listOfPredicatAndObject);
				System.out.println("LISTEEEEE SUJETTTS :"+listOfSubjects.toString());

				// Dans le cas où c'est la première branche
				if (i == 0) {

					//Si la liste des sujets trouvés est vide, c'est que la requête ne donnera aucun résultat
					if (listOfSubjects.size() == 0) {
						System.out.println("Branche numéro 0 : Aucun résultat trouvé dans l'index pour cette branche. On arrête les recherches");
						queryResult = new ArrayList<>();
						break;
					} else {
						// On remplit notre liste de résultat, qui était jusque là vide et on affiche le
						// résultat pour la branche 0
						queryResult = listOfSubjects;
						System.out.println("Résultat branche numéro 0 : "+ queryResult);
					}

				} else { // C'est une branche autre que la branche 0, je dois faire l'intersection entre
							// la branche précédente et la branche courante de mon étoile

					if (listOfSubjects == null) { // L'index n'a retourné aucun résultat pour la branche i
						System.out.println("Branche numéro "+i+" : Aucun résultat trouvé dans l'index pour cette branche. On arrête les recherches");
						
						// On vide notre arrayList vide 
						queryResult = new ArrayList<>();
						break;

					} else {
						// L'index a retourné au moins un résultat pour la branche i

						// On fait l'intersection entre queryResult et listOfSubjects
						queryResult.retainAll(listOfSubjects);

						//Si l'intersection n'est pas vide, on continue
						if (queryResult.size() != 0) {
							System.out.println("Intersection branches "+(i-1)+" et "+i+" donne : " +queryResult);
						} else {
							System.out.println("Intersection branches "+(i-1)+" et "+i+" ne donne aucun résultat");
							break;
						}
					}
				}

			} else {
				// Dans le cas où une ressource de la requête n'est pas enregistrée dans notre dictionnaire

				//On vide la liste de résultats et on explique que l'on stoppe les recherches
				queryResult = new ArrayList<>();
				System.out.println("Requête échouée, une des ressources de la requête n'est pas présente dans notre dictionnaire.");
				break;
			}
		}

		if (queryResult.size() != 0) {
			for (Integer resultat : queryResult) {
				System.out.println("Résultat requête (notre système) : "
				+ dictionnaire.getDictionaryIntegerToString().get(resultat));
				OurSystemResultsForCSV.add(dictionnaire.getDictionaryIntegerToString().get(resultat));
				OurSystemResults.add(dictionnaire.getDictionaryIntegerToString().get(resultat));
			}
			OurSystemResultsForCSV.add("");
		} else {
			System.out.println("Résultat requête (notre système) : AUCUN RESULTAT");
			OurSystemResultsForCSV.add("empty");
		}

		twoLists.add(OurSystemResultsForCSV);
		twoLists.add(OurSystemResults);

		return twoLists;
	}

	// Méthode qui permet d'utiliser Jena comme un Oracle pour avoir le résultat à
	// la requête passée en paramètre
	public static ArrayList<String> processAQueryWithJena(String queryToExecute) {

		ArrayList<String> JenaResults = new ArrayList<>();

		// On crée un modèle RDF
		Model model = ModelFactory.createDefaultModel();
		// On lit et on ajoute les ressources d'un fichier .csv, dans notre modèle
		model.read(dataFile);

		String queryString = queryToExecute.toString();
		Query query = QueryFactory.create(queryString);
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				JenaResults.add(soln.getResource("?v0").toString());
				System.out.println("Résultat requête (Jena) : "+soln.getResource("?v0").toString());
			}
		}
		return JenaResults;
	}

	public static int getnbRequest() {
		return nbRequest;
	}

	public static long getTotalTimeR() {
		return totalTimeR;
	}

	public static long getStopWatchQuery() {
		return stopwatchQuery;
	}

	public static long getTotalTimeData() {
		return totalTimeData;
	}

}
