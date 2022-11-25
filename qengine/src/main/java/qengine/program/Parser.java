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
import java.util.stream.Stream;
import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;


public class Parser {

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

	/**
	 * Traite chaque requête lue dans {@link #queryFile} avec {@link #processAQuery(ParsedQuery)}.
	 */
	public void parseQueries(Dictionnaire dictionnaire, Index index) throws FileNotFoundException, IOException {
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
	public void parseData(Dictionnaire dictionnaire, Index index) throws FileNotFoundException, IOException {

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

    /**
	 * Méthode utilisée ici lors du parsing de requête sparql pour agir sur l'objet obtenu.
	 */
	public static void processAQuery(ParsedQuery query, Dictionnaire dictionnaire, Index index) {

         //TODO faire des affichages + beau

		List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());

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
				ArrayList<Integer> listOfSubjects = index.findSubjectWithPOSindex(listOfPredicatAndObject);

 				//Dans le cas où c'est la première branche
				if(i==0){

					if(listOfSubjects == null){
						//System.out.println("Branche numéro 0 : Aucun résultat trouvé dans l'index pour cette branche. On arrête les recherches");
						//queryResult est vide à ce stade
						break;
					}else{
						//On remplit notre liste de résultat, qui était jusque là vide et on affiche le résultat pour la branche 0
						queryResult = listOfSubjects;
						//TODO tester si pas vide + affichages
						//System.out.println("Résultat branche numéro "+ i +" : "+ queryResult);
					}
					
				}else{ //C'est une branche autre que la branche 0, je dois faire l'intersection entre la branche précédente et la branche courante de mon étoile
					if(listOfSubjects == null){ //L'index n'a retourné aucun résultat pour la branche i
						//System.out.println("Branche numéro "+i+" : Aucun résultat trouvé dans l'index pour cette branche. On arrête les recherches");
						//On vide notre arrayList vide pour ensuite afficher dans le résultat de la requête, que cela n'a rien donné
						queryResult = new ArrayList<>();
						break;

					}else{
						//L'index a retourné au moins un résultat pour la branche i
						//On fait l'intersection en retenant dans queryResult, uniquement les éléments qui sont aussi dans listOfSubjects
						queryResult.retainAll(listOfSubjects);
						
						if(queryResult.size() !=0){
							//System.out.println("Intersection branches "+(i-1)+" et "+i+" donne : "+queryResult);
						}else{//Si la liste est vide suite à retainAll, alors cette requête ne donnera aucun résultat (queryResult est vidée à ce stade)
							//System.out.println("Intersection branches "+(i-1)+" et "+i+" ne donne aucun résultat");
							break;
						}
					}	
				}

			}else{
				//Dans le cas où une ressource de la requête n'est pas enregis	trée dans notre dictionnaire, on peut être sûrs que cela ne donnera aucun résultat dans les indexs
				//System.out.println("Requête échouée, une des ressources de la requête n'est pas présente dans notre dictionnaire.");
				break;
			}

		}

		if(queryResult.size()!=0){
			System.out.println("Résultat de la requête : "+queryResult);
		}else{
			System.out.println("Résultat de la requête : AUCUN RESULTAT");
		}
	
	}
    
}
