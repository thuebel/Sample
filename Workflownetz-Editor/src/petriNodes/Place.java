package petriNodes;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import resources.MyColors;

/**
 * Diese Klasse representiert eine Stelle im Workflownetz. Sie Beinhaltet die
 * von der Klasse PetriNode geerbten Attribute und Methoden, bzw. implementiert
 * die abstrakten Methoden. Außerdem beinhaltet sie zwei Circle-Objekte, welche
 * die Stelle und die Markierung einer Stelle in der Benutzeroberfläche
 * darstellen und weitere Attribute, die Eigenschaften der Stelle festhalten
 * (markiert, Start- oder Endknoten).
 *
 * @author Tobias Hübel, 5509840
 */
public class Place extends PetriNode {

    /**
     * circle dient als grafik-Objekt der definierten Stelle.
     */
    private final Circle circle = new Circle(20);

    /**
     * Der Kreis, der den Markierungzustand einer Stelle anzeigt. Die Makierung
     * einer Stelle wird durch einen kleineren Kreis innerhalb der Stelle
     * dargestellt. Das Attribut marking entspricht diesem Kreis.
     */
    private Circle marking;

    /**
     * Information darüber, ob diese Stelle im Workflownetz die Startstelle ist.
     * (In Form einer BooleanProperty)
     */
    private final BooleanProperty isStartPlace = new SimpleBooleanProperty(false);

    /**
     * Information darüber, ob diese Stelle im Workflownetz die Endstelle ist.
     * (In Form einer BooleanProperty)
     */
    private final BooleanProperty isEndPlace = new SimpleBooleanProperty(false);

    /**
     * Information darüber, ob diese Stelle im Workflownetz markiert ist. (In
     * Form einer BooleanProperty)
     */
    private final BooleanProperty isMarked = new SimpleBooleanProperty(false);

    /**
     * Dieser Code wird unabhängig vom verwendeten Konsruktor durchlaufen.
     */
    {
        initCircle();
        setInitialLabel();
    }

    /**
     * Dieser Konstruktor ruft den Konstruktor der Oberklasse PetriNode auf.
     *
     * @param id Id der Instanz
     * @param posX Wert der posX-Property
     * @param posY Wert der posY-Property
     */
    public Place(String id, double posX, double posY) {
        super(id, posX, posY);
    }

    /**
     * Dieser Konstruktor ruft den Konstruktor der Oberklasse PetriNode auf.
     *
     * @param id Id der Instanz
     */
    public Place(String id) {
        super(id);
    }

    /**
     * Diese Methode initialisiert den Kreis circle. Sie setzt die Füllfarbe,
     * die Rahmenfarbe, die Koordinaten des Kreises und den Wert des
     * zoomFaktor-Attributs. Sie bindet den Wert der radiusProperty an den Wert
     * des zoomFaktor, initialisiert die EventHandler (addEvents) und das
     * marking-Attribut (initMarking)
     */
    private void initCircle() {
        circle.setFill(MyColors.NODE_DEFAULT);
        circle.setStroke(MyColors.NODE_STROKE);
        circle.setCenterX(this.getPosX());
        circle.setCenterY(this.getPosY());
        zoomFaktor.set(20);
        circle.radiusProperty().bind(this.zoomFaktor);

        addEvents();
        initMarking();
    }

    /**
     * Diese Methode initialisiert den Kreis, der in der Benutzeroberfläche die
     * Markierung einer Stelle darstellt.
     */
    private void initMarking() {
        this.marking = new Circle(8);
        this.marking.setFill(MyColors.BLACK);
        this.marking.centerXProperty().bind(centerPosX);
        this.marking.centerYProperty().bind(centerPosY);
    }

    /**
     * Diese Methode setzt den Text des Labels, falls er noch nicht gesetzt
     * wurde, und positioniert es links unterhalb des Kreises.
     */
    @Override
    public void setInitialLabel() {
        if (this.getLabel() == null) {
            this.label = new Text("Place: " + this.getId());
        }
        this.label.setStyle("-fx-font: 17 arial;");
        this.label.setX(this.getPosX() - this.circle.getRadius());
        this.label.setY(this.getPosY() + this.circle.getRadius() + 15);
    }

    /**
     * Diese Methode gibt das Attribut circle zurück.
     *
     * @return circle Das Attribut circle.
     */
    @Override
    public Shape getShape() {
        return circle;
    }

    /**
     * Diese Methode setzt die X- und Y-Koordinate des Kreises circle gleich den
     * Werten der Klassenvariablen posX und posY.
     */
    @Override
    public void setXAndY() {
        this.circle.setCenterX(this.getPosX());
        this.circle.setCenterY(this.getPosY());
    }

