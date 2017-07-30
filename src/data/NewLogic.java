/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import data.graph.Edge;
import data.graph.Graph;
import data.graph.NewAnt;
import data.graph.Vertice;
import data.roughsets.Attribute;
import data.roughsets.DataObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Mateusz
 */
public class NewLogic {
    
    public void generateGraph() {
        List<Vertice> vertices = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < DataAccessor.getAllAttributes().size(); i++) {
            vertices.add(new Vertice(DataAccessor.getAllAttributes().get(i).getName(), i));
        }
        vertices.remove(DataAccessor.getDecisionMaker()); //usuwamy wierzcholek decyzyjny
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = i + 1; j < vertices.size(); j++) {
                edges.add(new Edge(vertices.get(i), vertices.get(j))); //graf pelny, wiec krawdzie miedzy kazdymi wierzcholkami
            }
        }
        DataAccessor.setGraph(new Graph(vertices, edges));
        DataAccessor.setAntsNumber(vertices.size());
        DataAccessor.setMaxList(vertices.size());
        fillIndiscMatrix();
        //System.out.println(graph.toString());
    }
    
    public void generateAntsPheromone(){
        generateBasicPheromone(); 
        List<NewAnt> newAnts = new ArrayList<>();
        for (int i=0; i<DataAccessor.getAntsNumber(); i++){
            NewAnt ant = new NewAnt(i);
            ant.initLists(DataAccessor.getGraph().getVertices());
            newAnts.add(ant);
        }
        DataAccessor.setAllAnts(newAnts);
    }
    
    public void initializeAntsRandom(){
        generateAntsPheromone();
        ExecutorService executor = Executors.newFixedThreadPool(DataAccessor.getAntsNumber()); 
        Random random = new Random();
        for (NewAnt ant : DataAccessor.getAllAnts()){
            int j = random.nextInt(DataAccessor.getGraph().getVertices().size()); //losowy wybór
            ant.pickVertice(ant.getUnpickedAttributes().get(j));
            ant.setDiscMatrix(DataAccessor.getIndiscMatrix());
        }
    }
    
    public void stepToNextVertice(){
        DataAccessor.setCalculationMode(ConstStrings.SINGLE_STEP);
        boolean foundSolution=false;
        List<NewAnt> ants = DataAccessor.getAllAnts();
        for (NewAnt ant : ants){
            if (ant.isFoundSolution()){
                foundSolution=true; //znaleziony redukt
                DataAccessor.setCurrentIter(DataAccessor.getCurrentIter()-1);
                evaluateSubsets();
                updatePheromone();
                return;
            }
        }
        ExecutorService executor = Executors.newFixedThreadPool(DataAccessor.getAntsNumber());
        DataAccessor.setCurrentIter(DataAccessor.getCurrentIter()+1);
        for (NewAnt ant : DataAccessor.getAllAnts()){
            executor.execute(ant);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {}
        System.out.println(DataAccessor.getAllAnts().toString());
        
    }
    
    public void generateBasicPheromone() {
        Random random = new Random();
        for (Edge x : DataAccessor.getGraph().getEdges()) {
            x.setPheromone(random.nextDouble() * 0.1 + 0.5);
        }
    }
    
    public void fillIndiscMatrix() {
        DataObject dat1, dat2;
        StringBuilder cellString = new StringBuilder();
        DataAccessor.setIndiscMatrix(new String[DataAccessor.getDataset().size()][DataAccessor.getDataset().size()]);
        for (int i = 0; i < DataAccessor.getDataset().size(); i++) {
            for (int j = (i + 1); j < DataAccessor.getDataset().size(); j++) {
                if (!DataAccessor.getDataset().get(i).getAttributes().get(DataAccessor.getDecisionMaker()).getValue().equals(DataAccessor.getDataset().get(j).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())) {
                    dat1 = DataAccessor.getDataset().get(i);
                    dat2 = DataAccessor.getDataset().get(j);
                    cellString.setLength(0);
                    cellString.append(",");
                    for (int k = 0; k < DataAccessor.getAllAttributes().size() - 1; k++) { //problem gdy decisionmaker nie na końcu
                        if (!dat1.getAttributes().get(k).getValue().equals(dat2.getAttributes().get(k).getValue())) {
                            cellString.append(k).append(",");
                        }
                    }
                    if (cellString.length()>2)
                        DataAccessor.getIndiscMatrix()[i][j] = cellString.toString();
                    /*if (cellString.length() > 0) {
                        cellString.deleteCharAt(cellString.length() - 1);
                    }*/
                }
            }
        }
    }
    public static int returnAntsNumberOnVertice(Vertice vertice){
        int i=0;
        if (DataAccessor.getAllAnts()!=null){
            for (NewAnt ant : DataAccessor.getAllAnts()){
                if (ant.getPickedAttributes().get(DataAccessor.getCurrentIter()-1).equals(vertice)){
                    i++;
                }
            }
        }
        return i;
    }
    
    public void evaluateSubsets() {
        for (NewAnt ant : DataAccessor.getAllAnts()) {
            if (ant.isFoundSolution()) {
                for (Vertice vertice : ant.getPickedAttributes()) {
                    System.out.print(vertice.getName() + ",");
                }
                System.out.println();
            }
        }
        if (DataAccessor.getCurrentReduct() == null) {
            DataAccessor.setCurrentReduct(new ArrayList<Attribute>(DataAccessor.getDataset().get(0).getAttributes()));
            DataAccessor.getCurrentReduct().remove(DataAccessor.getCurrentReduct().size()-1);
        }
        for (NewAnt ant : DataAccessor.getAllAnts()) {
            if (ant.getPickedAttributes().size() < DataAccessor.getLoopLimit()) {
                DataAccessor.setLoopLimit(ant.getPickedAttributes().size());
                DataAccessor.getCurrentReduct().clear();
                for (Vertice vertice : ant.getPickedAttributes()) {
                    DataAccessor.getCurrentReduct().add(DataAccessor.getAllAttributes().get(vertice.getIndex()));
                }
            }
        }
    }

    public void updatePheromone() {
        for (Edge x : DataAccessor.getGraph().getEdges()) {
            x.setPheromone(x.getPheromone() * (1 - DataAccessor.getPheromoneEvaporation()));
        }
        for (NewAnt y : DataAccessor.getAllAnts()) {
            double contribution = DataAccessor.getConstantForUpdating() / y.getChosenEdges().size();
            for (Edge x : y.getChosenEdges()) {
                x.setPheromone(x.getPheromone() + contribution);
            }
        }
    }
}
