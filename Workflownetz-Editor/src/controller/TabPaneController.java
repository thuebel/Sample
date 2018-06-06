package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * In dieser Klasse werden die Funktionen der Elemente im zentralen Teil der
 * Benutzeroberfläche implementiert.
 *
 * @author Tobias Hübel, 5509840
 */
public class TabPaneController implements Initializable {

    /**
     * Das TabPane-Objekt, das die geöffneten Tabs organisiert.
     */
    @FXML
    private TabPane tabPane;

    /**
     * Das MainController-Objekt stellt eine Verbindung zu anderen Elementen der
     * Benutzeroberfläche her.
     */
    @FXML
    private MainController mainController;

    /**
     * Mithilfe des SelectionModel-Objekts kann unter anderem auf das geöffnete
     * Tab zugegriffen werden. Dieses wird an verschiedenen Stellen benötigt.
     */
    private SelectionModel<Tab> selectionModel;

    /**
     * Diese Methode gibt das Attribut selectionModel zurück.
     *
     * @return Das Attribut selectionModel.
     */
    public SelectionModel<Tab> getSelectionModel() {
        return selectionModel;
    }

    /**
     * Diese Methode gibt das Attribut tabPane zurück.
     *
     * @return Das Attribut tabPane.
     */
    public TabPane getTabPane() {
        return tabPane;
    }

    /**
     * Diese Methode initialisiert das Attribut mainController. Diese Methode wird von der
     * Maincontroller-Instanz aufgerufen. Außerdem wird das Attribut
     * SelectionModel initialisiert (initSelectionModel).
     *
     * @param mainController Der Wert, den das Attribut mainController annehmen
     * soll.
     */
    void initMain(MainController mainController) {
        this.mainController = mainController;
        initSelectionModel();
    }

    /**
     * Diese Methode initialisiert das Attribut SelectionModel. Es wird ein ChnageListener
     * implementiert, der beim Wechsel zu einem anderen Tab die Stausanzeige
     * aktualisiert.
     */
    private void initSelectionModel() {
        selectionModel = this.tabPane.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) -> {
            if (selectionModel.getSelectedItem() != null) {
                WorkflownetContainerController wcc = this.mainController.getWccMap().get(newValue);
                wcc.resetStatusView();
            }
        });
    }

    /**
     * Diese Methode generiert mithilfe einer FXMLLoader-Instanz durch die Datei
     * WorkflownetContainer.fxml eine neue
     * WorkflownetContainerController-Instanz und eine neue Tab-Instanz. Diese
     * werden der Methode addNewTAb übergeben.
     *
     * @return Das erzeugte WorkflownetContainerController-Objekt
     * @throws IOException
     */
    protected WorkflownetContainerController createNewTabWithWcc() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/WorkflownetContainer.fxml"));
        Tab tab = loader.load();
        tab.setText("new Tab");
        WorkflownetContainerController wcc = loader.getController();
        wcc.getWorkflownetContentPane().setPadding(new Insets(40));
        addNewTab(tab, wcc);
        return wcc;
    }

    /**
     * In dieser Methode wird dem TabPane-Objekt ein neues Tab hinzugefügt, das
     * Attribut wccMap des MainControllers aktualisiert und die
     * WorkflowContainerController-Instanz initialisiert.
     *
     * @param tab
     * @param wcc
     */
    void addNewTab(Tab tab, WorkflownetContainerController wcc) {
        this.mainController.putIntoWccMap(tab, wcc);
        this.tabPane.getTabs().add(0, tab);
        selectionModel = this.tabPane.getSelectionModel();
        selectionModel.select(tab);
        wcc.setMainController(mainController);
        wcc.initScrollable();
        wcc.setAsSaved(true);
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
