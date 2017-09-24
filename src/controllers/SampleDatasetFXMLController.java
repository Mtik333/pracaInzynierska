/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import data.ConstStrings;
import data.DataAccessor;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

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
    public ComboBox exampleDataset;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        File dir = new File(ConstStrings.EXAMPLES_DIRECTORY_PATH);
        File dir_list[] = dir.listFiles();
        if (dir_list != null) {
            for (File child : dir_list) {
                exampleDataset.getItems().add(child.getName());
            }
        }
        exampleDataset.getSelectionModel().selectFirst();
    }

    @FXML
    public void loadFile(ActionEvent event) throws IOException {
        File file = new File(ConstStrings.EXAMPLES_DIRECTORY_PATH + ConstStrings.SLASH + exampleDataset.getSelectionModel().getSelectedItem().toString());
        DataAccessor.setFile(file);
        Stage stage = (Stage) exampleDataset.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void dismiss(ActionEvent event) {
        Stage stage = (Stage) exampleDataset.getScene().getWindow();
        stage.close();
    }
}
