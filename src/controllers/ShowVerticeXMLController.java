/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import data.ConstStrings;
import data.DataAccessor;
import data.JensenLogic;
import data.graph.Vertice;
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
public class ShowVerticeXMLController implements Initializable {

    private Vertice vertice;

    public Vertice getVertice() {
        return vertice;
    }

    public void setVertice(Vertice vertice) {
        this.vertice = vertice;
    }
    
    @FXML
    public TextField verticeName;
    @FXML
    public TextField antsAmount;
    @FXML
    public TextField isInReduct;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setVertice(DataAccessor.getAnalyzedVertice());
        verticeName.setText(vertice.getName());
        if (DataAccessor.isLoadedData()) {
            antsAmount.setText(String.valueOf(JensenLogic.returnAntsNumberOnVertice(vertice)));
        } else {
            antsAmount.setText(String.valueOf(ConstStrings.ZERO));
        }
        if (DataAccessor.getCurrentReduct()!=null){
            if (DataAccessor.ifVerticeInReduct(vertice)){
                isInReduct.setText(String.valueOf(true));
            }
            else isInReduct.setText(String.valueOf(false));
        }
        else isInReduct.setText(String.valueOf(false));
    }

    @FXML
    public void dismiss(ActionEvent event) {
        Stage stage = (Stage) verticeName.getScene().getWindow();
        this.vertice = null;
        stage.close();
    }
}
