package controllers;

import data.ConstStrings;
import data.DataAccessor;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static data.ConstStrings.*;

public class FishAlgorithmSettingsFXMLController implements Initializable {

    @FXML
    private ChoiceBox<String> algorithmChoice;
    @FXML
    private TextField qualityImportance;
    @FXML
    private TextField subsetImportance;
    @FXML
    private TextField deltaImprotance;
    @FXML
    private TextField loopLimit;
    @FXML
    private TextField fishNumber;
    @FXML
    private TextField fishVisual;
    @FXML
    private TextField fruitlessSearches;

    /**
     * Initializes the controller class.
     *
     * @param url default URL
     * @param rb  default ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        algorithmChoice.getItems().addAll(FSARSR, FSARSRC);
        if (DataAccessor.getFishAlgorithmType().equals(FSARSRC)) {
            algorithmChoice.getSelectionModel().selectLast();
            DataAccessor.setFishAlgorithmType(FSARSRC);
        } else {
            algorithmChoice.getSelectionModel().selectFirst();
        }
        qualityImportance.setText(String.valueOf(DataAccessor.getFishQualityRelevance()));
        subsetImportance.setText(String.valueOf(DataAccessor.getFishSubsetRelevance()));
        deltaImprotance.setText(String.valueOf(DataAccessor.getFishDeltaRelevance()));
        loopLimit.setText(String.valueOf(DataAccessor.getLoopLimit()));
        fishNumber.setText(String.valueOf(DataAccessor.getFishNumber()));
        if (DataAccessor.getDataset() == null) {
            fishNumber.setEditable(false);
        }
        fishVisual.setText(String.valueOf(DataAccessor.getFishVisual()));
        fruitlessSearches.setText(String.valueOf(DataAccessor.getFruitlessSearches()));
    }

    @FXML
    public void setSettings() {
        if (algorithmChoice.getSelectionModel().getSelectedIndex() == ConstStrings.ALGORITHM_DEFAULT_INDEX) {
            DataAccessor.setAlgorithmType(FSARSR);
        } else {
            DataAccessor.setAlgorithmType(FSARSRC);
        }
        DataAccessor.setFishQualityRelevance(Double.valueOf(qualityImportance.getText()));
        DataAccessor.setFishSubsetRelevance(Double.valueOf(subsetImportance.getText()));
        DataAccessor.setFishDeltaRelevance(Double.valueOf(deltaImprotance.getText()));
        DataAccessor.setLoopLimit(Integer.valueOf(loopLimit.getText()));
        DataAccessor.setFishNumber(Integer.valueOf(fishNumber.getText()));
        DataAccessor.setFishVisual(Integer.valueOf(fishVisual.getText()));
        DataAccessor.setFishMaxCycle(Integer.valueOf(fruitlessSearches.getText()));
        cancelSettings();
    }

    @FXML
    private void cancelSettings() {
        Stage stage = (Stage) algorithmChoice.getScene().getWindow();
        stage.close();
    }
}
