/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import controllers.FXMLDocumentController;
import data.graph.ChineseAnt;
import data.graph.Edge;
import data.graph.Graph;
import data.graph.InterfaceAnt;
import data.graph.Vertice;
import data.roughsets.Attribute;
import data.roughsets.DataObject;
import data.roughsets.DataObjectComparator;
import java.util.ArrayList;
import java.util.Arrays;
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
public class ChineseLogic {

    //generuje graf (wierzchołki i krawędzi)
    public void generateGraph() {
        coreCT2();
        List<Attribute> test = DataAccessor.getAllAttributes();
        calculateMutualInformation();
        List<Vertice> vertices = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < DataAccessor.getAllAttributes().size() - 1; i++) {
            if (!DataAccessor.getCoreAttributes().contains(DataAccessor.getAllAttributes().get(i))) {
                vertices.add(new Vertice(DataAccessor.getAllAttributes().get(i).getName(), i));
            }
        }
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = i + 1; j < vertices.size(); j++) {
                edges.add(new Edge(vertices.get(i), vertices.get(j))); //graf pelny, wiec krawdzie miedzy kazdymi wierzcholkami
            }
        }
        DataAccessor.setGraph(new Graph(vertices, edges));
        DataAccessor.setAntsNumber(vertices.size());
        DataAccessor.setMaxList(vertices.size());
