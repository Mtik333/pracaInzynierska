/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private double pheromoneEvaporation; //współczynnik wyparowywania
    private int currentLoopLimit; //ilosc przejsc w danej iteracji
    private int loopNumber=20; //ilosc iteracji
    private int amountOfAnts=5; //ilosc mrowek
    private String[] attributesNames; //nazwy atrybutów
    private List<Attribute> attributesAll; //atrybuty
    private String[][] indiscMatrix; //macierz rozróznialności
    private int decisionMaker; //indeks atrybutu decyzyjnego

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
        this.attributesAll=new ArrayList<>();
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
        attributesAll=dataset.get(0).getAttributes();
    }

    public void generateGraph() {
        List<Vertice> vertices = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        for (int i=0; i<attributesNames.length; i++){
            vertices.add(new Vertice(attributesNames[i],i));
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
        StringBuilder cellString=new StringBuilder();
        this.indiscMatrix = new String[getDataset().size()][getDataset().size()];
        for (int i = 0; i < dataset.size(); i++) {
            for (int j = (i + 1); j < dataset.size(); j++) {
                if (!dataset.get(i).getAttributes().get(decisionMaker).getValue().equals(dataset.get(j).getAttributes().get(decisionMaker).getValue())) {
                    dat1=dataset.get(i);
                    dat2=dataset.get(j);
                    cellString.setLength(0);
                    for (int k=0; k<attributesNames.length-1; k++){ //problem gdy decisionmaker nie na końcu
                        if (!dat1.getAttributes().get(k).getValue().equals(dat2.getAttributes().get(k).getValue()))
                            cellString.append(k).append(",");
                    }
                    if (cellString.length()>0)
                        cellString.deleteCharAt(cellString.length()-1);
                    indiscMatrix[i][j]=cellString.toString();
                }
            }
        }
    }
    public void generateAntsForFirstIteration(){
        antsList = new ArrayList<Ant>(); //lista mrówek
        Random random = new Random();
        if (getGraph() == null) {
            generateGraph();
        }
        currentLoopLimit = getGraph().getVertices().size()-1; //ile wierzchołków
        doTheAlgorithm(currentLoopLimit);
    }
    public void generateBasicPheromone(){
        Random random = new Random();
        for (Edge x : graph.getEdges()){
            x.setPheromone(random.nextDouble()*0.1+0.5);
        }
    }
    public void doTheAlgorithm(int maxIteration){
        ExecutorService executor = Executors.newFixedThreadPool(maxIteration); //wszystkie watki ruszajo
        generateBasicPheromone(); //generuj pierwotny feromon
        Random random = new Random();
        for (int i=0; i<amountOfAnts; i++){
            int j = random.nextInt(maxIteration);
            Ant ant = new Ant(i);
            ant.setMaxList(maxIteration);
            ant.setAllEdges(getGraph().getEdges());
            ant.initLists(getGraph().getVertices());
            ant.pickVertice(getGraph().getVertices().get(j));
            ant.setDiscMatrix(indiscMatrix);
            antsList.add(ant);
        }
        for (Ant x : antsList){
            executor.execute(x); //startuj watek
        }
        executor.shutdown();
        while (!executor.isTerminated()){}
        evaluateSubsets();
    }
    public void evaluateSubsets(){
        for (Ant ant : antsList){
            for (Vertice vertice : ant.getPickedAttributes()){
                System.out.print(vertice.getName()+",");
            }
            System.out.println();
        }
        System.out.println("\nFinished all threads");
        for (Ant x : antsList){
            if (x.getPickedAttributes().size()<currentLoopLimit){
                currentReduct=new ArrayList<Attribute>();
                for (Vertice vertice : x.getPickedAttributes()){
                    currentReduct.add(attributesAll.get(vertice.getIndex()));
                }
            }
        }
        System.out.println("testststs");
    }
    public void updatePheromone(){
        for (Edge x : getGraph().getEdges()){
            
        }
    }
}
