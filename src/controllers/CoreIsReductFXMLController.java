/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

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
    
    private int coreSize=0;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        if (DataAccessor.getCoreAttributes()!=null)
            coreSize=DataAccessor.getCoreAttributes().size();
        if (coreSize!=0){
            DataAccessor.getCoreAttributes().forEach((attribute) -> {
                    finalReduct.appendText(attribute.getName() + ", ");
                });
        }
        elapsedTime.setText(String.valueOf(DataAccessor.getElapsedTime())+" s");
    }    
    
    @FXML
    public void dismiss(ActionEvent event) {
        Stage stage = (Stage) finalReduct.getScene().getWindow();
        stage.close();
    }
}
