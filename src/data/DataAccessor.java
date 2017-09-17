/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import static data.ConstStrings.*;
import data.graph.Ant;
import data.graph.Edge;
import data.graph.Graph;
import data.graph.Vertice;
import data.roughsets.Attribute;
import data.roughsets.DataObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

/**
 *
 * @author Mateusz
 */
public class DataAccessor {

    private static Stage primaryStage; //widok UI
    private static String separator = ","; //separator do wczytywania obiektów
    private static boolean loadedData = false; //czy dane wczytane
    private static File file; //obiekt pliku
    private static List<DataObject> dataset; //zbior obiektów wczytanych
    private static List<Attribute> allAttributes; //zbior wszystkich atrybutów
    private static int decisionMaker; //atrybut decyzyjny
    private static int loopLimit = 100; //maksymalna ilosc wykonan algorytmu
    private static double pheromoneRelevance = 1; //waznosc feromonu na sciezce
    private static double edgeRelevance = 0.01; //waznosc wagi krawedzi
    private static int antsNumber; //liczba mrowek w algorytmie
    private static double constantForUpdating = 0.1; //stala do aktualizacji feromonow na ścieżkach
    private static Edge analyzedEdge; //analizowana krawędź (UI)
    private static Vertice analyzedVertice; //analizowany wierzchołek (UI)
    private static List<Ant> allAnts; //lista wszystkich mrówek (wątków)
    private static int currentIter = 0; //obecny krok (wewnątrz iteracji algorytmu)
    private static List<Attribute> currentReduct; //obecny redukt
    private static String[][] indiscMatrix; //macierz rozróznialności
    private static String calculationMode = COMPUTE_REDUCT; //tryb obliczania (znajdowania reduktu, jednej iteracji, jednego kroku)
    private static List<List<Attribute>> listOfReducts; //lista reduktów
    private static int performedIterations = 0; //ile razy wykonano algorytmu
    private static boolean calculatedReductInIteration = false; //czy w iteracji znaleziono juz redukt
    private static List<String> decisionValues; //wartosci decyzyjne
    private static List<Attribute> coreAttributes; //atrybuty należące do rdzenia
    private static double datasetMutualInformation; //wartosc informacji wzajemnej w zbiorze
    private static double decisionEntropy; //entropia decyzji w zbiorze
    private static double epsilonValue = 0.001; //wartosc "minimalna" do heurystyki
    private static String algorithmType = RSFSACO; //wybrany typ algorytmu
    private static double elapsedTime = 0; //czas znalezienia reduktu
    private static int fruitlessSearches = 5; //ilosc bezowocnych poszukiwan

    public static double getElapsedTime() {
        return elapsedTime;
    }

    public static void setElapsedTime(double elapsedTime) {
        DataAccessor.elapsedTime = elapsedTime;
    }

    public static int getFruitlessSearches() {
        return fruitlessSearches;
    }

    public static void setFruitlessSearches(int fruitlessSearches) {
        DataAccessor.fruitlessSearches = fruitlessSearches;
    }

    public static Logic createLogic() {
        if (getAlgorithmType().equals(JSACO)) {
            return new JensenLogic();
        }
        if (getAlgorithmType().equals(RSFSACO)) {
            return new ChineseLogic();
        }
        return null;
    }

    public static String getAlgorithmType() {
        return algorithmType;
    }

    public static void setAlgorithmType(String algorithmType) {
        DataAccessor.algorithmType = algorithmType;
    }

    public static double getEpsilonValue() {
        return epsilonValue;
    }

    public static void setEpsilonValue(double epsilonValue) {
        DataAccessor.epsilonValue = epsilonValue;
    }

    public static double getDecisionEntropy() {
        return decisionEntropy;
    }

    public static void setDecisionEntropy(double decisionEntropy) {
        DataAccessor.decisionEntropy = decisionEntropy;
    }

    public static double getDatasetMutualInformation() {
        return datasetMutualInformation;
    }

    public static void setDatasetMutualInformation(double datasetMutualInformation) {
        DataAccessor.datasetMutualInformation = datasetMutualInformation;
    }

    public static List<Attribute> getCoreAttributes() {
        return coreAttributes;
    }

    public static void setCoreAttributes(List<Attribute> coreAttributes) {
        DataAccessor.coreAttributes = coreAttributes;
    }

    public static List<String> getDecisionValues() {
        return decisionValues;
    }

    public static void setDecisionValues(List<String> decisionValues) {
        DataAccessor.decisionValues = decisionValues;
    }

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
    private static double pheromoneEvaporation = 0.9;

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

    public static void setFile(File file) {
        DataAccessor.file = file;
    }

    public static boolean parseFile() throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(getFile().getPath()));
        stringToAttribute(br.readLine().split(getSeparator()));
        if (getAllAttributes().size() == 1) {
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

    private static void stringToAttribute(String[] attributes) {
        setAllAttributes(new ArrayList<>());
        setDecisionMaker(attributes.length - 1);
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

    public static double getConstantForUpdating() {
        return constantForUpdating;
    }

    public static void setConstantForUpdating(double aConstantForUpdating) {
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

    public static List<Ant> getAllAnts() {
        return allAnts;
    }

    public static void setAllAnts(List<Ant> aAllAnts) {
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

    public static Attribute verticeToAttribute(Vertice vertice) {
        for (Attribute attribute : getAllAttributes()) {
            if (attribute.getName().equals(vertice.getName())) {
                return attribute;
            }
        }
        return null;
    }

    public static boolean ifVerticeInReduct(Vertice vertice) {
        return getCurrentReduct().stream().anyMatch((attribute) -> (attribute.getName().equals(vertice.getName())));
    }

    public static Map<Line, Edge> sortByValue(Map<Line, Edge> unsortMap) {
        // 1. Convert Map to List of Map
        List<Map.Entry<Line, Edge>> list
                = new LinkedList<>(unsortMap.entrySet());
        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, (Map.Entry<Line, Edge> o1, Map.Entry<Line, Edge> o2) -> {
            if (o1.getValue().getPheromone() < o2.getValue().getPheromone()) {
                return -1;
            } else if (o1.getValue().getPheromone() > o2.getValue().getPheromone()) {
                return 1;
            }
            return 0;
        });
        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<Line, Edge> sortedMap = new LinkedHashMap<>();
        list.forEach((entry) -> {
            sortedMap.put(entry.getKey(), entry.getValue());
        });
        return sortedMap;
    }

    public static void resetValues() {
        setLoadedData(false);
        setFile(null);
        setDataset(null);
        setAllAttributes(null);
        setDecisionMaker(0);
        setAntsNumber(0);
        setAnalyzedEdge(null);
        setAnalyzedVertice(null);
        setAllAnts(null);
        setCurrentIter(0);
        setCurrentReduct(null);
        setIndiscMatrix(null);
        setListOfReducts(null);
        setPerformedIterations(0);
        setCalculatedReductInIteration(false);
        setDecisionValues(null);
        setCoreAttributes(null);
        System.gc();
    }
}
