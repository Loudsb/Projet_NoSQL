package qengine.program;

import java.util.ArrayList;
import java.util.HashMap;

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

	//Dictionnaire qui contient des clés Integer et valeur String. Chaque entier représente une ressource String contenue dans des triplets
	public HashMap<Integer, String> dictionary = new HashMap<Integer, String>();

	//Classe qui va effectuer des opération sur les 6 index nécessaires dans l'approche hexastore, créer les fichiers représentant ces index, permettre l'ajout/suppression de ressources
	public ManipulerIndex classeManipulationIndex = ManipulerIndex.getInstance();

	//Nos 6 indexs
	private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> indexSPO = new HashMap<>();
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> indexSOP = new HashMap<>();
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> indexPSO = new HashMap<>();
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> indexPOS = new HashMap<>();
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> indexOSP = new HashMap<>();
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> indexOPS = new HashMap<>();
		
	public int cle = 0;

	@Override
	public void handleStatement(Statement st) {

		//ArrayList qui stocke les clés correspondant aux String du triplet dont on s'occupe
		ArrayList<Integer> keyList = new ArrayList<>();

		System.out.println("\n" + st.getSubject() + "\t " + st.getPredicate() + "\t " + st.getObject());
		
		if(!dictionary.containsValue(String.valueOf(st.getSubject()))) {
			dictionary.put(cle, String.valueOf(st.getSubject()));
			keyList.add(cle);
			cle++;
		}else{
			//Pour récupérer la clé associée à la ressource qui a déjà été ajoutée
			for(HashMap.Entry<Integer, String> entry : dictionary.entrySet()) {
				if(entry.getValue().equals(String.valueOf(st.getSubject()))){
					//On ajoute cette clé au triplet
					keyList.add(entry.getKey());
				}
			}
		}
		
		if(!dictionary.containsValue(String.valueOf(st.getPredicate()))){
			dictionary.put(cle, String.valueOf(st.getPredicate()));
			keyList.add(cle);
			cle++;
		}else{
			//Pour récupérer la clé associée à la ressource qui a déjà été ajoutée
			for(HashMap.Entry<Integer, String> entry : dictionary.entrySet()) {
				if(entry.getValue().equals(String.valueOf(st.getPredicate()))){
					//On ajoute cette clé au triplet
					keyList.add(entry.getKey());
				}
			}
		}
		
		if(!dictionary.containsValue(String.valueOf(st.getObject()))){
			dictionary.put(cle, String.valueOf(st.getObject()));
			keyList.add(cle);
			cle++;
		}else{
			//Pour récupérer la clé associée à la ressource qui a déjà été ajoutée
			for(HashMap.Entry<Integer, String> entry : dictionary.entrySet()) {
				if(entry.getValue().equals(String.valueOf(st.getObject()))){
					//On ajoute cette clé au triplet
					keyList.add(entry.getKey());
				}
			}
		}

		System.out.println(dictionary.toString());
		System.out.println("________________________________________________________");

		

		ArrayList<Integer> keyListSOP = new ArrayList<>();
		keyListSOP.add(keyList.get(0));
		keyListSOP.add(keyList.get(2));
		keyListSOP.add(keyList.get(1));
		
		ArrayList<Integer> keyListPSO = new ArrayList<>();
		keyListPSO.add(keyList.get(1));
		keyListPSO.add(keyList.get(0));
		keyListPSO.add(keyList.get(2));

		ArrayList<Integer> keyListPOS = new ArrayList<>();
		keyListPOS.add(keyList.get(1));
		keyListPOS.add(keyList.get(2));
		keyListPOS.add(keyList.get(0));

		ArrayList<Integer> keyListOSP = new ArrayList<>();
		keyListOSP.add(keyList.get(2));
		keyListOSP.add(keyList.get(0));
		keyListOSP.add(keyList.get(1));
		
		ArrayList<Integer> keyListOPS = new ArrayList<>();
		keyListOPS.add(keyList.get(2));
		keyListOPS.add(keyList.get(1));
		keyListOPS.add(keyList.get(0));
		
		System.out.println("\nIndex SPO :");
		classeManipulationIndex.addTriplet(keyList, indexSPO);
		System.out.println("\nIndex SOP :");
		classeManipulationIndex.addTriplet(keyListSOP, indexSOP);
		System.out.println("\nIndex PSO :");
		classeManipulationIndex.addTriplet(keyListPSO, indexPSO);
		System.out.println("\nIndex POS :");
		classeManipulationIndex.addTriplet(keyListPOS, indexPOS);
		System.out.println("\nIndex OSP :");
		classeManipulationIndex.addTriplet(keyListOSP, indexOSP);
		System.out.println("\nIndex OPS :");
		classeManipulationIndex.addTriplet(keyListOPS, indexOPS);
		
	};
}