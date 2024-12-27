
package quine.mccluskey.method.simulator;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;

/**
 * FXML Controller class
 */
public class SolsController implements Initializable {

	//A controller intended for the big box that would contain the solution, in case needed.
    @FXML
    TextArea ap = new TextArea();
    
    //sets the solution box to contain the visible output
    public void addSol(String s){
        ap.setText(s);
    }
   
    //built-in method to initialize the process
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
