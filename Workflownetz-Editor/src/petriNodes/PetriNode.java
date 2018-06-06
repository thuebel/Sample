package petriNodes;

import controller.PetriNodeContextMenu;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

/**
 * Diese abstrakte Klasse dient als Oberklasse für Transitionen und Stellen. Sie
 * bündelt Eigenschaften und Methoden, die sowohl in der Klasse Place (Stelle)
 * und Transition benötigt bzw. in beiden implemntiert werden müssen.
 *
 * @author Tobias Hübel, 5509840;
 */
public abstract class PetriNode {

    /**
     * ID der Stelle bzw. Transition
     */
    private String id;

    /**
     * Ist true, falls dieses Objekt selektiert wurde. Sonst false.
     */
    protected boolean isSelected = false;

    /**
     * X-Koordinate des entsprechenden Shape-Objekts in Form einer
     * DoubleProperty.
     */
    protected DoubleProperty posX = new SimpleDoubleProperty();

    /**
     * X-Koordinate des entsprechenden Shape-Objekts in Form einer
     * DoubleProperty..
     */
    protected DoubleProperty posY = new SimpleDoubleProperty();

    /**
     * X-Koordinate des Mittelpunktes des entsprechenden Shape-Objektes in Form
     * einer DoubleProperty. Bei Stellen (Place-Objekten) entspricht der dieser
     * Property dem Wert der posX-property.
     */
    protected DoubleProperty centerPosX = new SimpleDoubleProperty();

    /**
     * Y-Koordinate des Mittelpunktes des entsprechenden Shape-Objektes in Form
     * einer DoubleProperty. Bei Stellen (Place-Objekten) entspricht der dieser
     * Property dem Wert der posY-property.
     */
    protected DoubleProperty centerPosY = new SimpleDoubleProperty();

    /**
     * Beinhaltet das Label, das zusätzlich zur Stelle bzw. Transition angezeigt
     * wird.
     */
    protected Text label;

    /**
     * Binhalten den Faktor, um den das Shape-Objekt in der Benutzeroberfläche
     * skaliert wird, in Form einer DoubleProperty.
     */
    protected DoubleProperty zoomFaktor = new SimpleDoubleProperty();

    /**
     * Ein Set mit allen ausgehnden Kanten.
     */
    protected Set<Arc> nextArcs = new HashSet<>();

    /**
     * Ein Set mit allen eingehenden Kanten.
     */
    protected Set<Arc> prevArcs = new HashSet<>();

    /**
     * Wird bei der Überprüfung der Workflownetzeigenschafen benötigt. Wenn
     * dieser Wert wahr ist, ist der Knoten im Netz vom Startknoten aus
     * erreichbar.
     */
    protected boolean forwardReachable = false;

    /**
     * Wird bei der Überprüfung der Workflownetzeigenschafen benötigt. Wenn
     * dieser Wert wahr ist, ist der Knoten im Netz vom Endknoten über
     * invertierte Kanten erreichbar.
     */
    protected boolean backwardReachable = false;

    /**
     * Das Kontext-Menü, das bei einem Klick der rechten Maustaste
     * (ContextMenuRequest) auf das entsprechende Shape-Objekt, geöffnet wird.
     */
    protected PetriNodeContextMenu contextMenu;

    /**
     * Diese Methode gibt das entsprechende Shape-Objekt zurück.
     *
     * @return Das entsprechende Shape-Objekt.
     * @see Place
     * @see Transition
     */
    public abstract Shape getShape();

    /**
     * Diese Methode gleicht die Werte der Shape-X- und -Y-Werte mit den posX-
     * und posY- Werten ab.
     *
     * @see Place
     * @see Transition
     */
    public abstract void setXAndY();

    /**
     * Je nach implementierender Instanz (Place, Transition) wir das Label
     * initial gesetzt und die Koordinaten des Text-Objektes an das Shape-Objekt
     * angepasst.
     *
     * @see Place
     * @see Transition
     */
    public abstract void setInitialLabel();

    /**
     * Diese Methode setzt den Wert der posX-Property.
     *
     * @param posX Wert der posX-Property.
     */
    public abstract void setPosX(double posX);

