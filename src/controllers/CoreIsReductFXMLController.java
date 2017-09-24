/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import data.ConstStrings;
import data.DataAccessor;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    public TextField finalReduct;
    @FXML
    public TextField elapsedTime;

    private int coreSize = ConstStrings.ZERO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (DataAccessor.getCoreAttributes() != null) {
            coreSize = DataAccessor.getCoreAttributes().size();
        }
        if (coreSize != ConstStrings.EMPTY_CORE) {
            DataAccessor.getCoreAttributes().forEach((attribute) -> {
                finalReduct.appendText(attribute.getName() + ConstStrings.COMMA_SPACE);
            });
        }
        elapsedTime.setText(String.valueOf(DataAccessor.getElapsedTime()) + ConstStrings.SECONDS);
    }

    @FXML
    public void dismiss(ActionEvent event) {
        Stage stage = (Stage) finalReduct.getScene().getWindow();
        stage.close();
    }
}
