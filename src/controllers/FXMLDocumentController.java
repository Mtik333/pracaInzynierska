/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import data.ChineseLogic;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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
import javafx.scene.paint.Color;
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
    private TextArea examplesToString; //pole tekstowe na obiekty
    @FXML
    private AnchorPane solver; //widok grafu
    private List<Label> labels; //lista wierzcholkow (grafika)
    private List<Line> lines; //lista krawedzi (grafika)
    private static Map<Line, Edge> edgeLines; //zmapowanie krawedzi rzeczywistych na graficzne
    private Map<Label, Vertice> verticeLabels; //zmapowanie wierzcholkow rzeczywistych na graficzne
    double orgSceneX, orgSceneY; //do przenoszenia wierzcholkow/krawedzi
    double orgTranslateX, orgTranslateY; //do przenoszenia wierzcholkow/krawedzi
    private NewLogic newLogic = new NewLogic(); //cała logika aplikacji
    private ChineseLogic chineseLogic = new ChineseLogic(); //logika algorytmu chinskiego

    @FXML
    public void exitApp(ActionEvent event) {
        Platform.exit();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    //wczytuje zestaw danych
    @FXML
    public void loadDataset(ActionEvent event) {
        DataAccessor.resetValues();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty(USERDIR)));
        fileChooser.setTitle(CHOOSE_FILE);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(CSV_FILES, CSV_FILTER)
        );
        try {
            DataAccessor.setFile(fileChooser.showOpenDialog(examplesToString.getScene().getWindow()));
            if (DataAccessor.getFile() == null) {
                return;
            }
            if (!DataAccessor.parseFile()) {
                chineseLogic = new ChineseLogic();
                newLogic = new NewLogic();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(PARSING_ERROR);
                alert.setContentText(WRONG_SEPARATOR);
                alert.showAndWait();
            } else {
                objectsToTextArea(DataAccessor.getAllAttributes(), DataAccessor.getDataset());
            }
            //chineseLogic.generateGraph();
            newLogic.generateGraph();
            drawGraph();
        } catch (IOException ioException) {
            ioException.printStackTrace(System.out);
        }
        DataAccessor.setLoadedData(true);

    }

    //ustawienie mrówek losowo
    @FXML
    private void antsRandomButton(ActionEvent t) {
        if (DataAccessor.isLoadedData()) {
            newLogic.initializeAntsRandom();
        }
    }

    //wykonanie jednego wyboru kolejnej krawędzi przez mrówkę
    @FXML
    private void antsOneStep(ActionEvent t) {
        if (DataAccessor.isLoadedData()) {
            if (DataAccessor.getAllAnts() == null || DataAccessor.isCalculatedReductInIteration()) {
                newLogic.initializeAntsRandom();
                showStepStats();
            } else if (newLogic.stepToNextVertice()) {
                List<List<Attribute>> reducts = DataAccessor.getListOfReducts();
                System.out.println("xd");
                newLogic.initializeAntsRandom();
                colorEdges();
                showStepStats();
            }
            else showStepStats();
        }
    }

    //wykonanie jednej iteracji algorytmu
    @FXML
    private void antsOneIteration(ActionEvent t) {
        if (DataAccessor.isLoadedData()) {
            System.out.println(DataAccessor.getCurrentIter());
            if (DataAccessor.getCurrentIter() == 0 || DataAccessor.isCalculatedReductInIteration()) {
                newLogic.initializeAntsRandom();
            }
            newLogic.performOneIteration();
        }
        List<List<Attribute>> reducts = DataAccessor.getListOfReducts();
        colorEdges();
        showIterationStats();
        System.out.println("xd");
    }

    //znalezienie reduktu przez wykonanie określonej liczby iteracji algorytmu
    @FXML
    private void antsFindReduct(ActionEvent t) {
        if (DataAccessor.isLoadedData()) {
            Service<Void> service = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            newLogic.findReduct();
                            //Background work                       
                            final CountDownLatch latch = new CountDownLatch(1);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        showAlgorithmStats();
                                        //FX Stuff done here
                                    } finally {
                                        latch.countDown();
                                    }
                                }
                            });
                            latch.await();
                            //Keep with the background work
                            return null;
                        }
                    };
                }
            };
            service.start();

            //newLogic.findReduct();
        }
        //List<List<Attribute>> reducts = DataAccessor.getListOfReducts();
        //showAlgorithmStats();
        System.out.println("xd");
    }

    //FUNKCJE WCZYTYWANIA INNYCH WIDOKÓW
    //ustawia separator danych (średnik albo przecinek)
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

    //przechodzi do widoku ustawień algorytmu
    @FXML
    public void programSettings(ActionEvent event) throws IOException {
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

    //przechodzi do widoku edycji przykładów
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

    //pokazanie statystyk pojedynczego kroku mrówek w iteracji
    private void showStepStats() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxmls/OneStepFXML.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            OneStepFXMLController eec = fxmlLoader.<OneStepFXMLController>getController();
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

    //pokazanie statystyk jednej iteracji algorytmu
    private void showIterationStats() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxmls/OneIterationFXML.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            OneIterationFXMLController eec = fxmlLoader.<OneIterationFXMLController>getController();
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

    //pokazanie statystyk całego algorytmu
    private void showAlgorithmStats() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxmls/OneReductFXML.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            OneReductFXMLController eec = fxmlLoader.<OneReductFXMLController>getController();
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

    //FUNKCJE RYSOWANIA GRAFU I GŁÓWNEGO WIDOKU
    //rysuje graf w widoku
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
        for (int i = 0; i < lines.size(); i++) {
            edgeLines.put(lines.get(i), DataAccessor.getGraph().getEdges().get(i));
        }
        solver.getChildren().addAll(lines);
        solver.getChildren().addAll(labels);

        labels.forEach((label) -> {
            label.toFront();
        });
    }

    //wpisuje przykłady do okna tekstowego
    private void objectsToTextArea(List<Attribute> attributes, List<DataObject> objects) {
        examplesToString.setText(TEXTAREA_PARAMETERS);
        attributes.forEach((attribute) -> {
            examplesToString.appendText(attribute.getName() + ", ");
        });
        examplesToString.deleteText(examplesToString.getLength() - 2, examplesToString.getLength());
        examplesToString.appendText(TEXTAREA_APPEND);
        objects.forEach((x) -> {
            examplesToString.appendText(x.toString());
        });
    }

    //połączenie między wierzchołkami (grafika)
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

    public static void colorEdges() {
        //double min=0.0;
        //double max=Collections.max(DataAccessor.getGraph().getEdges(), Comparator.comparing(c -> c.getPheromone())).getPheromone();
        double max = Collections.max(DataAccessor.getGraph().getEdges(), Comparator.comparing(c -> c.getPheromone())).getPheromone();
        edgeLines.forEach((k, v) -> {
            double value = ((max - v.getPheromone()) / (max)) * 255;
            k.setStroke(Color.rgb((int) value, 255, (int) value));
            //z czerwonym kolorem
//            if (v.getPheromone()<max){
//                double value = (v.getPheromone()/(max))*255;
//                k.setStroke(Color.rgb(255,(int)value,(int)value));
//                //k.setStroke(Color.rgb((int)value, 0, 0));
//            }
//            else if (v.getPheromone()>max){
//                double value = ((v.getPheromone()-(max))/(max))*255;
//                k.setStroke(Color.rgb((int)value,255,(int)value));
//                //k.setStroke(Color.rgb(0,(int)value,0));
//            }
//            else k.setStroke(Color.WHITE);
        });

    }

    //FUNKCJE OBSŁUGUJĄCE MYSZKĘ
    //obsługa kliknięcia na wierzchołek
    EventHandler<MouseEvent> circleOnMousePressedEventHandler
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            if (t.getButton() == MouseButton.SECONDARY) {
                try {
                    DataAccessor.setAnalyzedVertice(verticeLabels.get((Label) t.getSource()));
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
            } else {
                orgSceneX = t.getSceneX();
                orgSceneY = t.getSceneY();
                orgTranslateX = ((Label) (t.getSource())).getTranslateX();
                orgTranslateY = ((Label) (t.getSource())).getTranslateY();
                ((Label) (t.getSource())).toFront();
            }
        }
    };

    //obsługa przesuwania wierzchołków poprzez myszkę
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

    //obsługa kliknięcia na krawędź
    EventHandler<MouseEvent> lineOnMouseEventHandler
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            if (t.getButton() == MouseButton.SECONDARY) {
                try {
                    DataAccessor.setAnalyzedEdge(edgeLines.get((Line) t.getSource()));
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
