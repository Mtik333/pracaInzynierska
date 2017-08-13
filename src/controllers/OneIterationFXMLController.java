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
public class OneIterationFXMLController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    public TextField newReductSize;
    @FXML
    public TextField reductList;
    @FXML
    public TextField previousReductSize;
    @FXML
    public TextField foundSolutions;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        newReductSize.setText(String.valueOf(DataAccessor.getCurrentReduct().size()));
        DataAccessor.getCurrentReduct().forEach((x) -> {
            reductList.appendText(x.getName() + ", ");
        });
        previousReductSize.setText(String.valueOf(DataAccessor.getListOfReducts().get(DataAccessor.getPerformedIterations() - 1).size()));
        int i = 0;
        i = DataAccessor.getAllAnts().stream().filter((ant) -> (ant.isFoundSolution())).map((_item) -> 1).reduce(i, Integer::sum);
        foundSolutions.setText(String.valueOf(i));
    }

    @FXML
    public void dismiss(ActionEvent event) {
        Stage stage = (Stage) foundSolutions.getScene().getWindow();
        stage.close();
    }
}
