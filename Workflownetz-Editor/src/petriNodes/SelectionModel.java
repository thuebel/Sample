package petriNodes;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Diese Klasse dient als Singelton, um eine Mehrfachauswahl von elementen im
 * Workflownetz zu ermöglichen.
 *
 * @author Tobias Hübel, 5509840
 */
public class SelectionModel {

    /**
     * instance enthält die einzige Instanz dieser Klasse.
     */
    private static SelectionModel instance = null;

    /**
     * selectedNodes beinhaltet alle selektierten Knoten.
     */
    private final Set<PetriNode> selectedNodes = new HashSet<>();

    /**
     * Privater Konstruktor. Wird in de rMEthode getInstance aufgerufen, falls
     * noch keine Instanz besteht.
     */
    private SelectionModel() {
    }

    /**
     * Gibt die einzige Instanz dieser Klasse zurück.
     *
     * @return einzige Instanz dieser Klasse.
     */
    public static SelectionModel getInstance() {
        if (instance == null) {
            instance = new SelectionModel();
        }
        return instance;
    }

    /**
     * Gibt das Attribut selectedNodes zurück.
     *
     * @return Das Attribut selectedNodes.
     */
    public Set<PetriNode> getSelectedNodes() {
        return selectedNodes;
    }

    /**
     * Diese Methode fügt dem Set-Objekt selectedNodes ein PetriNode-Objekt
     * hinzu. Außerdem wird das übergebene Objekt als selektiert (isSelected)
     * gesetzt.
     *
     * @param petriNode Das PetriNode-Objekt, das hinzugefügt werden soll.
     */
    public void addPetriNode(PetriNode petriNode) {
        selectedNodes.add(petriNode);
        petriNode.isSelected(true);
        petriNode.setFill();
    }

    /**
     * Falls das übergebne PEtriNode-Objekt im Set selectedNodes enthalten ist,
     * wird es entfernt.
     *
     * @param petriNode Das zu entfernende PetriNode-Objekt.
     */
    public void removePetriNode(PetriNode petriNode) {
        if (selectedNodes.contains(petriNode)) {
            selectedNodes.remove(petriNode);
        }
    }

    /**
     * Diese Methode hebt die Selektion auf und leer das Set-Objekt
     * selectedNodes.
     */
    public void clearSelectedNodes() {
        Iterator<PetriNode> iterator = selectedNodes.iterator();
        while (iterator.hasNext()) {
            PetriNode currentNode = iterator.next();
            currentNode.isSelected(false);
            currentNode.setFill();
        }
        selectedNodes.clear();
    }
}
