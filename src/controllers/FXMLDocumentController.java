/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import controllers.events.CoreMouseDraggedHandler;
import controllers.events.EdgeShowInfoHandler;
import controllers.events.VerticeMouseDraggedHandler;
import controllers.events.VerticeMousePressedHandler;
import data.ChineseLogic;
import data.ConstStrings;
import data.DataAccessor;
import data.Logic;
import data.fishsearch.FishLogic;
import data.graph.Edge;
import data.graph.Vertice;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static data.ConstStrings.*;

/**
 * @author Mateusz
 */
public class FXMLDocumentController implements Initializable {

    public static Map<Line, Edge> edgeLines; //zmapowanie krawedzi rzeczywistych na graficzne
    public static Map<Label, Vertice> verticeLabels; //zmapowanie wierzcholkow rzeczywistych na graficzne
    private final VerticeMousePressedHandler verticeMousePressedHandler = new VerticeMousePressedHandler(this);
    private final EdgeShowInfoHandler edgeShowInfoHandler = new EdgeShowInfoHandler(this);
    private final VerticeMouseDraggedHandler verticeMouseDraggedHandler = new VerticeMouseDraggedHandler(this);
    private final CoreMouseDraggedHandler coreMouseDraggedHandler = new CoreMouseDraggedHandler(this);
    @FXML
    private Button viewExamples;
    @FXML
    private Button nextEdge;
    @FXML
    private Button singleIteration;
    @FXML
    private Button singleReduct;
    @FXML
    private Button resetAlgorithm;
    @FXML
    private AnchorPane solver; //widok grafu
    private double orgSceneX;
    private double orgSceneY; //do przenoszenia wierzcholkow/krawedzi
    private double orgTranslateX;
    private double orgTranslateY; //do przenoszenia wierzcholkow/krawedzi
    private Logic newLogic; //logika algorytmu

    public static void colorEdges() {
        if (!DataAccessor.getCalculationMode().equals(ConstStrings.COMPUTE_REDUCT)) {
            edgeLines = DataAccessor.sortByValue(edgeLines);
        }
        double max = Collections.max(DataAccessor.getGraph().getEdges(), Comparator.comparing(Edge::getPheromone)).getPheromone();
        edgeLines.forEach((Line k, Edge v) -> {
            double value = ((max - v.getPheromone()) / (max)) * ConstStrings.RGB_MAX_VALUE;
            if (!DataAccessor.getCalculationMode().equals(ConstStrings.COMPUTE_REDUCT)) {
                if (value < ConstStrings.RGB_MAX_VALUE_DIV2) {
                    k.toFront();
                }
            }
            k.setStroke(Color.rgb((int) value, ConstStrings.RGB_MAX_VALUE, (int) value));
        });
        verticeLabels.forEach((t, u) -> {
            if (DataAccessor.ifVerticeInReduct(u)) {
                t.setStyle(ConstStrings.VERTICE_IN_REDUCT_STYLE);
            } else {
                t.setStyle(ConstStrings.VERTICE_DEFAULT_STYLE);
            }
            if (!DataAccessor.getCalculationMode().equals(ConstStrings.COMPUTE_REDUCT)) {
                t.toFront();
            }
        });
    }

