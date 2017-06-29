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
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import objects.*;

/**
 *
 * @author Mateusz
 */
public class MainDocumentController implements Initializable {
    private File loadedFile; //plik z danymi
    private String separator=","; //separator to ; lub ,
    public ArrayList<DataObject> dataset; //zbior obiektów wczytanych
    private String[] attributesNames;

    @FXML private ComboBox separatorChoice;
    @FXML private MenuItem closeButton;
    @FXML private TextArea examplesToString; //tekst z wypisanymi obiektami
    
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
        loadedFile = fileChooser.showOpenDialog(separatorChoice.getScene().getWindow());
        try {
            fileToObjects(loadedFile);
            objectsToTextArea();
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Cancelled");
        }
    }
    
    private void fileToObjects(File f) throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader(f.getPath()));
        attributesNames=br.readLine().split(separator);
        int j=1; //ilosc obiektow
        String line;
        dataset=new ArrayList<>();
        while ((line = br.readLine()) != null) {
            String oneObject[] = line.split(separator);
            int i = 0; //ktory z kolei atrybut wczytywany
            ArrayList<Attribute> all_attributes = new ArrayList<Attribute>() {
            };
            for (String x : oneObject) {
                Attribute attribute = new Attribute(attributesNames[i], x);
                if (i != oneObject.length - 1) {
                    all_attributes.add(attribute);
                } else {
                    attribute.setDecisionMaking(true);
                    all_attributes.add(attribute);
                }
                i++;
            }
            DataObject newObject = new DataObject(""+j);
            newObject.setAttributes(all_attributes);
            dataset.add(newObject);
            j++;
        }
    }
    private void objectsToTextArea(){
        examplesToString.setText("Parameters: ");
        for (int i=0; i<attributesNames.length; i++){
            examplesToString.appendText(attributesNames[i]+", ");
        }
        examplesToString.deleteText(examplesToString.getLength()-2, examplesToString.getLength());
        examplesToString.appendText(";\nObjects: \n");
        for (DataObject x : dataset){
            examplesToString.appendText(x.toString());
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        separatorChoice.getItems().removeAll();
        separatorChoice.getItems().addAll(",", ";");
        separatorChoice.getSelectionModel().select(",");
        examplesToString.setText("No dataset loaded.");
    }    
    
}
