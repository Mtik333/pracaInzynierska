/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import data.ConstStrings;
import data.DataAccessor;
import data.roughsets.Attribute;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static data.ConstStrings.FSARSR;

/**
 * FXML Controller class
 *
 * @author Mateusz
 */
public class OneReductFXMLController implements Initializable {

    @FXML
    private ComboBox<String> chosenIteration;
    @FXML
    private TextField finalReduct;
    @FXML
    private TextField reductSize;
    @FXML
    private TextField elapsedTime;
    @FXML
    private TextField attributesInIteration;

    private int coreSize = ConstStrings.ZERO;

    /**
     * Initializes the controller class.
     *
     * @param url default URL
     * @param rb  default ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (DataAccessor.getCoreAttributes() != null && !DataAccessor.isIsFishAlgorithmLastCalculated()) {
            coreSize = DataAccessor.getCoreAttributes().size();
        }
        for (int i = 0; i < DataAccessor.getListOfReducts().size(); i++) {
            chosenIteration.getItems().add(ConstStrings.ITERATION_VIEW_STRING + i);
        }
        chosenIteration.valueProperty().addListener((ov, t, t1) -> setNewValues());
        chosenIteration.getSelectionModel().select(0);
        if (coreSize != ConstStrings.ZERO && !DataAccessor.isIsFishAlgorithmLastCalculated()) {
            DataAccessor.getCoreAttributes().forEach((attribute) -> finalReduct.appendText(attribute.getName() + ConstStrings.COMMA_SPACE));
        }
        DataAccessor.getCurrentReduct().forEach((attribute) -> finalReduct.appendText(attribute.getName() + ConstStrings.COMMA_SPACE));
        reductSize.setText(String.valueOf(DataAccessor.getCurrentReduct().size() + coreSize));
        elapsedTime.setText(String.valueOf(DataAccessor.getElapsedTime()) + ConstStrings.SECONDS);
    }

    @FXML
    public void dismiss() {
        Stage stage = (Stage) finalReduct.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void setNewValues() {
        attributesInIteration.clear();
        List<Attribute> list = DataAccessor.getListOfReducts().get(chosenIteration.getSelectionModel().getSelectedIndex());
        if (coreSize != ConstStrings.ZERO) {
            DataAccessor.getCoreAttributes().forEach((attribute) -> attributesInIteration.appendText(attribute.getName() + ConstStrings.COMMA_SPACE));
        }
        list.forEach((attribute) -> attributesInIteration.appendText(attribute.getName() + ConstStrings.COMMA_SPACE));
    }
}
