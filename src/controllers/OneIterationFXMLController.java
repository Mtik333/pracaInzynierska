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
public class OneIterationFXMLController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private TextField newReductSize;
    @FXML
    private TextField reductList;
    @FXML
    private TextField previousReductSize;
    @FXML
    private TextField foundSolutions;

    private int coreSize = ConstStrings.ZERO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (DataAccessor.getCoreAttributes() != null) {
            coreSize = DataAccessor.getCoreAttributes().size();
        }
        newReductSize.setText(String.valueOf(DataAccessor.getCurrentReduct().size() + coreSize));
        if (coreSize != ConstStrings.ZERO) {
            DataAccessor.getCoreAttributes().forEach((attribute) -> reductList.appendText(attribute.getName() + ConstStrings.COMMA_SPACE));
        }
        DataAccessor.getCurrentReduct().forEach((x) -> reductList.appendText(x.getName() + ConstStrings.COMMA_SPACE));
        if (DataAccessor.getPerformedIterations() == ConstStrings.ONE) {
            previousReductSize.setText(String.valueOf(DataAccessor.getGraph().getVertices().size() + coreSize));
        } else {
            previousReductSize.setText(String.valueOf(DataAccessor.getListOfReducts().get(DataAccessor.getPerformedIterations() - ConstStrings.ONE).size() + coreSize));
        }
        int i = ConstStrings.ZERO;
        i = DataAccessor.getAllAnts().stream().filter((ant) -> (ant.isFoundSolution())).map((_item) -> 1).reduce(i, Integer::sum);
        foundSolutions.setText(String.valueOf(i));
    }

    @FXML
    public void dismiss() {
        Stage stage = (Stage) foundSolutions.getScene().getWindow();
        stage.close();
    }
}
