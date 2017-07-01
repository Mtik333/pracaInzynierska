/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pracainżynierska;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import objects.*;
import org.w3c.dom.events.MouseEvent;

/**
 *
 * @author Mateusz
 */
public class MainDocumentController implements Initializable {
    public Logic logic;
    private String separator=","; //separator to ; lub ,

    @FXML private ComboBox separatorChoice;
    @FXML private MenuItem closeButton;
    @FXML private TextArea examplesToString; //tekst z wypisanymi obiektami
    @FXML private TabPane tabPane;
    @FXML private AnchorPane solver;
    
    @FXML
    private void setSeparator(ActionEvent event){
        separator=separatorChoice.getSelectionModel().getSelectedItem().toString();
        System.out.println(separator);
    }
    
    @FXML
    private void closeApp(ActionEvent event){
        Platform.exit();
    }
    
    @FXML
    private void loadDataset(ActionEvent event) throws FileNotFoundException{
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setTitle("Choose file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV files","*.csv")
            );
        try {
            logic.setFile(fileChooser.showOpenDialog(separatorChoice.getScene().getWindow()));
            logic.fileToObjects(separator);
            logic.setGraph(null);
            objectsToTextArea(logic.getAttributesNames(), logic.getDataset());
            drawGraph();
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Cancelled");
        }
    }

    
    private void objectsToTextArea(String[] names, List<DataObject> objects){
        examplesToString.setText("Parameters: ");
        for (int i=0; i<names.length; i++){
            examplesToString.appendText(names[i]+", ");
        }
        examplesToString.deleteText(examplesToString.getLength()-2, examplesToString.getLength());
        examplesToString.appendText(";\nObjects: \n");
        for (DataObject x : objects){
            examplesToString.appendText(x.toString());
        }
    }
    
    private void drawGraph(){
        solver.getChildren().clear();
        for (int i=0; i<logic.getAttributesNames().length-1; i++){
            Circle circle = new Circle(i*100, 155, 25);
            Text text = new Text(i*100, 205, logic.getAttributesNames()[i]);
            Group group = new Group(circle, text);         
            text.setBoundsType(TextBoundsType.VISUAL); 
            solver.getChildren().add(group);
            //System.out.println(solver.getChildren().toString());
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        separatorChoice.getItems().removeAll();
        separatorChoice.getItems().addAll(",", ";");
        separatorChoice.getSelectionModel().select(",");
        examplesToString.setText("No dataset loaded.");
        this.logic = new Logic();
        System.out.println(com.sun.javafx.runtime.VersionInfo.getRuntimeVersion());
        System.out.println(tabPane.getSelectionModel().getSelectedIndex());
        tabPane.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue()==1){
                    if (logic.getDataset()==null){
                        tabPane.getSelectionModel().select(0);
                    }
                    else {
                        //drawGraph();
                    }
                }
            }
        }); 
    }    
    
}
