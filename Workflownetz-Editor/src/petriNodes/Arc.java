package petriNodes;

import controller.ArcContextMenu;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 * In der Klasse Arc sind die Kanten zwischen Transitionen und Stellen
 * implementiert. Sie hat einen Start- und Endknoten (PetriNode), eine id und
 * weitere Attribute, die die Koordinaten der Kante oder die Interaktion mit dem
 * Benutzer betreffen.
 *
 * @author Tobias Hübel, 5509840
 */
public class Arc extends Path {

    /**
     * Die id der Kante.
     */
    private final String id;

    /**
     * Der Startknoten der Kante.
     */
    private final PetriNode startNode;

    /**
     * Der Endknoten der Kante.
     */
    private final PetriNode endNode;

    /**
     * moveToStart dient dazu den Anfangspunkt der Kante festzulegen.
     */
    private final MoveTo moveToStart;

    /**
     * lineToEnd dient dazu den Endpunkt der Kante festzulegen.
     */
    private final LineTo lineToEnd;

    /**
     * moveToArrowHead dient dazu den Punkt der Pfeilspitze festzulegen.
     */
    private final MoveTo moveToArrowHead;

    /**
     * lineToLeftArrowEdge dient dazu den Punkt des linken Arms des Pfeils
     * festzulegen.
     */
    private final LineTo lineToLeftArrowEdge;

    /**
     * lineToRightArrowEdge dient dazu den Punkt des rechten Arms des Pfeils
     * festzulegen.
     */
    private final LineTo lineToRightArrowEdge;

    /**
     * collisionX beinhaltet die X-Koordinate des Schnittpunktes der Kante mit
     * dem Rand des Endknotens.
     */
    private final DoubleProperty collisionX = new SimpleDoubleProperty();

    /**
     * collisionY beinhaltet die Y-Koordinate des Schnittpunktes der Kante mit
     * dem Rand des Endknotens.
     */
    private final DoubleProperty collisionY = new SimpleDoubleProperty();

    /**
     * Das Kontext-Menü, das bei einem Klick der rechten Maustaste
     * (ContextMenuRequest) auf die Kante, geöffnet wird.
     */
    private final ArcContextMenu contextMenu;

    /**
     * Der Konstruktor initialisiert die Klassenvariablen, die Property-Bindings
     * der Pfad-Objekte (LineTo und MoveTo) und das Kontextmenü.
     *
     * @param id Die id der Kante
     * @param startNode Der Startknoten
     * @param endNode Der Endknoten
     */
    public Arc(String id, PetriNode startNode, PetriNode endNode) {
        this.id = id;
        this.startNode = startNode;
        this.endNode = endNode;
        this.moveToArrowHead = new MoveTo();
        this.lineToLeftArrowEdge = new LineTo();
        this.lineToRightArrowEdge = new LineTo();
        this.moveToStart = new MoveTo();
        this.lineToEnd = new LineTo();
        initPathWithBindings();
        this.setStrokeWidth(7);
        this.setStroke(Color.LIGHTGRAY);
        this.contextMenu = new ArcContextMenu(this);
        this.setOnContextMenuRequested((ContextMenuEvent event) -> {
            contextMenu.show(Arc.this, event.getScreenX(), event.getScreenY());
        });
    }

    /**
     * Diese Methode bindet die Start- und Endkoordinaten der Linie mit den
     * Mittelpunkt- Koordinaten der entsprechenden Stelle bzw. Transition und
     * die Koordinaten des MoveTo- Objekts moveToArrowHead mit den
     * Klassenvariablen collisionX und collisionY.
     */
    private void initPathWithBindings() {
        ArcChangeListener arcChangeListener = new ArcChangeListener();

        this.moveToStart.xProperty().bind(this.startNode.getCenterPosX());
        this.moveToStart.xProperty().addListener(arcChangeListener.startNodeXChangeListener);

        this.moveToStart.yProperty().bind(this.startNode.getCenterPosY());
        this.moveToStart.yProperty().addListener(arcChangeListener.startNodeYChangeListener);

        this.lineToEnd.xProperty().bind(this.endNode.getCenterPosX());
        this.lineToEnd.xProperty().addListener(arcChangeListener.endNodeXChangeListener);

        this.lineToEnd.yProperty().bind(this.endNode.getCenterPosY());
        this.lineToEnd.yProperty().addListener(arcChangeListener.endNodeYChangeListener);

        computeCollisionPoint(moveToStart.getX(), moveToStart.getY(), lineToEnd.getX(), lineToEnd.getY());

        this.moveToArrowHead.xProperty().bind(collisionX);
        this.moveToArrowHead.yProperty().bind(collisionY);

        this.getElements().addAll(moveToStart, lineToEnd,
                moveToArrowHead, lineToLeftArrowEdge,
                moveToArrowHead, lineToRightArrowEdge);
    }

