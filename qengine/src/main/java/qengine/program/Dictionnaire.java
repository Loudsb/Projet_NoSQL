package qengine.program;

import org.eclipse.rdf4j.model.Statement;

import java.util.ArrayList;
import java.util.HashMap;

public class Dictionnaire {

    // Dictionnaire qui associe à chaque String sa valeur en entier
    public HashMap<String, Integer> dictionaryStringToInteger = new HashMap<String, Integer>();

    // Dictionnaire qui associe à chaque entier sa valeur String
    public HashMap<Integer, String> dictionaryIntegerToString = new HashMap<Integer, String>();

    public int cle = 0;

    // Constructeur par défault
    public Dictionnaire() {
    }

    // Fonction qui remplit les deux dictionnaires
    public void addTriplet(Statement st) {

        // On ajoute le sujet au dictionnaire
        if (dictionaryStringToInteger.get(String.valueOf(st.getSubject())) == null) { // Si la valeur String n'a pas
                                                                                      // déjà été ajouté au dico
            dictionaryIntegerToString.put(cle, String.valueOf(st.getSubject()));
            dictionaryStringToInteger.put(String.valueOf(st.getSubject()), cle);
            cle++;
        }

        // On ajoute le prédicat au dictionnaire
        if (dictionaryStringToInteger.get(String.valueOf(st.getPredicate())) == null) { // Si la valeur String n'a pas
                                                                                        // déjà été ajouté au dico
            dictionaryIntegerToString.put(cle, String.valueOf(st.getPredicate()));
            dictionaryStringToInteger.put(String.valueOf(st.getPredicate()), cle);
            cle++;
        }

        // On ajoute l'objet au dictionnaire
        if (dictionaryStringToInteger.get(String.valueOf(st.getObject())) == null) { // Si la valeur String n'a pas déjà
                                                                                     // été ajouté au dico
            dictionaryIntegerToString.put(cle, String.valueOf(st.getObject()));
            dictionaryStringToInteger.put(String.valueOf(st.getObject()), cle);
            cle++;
        }

    }

    // Fonction qui récupère les entiers correspondant a l'URI
    public ArrayList<Integer> queryStringToInt(String predicat, String object) {

        ArrayList<Integer> list = new ArrayList<Integer>();

        // On cherche l'entier correspondant au prédicat et à l'objet
        if (dictionaryStringToInteger.get(predicat) != null) {
            int predicatInt = dictionaryStringToInteger.get(predicat);
            list.add(predicatInt);
        } else {
            return new ArrayList<>();
        }

        if (dictionaryStringToInteger.get(object) != null) {
            int objectInt = dictionaryStringToInteger.get(object);
            list.add(objectInt);
        } else {
            return new ArrayList<>();
        }

        return list;
    }

    public HashMap<String, Integer> getDictionaryStringToInteger() {
        return dictionaryStringToInteger;
    }

    public void setDictionaryStringToInteger(HashMap<String, Integer> dictionaryStringToInteger) {
        this.dictionaryStringToInteger = dictionaryStringToInteger;
    }

    public HashMap<Integer, String> getDictionaryIntegerToString() {
        return dictionaryIntegerToString;
    }

    public void setDictionaryIntegerToString(HashMap<Integer, String> dictionaryIntegerToString) {
        this.dictionaryIntegerToString = dictionaryIntegerToString;
    }

    public int getCle() {
        return cle;
    }

    public void setCle(int cle) {
        this.cle = cle;
    }

}