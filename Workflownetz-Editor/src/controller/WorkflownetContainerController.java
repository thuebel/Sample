package controller;

import petriNodes.Arc;
import petriNodes.PetriNode;
import petriNodes.Place;
import petriNodes.Transition;
import workflownetLogic.WorkflownetHandler;
import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.beans.property.DoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javax.xml.stream.XMLStreamException;
import persistenceService.PersistenceService;
import petriNodes.SelectionModel;
import resources.MyColors;
import resources.MyMessages;

/**
 * Dies ist die Kontroll-Klasse für ein Tab im TabPane. Sie beinhaltet ein
 * ScrollPane-Objekt (scrollPane) mit Pane-Objekt (workflownetContentPane). Das
 * Pane beinhaltet die Elemente des Workflownetzes. In dieser Klasse ist das
 * Netz in Form einer HashMap (petriNodes) definiert. Sie stellt ein zentrales
 * Element der Funktionalität der Anwendung dar.
 *
 * @author Tobias Hübel, 5509840
 */
public class WorkflownetContainerController implements Initializable {

    /**
     * Ein Tab in der TabPane. Es dient als Container für das ScrollPane-Objekt.
     */
    @FXML
    private Tab tab;

    /**
     * Dieses ScrollPane-Objekt realisiert die Scrolleigenschaften, falls
     * Elemente außerhalb des sichtbaren Bereichs verschoben werden.
     */
    @FXML
    private ScrollPane scrollPane;

    /**
     * Dieses Pane-Objekt beinhaltet alle Elemente eines Netzes (Stellen,
     * Transitionen und Kanten).
     */
    @FXML
    private Pane workflownetContentPane;

    /**
     * Dieses HashMap-Objekt repräsentiert das potentielle Workflownetz. Um den
     * Zugriff auf einzelne Elemete zu erleichtern werden die id's der Knoten
     * als Schlüsselwerte verwendet.
     */
    private Map<String, PetriNode> petriNodes = new HashMap<>();

    /**
     * Das MainController-Objekt stellt eine Verbindung zu anderen Elementen der
     * Benutzeroberfläche her.
     */
    private MainController mainController;

    /**
     * Dieses WorkflownetHandler-Objekt dient zu Überprüfung auf verschiedene
     * Eigenschaften des potentiellen Workfloenetzes.
     */
    private final WorkflownetHandler wfnHandler = new WorkflownetHandler();

    public WorkflownetHandler getWfnHandler() {
        return wfnHandler;
    }

    /**
     * In diesem Boolean-Objekt wird festgehalten ob die Knoten im
     * HashMap-Objekt ein gültiges Workflownetz repräsentieren.
     */
    private boolean isWorkflownet = false;

    /**
     * In diesem Boolean-Objekt wird festgehalten, ob nicht gespeicherte
     * Änderungen bestehen.
     */
    private boolean isSaved = false;

    /**
     * Anzahl der Transitionen in der HashMap petriNodes.
     */
    private int countOfTransition;

    /**
     * Anzahl der Stellen in der HashMap petriNodes.
     */
    private int countOfPlaces;

    /**
     * Anzahl der Kanten im Workflownetz.
     */
    private int countOfArcs = 0;

