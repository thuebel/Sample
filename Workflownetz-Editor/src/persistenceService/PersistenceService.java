package persistenceService;

import petriNodes.Arc;
import petriNodes.PetriNode;
import petriNodes.Place;
import petriNodes.Transition;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import javax.xml.stream.XMLStreamException;

/**
 * Diese Klasse bietet zwei statische Methoden, mit deren Hilfe eine Netz
 * geladen bzw. gespeichert werden kann.
 *
 * @author Tobias Hübel, 5509840
 */
public class PersistenceService {

    /**
     * Diese Methode initialisiert den PNMLParser und ruf die Methode parse auf.
     * Dabei wird das Attribut nodeMap mit Werten gefüllt.
     *
     * @param file Die Datei, die geladen werden soll.
     * @return Ein Map-Objekt, das mit den in der Datei gefundenen Elementen
     * gefüllt ist.
     * @throws javax.xml.stream.XMLStreamException Exception wird geworfen,
     * falls die Datei nicht eingelesen werden kann.
     */
    public static Map<String, PetriNode> loadNodeObjects(File file) throws XMLStreamException {
        PNMLParser parser = new PNMLParser(file);
        parser.initParser();
        parser.parse();
        return parser.getNodeMap();
    }

    /**
     * Diese Methode initialisiert einen PNMLWriter und schreibt die übergebenen
     * Elemente in die Datei.
     *
     * @param nodes Das Netz in Form eines Map-Objekts.
     * @param file Die Datei, in die geschrieben werden soll und die gespeichert
     * werden soll.
     * @throws java.lang.Exception Wirft eine Exception, falls das Speichern
     * nicht erfolgreich war.
     */
    public static void savePNML(Map<String, PetriNode> nodes, File file) throws Exception {
        try {
            PNMLWriter writer = new PNMLWriter(file);
            writer.startXMLDocument();
            nodes.forEach((key, value) -> {
                Transition trans;
                Place place;
                switch (value.getClass().getSimpleName()) {
                    case "Transition":
                        trans = (Transition) value;
                        writer.addTransition(
                                trans.getId(),
                                trans.getLabel().getText(),
                                String.valueOf(trans.getPosX()),
                                String.valueOf(trans.getPosY()));
                        break;
                    case "Place":
                        place = (Place) value;
                        writer.addPlace(
                                place.getId(),
                                place.getLabel().getText(),
                                String.valueOf(place.getPosX()),
                                String.valueOf(place.getPosY()),
                                place.getMarkingAsString());
                }
            });
            nodes.forEach((key, value) -> {
                Iterator<Arc> iterator = value.getNextArcs().iterator();
                while (iterator.hasNext()) {
                    Arc arc = iterator.next();
                    writer.addArc(
                            arc.getArcId(),
                            arc.getStartNode().getId(),
                            arc.getEndNode().getId());
                }
            });
            writer.finishXMLDocument();
        } catch (Exception e) {
            throw e;
        }

    }
}
