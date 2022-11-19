package qengine.program;

import java.util.ArrayList;
import java.util.HashMap;

public class Index {

    //Nos 6 indexs
	private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> indexSPO = new HashMap<>();
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> indexSOP = new HashMap<>();
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> indexPSO = new HashMap<>();
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> indexPOS = new HashMap<>();
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> indexOSP = new HashMap<>();
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> indexOPS = new HashMap<>();

    //Constructeur par défaut re-écrit
    public Index(){}

    //Méthode qui permet d'ajouter le triplet (permuté de la bonne manière) dans chaque index
    public void addTripletIndexes(ArrayList<Integer> triplet){
        
        //Le triplet arrive sous forme : SPO
        //On crée les permutations pour les autres indexs
        ArrayList<Integer> tripletPermutation = new ArrayList<>();

        addTriplet(indexSPO, triplet);

        tripletPermutation = new ArrayList<>();
        tripletPermutation.add(triplet.get(0));
        tripletPermutation.add(triplet.get(2));
        tripletPermutation.add(triplet.get(1));
        addTriplet(indexSOP, tripletPermutation);

        tripletPermutation = new ArrayList<>();
        tripletPermutation.add(triplet.get(1));
        tripletPermutation.add(triplet.get(0));
        tripletPermutation.add(triplet.get(2));
        addTriplet(indexPSO, tripletPermutation);

        tripletPermutation = new ArrayList<>();
        tripletPermutation.add(triplet.get(1));
        tripletPermutation.add(triplet.get(2));
        tripletPermutation.add(triplet.get(0));
        addTriplet(indexPOS, tripletPermutation);

        tripletPermutation = new ArrayList<>();
        tripletPermutation.add(triplet.get(2));
        tripletPermutation.add(triplet.get(0));
        tripletPermutation.add(triplet.get(1));
        addTriplet(indexOSP, tripletPermutation);

        tripletPermutation = new ArrayList<>();
        tripletPermutation.add(triplet.get(2));
        tripletPermutation.add(triplet.get(1));
        tripletPermutation.add(triplet.get(0));
        addTriplet(indexOPS, tripletPermutation);

    }

    //Méthode qui ajoute à l'index (passé en paramètre) le triplet (passé en paramètre)
    public void addTriplet(HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> index, ArrayList<Integer> triplet){
        
        //Si les entiers n'existent pas déjà dans les index, ces variables ne sont pas ré-affectées donc nouvelle hashMapArrayList et nouvelle arrayList
        HashMap<Integer, ArrayList<Integer>> hashMapArrayList = new HashMap<>();
        ArrayList<Integer> arrayList = new ArrayList<>();

        //Si le premier élément du triplet existe déjà, je veux récupérer la hashmap associé à cette clé/cet entier
        if(!(index.get(triplet.get(0)) == null)){
            hashMapArrayList = index.get(triplet.get(0));
        }

        //Si le deuxième élément du triplet existe déjà dans l'hashmap récupérée, je veux récupérer l'arraylist associée à cette clé/cet entier
        if(!(hashMapArrayList.get(triplet.get(1)) == null)){
            arrayList = hashMapArrayList.get(triplet.get(1));
        }

        //J'ajoute dans l'ArrayList le dernier élément du triplet
        //Soit je n'ai rien récupéré et on ajoute le troisième élément du triplet dans une liste vide
        //Soit j'ajoute le troisième élément du triplet à la suite dans l'arraylist récupérée
        arrayList.add(triplet.get(2));

        //J'ajoute à l'hashMapArrayList le deuxième élément du triplet ainsi que l'arrayList
        hashMapArrayList.put(triplet.get(1), arrayList);

        //On ajoute à notre index la permutation du triplet
        //Soit il n'existe pas et on l'ajoute soit on le met à jour
        index.put(triplet.get(0), hashMapArrayList);

        //On affiche l'état de l'index qui vient d'être mis à jour avec le nouveau tuple
        System.out.println(index);

    }
    
}
