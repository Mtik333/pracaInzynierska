/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import data.ConstStrings;
import data.DataAccessor;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
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
    private ComboBox<String> exampleDataset;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        URL url2 = getClass().getProtectionDomain().getCodeSource().getLocation();
        try {
            File file = getFile(url2);
            File[] listOfFiles = file.listFiles();
            if (listOfFiles != null) {
                for (File child : listOfFiles) {
                    if (child.getName().contains(ConstStrings.CSV_EXTENSION))
                        exampleDataset.getItems().add(child.getName());
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        exampleDataset.getSelectionModel().selectFirst();
    }

    private File getFile(URL url2) throws URISyntaxException {
        String path = url2.toURI().getPath();
        if (path.contains(ConstStrings.JAR_EXTENSION)){
            path=path.replace(ConstStrings.JAR_NAME, ConstStrings.EXAMPLES_DIR);
        }
        else path=path.concat(ConstStrings.EXAMPLES_DIR);
        return new File(path);
    }

    @FXML
    public void loadFile() {
        URL url2 = getClass().getProtectionDomain().getCodeSource().getLocation();
        try {
            String path = url2.toURI().getPath();
            if (path.contains(ConstStrings.JAR_EXTENSION)){
                path=path.replace(ConstStrings.JAR_NAME, ConstStrings.EXAMPLES_DIR+"/").concat(exampleDataset.getSelectionModel().getSelectedItem());
            }
            else path=path.concat(ConstStrings.EXAMPLES_DIR+"/").concat(exampleDataset.getSelectionModel().getSelectedItem());
            File file = new File(path);
            DataAccessor.setFile(file);
            Stage stage = (Stage) exampleDataset.getScene().getWindow();
            stage.close();
        }catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