    /**
     * Diese Methode gibt den Wert des Attributs isMarked in Form eines Strings
     * zurück.
     *
     * @return isMarked als String.
     */
    public String getMarkingAsString() {
        if (this.isMarked.getValue()) {
            return "1";
        } else {
            return "0";
        }
    }

    /**
     * Diese Methode setzt den Wert des Attributs isMarked.
     *
     * @param markingValue Der Wert, den das Attribut annehmen soll.
     */
    public void setMarking(int markingValue) {
        if (markingValue == 1) {
            this.isMarked.set(true);
        }
    }

    /**
     * Diese Methode setzt posX und centerPosX.
     *
     * @param posX Der Wert, den die posX-Property und die centerPosX-Property
     * annehmen sollen.
     */
    @Override
    public void setPosX(double posX) {
        this.posX.set(posX);
        this.centerPosX.set(posX);
    }

    /**
     * Diese Methode setzt posY und centerPosY.
     *
     * @param posY Der Wert, den die posY-Property und die centerPosY-Property
     * annehmen sollen.
     */
    @Override
    public void setPosY(double posY) {
        this.posY.set(posY);
        this.centerPosY.set(posY);
    }

    /**
     * Diese Methode gibt das Attribut marking zurück.
     *
     * @return Das Attribut marking.
     */
    public Circle getMarking() {
        return marking;
    }

    /**
     * Diese Methode gibt den Wert der isMarked-Property zurück
     *
     * @return Wert der isMarked-BooleanProperty
     */
    public boolean isMarked() {
        return isMarked.getValue();
    }

    /**
     * Diese Methode gibt den Wert der isEndPlace-Property zurück
     *
     * @return Wert der isEndPlace-BooleanProperty
     */
    public boolean isEndPlace() {
        return isEndPlace.getValue();
    }

    /**
     * Diese Methode definiert dieses Objekt als Anfangsknoten eines
     * Workflownetzes und passt die Füllfarbe entsprechenden an.
     */
    public void setAsStart() {
        this.isStartPlace.set(true);
        this.getShape().setFill(MyColors.NET_START);
    }

    /**
     * Diese Methode definiert dieses Objekt als Endknoten eines Workflownetzes
     * und passt die Füllfarbe entsprechenden an.
     */
    public void setAsEnd() {
        this.isEndPlace.set(true);
        this.getShape().setFill(MyColors.NET_END);
    }

    /**
     * Diese Methode setzt Diesen Knoten als markiert. Falls die Markierung der
     * Benutzeroberfläche noch nicht hinzugeffügt wurde, wird sie hinzugefügt.
     *
     * @param workflownetContentPane Das Pane, das das Shape-Objekt beinhaltet.
     */
    public void setAsMarked(Pane workflownetContentPane) {
        if (!workflownetContentPane.getChildren().contains(this.marking)) {
            workflownetContentPane.getChildren().add(this.marking);
        }
        this.isMarked.set(true);
        this.marking.toFront();
    }

    /**
     * Diese Methode setzt Diesen Knoten als unmarkiert. Falls die Markierung in
     * der Benutzeroberfläche noch nicht entfernt wurde, wird sie entfernt.
     *
     * @param workflownetContentPane Das Pane, das das Shape-Objekt beinhaltet.
     */
    public void unsetAsMarked(Pane workflownetContentPane) {
        if (workflownetContentPane.getChildren().contains(this.marking)) {
            workflownetContentPane.getChildren().remove(this.marking);
        }
        this.isMarked.set(false);
    }

    /**
     * Diese Methode setzt die Füllfarbe der Stelle. Je nach Zustand (Start-
     * oder Endknoten, markierter Knoten) wird eine andere Farbe gewählt.
     */
    @Override
    public void setFill() {
        if (isStartPlace.getValue()) {
            this.getShape().setFill(MyColors.NET_START);
        } else if (isEndPlace.getValue()) {
            this.getShape().setFill(MyColors.NET_END);
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
     * Danach ist er weder Start- noch Endknoten und unmarkiert. Die Füllfarbe
     * wird entsprechend angepasst.
     *
     * @param workflownetContentPane Das Pane, das das Shape-Objekt beinhaltet.
     */
    @Override
    public void resetStatusToDefault(Pane workflownetContentPane) {
        if (workflownetContentPane.getChildren().contains(this.marking)) {
            workflownetContentPane.getChildren().remove(this.marking);
        }
        this.isMarked.set(false);
        this.isEndPlace.set(false);
        this.isStartPlace.set(false);
        this.setFill();
    }
}
