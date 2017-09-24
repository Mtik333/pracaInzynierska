/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import data.ConstStrings;
import static data.ConstStrings.*;
import data.DataAccessor;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Mateusz
 */
public class SetSeparatorFXMLController implements Initializable {

    @FXML
    public ChoiceBox separators;
    @FXML
    public Button cancelButton;
    @FXML
    public Button okButton;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
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
    public void setSeparator(ActionEvent event) {
        String chosenSeparator = separators.getSelectionModel().getSelectedItem().toString();
        DataAccessor.setSeparator(chosenSeparator.substring(ConstStrings.ZERO, ConstStrings.ONE));
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void cancelSet(ActionEvent event) {
        if (DataAccessor.getSeparator().contains(ConstStrings.COMMA_NOSPACE)) {
            separators.getSelectionModel().select(ConstStrings.ZERO);
        } else {
            separators.getSelectionModel().select(ConstStrings.ONE);
        }
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

}
