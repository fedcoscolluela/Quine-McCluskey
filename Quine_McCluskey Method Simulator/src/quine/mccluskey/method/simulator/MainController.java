package quine.mccluskey.method.simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MainController {
	
	//initializing the empty fields for minterm and don't care inputs in the GUI
    @FXML
    ListView<String> mintermsList = new ListView<>();
    @FXML
    ListView<String> dontCaresList = new ListView<>();
    @FXML
    TextField mintermsTextField = new TextField();
    @FXML
    TextField dontCaresTextField = new TextField();
    @FXML
    //text area for the solution steps
    TextArea ap = new TextArea();
    
    Stage stage1;

    //cleaning the text area and sets its content to "s"
    public void addSol(String s){
        ap.clear();
        ap.setText(s);
    }
    
    //initializing integer array list as container for minterms and don't cares (if any)
    ArrayList<Integer> addedMinTerms = new ArrayList<Integer>();
    ArrayList<Integer> addedDontCares = new ArrayList<Integer>();
    
    //creating instance object of MainClass.java
    MainAlgorithm obj = new MainAlgorithm();
    
    //adding minterms to the list
    public void mintermsListAdd(){
        String minterm = mintermsTextField.getText();
        int value = Integer.parseInt(minterm);

        //index value is -1 if it is not yet in the array. Otherwise, it already is repeated
        if(addedMinTerms.indexOf(value) > -1 || addedDontCares.indexOf(value) > -1){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText("The same term is already in the list.");
            alert.showAndWait();	//waits user to close the warning
        }
        else{
            mintermsList.getItems().add(minterm);
            addedMinTerms.add(value);
        }
        mintermsTextField.clear();
    }
    
    //adding don't cares (if any) to the list
     public void dontCaresListAdd(){
        String dontCare = dontCaresTextField.getText();
        int value = Integer.parseInt(dontCare);
        // check if already added
        if(addedDontCares.indexOf(value) > -1 || addedMinTerms.indexOf(value) > -1){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText("The same term is already in the list.");
            alert.showAndWait();
        }
        else{
            obj.addDontCare(value);
            dontCaresList.getItems().add(dontCare);
            addedDontCares.add(value);
        }
        dontCaresTextField.clear();
    }
    
    //confirming the inputted values
    public void applyButton(){
        if(addedMinTerms.isEmpty()){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText("You have not entered any term.");
            alert.showAndWait();
            return;
        }
        
        obj = new MainAlgorithm();
        
        //adding all minterms to the MainClass for the actual computation
        for(int i = 0 ; i < addedMinTerms.size() ; i ++)
            obj.addMinterm(addedMinTerms.get(i));
        
        //adding all don't cares (if any) to the MainClass for the actual computation
        for(int i = 0 ; i < addedDontCares.size() ; i ++)
            obj.addDontCare(addedDontCares.get(i));
        
        //calling all calculation methods from the MainClass
        obj.tabulateNewGroups();
        obj.getPrimeImplicants();
        obj.fillChart();
		obj.printTabulation();
		obj.printChart();
		obj.printPrime();
		obj.solutionPrinter();
    }
    
    //displays the answer
    public void answerButton() throws IOException{
       if(addedMinTerms.isEmpty()){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText("You have not entered any term.");
            alert.showAndWait();
            return;
        }
        addSol(obj.printFormattedSols());
       
    }
    
    //shows the solution
    public void stepsButton(){
        if(addedMinTerms.isEmpty()){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText("You have not entered any term.");
            alert.showAndWait();
            return;
        }
        addSol(obj.finalString());
    }
    
    //deletes all entry and turns the GUI back to the beginning
    public void clearButton(){
        obj = new MainAlgorithm();
        addedDontCares = new ArrayList<Integer>();
        addedMinTerms = new ArrayList<Integer>();
        mintermsList.getItems().clear();
        dontCaresList.getItems().clear();
        ap.clear();
    }
    
    //closes the interface/app itself
    public void exitButton(){
        System.exit(0);
    }
    
    //saves the answer and solution in Output.txt file
    public void saveFile() throws FileNotFoundException{
        if(addedMinTerms.isEmpty()){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText("You have not entered any term.");
            alert.showAndWait();
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text doc(*.txt)", "*.txt"));
        fileChooser.setInitialFileName("Output.txt");
        File downloadFile = fileChooser.showSaveDialog(stage1);
        try{
        PrintWriter writer = new PrintWriter(downloadFile, "UTF-8");
        writer.println(obj.printFormattedSols().replaceAll("\n", System.lineSeparator()));
        writer.println();
        writer.println(obj.finalString().replaceAll("\n", System.lineSeparator()));
        writer.close();
        } catch (IOException e) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText("Sorry, no file detected.");
            alert.showAndWait();
            return;
        }
    }
}