    /**
     * Diese Methode wird aufgerufen, bevor das Tab tab geschlossen wird. Falls
     * nicht gespeicherte Änderungen bestehen, wird ein Dialog angezeigt, der
     * eine Bestätigung verlangt. Sonst wird das Tab nicht geschlossen.
     *
     * @param closeEvent Das Event, das beim Schließen eines Tabs ausgelöst
     * wird.
     */
    @FXML
    protected void closeTab(Event closeEvent) {
        if (!this.isSaved) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Achtung");
            alert.setHeaderText("Nicht gespeicherte Änderungen.");
            alert.setContentText("Wenn Sie das Tab schließen, gehen nicht gespeicherte Änderungen verloren. Wollen Sie es trotzdem schließen?");

            ButtonType closeType = new ButtonType("Schließen");
            ButtonType cancelClosingType = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(closeType, cancelClosingType);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == cancelClosingType) {
                closeEvent.consume();
            } else {
                mainController.getWccMap().remove(this.tab);
            }
        } else {
            mainController.getWccMap().remove(this.tab);
        }

    }

    /**
     * addPetriNode fügt ein PetriNode-Objekt der HashMap petriNodes und dem
     * Pane workflownetContentPane hinzu. Außerdem wird mithilfe der Methode
     * checkAndResetStatus der Status des Netzes überprüft.
     *
     * @param petriNode eine Instanz der Transition- oder Place-Klasse.
     */
    private void addPetriNode(PetriNode petriNode) {
        this.petriNodes.put(petriNode.getId(), petriNode);
        petriNode.getContextMenu().initMenuItems(this);
        checkAndResetStatus();
        workflownetContentPane.getChildren().addAll(petriNode.getShape(), petriNode.getLabel());
        this.isSaved = false;
    }

    /**
     * Diese Methode berechnet die Anzahl der Transitionen im Workflownetz.
     *
     * @return Anzahl der Transitionen im Workflownetz.
     */
    private int computeCountOfTransition() {
        int i = 0;
        i = this.petriNodes.values().stream().filter((value)
                -> (value.getClass().getSimpleName().equalsIgnoreCase("Transition"))).map((_item)
                -> 1).reduce(i, Integer::sum);
        return i;
    }

    /**
     * Diese Methode berechnet die Anzahl der Places im Workflownetz.
     *
     * @return Anzahl der Places im Workflownetz.
     */
    private int computeCountOfPlaces() {
        int i = 0;
        i = this.petriNodes.values().stream().filter((value)
                -> (value.getClass().getSimpleName().equalsIgnoreCase("Place"))).map((_item)
                -> 1).reduce(i, Integer::sum);
        return i;
    }

    /**
     * Diese Methode berechnet die Anzahl der Kanten im Workflownetz.
     *
     * @return Anzahl der Places im Workflownetz.
     */
    private int computeCountOfArcs() {
        int i = 0;
        i = this.petriNodes.values().stream().filter((value)
                -> (value.getClass().getSimpleName().equalsIgnoreCase("Arc"))).map((_item)
                -> 1).reduce(i, Integer::sum);
        return i;
    }

    /**
     * Diese Methode füllt die HashMap petriNodes mit den in einer pnml-File
     * gespeicherten Objekten und ruft daraufhin die MEthode drawNodes auf.
     * Abschließend wird der Netzstatus überprüft(checkAndResetStatus).
     *
     * @param file die pnml-File, die geladen werden soll.
     * @throws javax.xml.stream.XMLStreamException
     */
    protected void initWccFromFileloading(File file) throws XMLStreamException {

        this.tab.setText(file.getName());
        try {
            this.petriNodes = PersistenceService.loadNodeObjects(file);
            this.isSaved = true;
        } catch (XMLStreamException e) {
            throw e;
        }

        this.countOfTransition = computeCountOfTransition();
        this.countOfPlaces = computeCountOfPlaces();
        this.countOfArcs = computeCountOfArcs();
        initContextMenus();
        drawNodes();
        checkAndResetStatus();
    }

    /**
     * drawNodes fügt die in petriNodes enthaltenen Transition-, Place- und
     * zugehörige Arc-Objekte dem Pane-Objekt workflownetContentPane hinzu.
     * Außerdem werden die X-, Y- und Label-Werte diser Objekte gesetzt.
     */
    public void drawNodes() {
        this.petriNodes.forEach((String key, PetriNode value) -> {
            value.setXAndY();
            value.setInitialLabel();
            Iterator<Arc> iterator = value.getNextArcs().iterator();
            while (iterator.hasNext()) {
                Arc arc = iterator.next();
                this.workflownetContentPane.getChildren().add(arc);
                arc.toBack();
            }
            value.getShape().toFront();
            this.workflownetContentPane.getChildren().addAll(value.getShape(), value.getLabel());
        });
    }

    /**
     * Diese Methode gibt das Attribut tab zurück.
     *
     * @return Das Attribut tab.
     */
    public Tab getTab() {
        return this.tab;
    }

    /**
     * Diese Methode setzt den Wert der Klassenvariable mainController.
     *
     * @param mainController Der Wert, der gesetzt werden soll.
     */
    protected void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Diese Methode initialisiert das Speichern einer pnml-File und ändert den
     * Namen des Tabs tab in den Dateinamen. Das Speichern der Datei übernimmt
     * die statische Methode der Klasse PersistenceService.
     *
     * @param file Die Datei, die gespeichert werden soll.
     */
    protected void initSavePNML(File file) {
        try {
            PersistenceService.savePNML(this.petriNodes, file);
            tab.setText(file.getName());
            this.isSaved = true;
            this.mainController.getLeftVBoxController().showToast(MyMessages.SEVE_DONE);
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Fehler!");
            alert.setHeaderText("Fehler beim Speichern");
            alert.setContentText("Beim Speichern ist ein Fehler aufgetreten. Bitte versuchen Sie es erneut.");
            alert.showAndWait();
        }

    }

    /**
     * Diese Methode erzeugt ein neues Transition-Objekt, setzt das
     * Transitionsspezifische Label und übergibt die Transition an die Methode
     * addPetriNode.
     */
    protected void initNewTransition() {
        countOfTransition++;
        Transition trans = new Transition("T" + countOfTransition, 50, 50);
        addPetriNode(trans);
    }

    /**
     * Diese Methode erzeugt ein neues Place-Objekt, setzt das
     * Stellenspezifische Label und übergibt die Stelle an die Methode
     * addPetriNode.
     */
    protected void initNewPlace() {
        countOfPlaces++;
        Place place = new Place("P" + countOfPlaces, 50, 50);
        addPetriNode(place);
    }

    /**
     * Diese Methode setzt ein bestehendes Workflownetz auf den Anfangszustand
     * zurück. Es werden erst alle Markierungen aufgehoden, um daraufhin die
     * Anfangsstelle zu markieren.
     */
    void refreshNet() {
        if (isWorkflownet) {
            if (!this.wfnHandler.getStartPlace().isMarked()) {
                this.isSaved = false;
            }
            setPetriNodesToDefault();
            checkAndResetStatus();
        }
    }

    /**
     * Diese Methode setzt alle im Netz enthaltenen Knoten auf den
     * Default-Zustand zurück. Es wird die Methode resetStatusToDefault jedes
     * PetriNode-Objekts aufgerufen.
     */
    private void setPetriNodesToDefault() {
        Iterator<PetriNode> iterator = this.petriNodes.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().resetStatusToDefault(workflownetContentPane);
        }
    }

    /**
     * Diese Methode gibt das Attribut scrollPane zurück.
     *
     * @return Das Attribut scrollPane.
     */
    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    /**
     * Diese Methode gibt das Attribut workflownetContentPane zurück.
     *
     * @return Das Attribut workflownetContentPane.
     */
    public Pane getWorkflownetContentPane() {
        return workflownetContentPane;
    }

    /**
     * Diese Methode initialisiert die Scoll-Eigenschaften des Panes
     * workflownetContentPane. Es werden die Methoden
     * bindSliderToPaneScaleValues und addScrollEventToPane aufgerufen.
     *
     */
    protected void initScrollable() {
        bindSliderToPaneScaleValues();
        addScrollEventToPane();
    }

    /**
     * Diese Methode bindet die Werte der ScaleX- und ScaleY-Properties des
     * Panes workflownetContentPane mit der valueProperty des Zoom-Sliders des
     * linken Menüs. Damit ist die Größenverstellbarkeit der Netzelemente
     * realisiert.
     */
    private void bindSliderToPaneScaleValues() {
        DoubleProperty sliderValueProperty = mainController.getLeftVBoxController().getZoomSlider().valueProperty();
        workflownetContentPane.scaleXProperty().bind(sliderValueProperty);
        workflownetContentPane.scaleYProperty().bind(sliderValueProperty);
    }

    /**
     * Diese Methode fügt dem Pane workflownetContentPane ein Scroll-Event
     * hinzu. Falls die STRG-Taste beim scrollen gedrückt ist, wird der Wert der
     * der Value Property des Zoom- Slider des linken Menüs (damit die
     * ScaleValues des Panes) verändert.
     */
    private void addScrollEventToPane() {
        final double SCALE_DELTA = 1.1;

        workflownetContentPane.setOnScroll(event -> {

            if (event.isControlDown()) {
                if (event.getDeltaY() == 0) {
                    return;
                }
                double scaleFactor
                        = (event.getDeltaY() > 0)
                        ? SCALE_DELTA
                        : 1 / SCALE_DELTA;

                DoubleProperty sliderValueProperty = mainController.getLeftVBoxController().getZoomSlider().valueProperty();
                sliderValueProperty.set(sliderValueProperty.getValue() * scaleFactor);
                workflownetContentPane.autosize();
            }
        });
    }

    /**
     * Diese Methode initialisiert die Kontext-Menüs der PetriNode- und
     * Arc-Objekt. Es werden die 'initMenuItem'-Methoden der
     * 'ContextMenu'-Klassen aufgerufen und ihnen diese WCC-Instanz übergeben.
     */
    private void initContextMenus() {
        this.petriNodes.forEach((String key, PetriNode value) -> {
            value.getContextMenu().initMenuItems(this);
            Iterator<Arc> iterator = value.getPrevArcs().iterator();
            while (iterator.hasNext()) {
                Arc arc = iterator.next();
                arc.getContextMenu().initMenuItem(this);
            }
        });
    }

    /**
     * Diese Methode entfernt alle markierten PetriNode-Objekte aus dem
     * HashMap-Objekt petriNodes und die entsprechende grafische Darstellung der
     * Knoten aus der Pane workflownetContentPane. Außerdem wird die Methode
     * removeAllConnectedArcs aufgerufen, um die verbundenen Kanten zu
     * entfernen. Abschließend wird der Netzstatus überprüft
     * (checkAndResetStatus).
     *
     */
    public void removePetriNode() {
        Iterator<PetriNode> iterator = SelectionModel.getInstance().getSelectedNodes().iterator();
        while (iterator.hasNext()) {
            PetriNode petriNode = iterator.next();
            removeAllConnectedArcs(petriNode);
            this.workflownetContentPane.getChildren().removeAll(
                    petriNode.getShape(),
                    petriNode.getLabel());
            if (petriNode instanceof Place) {
                this.workflownetContentPane.getChildren().remove(
                        ((Place) petriNode).getMarking());
            }
            this.petriNodes.remove(petriNode.getId());
            this.isSaved = false;
            checkAndResetStatus();
        }
    }

    /**
     * Diese Methode entfernt alle Kanten, die mit dem übergegebenen Knoten
     * verbunden sind. Dabei muss bei jeder Kante das Attribut nextArcs bzw.
     * prevArcs des zweite Knoten der Kante aktualisiert werden. Außerdem werden
     * die entsprechenden Kanten vom Pane workflowContentPane entfernt.
     *
     * @param petriNode Der Knoten, dessen Kanten entfernt werden sollen.
     */
    private void removeAllConnectedArcs(PetriNode petriNode) {
        Iterator<Arc> nextIterator = petriNode.getNextArcs().iterator();
        Iterator<Arc> prevIterator = petriNode.getPrevArcs().iterator();
        while (nextIterator.hasNext()) {
            // Iteration über alle ausgehenden Kanten.
            Arc arc = nextIterator.next();
            nextIterator.remove();
            arc.getEndNode().getPrevArcs().remove(arc);
            this.workflownetContentPane.getChildren().remove(arc);
        }
        while (prevIterator.hasNext()) {
            // Iteration über alle eingehende Kanten.
            Arc arc = prevIterator.next();
            prevIterator.remove();
            arc.getStartNode().getNextArcs().remove(arc);
            this.workflownetContentPane.getChildren().remove(arc);
        }
    }

    /**
     * Diese Methode übeprüft mithilfe des WorkflownetHandler-Objekts
     * wfnHandler, ob die HashMap petriNodes ein gültiges Workflownetz
     * repräsentiert. Diese Information wird im Attribut isWorkflownet
     * festgehalten.
     *
     * Falls ein Workflownetz besteht, werden Start- und Endstelle gesetzt und
     * alle Transitionen aktualisiert.
     *
     * Falls kein Workflownetz besteht, wird die Nachricht, die im Attribut
     * message des wfnHandler-Objektes steht, dem Benutzer angezeigt und alle
     * Knoten des Netzes in den Default-Zustand zurückgesetzt.
     */
    private void checkAndResetStatus() {
        this.isWorkflownet = this.wfnHandler.checkWorkflowStatus(this.petriNodes.values());
        this.mainController.getLeftVBoxController().resetWNetStatus(this.isWorkflownet);
        if (isWorkflownet) {
            setStartAndEnd();
            resetTransitions();
        } else {
            mainController.getLeftVBoxController().showToast(wfnHandler.getMessage());
            Iterator<PetriNode> iterator = this.petriNodes.values().iterator();
            while (iterator.hasNext()) {
                PetriNode petriNode = iterator.next();
                petriNode.resetStatusToDefault(this.workflownetContentPane);
            }
        }
    }

    /**
     * Diese Methode initialisiert das Verhalten der Anwendung, das während des
     * Hinzufügen einer Kante erforderlich ist. Alle Knoten erhalten einen
     * EventHandler, der reagiert, wenn der Mauszeiger in der Benutzeroberfläche
     * über dem entsprechenden Knoten ist und einen EventHandler, der bei einem
     * Klick auf einen gültigen Knoten diesen als Endknoten der hinzuzufügenden
     * Kante definiert. Außerdem wird ein EventFilter definiert, der bei Klick
     * der rechten Maustaste den Hinzufügevorgang abbriicht.
     *
     * @param sourceNode Der Startknoten der Kante, die hinzugefügt werden soll.
     */
    public void initAddArc(PetriNode sourceNode) {
        MyEventHandler myEventHandler = new MyEventHandler();
        Iterator<PetriNode> iterator = petriNodes.values().iterator();
        while (iterator.hasNext()) {
            // Interation über alle Knoten
            PetriNode petriNode = iterator.next();
            if (!petriNode.getClass().equals(sourceNode.getClass())) {
                /**
                 * Der Endknoten der Kante muss unterschiedlich zum Startknoten
                 * sein, Stelle -> Transition oder Transition -> Stelle.
                 */
                petriNode.getShape().addEventHandler(MouseEvent.MOUSE_ENTERED, myEventHandler.mouseEnteredWhileArcAddingEvent);
                petriNode.getShape().addEventHandler(MouseEvent.MOUSE_EXITED, myEventHandler.mouseExitedWhileArcAddingEvent);
                petriNode.getShape().setOnMouseClicked((MouseEvent event) -> {
                    /**
                     * Dieser EventHandler initialisiert eine neue Kante,
                     * aktualisiert die Kantenlisten (prevArcs bzw. nextArcs)
                     * der betroffenen Knoten und entfernt alle EventHandler
                     * wieder, die den Hinzufügeprozess der Kante betreffen.
                     */
                    WorkflownetContainerController.this.countOfArcs += 1;
                    String id = "K" + countOfArcs;
                    Arc arc = new Arc(id, sourceNode, petriNode);
                    sourceNode.getNextArcs().add(arc);
                    petriNode.getPrevArcs().add(arc);
                    workflownetContentPane.getChildren().add(arc);
                    arc.getContextMenu().initMenuItem(WorkflownetContainerController.this);
                    arc.toBack();
                    removeAllAddArcEventHandler(petriNodes.values(), myEventHandler);
                    checkAndResetStatus();
                });
            }
        }

        sourceNode.getShape().getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            /**
             * Dieser EventFilter fängt bei einem Rechtsklick das MouseEvent ab,
             * entfernt alle EventHandler, die den Hinzufügeprozess der Kante
             * betreffen (auch diesen EventFilter) und beendet somit diesen
             * Prozess.
             *
             * @param event Das MouseEvent
             */
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    removeAllAddArcEventHandler(petriNodes.values(), myEventHandler);
                    sourceNode.getShape().getScene().removeEventFilter(MouseEvent.MOUSE_CLICKED, this);
                }
            }
        });
    }

    /**
     * Diese Methode Iteriert über alle Knoten und entfernt die EventHandler,
     * die beim Einfügeprozess einer Kante hinzugefügt wurden.
     *
     * @param petriNodes Die Knoten des Netzes.
     * @param myEventHandler Eine Instanz der Mitgliedsklasse MyEventHandler.
     */
    private void removeAllAddArcEventHandler(Collection<PetriNode> petriNodes, MyEventHandler myEventHandler) {
        Iterator<PetriNode> iterator = petriNodes.iterator();
        while (iterator.hasNext()) {
            // Iteration über alle Netzelemente
            PetriNode petriNode = iterator.next();
            petriNode.setFill();
            Shape shape = petriNode.getShape();
            shape.removeEventHandler(MouseEvent.MOUSE_ENTERED, myEventHandler.mouseEnteredWhileArcAddingEvent);
            shape.removeEventHandler(MouseEvent.MOUSE_EXITED, myEventHandler.mouseExitedWhileArcAddingEvent);
            shape.setOnMouseClicked(null);
        }
    }

    /**
     * Diese Methode aktulisiert, je nach Wert des Attributs isWorkflownetz, die
     * Statusanzeige im linken Bereich dr Benutzeroberfläche.
     */
    void resetStatusView() {
        mainController.getLeftVBoxController().resetWNetStatus(this.isWorkflownet);
    }

    /**
     * Diese Methode überprüft für jede Transition des Netzes, ob sie schaltbar
     * ist. Falls keine schaltbare Transition gefunden wird, wird überprüft, ob
     * ein Deadlock oder der Reguläre Endzustand besteht. Falls ja, wird dem
     * Benutzer eine entsprechende Nachricht ausgegeben.
     */
    private void resetTransitions() {
        boolean isPossibleDeadlock = true;
        Iterator<PetriNode> iterator = this.petriNodes.values().iterator();
        while (iterator.hasNext()) {
            PetriNode node = iterator.next();
            if (node instanceof Transition) {
                Transition trans = (Transition) node;
                trans.checkTickableAndContact();
                resetTickableEvent((Transition) trans);
                if (trans.isTickable()) {
                    isPossibleDeadlock = false;
                }
            }
        }
        if (isPossibleDeadlock) {
            wfnHandler.checkPossibleDeadlock(this.petriNodes.values());
            mainController.getLeftVBoxController().showToast(wfnHandler.getMessage());
        }
    }

    /**
     * Diese Methode setzt die Start- und Endstelle eines gültigen
     * Workflownetzes. Falls keine Stellen im Netz markiert sind, wird die
     * Anfangsstelle markiert.
     */
    private void setStartAndEnd() {
        Place endPlace = wfnHandler.getEndPlace();
        endPlace.setAsEnd();
        Place startPlace = wfnHandler.getStartPlace();
        HashSet<Place> markedPlaces = wfnHandler.getMarkedPlaces(this.petriNodes.values());
        if (markedPlaces.isEmpty()) {
            startPlace.setAsMarked(this.workflownetContentPane);
        } else {
            Iterator<Place> iterator = markedPlaces.iterator();
            while (iterator.hasNext()) {
                ((Place) iterator.next()).setAsMarked(workflownetContentPane);
            }
        }
        startPlace.setAsStart();
    }

    /**
     * Diese Methode definiert einen EventHandler, der eine Transition bei
     * Doppelklick der linken Maustaste schalten lässt. Falls die übergebene
     * Transition im Attribut isTickable den Wert true hält, wird ihr der
     * definierte EventHandler übergeben, falls nicht wird gegebenfalls ein
     * bestehender EventHandler entfernt.
     *
     * @param transition Die Transition, die aktualisiert werden soll.
     */
    private void resetTickableEvent(Transition transition) {
        EventHandler<MouseEvent> clickOnTickableTransitionEvent = (MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    Set<Place> placesForUnsetAsMarked = wfnHandler.getMarkedPrevPlaces(transition);
                    unmarkPlaces(placesForUnsetAsMarked);
                    Set<Place> placesForSetAsMarked = wfnHandler.getUnmarkedNextPlaces(transition);
                    markPlaces(placesForSetAsMarked);
                    transition.setTickable(false);
                    this.isSaved = false;
                    resetTransitions();
                }

            }

        };
        Rectangle rect = (Rectangle) transition.getShape();
        if (transition.isTickable()) {
            rect.setOnMouseClicked(clickOnTickableTransitionEvent);
        } else {
            if (rect.getOnMouseClicked() != null) {
                rect.setOnMouseClicked(null);
            }
         
        }
        transition.setFill();
    }

    /**
     * Diese Methode hebt die Markierung der Stellen auf, die im Set-Objekt
     * placesForUnset übergeben werden.
     *
     * @param placesForUnset Ein Set mit Stellen, deren Markierung aufgehoben
     * werden sollen.
     */
    private void unmarkPlaces(Set<Place> placesForUnset) {
        Iterator<Place> iterator = placesForUnset.iterator();
        while (iterator.hasNext()) {
            iterator.next().unsetAsMarked(this.workflownetContentPane);
        }
    }

    /**
     * Diese Methode markiert alle Stellen, die im Set-Objekt placesForSet
     * übergeben werden.
     *
     * @param placesForSet Ein Set mit Stellen, die markiert werden sollen.
     */
    private void markPlaces(Set<Place> placesForSet) {
        Iterator<Place> iterator = placesForSet.iterator();
        while (iterator.hasNext()) {
            iterator.next().setAsMarked(this.workflownetContentPane);
        }
    }

    /**
     * Diese Methode entfernt eine Kante. Dabei ist darauf zu achten, dass die
     * Kantenlisten der betroffenen Knoten aktualisiert werden. Abschließend
     * wird die entsprechende Kante vom Pane workflownetContentPane entfernt und
     * der Workflownetz-Status überprüft.
     *
     * @param sourceArc Die Kante, die entfernt werden soll.
     */
    public void removeArc(Arc sourceArc) {
        PetriNode startNode = sourceArc.getStartNode();
        PetriNode endNode = sourceArc.getEndNode();
        startNode.getNextArcs().remove(sourceArc);
        endNode.getPrevArcs().remove(sourceArc);
        this.workflownetContentPane.getChildren().remove(sourceArc);
        checkAndResetStatus();
    }

    /**
     * Diese Methode ist leer, sie muss überschrieben werden.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.workflownetContentPane.setOnContextMenuRequested((ContextMenuEvent event) -> {
            SelectionModel.getInstance().clearSelectedNodes();
        });

    }

    void setAsSaved(boolean isSaved) {
        this.isSaved = isSaved;
    }

    /**
     * In dieser Klasse werden die EventHandler definiert, die in den Methoden
     * benötigt werden.
     */
    class MyEventHandler {

        /**
         * Dieser EventHandler definiert die Funktinalität, die ausgeführt wird,
         * wenn, während des Hinzufügens einer Kante, die Position des
         * Mauszeigers von außerhalb des Knoten zu innerhalb des Knotens
         * wechselt.
         */
        EventHandler<MouseEvent> mouseEnteredWhileArcAddingEvent = (MouseEvent event) -> {
            ((Shape) event.getSource()).setFill(MyColors.NODE_ADD_ARC);
        };

        /**
         * Dieser EventHandler definiert die Funktinalität, die ausgeführt wird,
         * wenn, während des Hinzufügens einer Kante, die Position des
         * Mauszeigers von innerhalb des Knoten zu außerhalb des Knotens
         * wechselt.
         */
        EventHandler<MouseEvent> mouseExitedWhileArcAddingEvent = (MouseEvent event) -> {
            ((Shape) event.getSource()).setFill(MyColors.NODE_DEFAULT);
        };
    }
}
