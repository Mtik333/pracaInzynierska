package sample;

import data.ConstStrings;
import data.DataAccessor;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource(ConstStrings.MAIN_DOCUMENT_FXML_RES));
        Scene scene = new Scene(root);
        DataAccessor.setPrimaryStage(stage);
        stage.setTitle(ConstStrings.PROGRAM_NAME);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
