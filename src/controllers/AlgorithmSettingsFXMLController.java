/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import static data.ConstStrings.*;
import data.DataAccessor;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Mateusz
 */
public class AlgorithmSettingsFXMLController implements Initializable {

    @FXML
    public ChoiceBox algorithmChoice;
    @FXML
    public TextField pheromoneImportance;
    @FXML
    public TextField weightImportance;
    @FXML
    public TextField loopLimit;
    @FXML
    public TextField antsNumber;
    @FXML
    public TextField pheromoneConstant;
    @FXML
    public TextField pheromoneEvaporation;
    @FXML
    public TextField epsilonValue;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        algorithmChoice.getItems().addAll(JSACO, RSFSACO);
        if (DataAccessor.getAlgorithmType().equals(RSFSACO)){
            algorithmChoice.getSelectionModel().selectLast();
            DataAccessor.setAlgorithmType(RSFSACO);
        }
        else algorithmChoice.getSelectionModel().selectFirst();
        pheromoneImportance.setText(String.valueOf(DataAccessor.getPheromoneRelevance()));
        weightImportance.setText(String.valueOf(DataAccessor.getEdgeRelevance()));
        loopLimit.setText(String.valueOf(DataAccessor.getLoopLimit()));
        antsNumber.setText(String.valueOf(DataAccessor.getAntsNumber()));
        if (DataAccessor.getDataset() == null) {
            antsNumber.setEditable(false);
        }
        pheromoneConstant.setText(String.valueOf(DataAccessor.getConstantForUpdating()));
        pheromoneEvaporation.setText(String.valueOf(DataAccessor.getPheromoneEvaporation()));
        epsilonValue.setText(String.valueOf(DataAccessor.getEpsilonValue()));
    }

    @FXML
    public void setSettings(ActionEvent event) {
        if (algorithmChoice.getSelectionModel().getSelectedIndex()==0)
            DataAccessor.setAlgorithmType(JSACO);
        else DataAccessor.setAlgorithmType(RSFSACO);
        DataAccessor.setPheromoneRelevance(Double.valueOf(pheromoneImportance.getText()));
        DataAccessor.setEdgeRelevance(Double.valueOf(weightImportance.getText()));
        DataAccessor.setLoopLimit(Integer.valueOf(loopLimit.getText()));
        DataAccessor.setAntsNumber(Integer.valueOf(antsNumber.getText()));
        DataAccessor.setConstantForUpdating(Double.valueOf(pheromoneConstant.getText()));
        DataAccessor.setPheromoneEvaporation(Double.valueOf(pheromoneEvaporation.getText()));
        DataAccessor.setEpsilonValue(Double.valueOf(epsilonValue.getText()));
        cancelSettings(null);
    }

    @FXML
    public void cancelSettings(ActionEvent event) {
        Stage stage = (Stage) algorithmChoice.getScene().getWindow();
        stage.close();
    }
}
