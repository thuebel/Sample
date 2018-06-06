package workflownetLogic;

import java.util.ArrayList;
import petriNodes.Arc;
import petriNodes.PetriNode;
import petriNodes.Place;
import petriNodes.Transition;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import petriNodes.SelectionModel;
import resources.MyMessages;

/**
 * Diese Klasse dient dazu Eigenschaften eines Workflownetzes zu überprüfen. Sie
 * beinhaltet Methoden, die überprüfen, ob eine Start- bzw. Endstelle existiert
 * und ob alle Elemente eines Netzes auf einem Pfad von der Startstelle zur
 * Endstelle liegen. Des weitern beinhaltet sie Methoden für die
 * Deadlock-Erkennung und Methoden, die für das Markieren bzw. das Demarkieren
 * nach einer Schaltung notwendig sind.
 *
 * @author Tobias Hübel, 5509840
 */
public class WorkflownetHandler {

    List<PetriNode> visitedNodes = new ArrayList<>();
    List<PetriNode> inProcessNodes = new ArrayList<>();
    
    
    /**
     * Die Startstelle eines Workflownetzes.
     */
    private Place startPlace;

    /**
     * Die Endstelle eines Workflownetzes.
     */
    private Place endPlace;

    /**
     * Information darüber, ob ein Workflownetz besteht.
     */
    private boolean isWorkflownet;

    /**
     * Eine Nachricht, die Infomationen über das Workflownetz beinhaltet.
     */
    private String message;

    /**
     * Diese Methode überprüft, ob das übergebene Objekt ein gültiges
     * Workflownetz repräsentiert. Bei einer Fehler-Erkennnung (z.B. keine
     * Start- oder Endstelle) wird im Attribut message eine entsprechende
     * Meldung festgehalten.
     *
     * @param petriNodes Das potentielle Workflownetz in Form eines
     * Collection-Objekts
     * @return true, falls ein Workflownetz besteht, false, falls nicht.
     */
    public boolean checkWorkflowStatus(Collection<PetriNode> petriNodes) {
        boolean hasOneStartNode = false;
        boolean hasOneEndNode = false;
        Iterator<PetriNode> iterator = petriNodes.iterator();
        while (iterator.hasNext()) {
            PetriNode currentNode = iterator.next();
            if (currentNode.getPrevArcs().isEmpty() && (currentNode instanceof Place)) {
                // Eine Stelle ohne eingehende Kanten ist ein Kandidat für eine
                // Startstelle.
                if (!hasOneStartNode) {
                    // Es wurde noch keine andere potentielle Startstelle gefunden
                    hasOneStartNode = true;
                    startPlace = (Place) currentNode;
                } else {
                    // Es wurde ein zwiter Knoten ohne eingehende Kanten gefunden
                    this.message = MyMessages.MORE_STARTPLACES;
                    return false;
                }
            }
            if (currentNode.getNextArcs().isEmpty() && (currentNode instanceof Place)) {
                // Eine Stelle ohne ausgehende Kanten ist ein Kandidat für eine
                // Endstelle.
                if (!hasOneEndNode) {
                    // Es wurde noch keine andere potentielle Endstelle gefunden
                    endPlace = (Place) currentNode;
                    hasOneEndNode = true;
                } else {
                    // Es wurde ein zwiter Knoten ohne ausgehende Kanten gefunden
                    this.message = MyMessages.MORE_ENDPLACES;
                    return false;
                }
            }
        }
        if (!hasOneEndNode) {
            // Es wurde keine Endstelle gefunden.
            this.message = MyMessages.NO_ENDPLACE;
            return false;
        }
        if (!hasOneStartNode) {
            // Es wurde keine Startstelle gefunden.
            this.message = MyMessages.NO_STARTPLACE;
            return false;
        }

        // Nachdem Start- und Endstelle gefunden wurde, muss überprüft werden,
        // ob alle Knoten auf einem Pfad zwischen Start- und Endstelle liegen.
        boolean allNodesReachable = allNodesReachable(petriNodes);
        if (!allNodesReachable) {
            this.message = MyMessages.NOT_REACHABLE;
            return false;
        }

        isWorkflownet = hasOneEndNode && hasOneStartNode && allNodesReachable;
        return isWorkflownet;
    }

