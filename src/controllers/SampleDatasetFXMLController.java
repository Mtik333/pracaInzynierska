/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import com.sun.org.apache.xpath.internal.SourceTree;
import data.DataAccessor;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.File;
import java.net.*;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Mateusz
 */
public class SampleDatasetFXMLController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    public TextArea datasetDescription;
    @FXML
    private ComboBox<String> exampleDataset;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        URL url2 = getClass().getProtectionDomain().getCodeSource().getLocation();
        try {
            String path = url2.toURI().getPath();
            if (path.contains("jar")){
                path=path.replace("JavaFXApp.jar", "examples");
            }
            else path=path.concat("examples");
            System.out.println(path);
            File file = new File(path);
            File[] listOfFiles = file.listFiles();
            if (listOfFiles != null) {
                for (File child : listOfFiles) {
                    if (child.getName().contains(".csv"))
                        exampleDataset.getItems().add(child.getName());
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        exampleDataset.getSelectionModel().selectFirst();
    }

    @FXML
    public void loadFile() throws URISyntaxException {
        URL url2 = getClass().getProtectionDomain().getCodeSource().getLocation();
        try {
            String path = url2.toURI().getPath();
            if (path.contains("jar")){
                path=path.replace("JavaFXApp.jar", "examples/").concat(exampleDataset.getSelectionModel().getSelectedItem());
            }
            else path=path.concat("examples/").concat(exampleDataset.getSelectionModel().getSelectedItem());
            File file = new File(path);
            DataAccessor.setFile(file);
            Stage stage = (Stage) exampleDataset.getScene().getWindow();
            stage.close();
        }catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
