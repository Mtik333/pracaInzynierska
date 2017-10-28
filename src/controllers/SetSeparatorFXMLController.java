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
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static data.ConstStrings.SEPARATOR_COMMA;
import static data.ConstStrings.SEPARATOR_SEMICOLON;

/**
 * FXML Controller class
 *
 * @author Mateusz
 */
public class SetSeparatorFXMLController implements Initializable {

    @FXML
    private ChoiceBox<String> separators;
    @FXML
    private Button cancelButton;
    @FXML
    private Button okButton;

    /**
     * Initializes the controller class.
     *
     * @param url default URL
     * @param rb default ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        separators.getItems().addAll(SEPARATOR_COMMA, SEPARATOR_SEMICOLON);
        if (DataAccessor.getSeparator().contains(ConstStrings.COMMA_NOSPACE)) {
            separators.getSelectionModel().select(ConstStrings.ZERO);
        } else {
            separators.getSelectionModel().select(ConstStrings.ONE);
        }
    }

    @FXML
    public void setSeparator() {
        String chosenSeparator = separators.getSelectionModel().getSelectedItem();
        DataAccessor.setSeparator(chosenSeparator.substring(ConstStrings.ZERO, ConstStrings.ONE));
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void cancelSet() {
        if (DataAccessor.getSeparator().contains(ConstStrings.COMMA_NOSPACE)) {
            separators.getSelectionModel().select(ConstStrings.ZERO);
        } else {
            separators.getSelectionModel().select(ConstStrings.ONE);
        }
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

}
