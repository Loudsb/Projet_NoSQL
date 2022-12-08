package qengine.program;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
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

	static Model model = null;

	static int nbErreurs = 0;

	static final String baseURI = null;

	// Variable qui va contenir le chemin vers le dossier contenant les requêtes
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
	static long totalTimeReadQuery = 0;

	// variable qui stock le temps qu'on a mis à évaluer nos requêtes
	static long totalTimeEvaluateQuery = 0;

	// Variable qui stock combien de temps on a mis à lire notre fichier de données
	static long totalTimeData = 0;

	//Variable qui va stocker le nombre de résultats des requêtes
	static ArrayList<Integer> nombreResultatsRequete = new ArrayList<>();

	//Variable qui va stocker le nombre de conditions dans les requêtes
	static ArrayList<Integer> nombreConditionsRequete = new ArrayList<>();

	// Méthode qui traite chaque requête lue dans {@link #queryFile} avec {@link
	// #processAQuery(ParsedQuery)}.
	public ArrayList<String> parseQueries(Dictionnaire dictionnaire, Index index)
			throws FileNotFoundException, IOException {
	
		// ArrayList qui va recevoir les résultats de la méthode processAQuery
		ArrayList<ArrayList<String>> listResultprocessAQuery = new ArrayList<>();
		// ArrayList qui va contenir les résultats de requêtes, structurés pour le csv
		ArrayList<String> ourResultsForCSV = new ArrayList<>();

		// ArrayList qui va contenir les résultats de notre système
		ArrayList<String> ourResults = new ArrayList<>();
		// ArrayList qui va contenir les résultats de Jena
		ArrayList<String> jenaResults = new ArrayList<>();

		//On va accéder aux fichiers du dossier de requêtes
		File files = new File(queryFile);
	
		// Boucle qui permet de lire les fichiers dans un dossier
		for (File file : files.listFiles()) {

			try (Stream<String> lineStream = Files.lines(file.toPath())) {

				SPARQLParser sparqlParser = new SPARQLParser();
				Iterator<String> lineIterator = lineStream.iterator();
				StringBuilder queryString = new StringBuilder();

				// Chrono pour calculer le temps qu'on met à lire les requêtes
				Stopwatch stopwatchReadQuery = Stopwatch.createStarted();

				while (lineIterator.hasNext())
				/*
				 * On stocke plusieurs lignes jusqu'à ce que l'une d'entre elles se termine par
				 * un '}'
				 * On considère alors que c'est la fin d'une requête
				 */
				{
					ourResults = new ArrayList<>();
					jenaResults = new ArrayList<>();

					String line = lineIterator.next();
					queryString.append(line);

					if (line.trim().endsWith("}")) {

						//System.out.println("\nRequête numéro " + (nbRequest + 1));

						ParsedQuery query = sparqlParser.parseQuery(queryString.toString(), baseURI);

						// Chrono pour calculer le temps qu'on met à évaluer les requêtes
						Stopwatch stopwatchEvaluateQuery = Stopwatch.createStarted();

						// Traitement de la requête
						listResultprocessAQuery = processAQuery(query, dictionnaire, index);
						ourResultsForCSV.addAll(listResultprocessAQuery.get(0));
						ourResults.addAll(listResultprocessAQuery.get(1));

						if(ourResults.size() <= nombreResultatsRequete.size()){
							nombreResultatsRequete.set(ourResults.size(), nombreResultatsRequete.get(ourResults.size())+1);
						}else{
							System.out.println("Une requête dépasse le nombre de résultats maximum autorisé pour le stockage, ces résultats sont tout de même sauvés dans le .csv");
						}

						if (ourResults.size() != 0) {
							//System.out.println("Résultats de notre système : ");
							//System.out.println(ourResults);
						} else {
							//System.out.println("Résultats de notre système : aucun");
						}

						long endStopwatchEvaluateQuery = stopwatchEvaluateQuery.elapsed(TimeUnit.MILLISECONDS);
						totalTimeEvaluateQuery += endStopwatchEvaluateQuery;

						// Si l'option Jena est activée on vérifie l'exactitude des résultats renvoyés
						// par notre système d'évaluation par rapport à ceux renvoyés par Jena
						if (JenaVerification) {

							// On récupère le résultat de la requête, executée avec Jena (donc fiable)
							jenaResults = processAQueryWithJena(queryString.toString());

							if (jenaResults.size() != 0) {
								System.out.println("Résultats de Jena : ");
								System.out.println(jenaResults);
							} else {
								System.out.println("Résultats de Jena : aucun");
							}

							if (jenaResults.size() == 0 && ourResults.size() != 0) {
								System.out.println("Résultats trouvés alors que Jena n'en retourne aucun, ERREUR !");
								nbErreurs++;
							}

							else if (jenaResults.size() != 0 && ourResults.size() == 0) {
								System.out.println("Aucun résultat trouvé mais Jena en retourne, ERREUR !");
								nbErreurs++;
							}

							else if (jenaResults.size() == 0 && ourResults.size() == 0) {
								System.out.println("Aucun résultat trouvé par notre système et celui de Jena, OK !\n");
							}

							else if (ourResults.containsAll(jenaResults) && jenaResults.containsAll(ourResults)) {
								System.out.println("Résultats de notre système validés !\n");
							} else {
								System.out.println("Résultats de notre système faux :\n\n");
								nbErreurs++;
							}
						}

						queryString.setLength(0); // Reset le buffer de la requête en chaine vide

						// Pour compter combien il y a de requêtes au total:
						nbRequest++;
					}
				}

				totalTimeReadQuery = stopwatchReadQuery.elapsed(TimeUnit.MILLISECONDS);
			}

		}
		// Si la vérification de Jena est activée, on vérifie que l'on eut exactement
		// tous les mêmes résultats
		if (JenaVerification && nbErreurs == 0) {
			System.out.println("*****");
			System.out.println("****");
			System.out.println("***");
			System.out.println("**");
			System.out.println("*");
			System.out.println("Tous les résultats retournés par notre système sont identiques à ceux de Jena !");
			System.out.println("*");
			System.out.println("**");
			System.out.println("***");
			System.out.println("****");
			System.out.println("*****");
		} else if (JenaVerification && nbErreurs != 0) {
			System.out.println("*****");
			System.out.println("****");
			System.out.println("***");
			System.out.println("**");
			System.out.println("*");
			System.out
					.println("Tous les résultats retournés par notre système NE SONT PAS identiques à ceux de Jena...");
			System.out.println("*");
			System.out.println("**");
			System.out.println("***");
			System.out.println("****");
			System.out.println("*****");
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

			// Chrono pour calculer le temps qu'on met à lire notre fichier de données
			Stopwatch stopwatchData = Stopwatch.createStarted();

			// Parsing et traitement de chaque triple par le handler
			rdfParser.parse(dataReader, baseURI);

			// On arrête le chrono
			totalTimeData = stopwatchData.elapsed(TimeUnit.MILLISECONDS);

		}
	}

	/**
	 * Méthode utilisée ici lors du parsing de requête sparql pour agir sur l'objet
	 * obtenu.
	 * Elle retourne une liste qui contient tous les résulats des requêtes pour
	 * pouvoir les mettre dans le fichier CSV
	 */
	public static ArrayList<ArrayList<String>> processAQuery(ParsedQuery query, Dictionnaire dictionnaire,
			Index index) {

		// ArrayList pour les résultats
		ArrayList<ArrayList<String>> twoLists = new ArrayList<>();
		ArrayList<String> OurSystemResults = new ArrayList<>();
		ArrayList<String> OurSystemResultsForCSV = new ArrayList<>();

		//System.out.println("Requête : "+query);

		List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());

		//On ajoute à la liste qui compte le nombre de conditions des requêtes, cette nouvelle requête
		if(patterns.size() <= nombreConditionsRequete.size()){
			nombreConditionsRequete.set(patterns.size(), nombreConditionsRequete.get(patterns.size())+1);
		}else{
			System.out.println("Une requête dépasse le nombre de conditions maximum autorisé pour le stockage, cette requête est tout de même exécuté mais son nombre de conditons n'est pas stocké");
		}

		// Liste qui va contenir le résultat final
		ArrayList<Integer> queryResult = new ArrayList<>();

		// Pour pouvoir traiter les n branches de notre étoile on itère sur les n
		// patterns
		for (int i = 0; i < patterns.size(); i++) {

			// On récupère dans notre dictionnaire les entiers correspondant au P et au O
			ArrayList<Integer> listOfPredicatAndObject = dictionnaire.queryStringToInt(
					patterns.get(i).getPredicateVar().getValue().toString(),
					patterns.get(i).getObjectVar().getValue().toString());

			if (listOfPredicatAndObject.size() == 2) { // Si notre dictionnaire connaît toutes les ressources de la
														// requête

				// ArrayList qui récupère la recherche pour la branche i de notre étoile
				ArrayList<Integer> listOfSubjects = index.findSubjectWithPOSindex(listOfPredicatAndObject);

				// Dans le cas où c'est la première branche
				if (i == 0) {

					// Si la liste des sujets trouvés est vide, c'est que la requête ne donnera
					// aucun résultat
					if (listOfSubjects.size() == 0) {
						queryResult = new ArrayList<>();
						break;
					} else {
						queryResult = listOfSubjects;
					}

				} else { // C'est une branche autre que la première branche 0, on doit faire
							// l'intersection entre
							// la branche précédente et la branche courante de mon étoile

					if (listOfSubjects.size() == 0) { // L'index n'a retourné aucun résultat pour la branche i

						queryResult = new ArrayList<>();
						break;

					} else { // L'index a retourné au moins un résultat pour la branche i

						// On fait l'intersection entre queryResult et listOfSubjects
						queryResult.retainAll(listOfSubjects);

						// Si l'intersection est vide, on sort de la boucle
						if (queryResult.size() == 0) {
							break;
						}
					}
				}

			} else {// Dans le cas où une ressource de la requête n'est pas enregistrée dans notre
					// dictionnaire

				queryResult = new ArrayList<>();
				System.out.println(
						"Requête échouée, une des ressources de la requête n'est pas présente dans notre dictionnaire.");
				break;
			}
		}

		// Si la requête a retourné un résultat
		if (queryResult.size() != 0) {
			for (Integer resultat : queryResult) {
				// System.out.println("Résultat requête (notre système) : "
				// + dictionnaire.getDictionaryIntegerToString().get(resultat));
				OurSystemResultsForCSV.add(dictionnaire.getDictionaryIntegerToString().get(resultat));
				OurSystemResults.add(dictionnaire.getDictionaryIntegerToString().get(resultat));
			}
			OurSystemResultsForCSV.add("");

		} else { // Si la requête n'a retourné aucun résultat
			// System.out.println("Résultat requête (notre système) : Aucun résultat");
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

		// On crée un modèle RDF, uniquement s'il n'existe pas déjà
		if (model == null) {
			model = ModelFactory.createDefaultModel();
			// On lit et on ajoute les ressources d'un fichier .csv, dans notre modèle
			model.read(dataFile);
		}

		String queryString = queryToExecute.toString();
		Query query = QueryFactory.create(queryString);
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				JenaResults.add(soln.getResource("?v0").toString());
				// System.out.println("Résultat requête (Jena) : " +
				// soln.getResource("?v0").toString());
			}
		}
		return JenaResults;
	}

	public static int getnbRequest() {
		return nbRequest;
	}

	public static long getTotalTimeReadQuery() {
		return totalTimeReadQuery;
	}

	public static long getTotalTimeEvaluateQuery() {
		return totalTimeEvaluateQuery;
	}

	public static long getTotalTimeData() {
		return totalTimeData;
	}

	public static void initializeArrays(){

		for(int i=0; i<100; i++){
			nombreResultatsRequete.add(0);
		}

		for(int i=0; i<10; i++){
			nombreConditionsRequete.add(0);
		}

	}

}
