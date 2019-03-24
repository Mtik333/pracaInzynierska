/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import data.fishsearch.Fish;
import data.graph.Ant;
import data.graph.Edge;
import data.graph.Graph;
import data.graph.Vertice;
import data.roughsets.Attribute;
import data.roughsets.DataObject;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static data.ConstStrings.*;

/**
 * @author Mateusz
 */
public class DataAccessor {

    private static Stage primaryStage; //widok UI
    private static String separator = ","; //separator do wczytywania obiektów
    private static boolean loadedData = false; //czy dane wczytane
    private static File file; //obiekt pliku
    private static List<DataObject> dataset; //zbior obiektów wczytanych
    private static List<Attribute> allAttributes; //zbior wszystkich atrybutów
    private static int nonDecisionAttributesNumber;
    private static int decisionMaker; //atrybut decyzyjny
    private static int loopLimit = 100; //maksymalna ilosc wykonan algorytmu
    private static double pheromoneRelevance = 0.1; //waznosc feromonu na sciezce
    private static double edgeRelevance = 0.9; //waznosc wagi krawedzi
    private static int antsNumber; //liczba mrowek w algorytmie
    private static double constantForUpdating = 0.1; //stala do aktualizacji feromonow na ścieżkach
    private static Edge analyzedEdge; //analizowana krawędź (UI)
    private static Vertice analyzedVertice; //analizowany wierzchołek (UI)
    private static List<Ant> allAnts; //lista wszystkich mrówek (wątków)
    private static int currentIter = 0; //obecny krok (wewnątrz iteracji algorytmu)
    private static List<Attribute> currentReduct; //obecny redukt
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
    private static int fruitlessSearches = 3; //ilosc bezowocnych poszukiwan
    private static int maxList;
    private static double pheromoneEvaporation = 0.9;
    private static Graph graph;

    private static int fishNumber=10;
    private static List<Fish> allFishes; //lista wszystkich ryb
    private static String fishAlgorithmType = FSARSR;
    private static double fishQualityRelevance=0.9;
    private static double fishSubsetRelevance=0.1;
    private static double fishDeltaRelevance=0.9;
    private static double globalDependencyDegree=1;
    private static int fishMaxCycle=5;
    private static double coreDependency=0;
    private static boolean isFishAlgorithmLastCalculated=false;

    public static boolean isIsFishAlgorithmLastCalculated() {
        return isFishAlgorithmLastCalculated;
    }

    public static void setIsFishAlgorithmLastCalculated(boolean isFishAlgorithmLastCalculated) {
        DataAccessor.isFishAlgorithmLastCalculated = isFishAlgorithmLastCalculated;
    }

    public static double getCoreDependency() {
        return coreDependency;
    }

    public static void setCoreDependency(double coreDependency) {
        DataAccessor.coreDependency = coreDependency;
    }

    public static List<Attribute> getTemporaryReduct() {
        return temporaryReduct;
    }

    public static void setTemporaryReduct(List<Attribute> temporaryReduct) {
        DataAccessor.temporaryReduct = temporaryReduct;
    }

    private static List<Attribute> temporaryReduct;

    public static int getFishMaxCycle() {
        return fishMaxCycle;
    }

    public static void setFishMaxCycle(int fishMaxCycle) {
        DataAccessor.fishMaxCycle = fishMaxCycle;
    }

    public static double getGlobalDependencyDegree() {
        return globalDependencyDegree;
    }

    public static void setGlobalDependencyDegree(double globalDependencyDegree) {
        DataAccessor.globalDependencyDegree = globalDependencyDegree;
    }

    public static double getFishDeltaRelevance() {
        return fishDeltaRelevance;
    }

    public static void getFishDeltaRelevance(double fishDeltaRelevance) {
        DataAccessor.fishDeltaRelevance = fishDeltaRelevance;
    }

    public static int getFishVisual() {
        return fishVisual;
    }

    public static void setFishVisual(int fishVisual) {
        DataAccessor.fishVisual = fishVisual;
    }

    private static int fishVisual;

