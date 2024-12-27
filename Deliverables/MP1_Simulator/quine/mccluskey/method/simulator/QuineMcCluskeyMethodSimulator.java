
package quine.mccluskey.method.simulator;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class QuineMcCluskeyMethodSimulator extends Application {
    
	//Calls Main.fxml which displays the main scene/GUI 
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        Scene scene = new Scene(root);
        primaryStage.resizableProperty().setValue(Boolean.FALSE);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Quine-McCluskey Method Simulator");
        primaryStage.show();
        
    }

    //required method to launch the scene
    public static void main(String[] args) {
        launch(args);
    }
    
}