    /**
     * Diese Methode berechnet den Schnittpunkt der Kante mit dem Rand des
     * Endknotens.
     *
     * @param startX X-Koordinate des Startknoten
     * @param startY Y-Koordinate des Startknoten
     * @param endX X-Koordinate des Endknoten
     * @param endY Y-Koordinate des Endknoten
     */
    private void computeCollisionPoint(Double startX, Double startY, Double endX, Double endY) {
        Double deltaX = Math.abs(startX - endX);
        Double deltaY = Math.abs(startY - endY);
        Double xChange;
        Double yChange;
        Orientation orientation = null;
        /**
         * Es muss unterschieden werden, ob ein Schnittpunkt mit einem Kreis
         * (Stelle) oder Quadrat (Transition) berechnet werden muss. Weiter muss
         * unterschieden werden aus welcher Richtung (Orientierung) die Linie
         * den Endknoten schneidet. Mithilfe der Strahlensätze können dann die
         * Werte der Koordinaten bestimmt werden.
         */
        if (endNode instanceof Transition) {
            // Beim Rechteck müssen acht Fälle unterschieden werden.
            Double halfHeight = ((Rectangle) endNode.getShape()).getHeight() / 2;
            xChange = (halfHeight * deltaX) / deltaY;
            yChange = (halfHeight * deltaY) / deltaX;

            // Fall 1: Linie kommt aus Nordwest
            if (startX <= endX && startY <= endY) {
                orientation = Orientation.NW;
                /**
                 * 1a: deltaX gößer -> das bedeutet der Schnittpunkt liegt auf
                 * der oberen Hälfte der linken Seite des Rechtecks
                 */
                if (deltaX > deltaY) {
                    collisionX.set(endX - halfHeight);
                    collisionY.set(endY - yChange);
                } /**
                 * 1b: deltaY größer -> das bedeutet der Schnittpunkt liegt auf
                 * der linken Hälfte der oberen Seite des Rechtecks
                 */
                else {
                    collisionX.set(endX - xChange);
                    collisionY.set(endY - halfHeight);
                }
            } //Fall 2: Linie kommt aus Nordost
            else if (startX > endX && startY <= endY) {
                orientation = Orientation.NO;
                // 2a: deltaY größer -> rechte Hälfte der oberen Seite
                if (deltaX < deltaY) {
                    collisionX.set(endX + xChange);
                    collisionY.set(endY - halfHeight);
                } // 2b: deltaX größer -> obere Hälfte der rechten Seite
                else {
                    collisionX.set(endX + halfHeight);
                    collisionY.set(endY - yChange);
                }
            } // Fall 3: Linie kommt aus Südost
            else if (startX > endX && startY > endY) {
                orientation = Orientation.SO;
                // 3a: deltaX größer -> untere Hälfte der rechten Seite
                if (deltaX > deltaY) {
                    collisionX.set(endX + halfHeight);
                    collisionY.set(endY + yChange);
                } // 3b: deltaY größer -> rechte Hälfte der unteren Seite
                else {
                    collisionX.set(endX + xChange);
                    collisionY.set(endY + halfHeight);
                }
            } // Fall 4: Linie kommt aus Südwest
            else if (startX <= endX && startY > endY) {
                orientation = Orientation.SW;
                // deltaY größer -> linke Hälfte der unteren Seite
                if (deltaX < deltaY) {
                    collisionX.set(endX - xChange);
                    collisionY.set(endY + halfHeight);
                } // deltaX größer -> untere Hälfte der linken Seite
                else {
                    collisionX.set(endX - halfHeight);
                    collisionY.set(endY + yChange);
                }
            }
        } else if (endNode instanceof Place) {
            // Beim Kreis müssen nur vier Fälle unterschieden werden.
            Double arcLength = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
            Double radius = ((Circle) endNode.getShape()).getRadius();
            xChange = (radius * deltaX) / arcLength;
            yChange = (radius * deltaY) / arcLength;
            // Fall 1: Linie kommt aus Nordwest
            if (startX <= endX && startY <= endY) {
                orientation = Orientation.NW;
                collisionX.set(endX - xChange);
                collisionY.set(endY - yChange);
            } // Fall 2: Linie kommt aus Nordost
            else if (startX > endX && startY <= endY) {
                orientation = Orientation.NO;
                collisionX.set(endX + xChange);
                collisionY.set(endY - yChange);
            } // Fall 3: Linie kommt aus Südost
            else if (startX > endX && startY > endY) {
                orientation = Orientation.SO;
                collisionX.set(endX + xChange);
                collisionY.set(endY + yChange);
            } // Fall 4: Linie kommt aus Südwest
            else if (startX <= endX && startY > endY) {
                orientation = Orientation.SW;
                collisionX.set(endX - xChange);
                collisionY.set(endY + yChange);
            }
        }
        resetArrowValues(deltaX, deltaY, orientation);

    }

