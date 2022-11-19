package qengine.program;

import org.eclipse.rdf4j.model.Statement;
import java.util.HashMap;

public class Dictionnaire{

    //Dictionnaire qui associe à chaque String sa valeur en entier
    public HashMap<String, Integer> dictionaryStringToInteger = new HashMap<String, Integer>();

    //Dictionnaire qui associe à chaque entier sa valeur String
	public HashMap<Integer, String> dictionaryIntegerToString = new HashMap<Integer, String>();

    //Constructeur par défault
    public Dictionnaire(){}

    //Fonction qui remplit les deux dictionnaires
    public void addTriplet(Statement st){
        
        int cle = 0;
        
        //On ajoute le sujet au dictionnaire
        if(dictionaryStringToInteger.get(String.valueOf(st.getSubject())) == null){ //Si la valeur String n'a pas déjà été ajouté au dico
            dictionaryIntegerToString.put(cle, String.valueOf(st.getSubject()));
            dictionaryStringToInteger.put(String.valueOf(st.getSubject()), cle);
			cle++;
		}

		//On ajoute le prédicat au dictionnaire
        if(dictionaryStringToInteger.get(String.valueOf(st.getPredicate())) == null){ //Si la valeur String n'a pas déjà été ajouté au dico
            dictionaryIntegerToString.put(cle, String.valueOf(st.getPredicate()));
            dictionaryStringToInteger.put(String.valueOf(st.getPredicate()), cle);
			cle++;
		}

		//On ajoute l'objet au dictionnaire
		if(dictionaryStringToInteger.get(String.valueOf(st.getObject())) == null){ //Si la valeur String n'a pas déjà été ajouté au dico
            dictionaryIntegerToString.put(cle, String.valueOf(st.getObject()));
            dictionaryStringToInteger.put(String.valueOf(st.getObject()), cle);
			cle++;
		}

		System.out.println("\nEtat actuel de nos dictionnaires : ");
		System.out.println(dictionaryIntegerToString.toString());
        System.out.println(dictionaryStringToInteger.toString());
    }



}