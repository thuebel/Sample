package petriNodes;

import java.util.Iterator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import resources.MyColors;

/**
 * Diese Klasse repräsentiert eine Transition im Workflownetz. Sie Beinhaltet
 * die von der Klasse PetriNode geerbten Attribute und Methoden, bzw.
 * implementiert die abstrakten Methoden. Außerdem beinhaltet sie ein
 * Rectangle-Objekt, das die Transition in der Benutzeroberfläche darstellt und
 * weitere Attribute, die Eigenschaften der Transition festhalten (schaltbar,
 * Kontakt ermittelt).
 *
 * @author Tobias Hübel, 5509840
 */
public class Transition extends PetriNode {

    /**
     * rectangle dient als grafik-Objekt der definierten Stelle.
     */
    private final Rectangle rectangle = new Rectangle(40, 40);

    /**
     * Information darüber, ob diese Transition schaltbar ist. (In Form einer
     * BooleanProperty)
     */
    private final BooleanProperty tickable = new SimpleBooleanProperty(false);

    /**
     * Information darüber, ob diese Transition einen Kontakt ermittelt hat. (In
     * Form einer BooleanProperty)
     */
    private final BooleanProperty contactDetected = new SimpleBooleanProperty(false);

    /**
     * Dieser Code wird unabhängig vom verwendeten Konsruktor durchlaufen.
     */
    {
        initRectangle();
        setInitialLabel();
        initTranslation();
    }

    /**
     * Dieser Konstruktor ruft den Konstruktor der Oberklasse PetriNode auf.
     *
     * @param id Id der Instanz
     * @param posX Wert der posX-Property
     * @param posY Wert der posY-Property
     */
    public Transition(String id, double posX, double posY) {
        super(id, posX, posY);
    }

    /**
     * Dieser Konstruktor ruft den Konstruktor der Oberklasse PetriNode auf.
     *
     * @param id Id der Instanz
     */
    public Transition(String id) {
        super(id);
    }

    /**
     * Diese Methode initialisiert das Rechteck rectangle. Sie setzt die
     * Füllfarbe, die Rahmenfarbe, Koordinaten des Rechtecks und den Wert des
     * zoomFaktor-Attributs. Sie bindet den Werte der height- und widthProperty
     * an den Wert des zoomFaktor und initialisiert die EventHandler
     * (addEvents).
     */
    private void initRectangle() {
        rectangle.setFill(MyColors.NODE_DEFAULT);
        rectangle.setStroke(MyColors.NODE_STROKE);
        rectangle.setX(this.getPosX());
        rectangle.setY(this.getPosY());
        this.zoomFaktor.set(40);
        rectangle.heightProperty().bind(zoomFaktor);
        rectangle.widthProperty().bind(zoomFaktor);
        addEvents();
    }

    /**
     * Diese Methode fügt dem Rechteck eine initiale Translation hinzu, so dass
     * die Position des Rechtecks dem Mittelpunkt entspricht. Ursrpünglich
     * werden die Koordinaten der linken oberen Ecke verwendet.
     */
    private void initTranslation() {
        double xTranslation = rectangle.getTranslateX();
        double yTranslation = rectangle.getTranslateY();
        double width = rectangle.getWidth();
        double height = rectangle.getHeight();
        rectangle.setTranslateX(xTranslation - (width/2));
        rectangle.setTranslateY(yTranslation -(height/2));
        this.label.setTranslateX(xTranslation - (width/2));
        this.label.setTranslateY(yTranslation - (height/2));
    }

    /**
     * Diese Methode setzt den Text des Labels, falls er noch nicht gesetzt
     * wurde, und positioniert es links unterhalb des Rechtecks.
     */
    @Override
    public void setInitialLabel() {
        if (this.getLabel() == null) {
            this.label = new Text("Transition: " + this.getId());
        }
        this.label.setStyle("-fx-font: 17 arial; ");
        label.setX(this.getPosX());
        label.setY(this.getPosY() + this.rectangle.getHeight() + 15);
    }

    /**
     * Diese Methode setzt die X- und Y-Koordinate des Rechtecks rectangle
     * gleich den Werten der Klassenvariablen posX und posY.
     */
    @Override
    public void setXAndY() {
        this.rectangle.setX(this.getPosX());
        this.rectangle.setY(this.getPosY());
    }

    /**
     * Diese Methode gibt das Attribut rectangle zurück.
     *
     * @return rectangle Das Attribut circle.
     */
    @Override
    public Shape getShape() {
        return this.rectangle;
    }

