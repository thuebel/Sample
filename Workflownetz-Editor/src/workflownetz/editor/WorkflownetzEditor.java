package workflownetz.editor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Dies ist der Startpunkt der Anwendung. In ihr ist die Main-Methode
 * implementiert
 *
 * @author Tobias Hübel, 5509840
 */
public class WorkflownetzEditor extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane root = FXMLLoader.load(getClass().getResource("/view/main.fxml"));

        Scene scene = new Scene(root);

        stage.getIcons().add(new Image("/resources/logo.PNG"));
        stage.setTitle("Tobias Hübel, 5509840");
        
        
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
