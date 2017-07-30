/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import data.graph.*;
import data.roughsets.*;

/**
 *
 * @author Mateusz
 */
public class Logic {

    private Graph graph;  //graf
    private File loadedFile; //plik z danymi
    private List<DataObject> dataset; //zbior obiektów wczytanych
    private List<Ant> antsList; //lista mrówek
    private List<Attribute> currentReduct;
    private float pheromoneRelevance; //istotnosc feromonu
    private float edgeRelevance; //istotnosc wagi sciezki
    private double pheromoneEvaporation = 0.5; //współczynnik wyparowywania
    private int currentLoopLimit; //ilosc przejsc w danej iteracji
    private int loopNumber = 20; //ilosc iteracji
    private int amountOfAnts = 5; //ilosc mrowek\
    private int constantForUpdating = 1;
    private String[] attributesNames; //nazwy atrybutów
    private List<Attribute> attributesAll; //atrybuty
    private String[][] indiscMatrix; //macierz rozróznialności
    private int decisionMaker; //indeks atrybutu decyzyjnego
    private List<String> decisionValues; //wartosci decyzyjne

    public Logic() {
    }

    public List<Ant> getAntsList() {
        return antsList;
    }

    public void setAntsList(List<Ant> antsList) {
        this.antsList = antsList;
    }

    public int getDecisionMaker() {
        return decisionMaker;
    }

    public void setDecisionMaker(int decisionMaker) {
        this.decisionMaker = decisionMaker;
    }

