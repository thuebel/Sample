package controller;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

/**
 * Diese Kontroll-Klasse dient als übergeordnete Kotroll-Klasse aller Elemente
 * der Benutzeroberfläche. Eine Instanz dieser Kalsse wird aus der
 * JavaFx-start-Methode, über die Datei main.fxml, erzeugt.
 *
 * @author Tobias Hübel, 5509840
 */
public class MainController implements Initializable {

    /**
     * Innerhalb dieses BorderPane-Objekts sind die restlichen Elemente der
     * Benutzeroberfläche enthalten.
     */
    @FXML
    private BorderPane mainBorderPane;

    /**
     * Eine Instanz der Kontrollklasse, die für die Funktionalität des Menüs im
     * oberen Bereich der Anwendung verantwortlich ist.
     */
    @FXML
    private TopMenuController topMenuController;

    /**
     * Eine Instanz der Kontrollklasse, die für die Funktionalität des linken
     * Bereichs der Benutzeroberfläche verantwortlich ist.
     */
    @FXML
    private LeftVBoxController leftVBoxController;

    /**
     * Eine Instanz der Kontrollklasse, die für die Funktionalität des zentralen
     * Bereichs der Benutzeroberfläche verantwortlich ist.
     */
    @FXML
    private TabPaneController tabPaneController;

    /**
     * Dieses Map-Objekt beinhaltet alle
     * WorkflownetContainerController-Instatzen. Für jeden geöffneten Tab in der
     * Benutzeroberfläche existiert eine Instanz.
     */
    private final Map<Tab, WorkflownetContainerController> wccMap = new HashMap<>();

    /**
     * Das FileChooser-Objekt, das zum Speichern und Laden eines Workflownetzes
     * verwendet wird.
     */
    private final FileChooser fileChooser = new FileChooser();

    /**
     * In dieser ObjectProperty wird das, beim Speichern oder Laden einer Datei,
     * verwendete Verzeichnis festgehalten.
     */
    private final SimpleObjectProperty<File> lastKnownDirectory = new SimpleObjectProperty<>();

    /**
     * Diese Methode wird beim Erzeugen einer Instanz aufgerufen. In ihr werden
     * die initMain-Methoden der restlichen Kontroll-Klassen aufgerufen. So wird
     * eine Verbindung zwischen den Kontroll-Klassen realisiert. Außerdem wird
     * das initiale Verzeichnis des FileChooser-Objekt an den Wert der
     * ObjectProperty lastKnownDirectory gebunden.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        topMenuController.initMain(this);
        leftVBoxController.initMain(this);
        tabPaneController.initMain(this);
        fileChooser.initialDirectoryProperty().bind(lastKnownDirectory);
    }

    /**
     * Diese Methode gibt das Attribut tabPaneController zurück
     *
     * @return Das Attribut tabPaneController
     */
    public TabPaneController getTabPaneController() {
        return tabPaneController;
    }

    /**
     * Diese Methode gibt das Attribut fileChooser zurück.
     *
     * @return Das Attribut fileChooser.
     */
    public FileChooser getFileChooser() {
        return fileChooser;
    }

    /**
     * Diese Methode gibt das Attribut tabPane der TabPaneController-Instanz zurück.
     *
     * @return Das Attribut tabPane der TabPaneController-Instanz.
     */
    public TabPane getTabPane() {
        return this.tabPaneController.getTabPane();
    }

    /**
     * Diese Methode gibt das Attribut mainBorderPane zurück.
     *
     * @return Das Attribut mainBorderPane.
     */
    public BorderPane getMainBorderPane() {
        return mainBorderPane;
    }

    /**
     * Diese Methode gibt das Attribut leftVBoxController zurück.
     *
     * @return Das Attribut leftVBoxController.
     */
    public LeftVBoxController getLeftVBoxController() {
        return leftVBoxController;
    }

    /**
     * Diese Methode gibt das Attribut wccMap zurück.
     *
     * @return Das Attribut wccMap.
     */
    public Map<Tab, WorkflownetContainerController> getWccMap() {
        return wccMap;
    }

    /**
     * Diese Methode setzt den Wert der ObjectProperty lastKnownDirectory.
     *
     * @param file Den Wert, den die ObjectProperty annehmen soll.
     */
    public void setLastKnownDirectory(File file) {
        this.lastKnownDirectory.setValue(file);
    }

    /**
     * Diese Methode fügt der Map wccMap ein Element hinzu. Dabei wird auch die Methode
     * setMainController der WorkflowContainerController-Instanz (WCC-Instanz)
     * aufgerufen und ihr so diese MainController-Instanz übergeben.
     *
     * @param tab Die Tab-Instanz, die hinzugefügt werden soll.
     * @param wcc Die WCC-Instanz, die hinzugefügt werden soll.
     */
    public void putIntoWccMap(Tab tab, WorkflownetContainerController wcc) {
        wcc.setMainController(this);
        this.wccMap.put(tab, wcc);
    }

}
