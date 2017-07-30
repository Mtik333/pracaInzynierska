/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import data.DataAccessor;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import data.graph.Edge;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Mateusz
 */
public class ShowEdgeFXMLController implements Initializable {
    private Edge edge;

    public Edge getEdge() {
        return edge;
    }

    public void setEdge(Edge edge) {
        this.edge = edge;
    }
    @FXML public TextField weightAmount;
    @FXML public TextField startVerticeName;
    @FXML public TextField endVerticeName;
    @FXML public TextField pheromoneAmount;
    /**
     * Initializes the controller class.
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
    public void dismiss(ActionEvent event){
        Stage stage = (Stage) weightAmount.getScene().getWindow();
        this.edge=null;
        stage.close();
    }
}