    public static int getNonDecisionAttributesNumber() {
        return nonDecisionAttributesNumber;
    }

    public static void setNonDecisionAttributesNumber(int nonDecisionAttributesNumber) {
        DataAccessor.nonDecisionAttributesNumber = nonDecisionAttributesNumber;
    }

    public static double getFishQualityRelevance() {
        return fishQualityRelevance;
    }

    public static void setFishQualityRelevance(double fishQualityRelevance) {
        DataAccessor.fishQualityRelevance = fishQualityRelevance;
    }

    public static double getFishSubsetRelevance() {
        return fishSubsetRelevance;
    }

    public static void setFishSubsetRelevance(double fishSubsetRelevance) {
        DataAccessor.fishSubsetRelevance = fishSubsetRelevance;
    }

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

    public static String getFishAlgorithmType() {
        return fishAlgorithmType;
    }

    public static void setFishAlgorithmType(String fishAlgorithmType) {
        DataAccessor.fishAlgorithmType = fishAlgorithmType;
    }

    public static void setFishDeltaRelevance(double fishDeltaRelevance) {
        DataAccessor.fishDeltaRelevance = fishDeltaRelevance;
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

    static void setDecisionEntropy(double decisionEntropy) {
        DataAccessor.decisionEntropy = decisionEntropy;
    }

    public static double getDatasetMutualInformation() {
        return datasetMutualInformation;
    }

    static void setDatasetMutualInformation(double datasetMutualInformation) {
        DataAccessor.datasetMutualInformation = datasetMutualInformation;
    }

    public static List<Attribute> getCoreAttributes() {
        return coreAttributes;
    }

    static void setCoreAttributes(List<Attribute> coreAttributes) {
        DataAccessor.coreAttributes = coreAttributes;
    }

    public static List<String> getDecisionValues() {
        return decisionValues;
    }

    static void setDecisionValues(List<String> decisionValues) {
        DataAccessor.decisionValues = decisionValues;
    }

    public static boolean isCalculatedReductInIteration() {
        return calculatedReductInIteration;
    }

    static void setCalculatedReductInIteration(boolean calculatedReductInIteration) {
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

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        DataAccessor.primaryStage = primaryStage;
    }

    public static String getSeparator() {
        return separator;
    }

    public static void setSeparator(String separator) {
        DataAccessor.separator = separator;
    }

    public static boolean isLoadedData() {
        return loadedData;
    }

    public static void setLoadedData(boolean loadedData) {
        DataAccessor.loadedData = loadedData;
    }

    public static File getFile() {
        return file;
    }

    public static void setFile(File file) {
        DataAccessor.file = file;
    }

    public static List<DataObject> getDataset() {
        return dataset;
    }

    /**
     * @param aDataset the dataset to set
     */
    private static void setDataset(List<DataObject> aDataset) {
        dataset = aDataset;
    }

    public static List<Attribute> getAllAttributes() {
        return allAttributes;
    }

    /**
     * @param aAllAttributes the allAttributes to set
     */
    private static void setAllAttributes(List<Attribute> aAllAttributes) {
        allAttributes = aAllAttributes;
    }

    public static int getDecisionMaker() {
        return decisionMaker;
    }

    private static void setDecisionMaker(int decisionMaker) {
        DataAccessor.decisionMaker = decisionMaker;
    }

    public static boolean parseFile() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(getFile().getPath()));
        stringToAttribute(br.readLine().split(getSeparator()));
        if (getAllAttributes().size() == ConstStrings.ONE) {
            setAllAttributes(null);
            setFile(null);
            return false;
        }
        int j = ConstStrings.ONE; //ilosc obiektow
        String line;
        setDataset(new ArrayList<>());
        while ((line = br.readLine()) != null) {
            String oneObject[] = line.split(getSeparator());
            int i = ConstStrings.ZERO; //ktory z kolei atrybut wczytywany
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
        setDecisionMaker(attributes.length - ConstStrings.ONE);
        for (String attribute : attributes) {
            Attribute myAttribute = new Attribute(attribute);
            getAllAttributes().add(myAttribute);
        }
        getAllAttributes().get(getDecisionMaker()).setDecisionMaking(true);
        setNonDecisionAttributesNumber(getAllAttributes().size()-1);
        setFishVisual(getNonDecisionAttributesNumber()/2);
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

    static void setAllAnts(List<Ant> aAllAnts) {
        allAnts = aAllAnts;
    }

    public static int getCurrentIter() {
        return currentIter;
    }

    static void setCurrentIter(int aCurrentIter) {
        currentIter = aCurrentIter;
    }

    public static List<Attribute> getCurrentReduct() {
        return currentReduct;
    }

    public static void setCurrentReduct(List<Attribute> aCurrentReduct) {
        currentReduct = aCurrentReduct;
    }

    public static int getMaxList() {
        return maxList;
    }

    static void setMaxList(int aMaxList) {
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
        List<Map.Entry<Line, Edge>> list
                = new LinkedList<>(unsortMap.entrySet());
        list.sort((Map.Entry<Line, Edge> o1, Map.Entry<Line, Edge> o2) -> {
            if (o1.getValue().getPheromone() < o2.getValue().getPheromone()) {
                return ConstStrings.MINUS_ONE;
            } else if (o1.getValue().getPheromone() > o2.getValue().getPheromone()) {
                return ConstStrings.ONE;
            }
            return ConstStrings.ZERO;
        });
        Map<Line, Edge> sortedMap = new LinkedHashMap<>();
        list.forEach((entry) -> sortedMap.put(entry.getKey(), entry.getValue()));
        return sortedMap;
    }

    public static void resetValues() {
        setLoadedData(false);
        setDataset(null);
        setAllAttributes(null);
        setDecisionMaker(ConstStrings.ZERO);
        setAntsNumber(ConstStrings.ZERO);
        setAnalyzedEdge(null);
        setAnalyzedVertice(null);
        setAllAnts(null);
        setCurrentIter(ConstStrings.ZERO);
        setCurrentReduct(null);
        setListOfReducts(null);
        setPerformedIterations(ConstStrings.ZERO);
        setCalculatedReductInIteration(false);
        setDecisionValues(null);
        setCoreAttributes(null);
        setAllFishes(null);
        System.gc();
    }

    public static int hammingDistance(Fish f1, Fish f2){
        int distance=0;
        for (int i=0; i<f1.getValues().length; i++){
            if (f1.getValues()[i]!=f2.getValues()[i])
                distance++;
        }
        return distance;
    }

    public static Fish updateCenterFish(List<Fish> fishes){
        Fish centerFish = new Fish(-1, false);
        centerFish.getAttributeList().clear();
        centerFish.getNotUsedAttributeList().clear();
        for (int i=0; i<nonDecisionAttributesNumber; i++){
            int sum=0;
            double divideSum;
            for (Fish fish : fishes){
                if (fish.getValues()[i])
                    sum++;
            }
            divideSum = ((double)sum)/((double)(fishes.size()));
            if (divideSum>=0.5){
                centerFish.getValues()[i]=true;
                centerFish.getAttributeList().add(DataAccessor.getAllAttributes().get(i));
            }
            else centerFish.getValues()[i]=false;
        }
        return centerFish;
    }

    public static List<Attribute> returnNonUsedAttributes(List<Attribute> usedAttributes){
        List<Attribute> myList = new ArrayList<>();
        for (int i=0; i<getAllAttributes().size()-1; i++){
            if (!usedAttributes.contains(getAllAttributes().get(i))){
                myList.add(getAllAttributes().get(i));
            }
        }
        return myList;
    }

    public static int getFishNumber() {
        return fishNumber;
    }

    public static void setFishNumber(int fishNumber) {
        DataAccessor.fishNumber = fishNumber;
    }

    public static List<Fish> getAllFishes() {
        return allFishes;
    }

    public static void setAllFishes(List<Fish> allFishes) {
        DataAccessor.allFishes = allFishes;
    }

}
