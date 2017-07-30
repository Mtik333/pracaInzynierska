/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import static data.ConstStrings.*;
import data.DataAccessor;
import data.NewLogic;
import data.graph.Edge;
import data.graph.Vertice;
import data.roughsets.Attribute;
import data.roughsets.DataObject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Mateusz
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private TextArea examplesToString;
    @FXML
    private AnchorPane solver;
    private List<Label> labels;
    private List<Line> lines;
    private Map<Line, Edge> edgeLines;
    private Map<Label, Vertice> verticeLabels;
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;
    private NewLogic newLogic = new NewLogic();
    @FXML
    public void loadDataset(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty(USERDIR)));
        fileChooser.setTitle(CHOOSE_FILE);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(CSV_FILES, CSV_FILTER)
        );
        try {
            DataAccessor.setFile(fileChooser.showOpenDialog(examplesToString.getScene().getWindow()));
            if (DataAccessor.getFile()==null)
                return;
            if (!DataAccessor.parseFile()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(PARSING_ERROR);
                alert.setContentText(WRONG_SEPARATOR);
                alert.showAndWait();
            } else {
                objectsToTextArea(DataAccessor.getAllAttributes(), DataAccessor.getDataset());
            }
            newLogic.generateGraph();
            drawGraph();
//            logic.fillIndiscMatrix();
        } catch (Exception e) {
            e.printStackTrace();
        }
        DataAccessor.setLoadedData(true);

    }

    @FXML
    public void setSeparator(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(SET_SEPARATOR_FXML_RES));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle(SET_SEPARATOR_TITLE);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(DataAccessor.getPrimaryStage());
        stage.setScene(new Scene(root1));
        stage.show();
    }

    @FXML
    public void programSettings(ActionEvent event) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ALGORITHM_SETTINGS_FXML_RES));
        Parent root1 = (Parent) fxmlLoader.load();
        AlgorithmSettingsFXMLController algorithmSettings = fxmlLoader.<AlgorithmSettingsFXMLController>getController();
        Stage stage = new Stage();
        stage.setTitle(ALGORITHM_SETTINGS_TITLE);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(DataAccessor.getPrimaryStage());
        stage.setScene(new Scene(root1));
        stage.show();
    }
    
    @FXML
    public void editExamples(ActionEvent event) throws IOException {
        if (DataAccessor.isLoadedData()) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(EDIT_EXAMPLES_FXML_RES));
            Parent root1 = (Parent) fxmlLoader.load();
            EditExamplesController eec = fxmlLoader.<EditExamplesController>getController();
            Stage stage = new Stage();
            stage.setTitle(EDIT_EXAMPLES_TITLE);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(DataAccessor.getPrimaryStage());
            stage.setScene(new Scene(root1));
            stage.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(ALERT_ERROR_TITLE_NO_DATA);
            alert.setContentText(NO_DATA_TO_LOAD);
            alert.showAndWait();
        }
    }

    @FXML
    public void exitApp(ActionEvent event) {
        Platform.exit();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    private void objectsToTextArea(List<Attribute> attributes, List<DataObject> objects) {
        examplesToString.setText(TEXTAREA_PARAMETERS);
        for (Attribute attribute : attributes) {
            examplesToString.appendText(attribute.getName() + ", ");
        }
        examplesToString.deleteText(examplesToString.getLength() - 2, examplesToString.getLength());
        examplesToString.appendText(TEXTAREA_APPEND);
        for (DataObject x : objects) {
            examplesToString.appendText(x.toString());
        }
    }

    public void drawGraph() {
        solver.getChildren().clear();
        labels = new ArrayList<>();
        lines = new ArrayList<>();
        edgeLines = new HashMap<>();
        verticeLabels = new HashMap<>();
        double middleX = solver.getWidth() / 2;
        double middleY = solver.getHeight() / 2;
        double degreesIncrement = 360 / (DataAccessor.getGraph().getVertices().size());
        for (int i = 0; i < DataAccessor.getGraph().getVertices().size(); i++) {
                double cosinus = Math.cos(Math.toRadians(i * degreesIncrement));
                double sinus = Math.sin(Math.toRadians(i * degreesIncrement));
                Label label = new Label(DataAccessor.getGraph().getVertices().get(i).getName());
                label.setMinWidth(100);
                label.setAlignment(Pos.CENTER);
                label.setTranslateX(middleX + 150 * sinus);
                label.setTranslateY(middleY + 150 * cosinus);
                label.setStyle("-fx-border-color:red;-fx-background-color:white");
                label.setFont(javafx.scene.text.Font.font(18));
                label.setTextAlignment(TextAlignment.CENTER);
                label.setOnMousePressed(circleOnMousePressedEventHandler);
                label.setOnMouseDragged(circleOnMouseDraggedEventHandler);
                labels.add(label);
                verticeLabels.put(label, DataAccessor.getGraph().getVertices().get(i));
        }
        for (int i = 0; i < labels.size(); i++) {
            for (int j = i + 1; j < labels.size(); j++) {
                lines.add(connect(labels.get(i), labels.get(j)));
            }
        }
        for (int i = 0; i<lines.size(); i++){
            edgeLines.put(lines.get(i), DataAccessor.getGraph().getEdges().get(i));
        }
        solver.getChildren().addAll(lines);
        solver.getChildren().addAll(labels);
        
        labels.forEach((label) -> {
            label.toFront();
        });
    }

    
    @FXML
    private void antsRandomButton(ActionEvent t){
        if (DataAccessor.isLoadedData())
            newLogic.initializeAntsRandom();
    }
    
    @FXML
    private void antsOneStep(ActionEvent t){
        if (DataAccessor.isLoadedData()){
            if (DataAccessor.getAllAnts()==null){
                newLogic.initializeAntsRandom();
            }
            newLogic.stepToNextVertice();
        }
    }
    
    private Line connect(Label c1, Label c2) {
        Line line = new Line();
        line.startXProperty().bind(Bindings.createDoubleBinding(() -> {
            Bounds b = c1.getBoundsInParent();
            return b.getMinX() + b.getWidth() / 2;
        }, c1.boundsInParentProperty()));
        line.startYProperty().bind(Bindings.createDoubleBinding(() -> {
            Bounds b = c1.getBoundsInParent();
            return b.getMinY() + b.getHeight() / 2;
        }, c1.boundsInParentProperty()));
        line.endXProperty().bind(Bindings.createDoubleBinding(() -> {
            Bounds b = c2.getBoundsInParent();
            return b.getMinX() + b.getWidth() / 2;
        }, c2.boundsInParentProperty()));
        line.endYProperty().bind(Bindings.createDoubleBinding(() -> {
            Bounds b = c2.getBoundsInParent();
            return b.getMinY() + b.getHeight() / 2;
        }, c2.boundsInParentProperty()));
        line.setStrokeWidth(5);
        //line.setStrokeLineCap(StrokeLineCap.BUTT);
        //line.getStrokeDashArray().setAll(1.0, 4.0);
        line.toBack();
        line.setOnMouseClicked(lineOnMouseEventHandler);
        return line;
    }

    EventHandler<MouseEvent> circleOnMousePressedEventHandler
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            if (t.getButton() == MouseButton.SECONDARY){
                try {
                    DataAccessor.setAnalyzedVertice(verticeLabels.get((Label)t.getSource()));
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(SHOW_VERTICE_FXML_RES));
                    Parent root1 = (Parent) fxmlLoader.load();
                    ShowVerticeXMLController eec = fxmlLoader.<ShowVerticeXMLController>getController();
                    Stage stage = new Stage();
                    stage.setTitle(SHOW_VERTICE_TITLE);
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(DataAccessor.getPrimaryStage());
                    stage.setScene(new Scene(root1));
                    stage.show();
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else {
            orgSceneX = t.getSceneX();
            orgSceneY = t.getSceneY();
            orgTranslateX = ((Label) (t.getSource())).getTranslateX();
            orgTranslateY = ((Label) (t.getSource())).getTranslateY();
            ((Label) (t.getSource())).toFront();
            }
        }
    };

    EventHandler<MouseEvent> circleOnMouseDraggedEventHandler
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            double offsetX = t.getSceneX() - orgSceneX;
            double offsetY = t.getSceneY() - orgSceneY;
            double newTranslateX = orgTranslateX + offsetX;
            double newTranslateY = orgTranslateY + offsetY;
            ((Label) (t.getSource())).setTranslateX(newTranslateX);
            ((Label) (t.getSource())).setTranslateY(newTranslateY);

        }
    };

    EventHandler<MouseEvent> lineOnMouseEventHandler
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            if (t.getButton() == MouseButton.SECONDARY) {
                try {
                    DataAccessor.setAnalyzedEdge(edgeLines.get((Line)t.getSource()));
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(SHOW_EDGE_FXML_RES));
                    Parent root1 = (Parent) fxmlLoader.load();
                    ShowEdgeFXMLController eec = fxmlLoader.<ShowEdgeFXMLController>getController();
                    Stage stage = new Stage();
                    stage.setTitle(SHOW_EDGE_TITLE);
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(DataAccessor.getPrimaryStage());
                    stage.setScene(new Scene(root1));
                    stage.show();
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    };
}
