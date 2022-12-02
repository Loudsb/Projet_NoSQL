package qengine.program;

import java.util.ArrayList;
import java.util.HashMap;

public class Index {

    // Nos 6 indexs
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> indexSPO = new HashMap<>();
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> indexSOP = new HashMap<>();
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> indexPSO = new HashMap<>();
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> indexPOS = new HashMap<>();
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> indexOSP = new HashMap<>();
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> indexOPS = new HashMap<>();

    // Constructeur par défaut re-écrit
    public Index() {
    }

    // Méthode qui permet d'ajouter le triplet (permuté de la bonne manière) dans
    // chaque index
    public void addTripletIndexes(ArrayList<Integer> triplet) {

        // Le triplet arrive sous forme : SPO
        // On crée les permutations pour les autres indexs
        ArrayList<Integer> tripletPermutation = new ArrayList<>();

        // System.out.println("\nEtat de l'index SPO: ");
        addTriplet(indexSPO, triplet);

        tripletPermutation = new ArrayList<>();
        tripletPermutation.add(triplet.get(0));
        tripletPermutation.add(triplet.get(2));
        tripletPermutation.add(triplet.get(1));
        // System.out.println("\nEtat de l'index SOP: ");
        addTriplet(indexSOP, tripletPermutation);

        tripletPermutation = new ArrayList<>();
        tripletPermutation.add(triplet.get(1));
        tripletPermutation.add(triplet.get(0));
        tripletPermutation.add(triplet.get(2));
        // System.out.println("\nEtat de l'index PSO: ");
        addTriplet(indexPSO, tripletPermutation);

        tripletPermutation = new ArrayList<>();
        tripletPermutation.add(triplet.get(1));
        tripletPermutation.add(triplet.get(2));
        tripletPermutation.add(triplet.get(0));
        // System.out.println("\nEtat de l'index POS: ");
        addTriplet(indexPOS, tripletPermutation);

        tripletPermutation = new ArrayList<>();
        tripletPermutation.add(triplet.get(2));
        tripletPermutation.add(triplet.get(0));
        tripletPermutation.add(triplet.get(1));
        // System.out.println("\nEtat de l'index OSP: ");
        addTriplet(indexOSP, tripletPermutation);

        tripletPermutation = new ArrayList<>();
        tripletPermutation.add(triplet.get(2));
        tripletPermutation.add(triplet.get(1));
        tripletPermutation.add(triplet.get(0));
        // System.out.println("\nEtat de l'index OPS: ");
        addTriplet(indexOPS, tripletPermutation);

    }

    // Méthode qui ajoute à l'index (passé en paramètre) le triplet (passé en
    // paramètre)
    public void addTriplet(HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> index, ArrayList<Integer> triplet) {

        // Si les entiers n'existent pas déjà dans les index, ces variables ne sont pas
        // ré-affectées donc nouvelle hashMapArrayList et nouvelle arrayList
        HashMap<Integer, ArrayList<Integer>> hashMapArrayList = new HashMap<>();
        ArrayList<Integer> arrayList = new ArrayList<>();

        // Si le sujet du triplet est déjà présent dans l'index
        if (!(index.get(triplet.get(0)) == null)) {
            // on récupère l'hashmap correspondante
            hashMapArrayList = index.get(triplet.get(0));

            // Si sujet suivi de prédicat existe
            if (!(hashMapArrayList.get(triplet.get(1)) == null)) {
                // on récupère l'arraylist d'objets
                arrayList = hashMapArrayList.get(triplet.get(1));

                // si l'objet n'est pas déjà là on l'ajoute
                if (!arrayList.contains(triplet.get(2))) {
                    arrayList.add(triplet.get(2));
                    hashMapArrayList.put(triplet.get(1), arrayList);
                    index.put(triplet.get(0), hashMapArrayList);
                } else {
                    // on fait rien, triplet déjà dans l'index
                }

            } else {
                // cas ou le sujet existe dejà mais pas le predicat donc pas l'objet
                arrayList = new ArrayList<>();
                arrayList.add(triplet.get(2));
                hashMapArrayList.put(triplet.get(1), arrayList);
                index.put(triplet.get(0), hashMapArrayList);
            }

        } else {
            // le triplet n'est pas encore dans l'index donc on doit l'ajouter en entier
            // directement
            arrayList = new ArrayList<>();
            arrayList.add(triplet.get(2));
            hashMapArrayList.put(triplet.get(1), arrayList);
            index.put(triplet.get(0), hashMapArrayList);

        }
    }

    public ArrayList<Integer> findSubjectWithPOSindex(ArrayList<Integer> predicatAndObject) {

        HashMap<Integer, ArrayList<Integer>> Pfound = indexPOS.get(predicatAndObject.get(0));
        ArrayList<Integer> listeOfSubject = Pfound.get(predicatAndObject.get(1));
        if (listeOfSubject == null) {
            listeOfSubject = new ArrayList<>();
        }

        // on retourne une copie pour éviter de donner une référence de l'index et donc
        // la possibilité de le modifier directement (et de passer 28 heures à débugger)
        return new ArrayList<>(listeOfSubject);

    }

    public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getIndexSPO() {
        return indexSPO;
    }

    public void setIndexSPO(HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> indexSPO) {
        this.indexSPO = indexSPO;
    }

    public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getIndexSOP() {
        return indexSOP;
    }

    public void setIndexSOP(HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> indexSOP) {
        this.indexSOP = indexSOP;
    }

    public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getIndexPSO() {
        return indexPSO;
    }

    public void setIndexPSO(HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> indexPSO) {
        this.indexPSO = indexPSO;
    }

    public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getIndexPOS() {
        return indexPOS;
    }

    public void setIndexPOS(HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> indexPOS) {
        this.indexPOS = indexPOS;
    }

    public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getIndexOSP() {
        return indexOSP;
    }

    public void setIndexOSP(HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> indexOSP) {
        this.indexOSP = indexOSP;
    }

    public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getIndexOPS() {
        return indexOPS;
    }

    public void setIndexOPS(HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> indexOPS) {
        this.indexOPS = indexOPS;
    }

}