    /**
     * Diese Methode überprüft, ob alle Knoten auf einem Pfad zwischen Start- und Endstelle
     * liegen.
     *
     * @param petriNodes Das potentielle Workflownetz in Form eines
     * Collection-Objekts.
     * @return true, falls alle Knoten erreichbar sind, false, falls nicht.
     */
    private boolean allNodesReachable(Collection<PetriNode> petriNodes) {
        resetReachableValues(petriNodes);
        forwardReachableCheck(startPlace);
        backwardReachableCheck(endPlace);
        Iterator<PetriNode> iterator = petriNodes.iterator();
        boolean allNodesReachable = true;
        while (iterator.hasNext()) {
            PetriNode currentNode = iterator.next();
            if (!currentNode.isForwardReachable() || !currentNode.isBackwardReachable()) {
                // Falls ein Knoten nicht erreichbar ist, wird false zurück gegeben
                allNodesReachable = false;
                return allNodesReachable;

            }
        }
        return allNodesReachable;
    }

    /**
     * Diese Methode setzt die forwardReachable-Werte aller Knoten, die vom Startknoten aus
     * erreichbar sind auf true. Auch wenn so alle Knoten erreicht werden
     * bedeutet das noch nicht, dass sie alle auf einem Pfad zwischen Start- und
     * Endknoten liegen.
     *
     * @param startNode Der Startknoten des potentiellen Workflownetzes.
     */
    private void forwardReachableCheck(PetriNode startNode) {
        startNode.setForwardReachable(true);
        Iterator<Arc> iterator = startNode.getNextArcs().iterator();
        while (iterator.hasNext()) {
            Arc arc = iterator.next();
            PetriNode nextNode = arc.getEndNode();
            if (!nextNode.isForwardReachable()) {
                // rekursiver Aufruf - entspricht einer Tiefensuche.
                forwardReachableCheck(nextNode);
            }
        }
    }

    /**
     * Diese Methode setzt die backwardReachable-Werte aller Knoten, die vom Endknoten aus
     * über invertierte Kanten erreichbar sind auf true. Wenn so alle Knoten
     * erreicht werden und die forwardReachable-Eigenschaft auch wahr ist,
     * liegen alle Knoten auf einem Pfad zwischen Start- und Endknoten.
     *
     * @param endNode Der Endknoten des potentiellen Workflownetzes.
     */
    private void backwardReachableCheck(PetriNode endNode) {
        endNode.setBackwardReachable(true);
        Iterator<Arc> iterator = endNode.getPrevArcs().iterator();
        while (iterator.hasNext()) {
            Arc arc = iterator.next();
            PetriNode prevNode = arc.getStartNode();
            if (!prevNode.isBackwardReachable()) {
                // rekursiver Aufruf - entspricht einer Tiefensuche.
                backwardReachableCheck(prevNode);
            }
        }
    }

    /**
     * Diese Methode setzt alle Reachable-Werte der übergebnen Knoten auf false
     *
     * @param petriNodes Das potentielle Workflownetz in Form eines
     * Collection-Objekts.
     */
    private void resetReachableValues(Collection<PetriNode> petriNodes) {
        Iterator<PetriNode> iterator = petriNodes.iterator();
        while (iterator.hasNext()) {
            PetriNode currentNode = iterator.next();
            currentNode.setBackwardReachable(false);
            currentNode.setForwardReachable(false);
        }
    }

    /**
     * Diese Methode gibt den Startknoten zurück, falls der Wert isWorkflownetz wahr ist.
     *
     * @return Den Startknote, falls isWorkflownetz wahr ist, sonst null.
     */
    public Place getStartPlace() {
        return isWorkflownet ? startPlace : null;
    }

    /**
     * Diese Methode gibt den Endknoten zurück, falls der Wert isWorkflownetz wahr ist.
     *
     * @return Den Endtknote, falls isWorkflownetz wahr ist, sonst null.
     */
    public Place getEndPlace() {
        return isWorkflownet ? endPlace : null;
    }

    /**
     * Diese Methode gibt das Attribut message zurück.
     *
     * @return Das Attribut message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Diese Methode gibt alle markierten Stellen, in Form eines HashSet-Objekts, zurück, die
     * im Workflownetz direkt vor der übergebnen Transition sind.
     *
     * @param transition Transition, zu der die markierten Vorgängerstellen
     * gefunden werden sollen
     * @return HashSet-Objekt mit markierten Vorgängerstellen.
     */
    public HashSet<Place> getMarkedPrevPlaces(Transition transition) {
        HashSet<Place> placesForUnset = new HashSet<>();
        Iterator<Arc> prevIterator = transition.getPrevArcs().iterator();
        while (prevIterator.hasNext()) {
            Arc arc = prevIterator.next();
            Place currentPrevPlace = (Place) arc.getStartNode();
            if (currentPrevPlace.isMarked()) {
                placesForUnset.add(currentPrevPlace);
            }
        }
        return placesForUnset;
    }

