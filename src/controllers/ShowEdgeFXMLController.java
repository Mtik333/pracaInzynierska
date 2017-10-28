/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import data.DataAccessor;
import data.graph.Edge;
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
public class ShowEdgeFXMLController implements Initializable {

    private Edge edge;

    private void setEdge(Edge edge) {
        this.edge = edge;
    }
    
    @FXML
    private TextField weightAmount;
    @FXML
    private TextField startVerticeName;
    @FXML
    private TextField endVerticeName;
    @FXML
    private TextField pheromoneAmount;

    /**
     * Initializes the controller class.
     *
     * @param url default URL
     * @param rb default ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setEdge(DataAccessor.getAnalyzedEdge());
        weightAmount.setText(String.valueOf(edge.getWeight()));
        startVerticeName.setText(edge.getStart().getName());
        endVerticeName.setText(edge.getEnd().getName());
        pheromoneAmount.setText(String.valueOf(edge.getPheromone()));
    }

    @FXML
    public void dismiss() {
        Stage stage = (Stage) weightAmount.getScene().getWindow();
        this.edge = null;
        stage.close();
    }
}