    /**
     * Bei Änderung des Schnittpunktes und des Winkels, in dem eine Kante auf
     * einen Knoten trifft müssen die Koordinaten der Pfeilspitze neu angepasst
     * werden. Diese werden in den LineTo-Objekten lineToLeftArrowEdge und
     * lineToRightArrowEdge festgehalten.
     *
     * @param deltaX Die Differenz zwischen den Start-X- und End-X-Koordinaten
     * der Kante.
     * @param deltaY Die Differenz zwischen den Start-Y- und End-Y-Koordinaten
     * der Kante.
     * @param orientation Die Richtung aus der die Kante auf den Endknoten
     * trifft. Dargestellt in Himmelsrichtungen.
     */
    private void resetArrowValues(Double deltaX, Double deltaY, Orientation orientation) {

        double arrowAngle = Math.toRadians(25.0);
        double arrowLength = 10.0;
        double angle = Math.atan2(deltaY, deltaX);
        double x1;
        double y1;
        double x2;
        double y2;

        /**
         * Je nach Orientierung der Kante, müssen die Koordinaten der
         * Pfeilspitze unterschiedlich berechnet werden.
         *
         */
        switch (orientation) {
            case NW:
                x1 = Math.cos(angle + arrowAngle + Math.toRadians(180.0)) * arrowLength + collisionX.getValue();
                y1 = Math.sin(angle + arrowAngle + Math.toRadians(180.0)) * arrowLength + collisionY.getValue();
                x2 = Math.cos(angle - arrowAngle + Math.toRadians(180.0)) * arrowLength + collisionX.getValue();
                y2 = Math.sin(angle - arrowAngle + Math.toRadians(180.0)) * arrowLength + collisionY.getValue();
                break;
            case NO:
                x1 = Math.cos(-angle + arrowAngle) * arrowLength + collisionX.getValue();
                y1 = Math.sin(-angle + arrowAngle) * arrowLength + collisionY.getValue();
                x2 = Math.cos(-angle - arrowAngle) * arrowLength + collisionX.getValue();
                y2 = Math.sin(-angle - arrowAngle) * arrowLength + collisionY.getValue();
                break;
            case SO:
                x1 = Math.cos(angle + arrowAngle) * arrowLength + collisionX.getValue();
                y1 = Math.sin(angle + arrowAngle) * arrowLength + collisionY.getValue();
                x2 = Math.cos(angle - arrowAngle) * arrowLength + collisionX.getValue();
                y2 = Math.sin(angle - arrowAngle) * arrowLength + collisionY.getValue();
                break;
            case SW:
                x1 = Math.cos(Math.toRadians(180) - angle + arrowAngle) * arrowLength + collisionX.getValue();
                y1 = Math.sin(Math.toRadians(180) - angle + arrowAngle) * arrowLength + collisionY.getValue();
                x2 = Math.cos(Math.toRadians(180) - angle - arrowAngle) * arrowLength + collisionX.getValue();
                y2 = Math.sin(Math.toRadians(180) - angle - arrowAngle) * arrowLength + collisionY.getValue();
                break;
            default:
                x1 = collisionX.getValue();
                y1 = collisionY.getValue();
                x2 = collisionX.getValue();
                y2 = collisionY.getValue();
        }

        lineToLeftArrowEdge.setX(x1);
        lineToLeftArrowEdge.setY(y1);
        lineToRightArrowEdge.setX(x2);
        lineToRightArrowEdge.setY(y2);
    }