    /**
     * Diese Methode gibt alle unmarkierten Stellen, in Form eines HashSet-Objekts, zurück,
     * die im Workflownetz direkt nach der übergebnen Transition sind.
     *
     * @param transition Transition, zu der die unmarkierten Nachfolgerstellen
     * gefunden werden sollen
     * @return HashSet-Objekt mit unmarkierten Nachfolgerstellen.
     */
    public HashSet<Place> getUnmarkedNextPlaces(Transition transition) {
        HashSet<Place> placesForSet = new HashSet<>();
        Iterator<Arc> nextIterator = transition.getNextArcs().iterator();
        while (nextIterator.hasNext()) {
            Arc arc = nextIterator.next();
            Place currentNextPlace = (Place) arc.getEndNode();
            if (!currentNextPlace.isMarked()) {
                placesForSet.add(currentNextPlace);
            }
        }
        return placesForSet;
    }

    /**
     * Diese Methode überprüft, in einem Workflownetz, in dem keine Transition schaltbar ist,
     * ob ein Deadlock besteht. Die Überprüfung, ob ein Workflownetz besteht und
     * ob keine Transitionen schaltbar sind, übernimmt diese Methode nicht.
     *
     * @param petriNodes Das potentielle Workflownetz in Form eines
     * Collection-Objekts.
     */
    public void checkPossibleDeadlock(Collection<PetriNode> petriNodes) {
        boolean isDeadlock = false;
        Iterator<PetriNode> iterator = petriNodes.iterator();
        while (iterator.hasNext()) {
            PetriNode node = iterator.next();
            if (node instanceof Place) {
                Place place = (Place) node;
                if (place.isMarked() && !place.isEndPlace()) {
                    isDeadlock = true;
                    this.message = MyMessages.DEADLOCK;
                }
            }
        }
        if (!isDeadlock) {
            /**
             * An dieser Stelle gilt, dass ein Workflownetz besteht, keine
             * Transitionen schaltbar sind und nur die Endstelle markiert ist.
             * Das ist der reguläre Endzustand.
             */
            this.message = MyMessages.END_STATE;
        }
    }

    /**
     * Diese Methode gibt alle markierten Stellen des Workflownetzes in Form eines
     * HashSet-Objekts zurück.
     *
     * @param petriNodes Das potentielle Workflownetz in Form eines
     * Collection-Objekts.
     * @return Ein HashSet-Objekt mit allen markierten Stellen.
     */
    public HashSet<Place> getMarkedPlaces(Collection<PetriNode> petriNodes) {
        HashSet<Place> result = new HashSet<>();
        Iterator<PetriNode> iterator = petriNodes.iterator();
        while (iterator.hasNext()) {
            PetriNode currentNode = iterator.next();
            if (currentNode instanceof Place && ((Place) currentNode).isMarked()) {
                result.add((Place) currentNode);
            }
        }
        return result;
    }
    
    public boolean checkCycle(){
        if(isCycle(startPlace) == true){
            PetriNode circleConnector = inProcessNodes.get(inProcessNodes.size()-1);
            int idxStart = inProcessNodes.indexOf(circleConnector);
            int idxEnd = inProcessNodes.size()-1;
            List<PetriNode> circle = inProcessNodes.subList(idxStart, idxEnd);
            Iterator<PetriNode> iterator = circle.iterator();
            SelectionModel selectionModel = SelectionModel.getInstance();
            selectionModel.clearSelectedNodes();
            while(iterator.hasNext()){
                PetriNode currentNode = iterator.next();
                SelectionModel.getInstance().addPetriNode(currentNode);
            }
            return true;
        }
        else{
            message="Kein Kreis gefunden!";
            return false;
        }
    }
    
    public boolean isCycle(PetriNode petriNode){
        if(!visitedNodes.contains(petriNode)){
            visitedNodes.add(petriNode);
            inProcessNodes.add(petriNode);
            Iterator<Arc> iterator = petriNode.getNextArcs().iterator();
            while (iterator.hasNext()){
                Arc currentArc = iterator.next();
                PetriNode nextNode = currentArc.getEndNode();
                if(isCycle(nextNode)){
                    return true;
                }
                else if(inProcessNodes.contains(nextNode)){
                    inProcessNodes.add(nextNode);
                    return true;
                }
            }
            inProcessNodes.remove(petriNode);
        }
        return false;
    }
}
