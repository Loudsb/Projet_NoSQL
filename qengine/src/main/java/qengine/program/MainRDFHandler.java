package qengine.program;

import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.algebra.In;
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

	@Override
	public void handleStatement(Statement st) {

		//On appelle la fonction d'ajout du triplet au dictionnaire
		dictionnaire.addTriplet(st);

		//ArrayList qui stocke les clés correspondant aux String du triplet dont on s'occupe
		ArrayList<Integer> tripletInteger = new ArrayList<>();
		
		//On récupère dans le dictionnaire String vers Integer, les entiers correspondants aux String du triplet que l'on traite
		tripletInteger.add(dictionnaire.dictionaryStringToInteger.get(st.getSubject().toString()));
		tripletInteger.add(dictionnaire.dictionaryStringToInteger.get(st.getPredicate().toString()));
		tripletInteger.add(dictionnaire.dictionaryStringToInteger.get(st.getObject().toString()));

		//On appelle la fonction d'ajout du triplet aux indexs (les triplets sont permutés dans la classe Index)
		index.addTripletIndexes(tripletInteger);
	
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

}