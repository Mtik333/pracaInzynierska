/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import data.DataAccessor;
import data.roughsets.Attribute;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Mateusz
 */
public class OneReductFXMLController implements Initializable {

    @FXML
    public ComboBox chosenIteration;
    @FXML
    public TextField finalReduct;
    @FXML
    public TextField elapsedTime;
    @FXML
    public TextField attributesInIteration;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        for (int i = 0; i < DataAccessor.getListOfReducts().size(); i++) {
            chosenIteration.getItems().add("Iteration " + i);
        }
        chosenIteration.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                setNewValues();
            }
        });
        chosenIteration.getSelectionModel().select(0);
        DataAccessor.getCurrentReduct().forEach((attribute) -> {
            finalReduct.appendText(attribute.getName() + ", ");
        });

    }

    @FXML
    public void dismiss(ActionEvent event) {
        Stage stage = (Stage) finalReduct.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void setNewValues() {
        attributesInIteration.clear();
        List<Attribute> list = DataAccessor.getListOfReducts().get(chosenIteration.getSelectionModel().getSelectedIndex());
        list.forEach((attribute) -> {
            attributesInIteration.appendText(attribute.getName() + ", ");
        });
    }
}
