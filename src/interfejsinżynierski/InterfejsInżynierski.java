/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfejsinżynierski;

import data.ConstStrings;
import data.DataAccessor;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Mateusz
 */
public class InterfejsInżynierski extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(ConstStrings.MAIN_DOCUMENT_FXML_RES));
        Scene scene = new Scene(root);
        DataAccessor.setPrimaryStage(stage);
        stage.setTitle(ConstStrings.PROGRAM_NAME);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
