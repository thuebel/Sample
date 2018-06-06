package controller;

import petriNodes.PetriNode;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.stage.WindowEvent;
import petriNodes.SelectionModel;

/**
 * Diese Klasse implemntiert das Kontext-Menü, das bei einem ContextMenuRequest
 * auf ein PetriNode-Objekt (Stelle oder Transition) geöffnet wird.
 *
 * @author Tobias Hübel, 5509840
 */
public class PetriNodeContextMenu extends ContextMenu {

    /**
     * Menüelement, das das Hinzufügen einer Kante initialisiert.
     */
    private final MenuItem addArc;

    /**
     * Menüelement, das das Entfernen eines Knoten (Stelle oder Transition)
     * initialisiert.
     */
    private MenuItem deleteShape;

    /**
     * Menüelement, das das Ändern eines Labeltextes initialisiert.
     */
    private final MenuItem changeLabel;

    /**
     * Der Knoten, der den Request ausgelöst hat.
     */
    private final PetriNode sourceNode;

    /**
     * Der Konstruktor initialisiert alle Atribute, setzt die Anzeigetexte der
     * Menüelemente und definiert das ActionEvent des Menüelements changeLabel.
     * (öffnen eines Dialogfensters)
     *
     * @param sourceNode
     */
    public PetriNodeContextMenu(PetriNode sourceNode) {
        this.sourceNode = sourceNode;
        this.addArc = new MenuItem("Kante hinzufügen");
        this.deleteShape = new MenuItem("Löschen");
        this.changeLabel = new MenuItem("Label ändern");
        this.changeLabel.setOnAction((ActionEvent event) -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Label ändern");
            dialog.setHeaderText("Label ändern");
            dialog.setContentText("Geben Sie einen neues Label ein: ");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newLabel -> sourceNode.getLabel().setText(newLabel));
        });
        this.getItems().addAll(changeLabel, deleteShape, addArc);
        this.setOnShowing((WindowEvent event) -> {
            if (SelectionModel.getInstance().getSelectedNodes().size() > 1) {
                deleteShape.setText("Markierte Knoten löschen");
            } else {
                deleteShape.setText("Löschen");
            }
        });
    }

    /**
     * Diese Methode initialisiert die Funktionen der Menüelemente deleteShape
     * und addArc in Abhängigkeit zu einem Objekt der Klasse
     * WorkfloanetContainerController.
     *
     * @param wcc Eine Instanz der WorkflownetContainerController-Klasse
     */
    public void initMenuItems(WorkflownetContainerController wcc) {
        deleteShape.setOnAction((ActionEvent event) -> {
            wcc.removePetriNode();
        });
        addArc.setOnAction((ActionEvent event) -> {
            wcc.initAddArc(sourceNode);
        });
    }
}
