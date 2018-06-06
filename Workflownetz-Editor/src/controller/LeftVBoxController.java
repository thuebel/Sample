package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import resources.MyColors;
import resources.MyMessages;
import workflownetLogic.WorkflownetHandler;

/**
 * In dieser Klasse werden die Funktionen der Elemente im linken Teil der
 * Benutzeroberfläche implementiert.
 *
 * @author Tobias Hübel, 5509840
 */
public class LeftVBoxController implements Initializable {

    /**
     * Das Slider-Objekt der Benutzeroberfläche.
     */
    @FXML
    private Slider zoomSlider;

    /**
     * Das Circle-Objekt, das den Status anzeigt.
     */
    @FXML
    private Circle WNetStatus;

    /**
     * Das TextArea-Objekt, das Meldungen für den Benutzer anzeigt.
     */
    @FXML
    private TextArea messageArea;

    /**
     * Das MainController-Objekt stellt eine Verbindung zu anderen Elementen der
     * Benutzeroberfläche her.
     */
    private MainController mainController;

    /**
     * Ein Timeline-Objekt, das die Ein- Ausblendefunktion der Benutzermeldungen
     * realisiert.
     */
    private Timeline messageTimeline;
    @FXML
    private VBox leftVBox;
    @FXML
    private Button addTransitionBtn;
    @FXML
    private Button addPlaceBtn;
    @FXML
    private Button refreshBtn;
    @FXML
    private Button searchCycleBtn;

    /**
     * Diese Methode wird beim Erzeugen einer Instanz aufgerufen. Sie
     * initialisiert das Attribut messageTimeline.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initTimeline();
        this.zoomSlider.setValue(0.7);
    }

    /**
     * Diese Methode gibt den aktuell geöffneten WorkflownetContainerController
     * zurück.
     *
     * @return WorkflownetContainerController Die Kontroll-Klasse des geöffneten
     * Tabs. Falls kein Tab geöffnet ist, wird null zurückgegeben und eine
     * Meldung angezeigt.
     */
    private WorkflownetContainerController getWccOfSelectedTab() {
        TabPaneController TpaneController = mainController.getTabPaneController();
        Tab tab = TpaneController.getSelectionModel().getSelectedItem();
        WorkflownetContainerController wcc = mainController.getWccMap().get(tab);
        if (wcc == null) {
            showToast(MyMessages.NO_TAB);
        }
        return wcc;
    }

    /**
     * Diese Methode initialisiert das Hinzufügen einer Transistion. Diese
     * Methode wird aufgerufen, wenn der Button addTransitionBtn aktiviert wird.
     * Dieses Verhalten ist in der Datei view.leftVBox.fxml definiert.
     */
    @FXML
    private void addTransition() {
        WorkflownetContainerController wcc = getWccOfSelectedTab();
        if (wcc != null) {
            wcc.initNewTransition();
        }
    }

    /**
     * Diese Methode initialisiert das Hinzufügen einer Stelle. Diese Methode
     * wird aufgerufen, wenn der Button addPlaceBtn aktiviert wird. Dieses
     * Verhalten ist in der Datei view.leftVBox.fxml definiert.
     */
    @FXML
    private void addPlace() {
        WorkflownetContainerController wcc = getWccOfSelectedTab();
        if (wcc != null) {
            wcc.initNewPlace();
        }
    }

    /**
     * Diese Methode initialisiert das Zurücksetzen eines Workflownetzes. Diese
     * Methode wird aufgerufen, wenn der Button addPlaceBtn aktiviert wird.
     * Dieses Verhalten ist in der Datei view.leftVBox.fxml definiert.
     */
    @FXML
    private void refreshNet(ActionEvent event) {
        WorkflownetContainerController wcc = getWccOfSelectedTab();
        if (wcc != null) {
            wcc.refreshNet();
        }
    }

    /**
     * Diese Methode initialisiert die Verbindung zum MainController.
     *
     * @param mainController Ein Maincontroller-Objekt.
     */
    void initMain(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Diese Methode gibt das Slider-Element zurück.
     *
     * @return Das Attribut zoomSlider.
     */
    public Slider getZoomSlider() {
        return zoomSlider;
    }

    /**
     * Je nach übergebenem Wert bewirkt diese Methode das Schalten der
     * Status-Anzeige (WNetStatus) auf grün (true) oder auf rot (false).
     *
     * @param isWorkflownet Wird verwendet, um den Workflownetz-Status
     * festzuhalten.
     */
    void resetWNetStatus(boolean isWorkflownet) {
        if (isWorkflownet) {
            this.WNetStatus.setFill(MyColors.STATUS_TRUE);
        } else {
            this.WNetStatus.setFill(MyColors.STATUS_FALSE);
        }
    }

    /**
     * Diese Methode setzt den Text des Attributs messageArea und startet die
     * messageTimeline.
     *
     * @param message Der Text, der angezeigt werden soll.
     */
    void showToast(String message) {
        this.messageArea.setText(message);
        this.messageTimeline.play();
    }

    /**
     * Diese Methode initialisiert die Timline inklusive der KeyFrames, die sie
     * durchläuft. Dabei werden für unterschiedlichen Zeitpunkte
     * unterschiedliche Opazitäts-Werte definiert.
     */
    private void initTimeline() {
        KeyFrame frame0 = new KeyFrame(
                Duration.millis(0),
                new KeyValue(messageArea.opacityProperty(), 0)
        );
        KeyFrame frame1 = new KeyFrame(
                Duration.millis(500),
                new KeyValue(messageArea.opacityProperty(), 0.5)
        );
        KeyFrame frame2 = new KeyFrame(
                Duration.millis(1000),
                new KeyValue(messageArea.opacityProperty(), 1)
        );
        KeyFrame frame3 = new KeyFrame(
                Duration.millis(6500),
                new KeyValue(messageArea.opacityProperty(), 0.5)
        );
        KeyFrame frame4 = new KeyFrame(
                Duration.millis(7000),
                new KeyValue(messageArea.opacityProperty(), 0)
        );
        this.messageTimeline = new Timeline(frame0, frame1, frame2, frame3, frame4);
    }

    @FXML
    private void searchCycle() {
        WorkflownetContainerController wcc = getWccOfSelectedTab();
        if (wcc != null) {
            WorkflownetHandler wfnHandler = wcc.getWfnHandler();
            boolean isCycle =  wfnHandler.checkCycle();
            if(!isCycle){
                showToast(wfnHandler.getMessage());
            }
            
        }
    }

}
