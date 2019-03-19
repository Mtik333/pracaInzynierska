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

import static data.ConstStrings.JSACO;
import static data.ConstStrings.RSFSACO;

public class FishAlgorithmSettingsFXMLController implements Initializable {

    @FXML
    private ChoiceBox<String> algorithmChoice;
    @FXML
    private TextField pheromoneImportance;
    @FXML
    private TextField weightImportance;
    @FXML
    private TextField loopLimit;
    @FXML
    private TextField antsNumber;
    @FXML
    private TextField pheromoneConstant;
    @FXML
    private TextField pheromoneEvaporation;
    @FXML
    private TextField epsilonValue;
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
        algorithmChoice.getItems().addAll(JSACO, RSFSACO);
        if (DataAccessor.getAlgorithmType().equals(RSFSACO)) {
            algorithmChoice.getSelectionModel().selectLast();
            DataAccessor.setAlgorithmType(RSFSACO);
        } else {
            algorithmChoice.getSelectionModel().selectFirst();
        }
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
        fruitlessSearches.setText(String.valueOf(DataAccessor.getFruitlessSearches()));
    }

    @FXML
    public void setSettings() {
        if (algorithmChoice.getSelectionModel().getSelectedIndex() == ConstStrings.ALGORITHM_DEFAULT_INDEX) {
            DataAccessor.setAlgorithmType(JSACO);
        } else {
            DataAccessor.setAlgorithmType(RSFSACO);
        }
        DataAccessor.setPheromoneRelevance(Double.valueOf(pheromoneImportance.getText()));
        DataAccessor.setEdgeRelevance(Double.valueOf(weightImportance.getText()));
        DataAccessor.setLoopLimit(Integer.valueOf(loopLimit.getText()));
        DataAccessor.setAntsNumber(Integer.valueOf(antsNumber.getText()));
        DataAccessor.setConstantForUpdating(Double.valueOf(pheromoneConstant.getText()));
        DataAccessor.setPheromoneEvaporation(Double.valueOf(pheromoneEvaporation.getText()));
        DataAccessor.setEpsilonValue(Double.valueOf(epsilonValue.getText()));
        DataAccessor.setFruitlessSearches(Integer.valueOf(fruitlessSearches.getText()));
        cancelSettings();
    }

    @FXML
    private void cancelSettings() {
        Stage stage = (Stage) algorithmChoice.getScene().getWindow();
        stage.close();
    }
}
