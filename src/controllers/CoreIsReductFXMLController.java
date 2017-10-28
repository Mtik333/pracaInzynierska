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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Mateusz
 */
public class CoreIsReductFXMLController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private TextField finalReduct;
    @FXML
    private TextField elapsedTime;

    private int coreSize = ConstStrings.ZERO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (DataAccessor.getCoreAttributes() != null) {
            coreSize = DataAccessor.getCoreAttributes().size();
        }
        if (coreSize != ConstStrings.EMPTY_CORE) {
            DataAccessor.getCoreAttributes().forEach((attribute) -> finalReduct.appendText(attribute.getName() + ConstStrings.COMMA_SPACE));
        }
        elapsedTime.setText(String.valueOf(DataAccessor.getElapsedTime()) + ConstStrings.SECONDS);
    }

    @FXML
    public void dismiss() {
        Stage stage = (Stage) finalReduct.getScene().getWindow();
        stage.close();
    }
}
