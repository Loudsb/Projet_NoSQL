package qengine.program;

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
	
	public HashMap<Integer, String> dictionary = new HashMap<Integer, String>();
	
	public int cle = 0;

	@Override
	public void handleStatement(Statement st) {
		System.out.println("\n" + st.getSubject() + "\t " + st.getPredicate() + "\t " + st.getObject());
		
		if(!dictionary.containsValue(String.valueOf(st.getSubject()))){
			dictionary.put(cle, String.valueOf(st.getSubject()));
			cle++;
		}
		
		if(!dictionary.containsValue(String.valueOf(st.getPredicate()))){
			dictionary.put(cle, String.valueOf(st.getPredicate()));
			cle++;
		}
		
		if(!dictionary.containsValue(String.valueOf(st.getObject()))){
			dictionary.put(cle, String.valueOf(st.getObject()));
			cle++;
		}
		
		//System.out.println(dictionary.toString());
		
	};
}