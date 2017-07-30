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
    
    @FXML public ChoiceBox algorithmChoice;
    @FXML public TextField pheromoneImportance;
    @FXML public TextField weightImportance;
    @FXML public TextField loopLimit;
    @FXML public TextField antsNumber;
    @FXML public TextField pheromoneConstant;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        algorithmChoice.getItems().addAll(JSACO, RSFSACO);
        pheromoneImportance.setText(String.valueOf(DataAccessor.getPheromoneRelevance()));
        weightImportance.setText(String.valueOf(DataAccessor.getEdgeRelevance()));
        loopLimit.setText(String.valueOf(DataAccessor.getLoopLimit()));
        antsNumber.setText(String.valueOf(DataAccessor.getAntsNumber()));
        if (DataAccessor.getDataset()==null){
            antsNumber.setEditable(false);
        }
        pheromoneConstant.setText(String.valueOf(DataAccessor.getConstantForUpdating()));
        algorithmChoice.getSelectionModel().select(0);
    }    
    
    @FXML
    public void setSettings(ActionEvent event){
        DataAccessor.setPheromoneRelevance(Double.valueOf(pheromoneImportance.getText()));
        DataAccessor.setEdgeRelevance(Double.valueOf(weightImportance.getText()));
        DataAccessor.setLoopLimit(Integer.valueOf(loopLimit.getText()));
        DataAccessor.setAntsNumber(Integer.valueOf(antsNumber.getText()));
        DataAccessor.setConstantForUpdating(Integer.valueOf(pheromoneConstant.getText()));
        cancelSettings(null);
    }
    
    @FXML
    public void cancelSettings(ActionEvent event){
        Stage stage = (Stage) algorithmChoice.getScene().getWindow();
        stage.close();
    }
}