    @FXML
    public void exitApp() {
        Platform.exit();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    public void loadSampleDataset() {
        if (newLogic != null) {
            DataAccessor.resetValues();
        }
        showFXML(SAMPLE_DATASET_FXML_RES, SAMPLE_DATASET_TITLE);
        if (DataAccessor.getFile() == null) {
            return;
        }
        loadingDataset();
    }

    //wczytuje zestaw danych z pliku
    @FXML
    public void openDatasetDialog() {
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
            }
            assert newLogic != null;
            newLogic.generateGraph();
            drawGraph();
            if (newLogic instanceof ChineseLogic) {
                if (ChineseLogic.checkIfCoreIsReduct()) {
                    showFXML(CORE_IS_REDUCT_FXML_RES, CORE_IS_REDUCT_TITLE);
                    disableButtons();
                    DataAccessor.setCurrentReduct(new ArrayList<>());
                    DataAccessor.getCurrentReduct().addAll(DataAccessor.getCoreAttributes());
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace(System.out);
        }
        DataAccessor.setLoadedData(true);
    }

    //wykonanie jednego wyboru kolejnej krawędzi przez mrówkę
    @FXML
    private void antsOneStep() {
        disableButtons();
        if (DataAccessor.isLoadedData()) {
            if (DataAccessor.getAllAnts() == null || DataAccessor.isCalculatedReductInIteration()) {
                newLogic.initializeAntsRandom();
                showStepStats();
            } else if (newLogic.stepToNextVertice()) {
                newLogic.initializeAntsRandom();
                colorEdges();
                showStepStats();
            } else {
                showStepStats();
            }
        }
        enableButtons();
    }

    //wykonanie jednej iteracji algorytmu
    @FXML
    private void antsOneIteration() {
        disableButtons();
        if (DataAccessor.isLoadedData()) {
            if (DataAccessor.getCurrentIter() == ConstStrings.ZERO || DataAccessor.isCalculatedReductInIteration()) {
                newLogic.initializeAntsRandom();
            }
            DataAccessor.setCalculationMode(ConstStrings.SINGLE_ITERATION);
            newLogic.performOneIteration();
        }
        colorEdges();
        showIterationStats();
        enableButtons();
    }

    //znalezienie reduktu przez wykonanie określonej liczby iteracji algorytmu
    @FXML
    private void antsFindReduct() {
        disableButtons();
        resetAlgorithm.setDisable(true);
        if (DataAccessor.isLoadedData()) {
            DataAccessor.setCalculationMode(ConstStrings.COMPUTE_REDUCT);
            Service<Void> service = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            newLogic.findReduct();
                            Platform.runLater(() -> {
                                resetAlgorithm.setDisable(false);
                                DataAccessor.setCalculationMode(SINGLE_ITERATION);
                                colorEdges();
                                disableButtons();
                                showAlgorithmStats();
                            });
                            return null;
                        }
                    };
                }
            };
            service.start();
        }
    }

    //FUNKCJE WCZYTYWANIA INNYCH WIDOKÓW
    //ustawia separator danych (średnik albo przecinek)
    @FXML
    public void setSeparator() {
        showFXML(SET_SEPARATOR_FXML_RES, SET_SEPARATOR_TITLE);
    }

    //przechodzi do widoku ustawień algorytmu
    @FXML
    public void programSettings() {
        showFXML(ALGORITHM_SETTINGS_FXML_RES, ALGORITHM_SETTINGS_TITLE);
    }

    //przechodzi do widoku edycji przykładów
    @FXML
    public void editExamples() {
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
    public void showFXML(String resource, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(resource));
            Parent root1 = fxmlLoader.load();
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
        assert newLogic != null;
        newLogic.generateGraph();
        drawGraph();
        if (newLogic instanceof ChineseLogic) {
            if (ChineseLogic.checkIfCoreIsReduct()) {
                showFXML(CORE_IS_REDUCT_FXML_RES, CORE_IS_REDUCT_TITLE);
                DataAccessor.setElapsedTime(0);
                disableButtons();
            }

        }
        DataAccessor.setLoadedData(true);
    }

    @FXML
    private void testFish(){
        if (DataAccessor.isLoadedData()){
            FishLogic fishLogic = new FishLogic();
            int i=0;
            Service<Void> service = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            fishLogic.findReduct(false);
                            Platform.runLater(() -> {
                                resetAlgorithm.setDisable(false);
                                DataAccessor.setCalculationMode(SINGLE_ITERATION);
                            });
                            return null;
                        }
                    };
                }
            };
            service.start();
        }
    }

    @FXML
    private void testFishWithCore(){
        if (DataAccessor.isLoadedData()){
            FishLogic fishLogic = new FishLogic();
            int i=0;
            Service<Void> service = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            fishLogic.findReduct(true);
                            Platform.runLater(() -> {
                                resetAlgorithm.setDisable(false);
                                DataAccessor.setCalculationMode(SINGLE_ITERATION);
                            });
                            return null;
                        }
                    };
                }
            };
            service.start();
        }
    }

    //FUNKCJE RYSOWANIA GRAFU I GŁÓWNEGO WIDOKU
    //rysuje graf w widoku
    private void drawGraph() {
        solver.getChildren().clear();
        List<Label> labels = new ArrayList<>();
        List<Line> lines = new ArrayList<>();
        edgeLines = new HashMap<>();
        verticeLabels = new HashMap<>();
        double middleX = solver.getWidth() / ConstStrings.TWO;
        double middleY = solver.getHeight() / ConstStrings.TWO;
        double degreesIncrement = ConstStrings.CIRCLE_DEGREE / (DataAccessor.getGraph().getVertices().size());
        for (int i = 0; i < DataAccessor.getGraph().getVertices().size(); i++) {
            double cosinus = Math.cos(Math.toRadians(i * degreesIncrement));
            double sinus = Math.sin(Math.toRadians(i * degreesIncrement));
            Label label = getAttributeLabel(middleX, middleY, i, cosinus, sinus);
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
            stackPane.setTranslateX(middleX / ConstStrings.FOUR);
            stackPane.setTranslateY(middleY / ConstStrings.FOUR);
            stackPane.setAlignment(Pos.CENTER);
            stackPane.setStyle(ConstStrings.CORE_PANE_DEFAULT_STYLE);
            Label core = new Label(ConstStrings.CORE);
            core.setStyle(ConstStrings.CORE_TITLE_STYLE);
            core.setFont(javafx.scene.text.Font.font(ConstStrings.VERTICE_FONT_SIZE));
            core.setTextAlignment(TextAlignment.CENTER);
            core.setAlignment(Pos.CENTER);
            stackPane.getChildren().add(core);
            for (int i = 0; i < DataAccessor.getCoreAttributes().size(); i++) {
                Label label = addCoreLabel(i);
                stackPane.getChildren().add(label);
            }
            stackPane.setOnMouseDragged(coreMouseDraggedHandler.coreOnMouseDraggedEventHandler);
            solver.getChildren().add(stackPane);

        }
        solver.getChildren().addAll(lines);
        solver.getChildren().addAll(labels);
        labels.forEach(Node::toFront);
    }

    private Label getAttributeLabel(double middleX, double middleY, int i, double cosinus, double sinus) {
        Label label = new Label(DataAccessor.getGraph().getVertices().get(i).getName());
        label.setMinWidth(ConstStrings.LABEL_MIN_WIDTH);
        label.setAlignment(Pos.CENTER);
        label.setTranslateX(middleX + ConstStrings.GRAPH_TRANSLATE_X * sinus);
        label.setTranslateY(middleY + ConstStrings.GRAPH_TRANSLATE_Y * cosinus);
        label.setStyle(ConstStrings.VERTICE_DEFAULT_STYLE);
        label.setFont(javafx.scene.text.Font.font(ConstStrings.VERTICE_FONT_SIZE));
        label.setTextAlignment(TextAlignment.CENTER);
        label.setOnMousePressed(verticeMousePressedHandler.circleOnMousePressedEventHandler);
        label.setOnMouseDragged(verticeMouseDraggedHandler.circleOnMouseDraggedEventHandler);
        return label;
    }

    private Label addCoreLabel(int i) {
        Label label = new Label(DataAccessor.getCoreAttributes().get(i).getName());
        label.setMinWidth(LABEL_MIN_WIDTH);
        label.setStyle(ConstStrings.CORE_VERTICES_STYLE);
        label.setFont(javafx.scene.text.Font.font(ConstStrings.VERTICE_FONT_SIZE));
        label.setTextAlignment(TextAlignment.CENTER);
        label.setAlignment(Pos.CENTER);
        return label;
    }

    //połączenie między wierzchołkami (grafika)
    private Line connect(Label c1, Label c2) {
        Line line = new Line();
        line.startXProperty().bind(Bindings.createDoubleBinding(() -> {
            Bounds b = c1.getBoundsInParent();
            return b.getMinX() + b.getWidth() / ConstStrings.TWO;
        }, c1.boundsInParentProperty()));
        line.startYProperty().bind(Bindings.createDoubleBinding(() -> {
            Bounds b = c1.getBoundsInParent();
            return b.getMinY() + b.getHeight() / ConstStrings.TWO;
        }, c1.boundsInParentProperty()));
        line.endXProperty().bind(Bindings.createDoubleBinding(() -> {
            Bounds b = c2.getBoundsInParent();
            return b.getMinX() + b.getWidth() / ConstStrings.TWO;
        }, c2.boundsInParentProperty()));
        line.endYProperty().bind(Bindings.createDoubleBinding(() -> {
            Bounds b = c2.getBoundsInParent();
            return b.getMinY() + b.getHeight() / ConstStrings.TWO;
        }, c2.boundsInParentProperty()));
        line.setStrokeWidth(ConstStrings.EDGE_STROKE_WIDTH);
        line.toBack();
        line.setOnMouseClicked(edgeShowInfoHandler.lineOnMouseEventHandler);
        return line;
    }

    public double getOrgTranslateY() {
        return orgTranslateY;
    }

    public void setOrgTranslateY(double orgTranslateY) {
        this.orgTranslateY = orgTranslateY;
    }

    public double getOrgTranslateX() {
        return orgTranslateX;
    }

    public void setOrgTranslateX(double orgTranslateX) {
        this.orgTranslateX = orgTranslateX;
    }

    public double getOrgSceneX() {
        return orgSceneX;
    }

    public void setOrgSceneX(double orgSceneX) {
        this.orgSceneX = orgSceneX;
    }

    public double getOrgSceneY() {
        return orgSceneY;
    }

    public void setOrgSceneY(double orgSceneY) {
        this.orgSceneY = orgSceneY;
    }
}
