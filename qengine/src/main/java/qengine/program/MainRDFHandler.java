package qengine.program;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.jena.ext.com.google.common.base.Stopwatch;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;

/**
 * Le RDFHandler intervient lors du parsing de données et permet d'appliquer un traitement pour chaque élément lu par le parseur.
 * 
 * <p>
 * Ce qui servira surtout dans le programme est la méthode {@link #handleStatement(Statement)} qui va permettre de traiter chaque triple lu.
 * </p>
 * <p>
 * À adapter/réécrire selon vos traitements.
 * </p>
 */
public final class MainRDFHandler extends AbstractRDFHandler {

	public Dictionnaire dictionnaire;

	public Index index;

	// Variables pour stocker le temps qu'on met à créer les dicos et les indexs
	public static long totalTimeDico = 0;
	public static long totalTimeIndex = 0;
	public static int cptTriplet = 0;

	@Override
	public void handleStatement(Statement st) {

		// Chrono pour calculer le temps qu'on met à remplir le dictionnaire
		//Stopwatch stopwatchDico = Stopwatch.createStarted();

		//On appelle la fonction d'ajout du triplet au dictionnaire
		dictionnaire.addTriplet(st);

		//long endStopwatchDico = stopwatchDico.elapsed(TimeUnit.MILLISECONDS);
		//totalTimeDico += endStopwatchDico;

		//ArrayList qui stocke les clés correspondant aux String du triplet dont on s'occupe
		ArrayList<Integer> tripletInteger = new ArrayList<>();
		
		//On récupère dans le dictionnaire String vers Integer, les entiers correspondants aux String du triplet que l'on traite
		tripletInteger.add(dictionnaire.dictionaryStringToInteger.get(st.getSubject().toString()));
		tripletInteger.add(dictionnaire.dictionaryStringToInteger.get(st.getPredicate().toString()));
		tripletInteger.add(dictionnaire.dictionaryStringToInteger.get(st.getObject().toString()));
		/*if(tripletInteger.get(0)==null || tripletInteger.get(1)==null || tripletInteger.get(2)==null){
			System.out.println("ERREUR valeur manquante dictionnaire");
			System.exit(1);
		}*/

		// Chrono pour calculer le temps qu'on met à créer les indexs
		//Stopwatch stopwatchIndex = Stopwatch.createStarted();
		
		//On appelle la fonction d'ajout du triplet aux indexs (les triplets sont permutés dans la classe Index)
		index.addTripletIndexes(tripletInteger);
		
		//long endStopwatchIndex = stopwatchIndex.elapsed(TimeUnit.MILLISECONDS);
		//totalTimeIndex += endStopwatchIndex;

		// On incrémente le compteur de Triplets
		cptTriplet++;

	};

	public Dictionnaire getDictionnaire() {
		return dictionnaire;
	}

	public void setDictionnaire(Dictionnaire dictionnaire) {
		this.dictionnaire = dictionnaire;
	}

	public Index getIndex() {
		return index;
	}

	public void setIndex(Index index) {
		this.index = index;
	}

	public static long getTotalTimeDico(){
		return totalTimeDico;
	}

	public static long getTotalTimeIndex(){
		return totalTimeIndex;
	}

	public static int getCptTriplet(){
		return cptTriplet;
	}

}