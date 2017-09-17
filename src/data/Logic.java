/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import controllers.FXMLDocumentController;
import data.graph.Ant;
import data.graph.Edge;
import data.graph.Vertice;
import data.roughsets.Attribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Mateusz
 */
public abstract class Logic {

    //generuje graf (wierzchołki i krawędzi)
    public abstract void generateGraph();

    //generuje feromony na ścieżkach
    public abstract void generateAntsPheromone();

    //inicjalizacja mrowek na losowych pozycjach
    public void initializeAntsRandom() {
        DataAccessor.setCalculatedReductInIteration(false);
        generateAntsPheromone();
        Random random = new Random();
        DataAccessor.getAllAnts().forEach((ant) -> {
            int j = random.nextInt(DataAccessor.getGraph().getVertices().size()); //losowy wybór
            ant.pickVertice(ant.getUnpickedAttributes().get(j));
        });
        DataAccessor.setCurrentIter(1);
    }

    //generowanie bazowego feromonu
    public void generateBasicPheromone() {
        Random random = new Random();
        DataAccessor.getGraph().getEdges().forEach((x) -> {
            x.setPheromone(random.nextDouble() * 0.01 + 0.5);
        });
    }

    //tryb znajdowania reduktu
    public void findReduct() {
        long timeElapsed = (long) DataAccessor.getElapsedTime();
        while (DataAccessor.getPerformedIterations() < DataAccessor.getLoopLimit()) {
            initializeAntsRandom();
            long startTime = new Date().getTime();
            performOneIteration();
            long stopTime = new Date().getTime();
            timeElapsed = timeElapsed + (stopTime - startTime);
            FXMLDocumentController.colorEdges();
            if (checkFruitlessSearches()) {
                break;
            }
        }
        DataAccessor.setElapsedTime(((double) timeElapsed / 1000));
        System.out.println(timeElapsed);
    }

    //tryb wykonania jednej iteracji algorytmu
    public void performOneIteration() {
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
        List<Ant> ants = DataAccessor.getAllAnts();
        for (Ant ant : ants) {
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
        List<Ant> ants = DataAccessor.getAllAnts();
        if (DataAccessor.getCurrentIter() == DataAccessor.getMaxList()) {
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
        //przy pierwszej iteracji aktualizacja tylko tam gdzie najmniejszy redukt zamiast wszedzie
        if (DataAccessor.getPerformedIterations() == 1) {
            DataAccessor.getAllAnts().stream().filter((ant) -> (ant.getPickedAttributes().size() != DataAccessor.getMaxList())).forEachOrdered((ant) -> {
                ant.setFoundSolution(false);
            });
        }

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
            double contribution = (DataAccessor.getConstantForUpdating() / (double) y.getChosenEdges().size());
            y.getChosenEdges().forEach((x) -> {
                x.setPheromone(x.getPheromone() + contribution);
            });
        });
        List<Edge> edges = DataAccessor.getGraph().getEdges();
        Edge d = Collections.max(edges, Comparator.comparing(c -> c.getPheromone()));
        //System.out.println("xd");
    }

    public int countDecisionClasses() {
        DataAccessor.setDecisionValues(new ArrayList<>());
        DataAccessor.getDataset().stream().filter((dataObject) -> (!DataAccessor.getDecisionValues().contains(dataObject.getAttributes().get(DataAccessor.getDecisionMaker()).getValue()))).forEachOrdered((dataObject) -> {
            DataAccessor.getDecisionValues().add(dataObject.getAttributes().get(DataAccessor.getDecisionMaker()).getValue());
        });
        return DataAccessor.getDecisionValues().size();
    }

    public boolean checkFruitlessSearches() {
        if (DataAccessor.getListOfReducts().size() > DataAccessor.getFruitlessSearches()) {
            List<List<Attribute>> list = DataAccessor.getListOfReducts();
            int performedIterations = DataAccessor.getPerformedIterations();
            int size = DataAccessor.getListOfReducts().get(performedIterations - 1).size();
            for (int i = 2; i <= DataAccessor.getFruitlessSearches(); i++) {
                if (DataAccessor.getListOfReducts().get(performedIterations - i).size() != size) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
