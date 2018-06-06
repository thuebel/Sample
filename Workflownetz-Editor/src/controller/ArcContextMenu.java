package controller;

import petriNodes.Arc;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

/**
 * Diese Klasse implemntiert das Kontext-Menü, das bei einem ContextMenuRequest
 * auf ein Arc-Objekt geöffnet wird.
 *
 * @author Tobias Hübel, 5509840
 */
public class ArcContextMenu extends ContextMenu {

    /**
     * Das einzige Menuelement des Kontextmenüs. Es initialisiert das Entfernen
     * einer Kante.
     */
    private final MenuItem deleteArc;

    /**
     * Das Arc-Objekt, das den Request ausgelöst hat.
     */
    private final Arc sourceArc;

    /**
     * Der Konstruktor initialisiert die Attribute sourceArc und deleteArc,
     * setzt den Anzeigetext des Menüelements und fügt es zu diesem Kontextmenü
     * hinzu.
     *
     * @param sourceArc Das Arc-Obkekt, das den Request ausgelöst hat.
     */
    public ArcContextMenu(Arc sourceArc) {
        this.sourceArc = sourceArc;
        deleteArc = new MenuItem("Löschen");
        this.getItems().add(deleteArc);
    }

    /**
     * Diese Methode initialisiert die Funktion des Menüelements in Abhängigkeit zu einem
     * Objekt der Klasse WorkfloanetContainerController.
     *
     * @param wcc Eine Instanz der WorkflownetContainerController-Klasse
     */
    public void initMenuItem(WorkflownetContainerController wcc) {
        deleteArc.setOnAction((ActionEvent event) -> {
            wcc.removeArc(sourceArc);
        });
    }

}
