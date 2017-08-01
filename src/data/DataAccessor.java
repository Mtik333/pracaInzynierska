/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import static data.ConstStrings.*;
import data.graph.Edge;
import data.graph.Graph;
import data.graph.NewAnt;
import data.graph.Vertice;
import data.roughsets.Attribute;
import data.roughsets.DataObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.stage.Stage;

/**
 *
 * @author Mateusz
 */
public class DataAccessor {
    private static Stage primaryStage;
    private static String separator=",";
    private static boolean loadedData=false;
    private static File file;
    private static List<DataObject> dataset; //zbior obiektów wczytanych
    private static List<Attribute> allAttributes;
    private static int decisionMaker;
    private static int loopLimit=20;
    private static double pheromoneRelevance=1;
    private static double edgeRelevance=0.1;
    private static int antsNumber;
    private static int constantForUpdating=1;
    private static Edge analyzedEdge;
    private static Vertice analyzedVertice;
    private static List<NewAnt> allAnts;
    private static int currentIter=0;
    private static List<Attribute> currentReduct;
    private static String[][] indiscMatrix; //macierz rozróznialności
    private static String calculationMode=COMPUTE_REDUCT;
    private static List<List<Attribute>> listOfReducts;
    private static int performedIterations=0;
    private static boolean calculatedReductInIteration=false;

    public static boolean isCalculatedReductInIteration() {
        return calculatedReductInIteration;
    }

    public static void setCalculatedReductInIteration(boolean calculatedReductInIteration) {
        DataAccessor.calculatedReductInIteration = calculatedReductInIteration;
    }

    public static int getPerformedIterations() {
        return performedIterations;
    }

    public static void setPerformedIterations(int performedIterations) {
        DataAccessor.performedIterations = performedIterations;
    }

    public static List<List<Attribute>> getListOfReducts() {
        return listOfReducts;
    }

    public static void setListOfReducts(List<List<Attribute>> listOfReducts) {
        DataAccessor.listOfReducts = listOfReducts;
    }
    private static int maxList;
    private static double pheromoneEvaporation = 0.5;

    public static double getPheromoneEvaporation() {
        return pheromoneEvaporation;
    }

    public static void setPheromoneEvaporation(double pheromoneEvaporation) {
        DataAccessor.pheromoneEvaporation = pheromoneEvaporation;
    }
    public static String getCalculationMode() {
        return calculationMode;
    }

    public static void setCalculationMode(String calculationMode) {
        DataAccessor.calculationMode = calculationMode;
    }

    public static Edge getAnalyzedEdge() {
        return analyzedEdge;
    }

    public static void setAnalyzedEdge(Edge analyzedEdge) {
        DataAccessor.analyzedEdge = analyzedEdge;
    }

    public static Vertice getAnalyzedVertice() {
        return analyzedVertice;
    }

    public static void setAnalyzedVertice(Vertice analyzedVertice) {
        DataAccessor.analyzedVertice = analyzedVertice;
    }
    private static Graph graph;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static String getSeparator() {
        return separator;
    }

    public static boolean isLoadedData() {
        return loadedData;
    }

    public static File getFile() {
        return file;
    }

    public static List<DataObject> getDataset() {
        return dataset;
    }

    public static List<Attribute> getAllAttributes() {
        return allAttributes;
    }

    public static int getDecisionMaker() {
        return decisionMaker;
    }

    public static void setDecisionMaker(int decisionMaker) {
        DataAccessor.decisionMaker = decisionMaker;
    }
    
    public static void setLoadedData(boolean loadedData) {
        DataAccessor.loadedData = loadedData;
    }

    public static void setSeparator(String separator) {
        DataAccessor.separator = separator;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        DataAccessor.primaryStage = primaryStage;
    }
    
    public static void setFile(File file){
        DataAccessor.file = file;
    }
    
