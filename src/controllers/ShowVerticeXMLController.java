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
public class ShowVerticeXMLController implements Initializable {

    private Vertice vertice;

    private void setVertice(Vertice vertice) {
        this.vertice = vertice;
    }
    
    @FXML
    private TextField verticeName;
    @FXML
    private TextField antsAmount;
    @FXML
    private TextField isInReduct;

    /**
     * Initializes the controller class.
     *
     * @param url default URL
     * @param rb default ResourceBundle
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
    public void dismiss() {
        Stage stage = (Stage) verticeName.getScene().getWindow();
        this.vertice = null;
        stage.close();
    }
}
