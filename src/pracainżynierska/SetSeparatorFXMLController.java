/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pracainżynierska;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import objects.Logic;

/**
 * FXML Controller class
 *
 * @author Mateusz
 */
public class SetSeparatorFXMLController implements Initializable {
    @FXML private GridPane gridPane;
    public Logic logic;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
    public Logic getLogic() {
        return logic;
    }

    public void setLogic(Logic logic) {
        this.logic = logic;
        setupStage();
    }
    private void setupStage(){
        gridPane = new GridPane();
        String[][] discMatrix = logic.getIndiscMatrix();
        final int rowsAmount = logic.getDataset().size();
        for (int i=0; i<rowsAmount; i++){
            for (int j=0; j<rowsAmount; j++){
                TextField tf = new TextField();
                tf.setPrefHeight(50);
                tf.setPrefWidth(50);
                tf.setAlignment(Pos.CENTER);
                tf.setEditable(false);
                tf.setText(discMatrix[i][j]);
                gridPane.add(tf, j, i);
            }
        }
        Scene scene = new Scene(gridPane, 500, 500);    
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Random Binary Matrix (JavaFX)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