    /**
     * Diese Methode gibt den Startknoten der Kante zurück.
     *
     * @return Startknoten der Kante
     */
    public PetriNode getStartNode() {
        return startNode;
    }

    /**
     * Diese Methode gibt den Endknoten der Kante zurück.
     *
     * @return Endknoten der Kante.
     */
    public PetriNode getEndNode() {
        return endNode;
    }

    /**
     * Diese Methode gibt die id des Arc-Objekts zurück.
     *
     * @return Id des Arc-Objekts
     */
    public String getArcId() {
        return id;
    }

    /**
     * Diese Methode gibt das Attribut contextMenu zurück.
     *
     * @return Das Attribut contextMenu
     */
    public ArcContextMenu getContextMenu() {
        return contextMenu;
    }

    /**
     * In dieser Klassen werden EventListener implementiert, die innerhalb der
     * Methoden verwendet werden.
     */
    class ArcChangeListener {

        /**
         * Durch diesen Listener wird bei Änderung der X-Koordinate des
         * Endknoten der Wert der Schnittpunktekoordinaten neu berechnet.
         */
        ChangeListener<Number> endNodeXChangeListener = (ObservableValue<? extends Number> observable, Number oldEndNodeX, Number newEndNodeX) -> {
            computeCollisionPoint(
                    moveToStart.getX(),
                    moveToStart.getY(),
                    (Double) newEndNodeX,
                    lineToEnd.getY());
        };
        /**
         * Durch diesen Listener wird bei Änderung der Y-Koordinate des
         * Endknoten der Wert der Schnittpunktekoordinaten neu berechnet.
         */
        ChangeListener<Number> endNodeYChangeListener = (ObservableValue<? extends Number> observable, Number oldEndNodeY, Number newEndNodeY) -> {
            computeCollisionPoint(
                    moveToStart.getX(),
                    moveToStart.getY(),
                    lineToEnd.getX(),
                    (Double) newEndNodeY);
        };
        /**
         * Durch diesen Listener wird bei Änderung der X-Koordinate des
         * Startknoten der Wert der Schnittpunktekoordinaten neu berechnet.
         */
        ChangeListener<Number> startNodeXChangeListener = (ObservableValue<? extends Number> observable, Number oldStartNodeX, Number newStartNodeX) -> {
            computeCollisionPoint(
                    (Double) newStartNodeX,
                    moveToStart.getY(),
                    lineToEnd.getX(),
                    lineToEnd.getY());
        };
        /**
         * Durch diesen Listener wird bei Änderung der Y-Koordinate des
         * Startknoten der Wert der Schnittpunktekoordinaten neu berechnet.
         */
        ChangeListener<Number> startNodeYChangeListener = (ObservableValue<? extends Number> observable, Number oldStartNodeY, Number newStartNodeY) -> {
            computeCollisionPoint(
                    moveToStart.getX(),
                    (Double) newStartNodeY,
                    lineToEnd.getX(),
                    lineToEnd.getY());
        };

    }
}

/**
 * Enum, um die Orientierung der Kante in Form der Himmelsrichtungen
 * darzustellen.
 *
 * @author Tobias Hübel, 5509840
 */
enum Orientation {
    NW, NO, SO, SW
}