    /**
     * Diese Methode setzt den Wert der posY-Property.
     *
     * @param posY Wert der posX-Property.
     * @see Place
     * @see Transition
     */
    public abstract void setPosY(double posY);

    /**
     * Je nach Zustand des implementierenden Objekts wird die Farbe des
     * entsprechenden Shape-Objekts gesetzt.
     *
     * @see Place
     * @see Transition
     */
    public abstract void setFill();

    /**
     * Diese Methode setzt dieses Obejkt in den Default-Zustand zurück. Je nach
     * Typ der implementierenden Instanz werden unterschiedliche Eigenschaften
     * zurückgesetzt.
     *
     * @param workflownetContentPane Das Pane, das das Shape-Objekt beinhaltet.
     * @see Place
     * @see Transition
     */
    public abstract void resetStatusToDefault(Pane workflownetContentPane);

    /**
     * Dieser Konstruktor wird beim erstellen von Objekten aus einer Datei
     * geraus benötigt. Es werden lediglich die Klassenvariablen id und
     * contextMenu initialisiert.
     *
     * @param id Id der Instanz
     * @see Place
     * @see Transition
     */
    public PetriNode(String id) {
        this.id = id;
        this.contextMenu = new PetriNodeContextMenu(this);
        this.centerPosX.bindBidirectional(posX);
        this.centerPosY.bindBidirectional(posY);
    }

    /**
     * Dieser Konstruktor initialisiert die ID, die X- und Y-Koordinaten und das
     * Kontext-Menü.
     *
     * @param id Id der Instanz
     * @param posX Wert der posX-Property
     * @param posY Wert der posY-Property
     * @see Place
     * @see Transition
     */
    public PetriNode(String id, double posX, double posY) {
        this(id);
        this.posX.set(posX);
        this.posY.set(posY);
        this.centerPosX.set(posX);
        this.centerPosY.set(posY);
        this.contextMenu = new PetriNodeContextMenu(this);
    }

    /**
     * In dieser Methode werden die Klassenvariablen posX und posY gesetzt und
     * daraus die Klassenvariablen centerPosX und centerPosY berechnet. Dies
     * sind die Koordinaten des Mittelpunktes des entsprechenden Shape-Objektes
     * (Kreis bzw. Quadrat).
     */
    public void resetPosXAndY() {
        Shape shape = getShape();
        if (shape instanceof Circle) {
            Circle circ = (Circle) shape;
            Bounds circLayoutBounds = circ.getBoundsInParent();
            posX.set((circLayoutBounds.getMaxX() - (circLayoutBounds.getWidth() / 2)));
            posY.set((circLayoutBounds.getMaxY() - (circLayoutBounds.getHeight() / 2)));
            centerPosX.set(posX.getValue());
            centerPosY.set(posY.getValue());
        } else if (shape instanceof Rectangle) {
            Rectangle rect = (Rectangle) shape;
            Bounds rectLayoutBounds = rect.getBoundsInParent();
            posX.set(rectLayoutBounds.getMinX());
            posY.set(rectLayoutBounds.getMinY());
            centerPosX.set(rectLayoutBounds.getMinX() + (rect.getWidth() / 2));
            centerPosY.set(rectLayoutBounds.getMinY() + (rect.getHeight() / 2));
        }
    }

    /**
     * In dieser Methode werden Events dem Shape-Objekt hinzugefügt. Diese
     * Events regeln realisieren das Drag-Verhalten und das Öffnen des
     * Kontext-Menüs bei einem Klick der rechten Maustaste.
     *
     * @see PetriNode.MyEventHandler
     *
     */
    protected void addEvents() {
        Shape shape = getShape();
        MyEventHandler myEventHandler = new MyEventHandler();
        shape.setOnDragDetected((MouseEvent event) -> {
            shape.startFullDrag();
        });
        shape.setOnMousePressed(myEventHandler.myOnMousePressedEventHandler);
        shape.setOnMouseDragged(myEventHandler.myOnMouseDraggedEventHandler);
        shape.setOnMouseDragReleased(myEventHandler.myDragDoneEventHandler);
        shape.setOnContextMenuRequested(myEventHandler.onPetriNodeContextMenuRequest);
    }

