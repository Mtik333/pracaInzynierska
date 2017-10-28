/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import controllers.FXMLDocumentController;
import data.graph.Ant;
import data.graph.Vertice;
import data.roughsets.Attribute;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Mateusz
 */
public abstract class Logic {

    //zwraca ile mrowek znajduje sie w danym wierzcholku w danym kroku (tryb pojedynczych krokow)
    public static int returnAntsNumberOnVertice(Vertice vertice) {
        int i = ConstStrings.ZERO;
        if (DataAccessor.getCalculationMode().equals(ConstStrings.SINGLE_STEP)) {
            if (DataAccessor.getAllAnts() != null) {
                i = DataAccessor.getAllAnts().stream().filter((ant) -> (ant.getPickedAttributes().get(DataAccessor.getCurrentIter() - ConstStrings.ONE).equals(vertice))).map((_item) -> ConstStrings.ONE).reduce(i, Integer::sum);
            }
        }
        return i;
    }

    //generuje graf (wierzchołki i krawędzi)
    public abstract void generateGraph();

    //generuje feromony na ścieżkach
    protected abstract void generateAntsPheromone();

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
    void generateBasicPheromone() {
        Random random = new Random();
        DataAccessor.getGraph().getEdges().forEach((x) -> x.setPheromone(random.nextDouble() * ConstStrings.PERTURBATION + ConstStrings.HALF));
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
        DataAccessor.setElapsedTime(((double) timeElapsed / ConstStrings.THOUSAND));
    }

    //tryb wykonania jednej iteracji algorytmu
    public void performOneIteration() {
        ExecutorService executor = Executors.newFixedThreadPool(DataAccessor.getAntsNumber());
        if (DataAccessor.getCurrentIter() == ConstStrings.ZERO) {
            DataAccessor.setCurrentIter(ConstStrings.ONE);
        }
        DataAccessor.getAllAnts().forEach(executor::execute);
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        DataAccessor.setCalculatedReductInIteration(true);
        DataAccessor.setCurrentIter(ConstStrings.ZERO);
        DataAccessor.setPerformedIterations(DataAccessor.getPerformedIterations() + ConstStrings.ONE);
        List<Ant> ants = DataAccessor.getAllAnts();
        for (Ant ant : ants) {
            if (ant.isFoundSolution()) {
                evaluateSubsets();
                break;
            }
        }
        updatePheromone();
        if (DataAccessor.getListOfReducts().size() != DataAccessor.getPerformedIterations()) {
            addPreviousReduct();
        }
    }

    //tryb wykonania jednego kroku w iteracji
    public boolean stepToNextVertice() {
        DataAccessor.setCalculationMode(ConstStrings.SINGLE_STEP);
        if (DataAccessor.getCurrentIter() == DataAccessor.getMaxList()) {
            evaluateSubsets();
            updatePheromone();
            DataAccessor.setCurrentIter(ConstStrings.ZERO);
            DataAccessor.setCalculatedReductInIteration(false);
            DataAccessor.setMaxList(DataAccessor.getCurrentReduct().size());
            DataAccessor.setPerformedIterations(DataAccessor.getPerformedIterations() + ConstStrings.ONE);
            return true;
        }
        ExecutorService executor = Executors.newFixedThreadPool(DataAccessor.getAntsNumber());
        DataAccessor.setCurrentIter(DataAccessor.getCurrentIter() + ConstStrings.ONE);
        DataAccessor.getAllAnts().forEach(executor::execute);
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        return false;
    }

    //weryfikacja otrzymanych rozwiazan
    private void evaluateSubsets() {
        DataAccessor.getAllAnts().stream().filter((ant) -> (ant.isFoundSolution())).peek((ant) -> ant.getPickedAttributes().forEach((vertice) -> {
        })).forEachOrdered((_item) -> {
        });
        if (DataAccessor.getCurrentReduct() == null) {
            DataAccessor.setCurrentReduct(new ArrayList<>(DataAccessor.getDataset().get(ConstStrings.ZERO).getAttributes()));
            DataAccessor.setListOfReducts(new ArrayList<>());
            DataAccessor.getCurrentReduct().remove(DataAccessor.getCurrentReduct().size() - ConstStrings.ONE);
        }
        List<Attribute> newReduct = new ArrayList<>();
        DataAccessor.getAllAnts().stream().filter((ant) -> (ant.getPickedAttributes().size() < DataAccessor.getMaxList())).peek((ant) -> DataAccessor.setMaxList(ant.getPickedAttributes().size())).peek((ant) -> {
            newReduct.clear();
            DataAccessor.getCurrentReduct().clear();
        }).forEachOrdered((ant) -> ant.getPickedAttributes().stream().peek((vertice) -> newReduct.add(DataAccessor.getAllAttributes().get(vertice.getIndex()))).forEachOrdered((vertice) -> {
            DataAccessor.getCurrentReduct().add(DataAccessor.getAllAttributes().get(vertice.getIndex()));
        }));
        //przy pierwszej iteracji aktualizacja tylko tam gdzie najmniejszy redukt zamiast wszedzie
        if (DataAccessor.getPerformedIterations() == ConstStrings.ONE) {
            DataAccessor.getAllAnts().stream().filter((ant) -> (ant.getPickedAttributes().size() != DataAccessor.getMaxList())).forEachOrdered((ant) -> ant.setFoundSolution(false));
        }
        if (!newReduct.isEmpty()) {
            DataAccessor.getListOfReducts().add(newReduct);
        }
    }

    //dodanie do listy reduktów w kolejnych iteracji reduktu z poprzedniej pętli (rozwiazanie z poprzedniej iteracji bylo lepsze)
    private void addPreviousReduct() {
        List<Attribute> newReduct = new ArrayList<>();
        newReduct.addAll(DataAccessor.getCurrentReduct());
        DataAccessor.getListOfReducts().add(newReduct);
    }

    //aktualizuj feromony na ścieżkach
    private void updatePheromone() {
        DataAccessor.getGraph().getEdges().forEach((x) -> x.setPheromone(x.getPheromone() * (ConstStrings.ONE - DataAccessor.getPheromoneEvaporation())));
        DataAccessor.getAllAnts().stream().filter((y) -> (y.isFoundSolution())).forEachOrdered((y) -> {
            double contribution = (DataAccessor.getConstantForUpdating() / (double) y.getChosenEdges().size());
            y.getChosenEdges().forEach((x) -> x.setPheromone(x.getPheromone() + contribution));
        });
    }

    void countDecisionClasses() {
        DataAccessor.setDecisionValues(new ArrayList<>());
        DataAccessor.getDataset().stream().filter((dataObject) -> (!DataAccessor.getDecisionValues().contains(dataObject.getAttributes().get(DataAccessor.getDecisionMaker()).getValue()))).forEachOrdered((dataObject) -> DataAccessor.getDecisionValues().add(dataObject.getAttributes().get(DataAccessor.getDecisionMaker()).getValue()));
        DataAccessor.getDecisionValues().size();
    }

    private boolean checkFruitlessSearches() {
        if (DataAccessor.getListOfReducts().size() > DataAccessor.getFruitlessSearches()) {
            int performedIterations = DataAccessor.getPerformedIterations();
            int size = DataAccessor.getListOfReducts().get(performedIterations - ConstStrings.ONE).size();
            for (int i = 2; i < DataAccessor.getFruitlessSearches(); i++) {
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
