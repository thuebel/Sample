package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javax.xml.stream.XMLStreamException;
import resources.MyMessages;

/**
 * In dieser Klasse werden die Funktionen der Elemente des oberen Menüs
 * implementiert.
 *
 * @author Tobias Hübel, 5509840
 */
public class TopMenuController implements Initializable {

    /**
     * Das MenuBar-Objekt, das das obere Menü realisiert.
     */
    @FXML
    private MenuBar topMenu;

    /**
     * Das MainController-Objekt stellt eine Verbindung zu anderen Elementen der
     * Benutzeroberfläche her.
     */
    private MainController mainController;


    /**
     * Diese Methode wird aufgerufen, wenn das Menüelement newTab aktiviert
     * wird. Sie ruft die Methode createNewTabWithWcc der
     * TabPaneController-Instanz auf
     *
     * @throws IOException
     */
    @FXML
    private void newTab() throws IOException {
        this.mainController.getTabPaneController().createNewTabWithWcc();
    }

    /**
     * Diese Methode öffnet ein Fenster für die Auswahl einer pnml-Datei.
     * Inititalisiert nach Auswahl das Laden der Datei.
     *
     * @throws IOException
     */
    @FXML
    private void loadPNML() throws IOException {
        File file = mainController.getFileChooser().showOpenDialog(topMenu.getScene().getWindow());
        if (file != null) {
            mainController.setLastKnownDirectory(file.getParentFile());
            WorkflownetContainerController wcc = this.mainController.getTabPaneController().createNewTabWithWcc();
            try {
                wcc.initWccFromFileloading(file);
            } catch (XMLStreamException e) {
                wcc.getTab().setText("new Tab");
                mainController.getLeftVBoxController().showToast(MyMessages.NO_VALID_FILE);
            }

        }
    }

    /**
     * Diese Methode initialisiert das Speichern des aktuell geöffneten
     * Petrinetzes.
     */
    @FXML
    private void savePNML() {
        TabPane tabPane = this.mainController.getTabPane();
        if (!tabPane.getTabs().isEmpty()) {
            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            File file = mainController.getFileChooser().showSaveDialog(tabPane.getScene().getWindow());
            if (file != null) {
                mainController.setLastKnownDirectory(file.getParentFile());
                WorkflownetContainerController wcc = mainController.getWccMap().get(tab);
                wcc.initSavePNML(file);
            }
        } else {
            mainController.getLeftVBoxController().showToast(MyMessages.NO_NET);
        }
    }

    /**
     * Diese Methode schließt die Anwendung.
     */
    @FXML
    private void closeApplication() {
        Platform.exit();
    }

    /**
     * Diese Methode stellt eine Verbindung zum MainController her.
     *
     * @param mainController Der Wert, den das Attribut mainController annehmen
     * soll.
     */
    void initMain(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Diese Methode ist leer, sie muss überschrieben werden.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

}