    public static boolean parseFile() throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader(getFile().getPath()));
        stringToAttribute(br.readLine().split(getSeparator()));
        if (getAllAttributes().size()==1){
            setAllAttributes(null);
            setFile(null);
            return false;
        }
        int j = 1; //ilosc obiektow
        String line;
        setDataset(new ArrayList<>());
        while ((line = br.readLine()) != null) {
            String oneObject[] = line.split(getSeparator());
            int i = 0; //ktory z kolei atrybut wczytywany
            List<Attribute> singleObjectAttributes = new ArrayList<>();
            for (String x : oneObject) {
                Attribute attribute = new Attribute(getAllAttributes().get(i).getName(), x);
                if (i != getDecisionMaker()) {
                    singleObjectAttributes.add(attribute);
                } else {
                    attribute.setDecisionMaking(true);
                    singleObjectAttributes.add(attribute);
                }
                i++;
            }
            DataObject newObject = new DataObject("" + j);
            newObject.setAttributes(singleObjectAttributes);
            getDataset().add(newObject);
            j++;
        }
        return true;
    }
    
    private static void stringToAttribute(String[] attributes){
        setAllAttributes(new ArrayList<Attribute>());
        setDecisionMaker(attributes.length-1);
        for (String attribute : attributes) {
            Attribute myAttribute = new Attribute(attribute);
            getAllAttributes().add(myAttribute);
        }
        getAllAttributes().get(getDecisionMaker()).setDecisionMaking(true);
    }

    public static int getLoopLimit() {
        return loopLimit;
    }

    public static void setLoopLimit(int aLoopLimit) {
        loopLimit = aLoopLimit;
    }

    public static double getPheromoneRelevance() {
        return pheromoneRelevance;
    }

    public static void setPheromoneRelevance(double aPheromoneRelevance) {
        pheromoneRelevance = aPheromoneRelevance;
    }

    public static double getEdgeRelevance() {
        return edgeRelevance;
    }

    public static void setEdgeRelevance(double aEdgeRelevance) {
        edgeRelevance = aEdgeRelevance;
    }

    public static int getAntsNumber() {
        return antsNumber;
    }

    public static void setAntsNumber(int aAntsNumber) {
        antsNumber = aAntsNumber;
    }

    public static int getConstantForUpdating() {
        return constantForUpdating;
    }

    public static void setConstantForUpdating(int aConstantForUpdating) {
        constantForUpdating = aConstantForUpdating;
    }

    /**
     * @param aDataset the dataset to set
     */
    public static void setDataset(List<DataObject> aDataset) {
        dataset = aDataset;
    }

    /**
     * @param aAllAttributes the allAttributes to set
     */
    public static void setAllAttributes(List<Attribute> aAllAttributes) {
        allAttributes = aAllAttributes;
    }

    /**
     * @return the graph
     */
    public static Graph getGraph() {
        return graph;
    }

    /**
     * @param aGraph the graph to set
     */
    public static void setGraph(Graph aGraph) {
        graph = aGraph;
    }

    public static List<NewAnt> getAllAnts() {
        return allAnts;
    }

    public static void setAllAnts(List<NewAnt> aAllAnts) {
        allAnts = aAllAnts;
    }

    public static int getCurrentIter() {
        return currentIter;
    }

    public static void setCurrentIter(int aCurrentIter) {
        currentIter = aCurrentIter;
    }

    public static List<Attribute> getCurrentReduct() {
        return currentReduct;
    }

    public static void setCurrentReduct(List<Attribute> aCurrentReduct) {
        currentReduct = aCurrentReduct;
    }

    public static String[][] getIndiscMatrix() {
        return indiscMatrix;
    }

    public static void setIndiscMatrix(String[][] aIndiscMatrix) {
        indiscMatrix = aIndiscMatrix;
    }

    public static int getMaxList() {
        return maxList;
    }

    public static void setMaxList(int aMaxList) {
        maxList = aMaxList;
    }
    
}