    public File getFile() {
        return loadedFile;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public void setFile(File f) {
        this.loadedFile = new File(f.toURI());
    }

    public List<DataObject> getDataset() {
        return dataset;
    }

    public void setDataset(List<DataObject> dataset) {
        this.dataset = dataset;
    }

    public String[] getAttributesNames() {
        return attributesNames;
    }

    public void setAttributesNames(String[] attributesNames) {
        this.attributesNames = attributesNames;
    }

    public String[][] getIndiscMatrix() {
        return indiscMatrix;
    }

    public void setIndiscMatrix(String[][] indiscMatrix) {
        this.indiscMatrix = indiscMatrix;
    }

    public void fileToObjects(String separator) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(loadedFile.getPath()));
        setAttributesNames(br.readLine().split(separator));
        this.attributesAll = new ArrayList<>();
        setDecisionMaker(getAttributesNames().length - 1);
        int j = 1; //ilosc obiektow
        String line;
        dataset = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            String oneObject[] = line.split(separator);
            int i = 0; //ktory z kolei atrybut wczytywany
            List<Attribute> all_attributes = new ArrayList<>();
            for (String x : oneObject) {
                Attribute attribute = new Attribute(attributesNames[i], x);
                if (i != getDecisionMaker()) {
                    all_attributes.add(attribute);
                } else {
                    attribute.setDecisionMaking(true);
                    all_attributes.add(attribute);
                }
                i++;
            }
            DataObject newObject = new DataObject("" + j);
            newObject.setAttributes(all_attributes);
            dataset.add(newObject);
            j++;
        }
        if (getGraph() == null) {
            generateGraph();
        }
        attributesAll = dataset.get(0).getAttributes();
    }

    public void generateGraph() {
        List<Vertice> vertices = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < attributesNames.length; i++) {
            vertices.add(new Vertice(attributesNames[i], i));
        }
        vertices.remove(decisionMaker); //usuwamy wierzcholek decyzyjny
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = i + 1; j < vertices.size(); j++) {
                edges.add(new Edge(vertices.get(i), vertices.get(j))); //graf pelny, wiec krawdzie miedzy kazdymi wierzcholkami
            }
        }
        setGraph(new Graph(vertices, edges));
        //System.out.println(graph.toString());
    }

    public void fillIndiscMatrix() {
        DataObject dat1, dat2;
        StringBuilder cellString = new StringBuilder();
        this.indiscMatrix = new String[getDataset().size()][getDataset().size()];
        for (int i = 0; i < dataset.size(); i++) {
            for (int j = (i + 1); j < dataset.size(); j++) {
                if (!dataset.get(i).getAttributes().get(decisionMaker).getValue().equals(dataset.get(j).getAttributes().get(decisionMaker).getValue())) {
                    dat1 = dataset.get(i);
                    dat2 = dataset.get(j);
                    cellString.setLength(0);
                    cellString.append(",");
                    for (int k = 0; k < attributesNames.length - 1; k++) { //problem gdy decisionmaker nie na końcu
                        if (!dat1.getAttributes().get(k).getValue().equals(dat2.getAttributes().get(k).getValue())) {
                            cellString.append(k).append(",");
                        }
                    }
                    if (cellString.length()>2)
                        indiscMatrix[i][j] = cellString.toString();
                    /*if (cellString.length() > 0) {
                        cellString.deleteCharAt(cellString.length() - 1);
                    }*/
                }
            }
        }
    }

    public void generateAntsForFirstIteration() {
        Random random = new Random();
        if (getGraph() == null) {
            generateGraph();
        }
        amountOfAnts = getGraph().getVertices().size();
        antsList = new ArrayList<Ant>(); //lista mrówek
        currentLoopLimit = getGraph().getVertices().size() - 1; //ile wierzchołków
        constantForUpdating = getGraph().getVertices().size() / 2;
        for (int i = 0; i < loopNumber; i++) {
            doTheAlgorithm(currentLoopLimit);
        }
        System.out.println("The reduct:");
        for (Attribute x : currentReduct) {
            System.out.print(x.getName() + ",");
        }
    }

    public void generateBasicPheromone() {
        Random random = new Random();
        for (Edge x : graph.getEdges()) {
            x.setPheromone(random.nextDouble() * 0.1 + 0.5);
        }
    }

    public void doTheAlgorithm(int maxIteration) {
        antsList.clear();
        ExecutorService executor = Executors.newFixedThreadPool(amountOfAnts); //wszystkie watki ruszajo
        generateBasicPheromone(); //generuj pierwotny feromon
        Random random = new Random();
        for (int i = 0; i < amountOfAnts; i++) {
            int j = random.nextInt(getGraph().getVertices().size());
            Ant ant = new Ant(i);
            ant.setMaxList(maxIteration);
            ant.setAllEdges(getGraph().getEdges());
            ant.initLists(getGraph().getVertices());
            ant.pickVertice(getGraph().getVertices().get(j));
            ant.setDiscMatrix(indiscMatrix);
            antsList.add(ant);
        }
        for (Ant x : antsList) {
            executor.execute(x); //startuj watek
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        evaluateSubsets();
        updatePheromone();
    }

    public void evaluateSubsets() {
        for (Ant ant : antsList) {
            if (ant.isFoundSolution()) {
                for (Vertice vertice : ant.getPickedAttributes()) {
                    System.out.print(vertice.getName() + ",");
                }
                System.out.println();
            }
        }
        System.out.println("\nFinished all threads");
        if (currentReduct == null) {
            currentReduct = new ArrayList<Attribute>(dataset.get(0).getAttributes());
            currentReduct.remove(currentReduct.size()-1);
        }
        for (Ant x : antsList) {
            if (x.getPickedAttributes().size() < currentLoopLimit) {
                currentLoopLimit = x.getPickedAttributes().size();
                currentReduct.clear();
                for (Vertice vertice : x.getPickedAttributes()) {
                    currentReduct.add(attributesAll.get(vertice.getIndex()));
                }
            }
        }
    }

    public void updatePheromone() {
        for (Edge x : getGraph().getEdges()) {
            x.setPheromone(x.getPheromone() * (1 - pheromoneEvaporation));
        }
        for (Ant y : antsList) {
            double contribution = constantForUpdating / y.getChosenEdges().size();
            for (Edge x : y.getChosenEdges()) {
                x.setPheromone(x.getPheromone() + contribution);
            }
        }
    }

    public int countDecisionClasses() {
        decisionValues = new ArrayList<>();
        for (DataObject dataObject : dataset) {
            if (!decisionValues.contains(dataObject.getAttributes().get(decisionMaker).getValue())) {
                decisionValues.add(dataObject.getAttributes().get(decisionMaker).getValue());
            }
        }
        return decisionValues.size();
    }

    public int countConflictsRow(int[] ctRow) {
        double initialValue = Math.pow(Arrays.stream(ctRow).sum(), 2);
        for (int value : ctRow) {
            initialValue = initialValue - Math.pow(value, 2);
        }
        initialValue = initialValue * 0.5;
        return (int) initialValue;
    }

    public int countConflictsTotal(int[] ctTotal) {
        double finalValue = Math.pow(Arrays.stream(ctTotal).sum(), 2);
        for (int value : ctTotal) {
            finalValue = finalValue - Math.pow(value, 2);
        }
        finalValue = finalValue * 0.5;
        return (int) finalValue;
    }

    public void coreDDM() {
        List<String> foundCore = new ArrayList<>();
        for (int i = 0; i < indiscMatrix.length; i++) {
            for (int j = i; j < indiscMatrix[i].length; j++) {
                if (indiscMatrix[i][j] != null) {
                    if (indiscMatrix[i][j].chars().filter(ch -> ch == ',').count() == 2) {
                        int singleton = (int)indiscMatrix[i][j].replace(",", "").charAt(0)-'0';
                        if (!foundCore.contains(attributesAll.get(singleton).getName())){
                            foundCore.add(attributesAll.get(singleton).getName());
                        }
                    }
                }
            }
        }
        for (String x : foundCore) {
            System.out.print(x + ",");
        }
    }
    
    public void coreCT2(){
        List<Attribute> foundCore = new ArrayList<>();
        int decisionClasses = countDecisionClasses();
        System.out.println(decisionClasses);
        for (int i = 0; i < attributesAll.size() - 1; i++) {
            int[] ctRow = new int[decisionClasses];
            int[] ctTotal = new int[decisionClasses];
            int confs = 0;
            DataObject prev = null;
            boolean difference = false;
            Collections.sort(dataset, new DataObjectComparator(i));
            for (int k=0; k<dataset.size(); k++){
                for (int j = 0; j < dataset.get(0).getAttributes().size() - 1; j++) {
                    if (j != i) {
                        if (prev != null) {
                            if (!prev.getAttributes().get(j).getValue().equals(dataset.get(k).getAttributes().get(j).getValue())) {
                                difference = true;
                                break;
                            }
                        }
                    }
                }
                if (difference == false) {
                    ctTotal[decisionValues.indexOf(dataset.get(k).getAttributes().get(decisionMaker).getValue())]++;
                    ctRow[decisionValues.indexOf(dataset.get(k).getAttributes().get(decisionMaker).getValue())]++;
                    prev = dataset.get(k);
                } else {
                    int confsRow = countConflictsRow(ctRow);
                    confs = confs + confsRow;
                    Arrays.fill(ctRow, 0);
                    difference = false;
                    prev = null;
                    k--;
                }
            }
            int discdA = countConflictsTotal(ctTotal);
            int discdAminus = discdA - confs;
            if (discdAminus < discdA) {
                foundCore.add(attributesAll.get(i));
            }
        }
        for (Attribute a : foundCore){
            System.out.print(a.getName()+",");
        }
    }
}
