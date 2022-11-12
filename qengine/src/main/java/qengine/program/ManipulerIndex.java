package qengine.program;

import java.util.ArrayList;
import java.util.HashMap;

//Classe qui contient des méthodes de manipulation d'index
public final class ManipulerIndex {

    //On ne veut pouvoir créer qu'une instance de cette classe utilitaire
    private static ManipulerIndex instance;

    private ManipulerIndex(){}

    public static ManipulerIndex getInstance() {
        if (instance == null) {
            instance = new ManipulerIndex();
        }
        return instance;
    }

    //Méthode qui met un triplet dans l'index demandé
    public void addTriplet(ArrayList<Integer> triplet, HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> index){
        
        //Si ils n'existent pas, ces variables ne sont pas ré-affectées donc nouvelle hashMapArrayList et nouvelle arrayList
        HashMap<Integer, ArrayList<Integer>> hashMapArrayList = new HashMap<>();
        ArrayList<Integer> arrayList = new ArrayList<>();

        //Je récupère le HashMap etc associé au premier entier du triplet
        for(Integer key : index.keySet()) {
            if(key == triplet.get(0)){
                //Si on entre ici c'est que le premier entier du triplet du triplet existe dans l'index (première colonne)
                hashMapArrayList = index.get(key);
            }
        }

        //Je récupère l'ArrayList associée au deuxième élément du triplet
        for(Integer key : hashMapArrayList.keySet()) {
            if(key == triplet.get(1)){
                //Si on entre ici c'est que le deuxième entier du triplet du triplet existe dans l'index (deuxième colonne)
                arrayList = hashMapArrayList.get(key);
            }
        }

        //J'ajoute dans l'ArrayList le dernier élément du triplet
        //Soit ça met le premier élément, soit ça ajoute à la suite
        arrayList.add(triplet.get(2));

        //J'ajoute à l'hashMapArrayList le deuxième + l'arrayList
        //Soit ça ajoute pour la première fois soit ça remplace l'ancienne occurence
        hashMapArrayList.put(triplet.get(1), arrayList);

        //On ajoute à notre index la permutation du triplet
        //Soit il n'existe pas et on l'ajoute soit on le met à jour
        index.put(triplet.get(0), hashMapArrayList);

        //On affiche l'état de l'index qui vient d'être mis à jour avec le nouveau tuple
        System.out.println(index);

    }  

}