    /**
     * Diese Methode überprüft, ob diese Instanz im Workflownetz Nachfolger der
     * übergebenen Instanz ist.
     *
     * @param petriNode Der potentielle Vorgänger im Workfloenetz
     * @return true, falls diese Instanz Nachfolger ist, false, fals nicht.
     */
    protected boolean isNextTo(PetriNode petriNode) {
        Iterator<Arc> iterator = this.getPrevArcs().iterator();
        while (iterator.hasNext()) {
            Arc arc = iterator.next();
            if (arc.getStartNode().equals(petriNode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Diese Methode gibt das Set mit Arc-Objekten, die von diesem Knoten
     * ausgehen, zurück.
     *
     * @return Set mit ausgehnden Arc-Objekten.
     */
    public Set<Arc> getNextArcs() {
        return nextArcs;
    }

    /**
     * Diese Methode gibt das Set mit Arc-Objekten, die zu diesem Knoten
     * hinführen, zurück.
     *
     * @return Set mit eingehenden Arc-Objekten.
     */
    public Set<Arc> getPrevArcs() {
        return prevArcs;
    }

    /**
     * Diese Methode gibt das Attribut Label zurück.
     *
     * @return Das Attribut Label.
     */
    public Text getLabel() {
        return label;
    }

    /**
     * Diese Methode gibt den Wert der posX-Property zurück.
     *
     * @return Wert der posX-Property.
     */
    public double getPosX() {
        return posX.getValue();
    }

    /**
     * Diese Methode gibt den Wert der posY-Property zurück.
     *
     * @return Wert der posY-Property
     */
    public double getPosY() {
        return posY.getValue();
    }

    /**
     * Diese Methode gibt das Attribut posX zurück.
     *
     * @return Das Attribut posX.
     */
    public DoubleProperty getPosXProperty() {
        return posX;
    }

    /**
     * Diese Methode gibt das Attribut posY zurück
     *
     * @return Das Attribut posY.
     */
    public DoubleProperty getPosYProperty() {
        return posY;
    }

    /**
     * Diese Methode gibt das Attribut centerPosX zurück
     *
     * @return Das Attribut centerPosX.
     */
    public DoubleProperty getCenterPosX() {
        return centerPosX;
    }

    /**
     * Diese Methode gibt das Attribut centerPosY zurück
     *
     * @return Das Attribut centerPosY.
     */
    public DoubleProperty getCenterPosY() {
        return centerPosY;
    }

    /**
     * Diese Methode gibt das Attribut id zurück
     *
     * @return Das Attribut id.
     */
    public String getId() {
        return id;
    }

    /**
     * Diese Methode gibt das Attribut contextMenu zurück
     *
     * @return Das Attribut contextMenu.
     */
    public PetriNodeContextMenu getContextMenu() {
        return contextMenu;
    }

    /**
     * Diese Methode setzt den posX- und posY-Wert (übergeben als double).
     *
     * @param x Der Wert, den die posX-Property annehmen soll.
     * @param y Der Wert, den die posY-Property annehmen soll.
     */
    public void setPosXAndY(double x, double y) {
        this.setPosX(x);
        this.setPosY(y);
    }

    /**
     * Diese Methode setzt den posX- und posY-Wert (übergeben als String).
     *
     * @param x Der Wert, den die posX-Property annehmen soll.
     * @param y Der Wert, den die posY-Property annehmen soll.
     */
    public void setPosXAndY(String x, String y) {
        this.setPosX(Double.parseDouble(x));
        this.setPosY(Double.parseDouble(y));
    }

    /**
     * Diese Methode setzt die id.
     *
     * @param id Der string, den die id annehmen soll.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Diese Methode gibt den Wert des Attributs forwardReachable zurück.
     *
     * @return Den Wert des Attributs forwardReachable.
     */
    public boolean isForwardReachable() {
        return forwardReachable;
    }

    /**
     * Diese Methode gibt den Wert des Attributs backwardReachable zurück.
     *
     * @return Den Wert des Attributs backwardReachable.
     */
    public boolean isBackwardReachable() {
        return backwardReachable;
    }

    /**
     * Diese Methode setzt den Wert des Attributs forwardReachable.
     *
     * @param forwardReachable Der Wert, den das Attribut annehmen soll.
     */
    public void setForwardReachable(boolean forwardReachable) {
        this.forwardReachable = forwardReachable;
    }

    /**
     * Diese Methode setzt den Wert des Attributs backwardReachable.
     *
     * @param backwardReachable Der Wert, den das Attribut annehmen soll.
     */
    public void setBackwardReachable(boolean backwardReachable) {
        this.backwardReachable = backwardReachable;
    }

    void isSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    /**
     * In dieser Klasse werden die EventHandler definiert, die in den Methoden
     * benötigt werden.
     */
    class MyEventHandler {

        double orgSceneX, orgSceneY;
        Map<PetriNode, double[]> translations = new HashMap<>();
        double offsetX, offsetY;

        /**
         * Dieser EventHandler dient dazu die Koordinaten und Werte der
         * Translationen (Verschiebungen) vor dem Verschieben festzuhalten.
         */
        EventHandler<MouseEvent> myOnMousePressedEventHandler
                = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                SelectionModel selectionModel = SelectionModel.getInstance();
                if (!selectionModel.getSelectedNodes().contains(PetriNode.this)) {
                    if (!event.isControlDown()) {
                        SelectionModel.getInstance().clearSelectedNodes();
                    }
                    SelectionModel.getInstance().addPetriNode(PetriNode.this);
                }

                orgSceneX = event.getSceneX();
                orgSceneY = event.getSceneY();
                Iterator<PetriNode> iterator = SelectionModel.getInstance().getSelectedNodes().iterator();
                while (iterator.hasNext()) {
                    PetriNode currentNode = iterator.next();
                    double transX = currentNode.getShape().getTranslateX();
                    double transY = currentNode.getShape().getTranslateY();
                    double[] trans = {transX, transY};
                    translations.put(currentNode, trans);
                }
            }
        };

        /**
         * Dieser EventHandler dient dazu die Werte zu bestimmen, um die
         * verschoben werden soll. Diese Werte sind abhängig von ursprünglichen
         * Koordinaten, Translation und dem aktuellen Scale-Wert. (Der
         * Scale-Wert wird für das Zoomen in der Benutzeroberfläche benötigt)
         */
        EventHandler<MouseEvent> myOnMouseDraggedEventHandler
                = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                offsetX = event.getSceneX() - orgSceneX;
                offsetY = event.getSceneY() - orgSceneY;
                Shape shape = ((Shape) (event.getSource()));
                double scaleFactor = 1 / shape.getParent().getScaleX();
                Iterator<PetriNode> iterator = SelectionModel.getInstance().getSelectedNodes().iterator();
                while (iterator.hasNext()) {
                    PetriNode currentNode = iterator.next();

                    double newTranslateX = translations.get(currentNode)[0] + (offsetX * scaleFactor);
                    double newTranslateY = translations.get(currentNode)[1] + (offsetY * scaleFactor);

                    Shape currentShape = currentNode.getShape();
                    Text currentLabel = currentNode.getLabel();
                    currentShape.setTranslateX(newTranslateX);
                    currentLabel.setTranslateX(newTranslateX);
                    currentShape.setTranslateY(newTranslateY);
                    currentLabel.setTranslateY(newTranslateY);
                    currentNode.resetPosXAndY();

                }
            }
        };

        EventHandler<MouseEvent> myDragDoneEventHandler
                = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                Iterator<PetriNode> iterator = SelectionModel.getInstance().getSelectedNodes().iterator();
                while (iterator.hasNext()) {
                    PetriNode currentNode = iterator.next();
                    Shape currentShape = currentNode.getShape();
                    double[] newTrans = {currentShape.getTranslateX(), currentShape.getTranslateY()};
                    translations.put(currentNode, newTrans);
                }
            }
        };

        /**
         * Dieser EventHandler öffnet das Kontext-Menü.
         */
        EventHandler<ContextMenuEvent> onPetriNodeContextMenuRequest
                = (ContextMenuEvent event) -> {
                    contextMenu.show(getShape(), event.getScreenX(), event.getScreenY());
                    event.consume();
                };
    }
}