//        DataObjectMultipleComparator dbmp = new DataObjectMultipleComparator();
//        List<Integer> test5 = new ArrayList<Integer>();
//        test5.add(1);
//        test5.add(2);
//        test5.add(DataAccessor.getAllAttributes().get(1));
//        test5.add(DataAccessor.getAllAttributes().get(2));
//        dbmp.setSortingBy(test5);
//        Collections.sort(DataAccessor.getDataset(), dbmp);
//        for (DataObject db : DataAccessor.getDataset()){
//            System.out.println(db.toString());
//        }
//        System.out.println();
    }

    //generuje feromony na ścieżkach
    public void generateAntsPheromone() {
        if (DataAccessor.getCurrentReduct() == null) {
            generateBasicPheromone();
        }
        List<InterfaceAnt> newAnts = new ArrayList<>();
        for (int i = 0; i < DataAccessor.getAntsNumber(); i++) {
            ChineseAnt ant = new ChineseAnt(i);
            ant.initLists(DataAccessor.getGraph().getVertices());
            newAnts.add(ant);
        }
        DataAccessor.setAllAnts(newAnts);
    }

    //generowanie bazowego feromonu
    public void generateBasicPheromone() {
        Random random = new Random();
        DataAccessor.getGraph().getEdges().forEach((x) -> {
            x.setPheromone(random.nextDouble() * 0.1 + 0.5);
        });
    }

    //inicjalizacja mrowek na losowych pozycjach
    public void initializeAntsRandom() {
        DataAccessor.setCalculatedReductInIteration(false);
        generateAntsPheromone();
        Random random = new Random();
        for (InterfaceAnt ant : DataAccessor.getAllAnts()) {
            int j = random.nextInt(DataAccessor.getGraph().getVertices().size()); //losowy wybór
            ant.pickVertice(ant.getUnpickedAttributes().get(j));
        }
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
        List<InterfaceAnt> ants = DataAccessor.getAllAnts();
        for (InterfaceAnt ant : ants) {
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
        List<InterfaceAnt> ants = DataAccessor.getAllAnts();
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

    //funkcje do algorytmu CORE-CT
    //zlicza ilosc klas decyzyjnych
    public int countDecisionClasses() {
        DataAccessor.setDecisionValues(new ArrayList<>());
        DataAccessor.getDataset().stream().filter((dataObject) -> (!DataAccessor.getDecisionValues().contains(dataObject.getAttributes().get(DataAccessor.getDecisionMaker()).getValue()))).forEachOrdered((dataObject) -> {
            DataAccessor.getDecisionValues().add(dataObject.getAttributes().get(DataAccessor.getDecisionMaker()).getValue());
        });
        return DataAccessor.getDecisionValues().size();
    }

    //zlicza ilosc konfliktow (w klasie)
    public int countConflictsRow(int[] ctRow) {
        double initialValue = Math.pow(Arrays.stream(ctRow).sum(), 2);
        for (int value : ctRow) {
            initialValue = initialValue - Math.pow(value, 2);
        }
        initialValue = initialValue * 0.5;
        return (int) initialValue;
    }

    //zlicza ilosc konfliktow ogolem
    public int countConflictsTotal(int[] ctTotal) {
        double finalValue = Math.pow(Arrays.stream(ctTotal).sum(), 2);
        for (int value : ctTotal) {
            finalValue = finalValue - Math.pow(value, 2);
        }
        finalValue = finalValue * 0.5;
        return (int) finalValue;
    }

    //caly algorytm CT
    public void coreCT2() {
        List<Attribute> foundCore = new ArrayList<>();
        int decisionClasses = countDecisionClasses();
        System.out.println(decisionClasses);
        for (int i = 0; i < DataAccessor.getAllAttributes().size() - 1; i++) {
            int[] ctRow = new int[decisionClasses];
            int[] ctTotal = new int[decisionClasses];
            int confs = 0;
            DataObject prev = null;
            boolean difference = false;
            Collections.sort(DataAccessor.getDataset(), new DataObjectComparator(i));
            for (int k = 0; k < DataAccessor.getDataset().size(); k++) {
                for (int j = 0; j < DataAccessor.getDataset().get(0).getAttributes().size() - 1; j++) {
                    if (j != i) {
                        if (prev != null) {
                            if (!prev.getAttributes().get(j).getValue().equals(DataAccessor.getDataset().get(k).getAttributes().get(j).getValue())) {
                                difference = true;
                                break;
                            }
                        }
                    }
                }
                if (difference == false) {
                    ctTotal[DataAccessor.getDecisionValues().indexOf(DataAccessor.getDataset().get(k).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())]++;
                    ctRow[DataAccessor.getDecisionValues().indexOf(DataAccessor.getDataset().get(k).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())]++;
                    prev = DataAccessor.getDataset().get(k);
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
                foundCore.add(DataAccessor.getAllAttributes().get(i));
            }
        }
        foundCore.forEach((a) -> {
            System.out.print(a.getName() + ",");
        });
        if (foundCore.size() == DataAccessor.getAllAttributes().size() - 1) {
            DataAccessor.setCoreAttributes(new ArrayList<Attribute>());
        } else {
            DataAccessor.setCoreAttributes(foundCore);
        }
        Collections.sort(DataAccessor.getDataset());
        //DataObjectMultipleComparator domc = new DataObjectMultipleComparator(foundCore);
        //Collections.sort(DataAccessor.getDataset(), domc);
    }

    //FUNKCJE DO LICZENIA ENTROPII
    private void calculateMutualInformation() {
        double mutualInformation = informationEntropyD() - conditionalEntropyC();
        DataAccessor.setDatasetMutualInformation(mutualInformation);
        System.out.println(mutualInformation);
    }

    private double informationEntropyD() {
        double value = 0;
        for (String decision : DataAccessor.getDecisionValues()) {
            int instances = 0;
            for (DataObject dataObject : DataAccessor.getDataset()) {
                if (dataObject.getAttributes().get(DataAccessor.getDecisionMaker()).getValue().equals(decision)) {
                    instances++;
                }
            }
            double probability = ((double) instances) / ((double) DataAccessor.getDataset().size());
            double logarithm = (Math.log(probability) / Math.log(2));
            value += (-1) * probability * logarithm;
        }
        DataAccessor.setDecisionEntropy(value);
        return value;
    }

    private double conditionalEntropyC() {
//        for (DataObject x : DataAccessor.getDataset())
//            System.out.println(x.toString());
        double finalValue = 0;
        double singleAttrValue = 0;
        int numberOfClassInstances = 0;
        int[] decisionsInstances = new int[DataAccessor.getDecisionValues().size()];
        DataObject prev = null;
        for (int i = 0; i < DataAccessor.getDataset().size(); i++) {
            if (prev == null) {
                Arrays.fill(decisionsInstances, 0);
                prev = DataAccessor.getDataset().get(i);
                numberOfClassInstances++;
                decisionsInstances[DataAccessor.getDecisionValues().indexOf(DataAccessor.getDataset().get(i).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())]++;
            } else {
                boolean theSame = true;
                for (int j = 0; j < prev.getAttributes().size() - 1; j++) {
                    if (!prev.getAttributes().get(j).getValue().equals(DataAccessor.getDataset().get(i).getAttributes().get(j).getValue())) {
                        theSame = false;
                        break;
                    }
                }
                if (theSame) {
                    numberOfClassInstances++;
                    decisionsInstances[DataAccessor.getDecisionValues().indexOf(DataAccessor.getDataset().get(i).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())]++;
                } else {
                    singleAttrValue = directConditionalEntropyCalc(decisionsInstances, numberOfClassInstances);
                    singleAttrValue *= (-1) * ((double) numberOfClassInstances) / ((double) DataAccessor.getDataset().size());
                    numberOfClassInstances = 0;
                    finalValue += singleAttrValue;
                    singleAttrValue = 0;
                    i--;
                    theSame = true;
                    prev = null;
                }
            }
        }
        //obliczanie ostatniego zbioru
        if (numberOfClassInstances > 1) {
            singleAttrValue = directConditionalEntropyCalc(decisionsInstances, numberOfClassInstances);
            finalValue += singleAttrValue;
            prev = null;
        }
        return finalValue;
    }

    private double directConditionalEntropyCalc(int[] decisionsInstances, int numberOfClassInstances) {
        double singleAttrValue = 0;
        for (int k = 0; k < decisionsInstances.length; k++) {
            double probability = ((double) decisionsInstances[k]) / ((double) numberOfClassInstances);
            if (probability == 0) {
                singleAttrValue += 0;
            } else {
                double logarithm = (Math.log(probability) / Math.log(2));
                singleAttrValue = singleAttrValue + (probability * logarithm);
            }
        }
        return singleAttrValue;
    }

//    public void coreDDM() {
//        List<String> foundCore = new ArrayList<>();
//        for (int i = 0; i < indiscMatrix.length; i++) {
//            for (int j = i; j < indiscMatrix[i].length; j++) {
//                if (indiscMatrix[i][j] != null) {
//                    if (indiscMatrix[i][j].chars().filter(ch -> ch == ',').count() == 2) {
//                        int singleton = (int)indiscMatrix[i][j].replace(",", "").charAt(0)-'0';
//                        if (!foundCore.contains(attributesAll.get(singleton).getName())){
//                            foundCore.add(attributesAll.get(singleton).getName());
//                        }
//                    }
//                }
//            }
//        }
//        for (String x : foundCore) {
//            System.out.print(x + ",");
//        }
//    }
}
