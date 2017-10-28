/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import data.ConstStrings;
import data.DataAccessor;
import data.graph.Ant;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Mateusz
 */
public class OneStepFXMLController implements Initializable {

    private Ant ant;

    private void setAnt(Ant ant) {
        this.ant = ant;
    }

    @FXML
    private ComboBox<String> chosenAnt;
    @FXML
    private TextField pickedAttribute;
    @FXML
    private TextField allAttributes;
    @FXML
    private TextField isSolutionFound;

    /**
     * Initializes the controller class.
     *
     * @param url default URL
     * @param rb default ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DataAccessor.getAllAnts().forEach((Ant newAnt) -> chosenAnt.getItems().add(ConstStrings.ANT_VIEW_STRING + newAnt.getIndex()));
        chosenAnt.valueProperty().addListener((ov, t, t1) -> setNewValues());
        chosenAnt.getSelectionModel().select(ConstStrings.ZERO);
    }

    @FXML
    private void setNewValues() {
        pickedAttribute.clear();
        setAnt(DataAccessor.getAllAnts().get(chosenAnt.getSelectionModel().getSelectedIndex()));
        pickedAttribute.setText(ant.getPickedAttributes().get(ant.getPickedAttributes().size() - ConstStrings.ONE).getName());
        allAttributes.clear();
        ant.getPickedAttributes().forEach((vertice) -> allAttributes.appendText(vertice.getName() + ConstStrings.COMMA_NOSPACE));
        isSolutionFound.clear();
        if (ant.isFoundSolution()) {
            isSolutionFound.setText(String.valueOf(true));
        } else {
            isSolutionFound.setText(String.valueOf(false));
        }
    }

    @FXML
    public void dismiss() {
        Stage stage = (Stage) isSolutionFound.getScene().getWindow();
        this.ant = null;
        stage.close();
    }
}
