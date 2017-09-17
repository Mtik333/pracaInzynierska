/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import data.ChineseLogic;
import data.ConstStrings;
import static data.ConstStrings.*;
import data.DataAccessor;
import data.Logic;
import data.graph.Edge;
import data.graph.Vertice;
import data.roughsets.Attribute;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
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
    private Button viewExamples;
    @FXML
    private Button manualAnts;
    @FXML
    private Button nextEdge;
    @FXML
    private Button singleIteration;
    @FXML
    private Button singleReduct;
    @FXML
    private Button showStatistics;
    @FXML
    private Button resetAlgorithm;
    //@FXML private TextArea examplesToString; //pole tekstowe na obiekty
    @FXML
    private AnchorPane solver; //widok grafu
    private List<Label> labels; //lista wierzcholkow (grafika)
    private List<Line> lines; //lista krawedzi (grafika)
    private static Map<Line, Edge> edgeLines; //zmapowanie krawedzi rzeczywistych na graficzne
    private static Map<Label, Vertice> verticeLabels; //zmapowanie wierzcholkow rzeczywistych na graficzne
    double orgSceneX, orgSceneY; //do przenoszenia wierzcholkow/krawedzi
    double orgTranslateX, orgTranslateY; //do przenoszenia wierzcholkow/krawedzi
    private Logic newLogic; //logika algorytmu

    @FXML
    public void exitApp(ActionEvent event) {
        Platform.exit();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    public void loadSampleDataset(ActionEvent event) {
        if (newLogic != null) {
            DataAccessor.resetValues();
        }
        showFXML(SAMPLE_DATASET_FXML_RES, SAMPLE_DATASET_TITLE);
        loadingDataset();
    }

    //wczytuje zestaw danych z pliku
    @FXML
    public void loadDataset(ActionEvent event) {
        if (newLogic != null) {
            DataAccessor.resetValues();
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty(USERDIR)));
        fileChooser.setTitle(CHOOSE_FILE);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(CSV_FILES, CSV_FILTER)
        );
        DataAccessor.setFile(fileChooser.showOpenDialog(solver.getScene().getWindow()));
        if (DataAccessor.getFile() == null) {
            return;
        }
        loadingDataset();
    }

    private void loadingDataset() {
        try {
            if (!DataAccessor.parseFile()) {
                disableButtons();
                newLogic = DataAccessor.createLogic();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(PARSING_ERROR);
                alert.setContentText(WRONG_SEPARATOR);
                alert.showAndWait();
            } else {
                enableButtons();
                newLogic = DataAccessor.createLogic();
                //objectsToTextArea(DataAccessor.getAllAttributes(), DataAccessor.getDataset());
            }
            newLogic.generateGraph();
            drawGraph();
            if (newLogic instanceof ChineseLogic) {
                if (ChineseLogic.checkIfCoreIsReduct()) {
                    showFXML(CORE_IS_REDUCT_FXML_RES, CORE_IS_REDUCT_TITLE);
                    disableButtons();
                    //pokaz okienko ze rdzen = redukt
                }
            }
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
            } else {
                showStepStats();
            }
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
            DataAccessor.setCalculationMode(ConstStrings.SINGLE_ITERATION);
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
            DataAccessor.setCalculationMode(ConstStrings.COMPUTE_REDUCT);
//            newLogic.findReduct();
            Service<Void> service = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            newLogic.findReduct();
                            //Background work
                            final CountDownLatch latch = new CountDownLatch(1);
                            Platform.runLater(() -> {
                                try {
                                    DataAccessor.setCalculationMode(SINGLE_ITERATION);
                                    colorEdges();
                                    showAlgorithmStats();
                                    disableButtons();
                                    //FX Stuff done here
                                } finally {
                                    //latch.countDown();
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
        showFXML(SET_SEPARATOR_FXML_RES, SET_SEPARATOR_TITLE);
    }

    //przechodzi do widoku ustawień algorytmu
    @FXML
    public void programSettings(ActionEvent event) throws IOException {
        showFXML(ALGORITHM_SETTINGS_FXML_RES, ALGORITHM_SETTINGS_TITLE);
    }

    //przechodzi do widoku edycji przykładów
    @FXML
    public void editExamples(ActionEvent event) throws IOException {
        if (DataAccessor.isLoadedData()) {
            showFXML(EDIT_EXAMPLES_FXML_RES, EDIT_EXAMPLES_TITLE);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(ALERT_ERROR_TITLE_NO_DATA);
            alert.setContentText(NO_DATA_TO_LOAD);
            alert.showAndWait();
        }
    }

    //pokazanie statystyk pojedynczego kroku mrówek w iteracji
    private void showStepStats() {
        showFXML(ONE_STEP_FXML_RES, ONE_STEP_TITLE);
    }

    //pokazanie statystyk jednej iteracji algorytmu
    private void showIterationStats() {
        showFXML(ONE_ITERATION_FXML_RES, ONE_ITERATION_TITLE);
    }

    //pokazanie statystyk całego algorytmu
    private void showAlgorithmStats() {
        showFXML(ONE_REDUCT_FXML_RES, ONE_REDUCT_TITLE);
    }

    //ŁADOWANIE DOWOLNEGO KONTROLERA
    private void showFXML(String resource, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(resource));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(DataAccessor.getPrimaryStage());
            stage.setScene(new Scene(root1));
            stage.showAndWait();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void disableButtons() {
        nextEdge.setDisable(true);
        singleIteration.setDisable(true);
        singleReduct.setDisable(true);
    }

    private void enableButtons() {
        viewExamples.setDisable(false);
        manualAnts.setDisable(false);
        showStatistics.setDisable(false);
        nextEdge.setDisable(false);
        singleIteration.setDisable(false);
        singleReduct.setDisable(false);
        resetAlgorithm.setDisable(false);
    }

    @FXML
    private void resetAlgorithm() throws IOException {
        DataAccessor.resetValues();
        DataAccessor.parseFile();
        enableButtons();
        newLogic = DataAccessor.createLogic();
        newLogic.generateGraph();
        drawGraph();
        if (newLogic instanceof ChineseLogic) {
            if (ChineseLogic.checkIfCoreIsReduct()) {
                showFXML(CORE_IS_REDUCT_FXML_RES, CORE_IS_REDUCT_TITLE);
                disableButtons();
                //pokaz okienko ze rdzen = redukt
            }

        }
        DataAccessor.setLoadedData(true);
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
            label.setTranslateX(middleX + 200 * sinus);
            label.setTranslateY(middleY + 200 * cosinus);
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
        if (DataAccessor.getAlgorithmType().equals(ConstStrings.RSFSACO)) {
            VBox stackPane = new VBox();
            stackPane.setTranslateX(middleX / 4);
            stackPane.setTranslateY(middleY / 4);
            stackPane.setAlignment(Pos.CENTER);
            stackPane.setStyle("-fx-border-color:red;-fx-background-color:transparent");
            //stackPane.setSpacing(10);
            Label core = new Label("Core");
            //core.setMinWidth(100);
            core.setStyle("-fx-border-color:white;-fx-background-color:white");
            core.setFont(javafx.scene.text.Font.font(18));
            core.setTextAlignment(TextAlignment.CENTER);
            core.setAlignment(Pos.CENTER);
            stackPane.getChildren().add(core);
            for (int i = 0; i < DataAccessor.getCoreAttributes().size(); i++) {
                Label label = new Label(DataAccessor.getCoreAttributes().get(i).getName());
                label.setMinWidth(100);
                label.setStyle("-fx-border-color:blue;-fx-background-color:yellow");
                label.setFont(javafx.scene.text.Font.font(18));
                label.setTextAlignment(TextAlignment.CENTER);
                label.setAlignment(Pos.CENTER);
                stackPane.getChildren().add(label);
            }
            stackPane.setOnMouseDragged(coreOnMouseDraggedEventHandler);
            solver.getChildren().add(stackPane);

        }
        solver.getChildren().addAll(lines);
        solver.getChildren().addAll(labels);

        labels.forEach((label) -> {
            label.toFront();
        });
    }

    //wpisuje przykłady do okna tekstowego
//    private void objectsToTextArea(List<Attribute> attributes, List<DataObject> objects) {
//        examplesToString.setText(TEXTAREA_PARAMETERS);
//        attributes.forEach((attribute) -> {
//            examplesToString.appendText(attribute.getName() + ", ");
//        });
//        examplesToString.deleteText(examplesToString.getLength() - 2, examplesToString.getLength());
//        examplesToString.appendText(TEXTAREA_APPEND);
//        objects.forEach((x) -> {
//            examplesToString.appendText(x.toString());
//        });
//    }
    //połączenie między wierzchołkami (grafika)
    private Line connect(Label c1, Label c2) {
        Line line = new Line();
        //line.setStyle("-fx-border-color:black");
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

        //test sortowania
        if (!DataAccessor.getCalculationMode().equals(ConstStrings.COMPUTE_REDUCT)) {
            edgeLines = DataAccessor.sortByValue(edgeLines);
        }
        double max = Collections.max(DataAccessor.getGraph().getEdges(), Comparator.comparing(c -> c.getPheromone())).getPheromone();
        edgeLines.forEach((Line k, Edge v) -> {
            double value = ((max - v.getPheromone()) / (max)) * 255;
            if (!DataAccessor.getCalculationMode().equals(ConstStrings.COMPUTE_REDUCT)) {
                if (value < 128) {
                    k.toFront();
                }
            }
            k.setStroke(Color.rgb((int) value, 255, (int) value));
        });
        //if (!DataAccessor.getCalculationMode().equals(ConstStrings.COMPUTE_REDUCT)){
        verticeLabels.forEach((t, u) -> {
            if (DataAccessor.ifVerticeInReduct(u)) {
                t.setStyle("-fx-background-color:#CCFF99");
            } else {
                t.setStyle("-fx-border-color:red;-fx-background-color:white");
            }
            if (!DataAccessor.getCalculationMode().equals(ConstStrings.COMPUTE_REDUCT)) {
                t.toFront();
            }
        });
        //}
    }

    //FUNKCJE OBSŁUGUJĄCE MYSZKĘ
    //obsługa kliknięcia na wierzchołek
    EventHandler<MouseEvent> circleOnMousePressedEventHandler
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            if (t.getButton() == MouseButton.SECONDARY) {
                showFXML(SHOW_VERTICE_FXML_RES, SHOW_VERTICE_TITLE);
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

    //obsługa przesuwania rdzenia poprzez myszkę
    EventHandler<MouseEvent> coreOnMouseDraggedEventHandler
            = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            double offsetX = t.getSceneX() - orgSceneX;
            double offsetY = t.getSceneY() - orgSceneY;
            double newTranslateX = orgTranslateX + offsetX;
            double newTranslateY = orgTranslateY + offsetY;
            ((VBox) (t.getSource())).setTranslateX(newTranslateX);
            ((VBox) (t.getSource())).setTranslateY(newTranslateY);

        }
    };

    //obsługa kliknięcia na krawędź
    EventHandler<MouseEvent> lineOnMouseEventHandler
            = (MouseEvent t) -> {
                if (t.getButton() == MouseButton.SECONDARY) {
                    showFXML(SHOW_EDGE_FXML_RES, SHOW_EDGE_TITLE);
                }
            };
}
