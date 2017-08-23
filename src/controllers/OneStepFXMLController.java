/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import data.DataAccessor;
import data.graph.InterfaceAnt;
import java.net.URL;
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
public class OneStepFXMLController implements Initializable {

    private InterfaceAnt ant;

    public InterfaceAnt getAnt() {
        return ant;
    }

    public void setAnt(InterfaceAnt ant) {
        this.ant = ant;
    }

    @FXML
    public ComboBox chosenAnt;
    @FXML
    public TextField pickedAttribute;
    @FXML
    public TextField allAttributes;
    @FXML
    public TextField isSolutionFound;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
//        setAnt(DataAccessor.getAllAnts().get(0));
//        pickedAttribute.setText(ant.getPickedAttributes().get(ant.getPickedAttributes().size()-1).getName());
//        for (Vertice vertice : ant.getPickedAttributes()){
//            allAttributes.appendText(vertice.getName()+",");
//        }
//        if (ant.isFoundSolution())
//            isSolutionFound.setText(String.valueOf(true));
//        else isSolutionFound.setText(String.valueOf(false));
        DataAccessor.getAllAnts().forEach((InterfaceAnt newAnt) -> {
            chosenAnt.getItems().add("Ant " + newAnt.getIndex());
        });
        chosenAnt.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                setNewValues();
            }
        });
        chosenAnt.getSelectionModel().select(0);
    }

    @FXML
    public void setNewValues() {
        pickedAttribute.clear();
        setAnt(DataAccessor.getAllAnts().get(chosenAnt.getSelectionModel().getSelectedIndex()));
        pickedAttribute.setText(ant.getPickedAttributes().get(ant.getPickedAttributes().size() - 1).getName());
        allAttributes.clear();
        ant.getPickedAttributes().forEach((vertice) -> {
            allAttributes.appendText(vertice.getName() + ",");
        });
        isSolutionFound.clear();
        if (ant.isFoundSolution()) {
            isSolutionFound.setText(String.valueOf(true));
        } else {
            isSolutionFound.setText(String.valueOf(false));
        }
    }

    @FXML
    public void dismiss(ActionEvent event) {
        Stage stage = (Stage) isSolutionFound.getScene().getWindow();
        this.ant = null;
        stage.close();
    }
}