    /**
     * Diese Methode setzt posX und centerPosX. Da die Position eins
     * Rectangle-Objekts über die Koordinaten der linken oberen Ecke definiert
     * wird, weicht der Wert der centerPosX-Property von der posX-Property ab.
     * Er muss auf Grundlage der Breite des Rechtecks berechnet werden.
     *
     * @param posX Der Wert, den die posX-Property annehmen soll und aus dem der
     * Wert der centerPosX-Property berechnet wird.
     */
    @Override
    public void setPosX(double posX) {
        this.posX.set(posX);
    }

    /**
     * Diese Methode setzt posY und centerPosY. Da die Position eins
     * Rectangle-Objekts über die Koordinaten der linken oberen Ecke definiert
     * wird, weicht der Wert der centerPosY-Property von der posY-Property ab.
     * Er muss auf Grundlage der Höhe des Rechtecks berechnet werden.
     *
     * @param posY Der Wert, den die posY-Property annehmen soll und aus dem der
     * Wert der centerPosY-Property berechnet wird.
     */
    @Override
    public void setPosY(double posY) {
        this.posY.set(posY);
    }

    /**
     * Diese Methode gibt den Wert der tickable-Property zurück.
     *
     * @return Wert der tickable-BooleanProperty.
     */
    public boolean isTickable() {
        return tickable.getValue();
    }

    /**
     * Diese Methode setzt den Wert der tickable-Property.
     *
     * @param bool Der Wert, den die tickable-BooleanProperty annehmen soll.
     */
    public void setTickable(boolean bool) {
        this.tickable.setValue(bool);
        this.setFill();
    }

    /**
     * Diese Methode setzt die Füllfarbe der Transition. Je nach Zustand
     * (default, schaltbar oder Kontakt erkannt) wird eine andere Farbe gewählt.
     */
    @Override
    public void setFill() {
        if (this.tickable.getValue()) {
            this.getShape().setFill(MyColors.TRANS_TICKABLE);
        } else if (this.contactDetected.getValue()) {
            this.getShape().setFill(MyColors.TRANS_CONTACT);
        } else {
            this.getShape().setFill(MyColors.NODE_DEFAULT);
        }
        if (this.isSelected) {
            this.getShape().setStroke(MyColors.NODE_SELECTED);
            this.getShape().setStrokeWidth(2.5);
        } else {
            this.getShape().setStroke(MyColors.NODE_STROKE);
            this.getShape().setStrokeWidth(1);
        }

    }

    /**
     * Diese Methode setzt den Zustand dieses Knoten auf default-Werte zurück.
     * Danach ist er weder schaltbar noch wird angezeigt, dass ein Kontakt
     * erkannt wurde. Die Füllfarbe wird entsprechend angepasst.
     *
     * @param workflownetContentPane Das Pane, das das Shape-Objekt beinhaltet.
     */
    @Override
    public void resetStatusToDefault(Pane workflownetContentPane) {
        this.tickable.setValue(false);
        this.contactDetected.setValue(false);
        this.setFill();
    }

    /**
     * Diese Methode überprüft, ob diese Transition schaltbar (alle
     * Vorgängerstellen sind markiert) ist und ob ein Kontakt (eine
     * Nachfolgerstelle, die nicht zugleich Vorgänger ist, ist markiert)
     * vorliegt. Die Füllfarbe wird entsprechend angepasst.
     */
    public void checkTickableAndContact() {
        if (this.getPrevArcs().size() > 0) {
            this.tickable.setValue(true);
            Iterator<Arc> iterator = this.getPrevArcs().iterator();
            while (iterator.hasNext()) {
                Arc arc = iterator.next();
                Place prevPlace = (Place) arc.getStartNode();
                if (!prevPlace.isMarked()) {
                    // Hier wurde eine unmarkierte Vorgängerstelle gefunden
                    this.tickable.setValue(false);
                }
            }
        }
        this.contactDetected.setValue(false);
        if (this.isTickable()) {
            Iterator<Arc> iterator = this.getNextArcs().iterator();
            while (iterator.hasNext()) {
                Arc arc = iterator.next();
                Place nextPlace = (Place) arc.getEndNode();
                if (nextPlace.isMarked() && !this.isNextTo(nextPlace)) {
                    // eine markierte Nachfolgerstelle, die nicht zugleich
                    // Vorgänger ist wurde gefunden.
                    this.contactDetected.setValue(true);
                    this.setTickable(false);
                }
            }

        }
        this.setFill();
    }

}
