/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import controllers.FXMLDocumentController;
import data.graph.Edge;
import data.graph.Graph;
import data.graph.NewAnt;
import data.graph.Vertice;
import data.roughsets.Attribute;
import data.roughsets.DataObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Mateusz
 */
public class NewLogic {

    //generuje graf (wierzchołki i krawędzi)
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
        String[][] indiscMatrix = DataAccessor.getIndiscMatrix();
        //test
//        ChineseLogic chineseLogic = new ChineseLogic();
//        chineseLogic.generateGraph();
        
        //System.out.println(graph.toString());
    }

    //generuje feromony na ścieżkach
    public void generateAntsPheromone() {
        if (DataAccessor.getCurrentReduct() == null) {
            generateBasicPheromone();
        }
        List<NewAnt> newAnts = new ArrayList<>();
        for (int i = 0; i < DataAccessor.getAntsNumber(); i++) {
            NewAnt ant = new NewAnt(i);
            ant.initLists(DataAccessor.getGraph().getVertices());
            newAnts.add(ant);
        }
        DataAccessor.setAllAnts(newAnts);
    }

    //inicjalizacja mrowek na losowych pozycjach
    public void initializeAntsRandom() {
        DataAccessor.setCalculatedReductInIteration(false);
        generateAntsPheromone();
        Random random = new Random();
        DataAccessor.getAllAnts().stream().map((ant) -> {
            int j = random.nextInt(DataAccessor.getGraph().getVertices().size()); //losowy wybór
            ant.pickVertice(ant.getUnpickedAttributes().get(j));
            return ant;
        }).forEachOrdered((ant) -> {
            ant.setDiscMatrix(DataAccessor.getIndiscMatrix());
        });
        DataAccessor.setCurrentIter(1);
    }

    //tryb znajdowania reduktu
    public void findReduct() {
        while (DataAccessor.getPerformedIterations() < DataAccessor.getLoopLimit()) {
            initializeAntsRandom();
            performOneIteration();
            FXMLDocumentController.colorEdges();
        }
    }

    //tryb wykonania jednej iteracji algorytmu
    public void performOneIteration() {
        DataAccessor.setCalculationMode(ConstStrings.SINGLE_ITERATION);
        ExecutorService executor = Executors.newFixedThreadPool(DataAccessor.getAntsNumber());
        if (DataAccessor.getCurrentIter() == 0) {
            DataAccessor.setCurrentIter(1);
        }
        DataAccessor.getAllAnts().forEach((ant) -> {
            executor.execute(ant);
        });
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        DataAccessor.setCalculatedReductInIteration(true);
        DataAccessor.setCurrentIter(0);
        DataAccessor.setPerformedIterations(DataAccessor.getPerformedIterations() + 1);
        List<NewAnt> ants = DataAccessor.getAllAnts();
        for (NewAnt ant : ants) {
            if (ant.isFoundSolution()) {
                //DataAccessor.setCurrentIter(DataAccessor.getCurrentIter()-1);
                evaluateSubsets();
                break;
            }
        }
        updatePheromone();
        if (DataAccessor.getListOfReducts().size() != DataAccessor.getPerformedIterations()) {
            addPreviousReduct();
        }
        //System.out.println(DataAccessor.getCurrentReduct().size());
    }

    //tryb wykonania jednego kroku w iteracji
    public boolean stepToNextVertice() {
        DataAccessor.setCalculationMode(ConstStrings.SINGLE_STEP);
        List<NewAnt> ants = DataAccessor.getAllAnts();
        if (DataAccessor.getCurrentIter()==DataAccessor.getMaxList()){
            evaluateSubsets();
            updatePheromone();
            DataAccessor.setCurrentIter(0);
            DataAccessor.setCalculatedReductInIteration(false);
            DataAccessor.setMaxList(DataAccessor.getCurrentReduct().size());
            DataAccessor.setPerformedIterations(DataAccessor.getPerformedIterations() + 1);
            return true;
        }
        ExecutorService executor = Executors.newFixedThreadPool(DataAccessor.getAntsNumber());
        DataAccessor.setCurrentIter(DataAccessor.getCurrentIter() + 1);
        DataAccessor.getAllAnts().forEach((ant) -> {
            executor.execute(ant);
        });
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        //System.out.println(DataAccessor.getAllAnts().toString());
        return false;
    }

    //generowanie bazowego feromonu
    public void generateBasicPheromone() {
        Random random = new Random();
        DataAccessor.getGraph().getEdges().forEach((x) -> {
            x.setPheromone(random.nextDouble() * 0.1 + 0.5);
        });
    }

    //wypelnienie macierzy rozroznialnosci
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
                    if (cellString.length() > 2) {
                        DataAccessor.getIndiscMatrix()[i][j] = cellString.toString();
                    }
                    /*if (cellString.length() > 0) {
                        cellString.deleteCharAt(cellString.length() - 1);
                    }*/
                }
            }
        }
    }

    //zwraca ile mrowek znajduje sie w danym wierzcholku w danym kroku (tryb pojedynczych krokow)
    public static int returnAntsNumberOnVertice(Vertice vertice) {
        int i = 0;
        if (DataAccessor.getAllAnts() != null) {
            i = DataAccessor.getAllAnts().stream().filter((ant) -> (ant.getPickedAttributes().get(DataAccessor.getCurrentIter() - 1).equals(vertice))).map((_item) -> 1).reduce(i, Integer::sum);
        }
        return i;
    }

    //weryfikacja otrzymanych rozwiazan
    public void evaluateSubsets() {
        DataAccessor.getAllAnts().stream().filter((ant) -> (ant.isFoundSolution())).map((ant) -> {
            ant.getPickedAttributes().forEach((vertice) -> {
                //System.out.print(vertice.getName() + ",");
            });
            return ant;
        }).forEachOrdered((_item) -> {
            //System.out.println();
        });
        if (DataAccessor.getCurrentReduct() == null) {
            DataAccessor.setCurrentReduct(new ArrayList<>(DataAccessor.getDataset().get(0).getAttributes()));
            DataAccessor.setListOfReducts(new ArrayList<>());
            DataAccessor.getCurrentReduct().remove(DataAccessor.getCurrentReduct().size() - 1);
        }
        List<Attribute> newReduct = new ArrayList<>();
        DataAccessor.getAllAnts().stream().filter((ant) -> (ant.getPickedAttributes().size() < DataAccessor.getMaxList())).map((ant) -> {
            DataAccessor.setMaxList(ant.getPickedAttributes().size());
            return ant;
        }).map((ant) -> {
            newReduct.clear();
            DataAccessor.getCurrentReduct().clear();
            return ant;
        }).forEachOrdered((ant) -> {
            ant.getPickedAttributes().stream().map((vertice) -> {
                newReduct.add(DataAccessor.getAllAttributes().get(vertice.getIndex()));
                return vertice;
            }).forEachOrdered((vertice) -> {
                DataAccessor.getCurrentReduct().add(DataAccessor.getAllAttributes().get(vertice.getIndex()));
            });
        });
        if (!newReduct.isEmpty()) {
            DataAccessor.getListOfReducts().add(newReduct);
        }
    }

    //dodanie do listy reduktów w kolejnych iteracji reduktu z poprzedniej pętli (rozwiazanie z poprzedniej iteracji bylo lepsze)
    public void addPreviousReduct() {
        List<Attribute> newReduct = new ArrayList<>();
        DataAccessor.getCurrentReduct().forEach((x) -> {
            newReduct.add(x);
        });
        DataAccessor.getListOfReducts().add(newReduct);
    }

    //aktualizuj feromony na ścieżkach
    public void updatePheromone() {
        DataAccessor.getGraph().getEdges().forEach((x) -> {
            x.setPheromone(x.getPheromone() * (1 - DataAccessor.getPheromoneEvaporation()));
        });
        DataAccessor.getAllAnts().stream().filter((y) -> (y.isFoundSolution())).forEachOrdered((y) -> {
            double contribution = ((double) DataAccessor.getConstantForUpdating() / (double) y.getChosenEdges().size());
            y.getChosenEdges().forEach((x) -> {
                x.setPheromone(x.getPheromone() + contribution);
            });
        });
        List<Edge> edges = DataAccessor.getGraph().getEdges();
        Edge d = Collections.max(edges, Comparator.comparing(c -> c.getPheromone()));
        //System.out.println("xd");
    }
}