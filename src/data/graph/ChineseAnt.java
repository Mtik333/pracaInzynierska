/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.graph;

import data.ConstStrings;
import data.DataAccessor;
import data.roughsets.DataObject;
import data.roughsets.DataObjectMultipleComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static data.ConstStrings.SINGLE_STEP;

/**
 * @author Mateusz
 */
public class ChineseAnt extends Ant {

    private final Map<Vertice, Double> heuristicValues; //informacja heurystyczna
    private double reducedDataset;

    public ChineseAnt(int index) {
        this.index = index;
        this.pickedAttributes = new ArrayList<>();
        this.unpickedAttributes = new ArrayList<>();
        this.allEdges = DataAccessor.getGraph().getEdges();
        this.chosenEdges = new ArrayList<>();
        this.sortedDataset = new ArrayList<>();
        this.heuristicValues = new HashMap<>();
        this.sortByAttributes = new ArrayList<>();
        sortedDataset.addAll(DataAccessor.getDataset());
        sortByAttributes.addAll(DataAccessor.getCoreAttributes());
    }

    @Override
    public void run() {
        currentIter = ConstStrings.ZERO;
        reducedDataset = calculateMutualInformation();
        while (currentIter < DataAccessor.getMaxList() - 1 && DataAccessor.getDatasetMutualInformation() != reducedDataset) {
            unpickedAttributes.forEach(this::computeHeuristic);
            double pheromoneSum = calculateSum();
            for (int i = 0; i < probabilities.size(); i++) {
                probabilities.computeIfPresent(unpickedAttributes.get(i), (t, u) -> {
                    return u / pheromoneSum; //To change body of generated lambdas, choose Tools | Templates.
                });
            }
            pickedAttributes.add(pickVerticeByProbability());
            reducedDataset = calculateMutualInformation();
            currentIter++;
            addEdgeToSolution();
            if (DataAccessor.getCalculationMode().equals(SINGLE_STEP)) {
                foundSolution = DataAccessor.getDatasetMutualInformation() - reducedDataset == ConstStrings.ZERO;
                return;
            }
        }
        foundSolution = DataAccessor.getDatasetMutualInformation() - reducedDataset == ConstStrings.ZERO;
    }

    private double calculateSum() {
        double sumPheromone = ConstStrings.ZERO;
        this.probabilities = new HashMap<>();
        Vertice v = null;
        for (Edge x : allEdges) {
            if (x.getStart().equals(pickedAttributes.get(currentIter)) || x.getEnd().equals(pickedAttributes.get(currentIter))) {
                if (x.getStart().equals(pickedAttributes.get(currentIter))) {
                    v = x.getEnd(); //bierzemy ten drugi węzeł
                }
                if (x.getEnd().equals(pickedAttributes.get(currentIter))) {
                    v = x.getStart();
                }
                if (v != null) {
                    if (!pickedAttributes.contains(v)) {
                        double test1 = Math.pow(x.getPheromone(), DataAccessor.getPheromoneRelevance());
                        double test2 = Math.pow(findHeuristicValue(v), DataAccessor.getEdgeRelevance());
                        double addedValue = test1 * test2;
                        sumPheromone += addedValue;
                        probabilities.put(v, addedValue);
                    }
                }
            }
        }
        return sumPheromone;
    }

    private double findHeuristicValue(Vertice v) {
        for (Map.Entry<Vertice, Double> entry : heuristicValues.entrySet()) {
            if (entry.getKey().getIndex() == v.getIndex()) {
                if (entry.getValue() < DataAccessor.getEpsilonValue()) {
                    return DataAccessor.getEpsilonValue();
                } else {
                    return entry.getValue();
                }
            }
        }
        return 0;
    }

    private double calculateMutualInformation() {
        sortByAttributes.add(DataAccessor.verticeToAttribute(pickedAttributes.get(pickedAttributes.size() - ConstStrings.ONE)));
        domc = new DataObjectMultipleComparator(sortByAttributes);
        sortedDataset.sort(domc);
        return DataAccessor.getDecisionEntropy() - conditionalEntropyC();
    }

    private double conditionalEntropyC() {
        double finalValue = ConstStrings.ZERO;
        double singleAttrValue;
        int numberOfClassInstances = ConstStrings.ZERO;
        int[] decisionsInstances = new int[DataAccessor.getDecisionValues().size()];
        DataObject prev = null;
        for (int i = 0; i < sortedDataset.size(); i++) {
            if (prev == null) {
                Arrays.fill(decisionsInstances, ConstStrings.ZERO);
                prev = sortedDataset.get(i);
                numberOfClassInstances++;
                decisionsInstances[DataAccessor.getDecisionValues().indexOf(sortedDataset.get(i).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())]++;
            } else {
                boolean theSame = true;
                for (int j = 0; j < domc.getSortingBy().size(); j++) {
                    if (!prev.getAttributes().get(domc.getSortingBy().get(j)).getValue().equals(sortedDataset.get(i).getAttributes().get(domc.getSortingBy().get(j)).getValue())) {
                        theSame = false;
                        break;
                    }
                }
                if (theSame) {
                    numberOfClassInstances++;
                    decisionsInstances[DataAccessor.getDecisionValues().indexOf(sortedDataset.get(i).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())]++;
                } else {
                    singleAttrValue = directConditionalEntropyCalc(decisionsInstances, numberOfClassInstances);
                    singleAttrValue *= (ConstStrings.MINUS_ONE) * ((double) numberOfClassInstances) / ((double) sortedDataset.size());
                    numberOfClassInstances = ConstStrings.ZERO;
                    finalValue += singleAttrValue;
                    i--;
                    prev = null;
                }
            }
        }
        //obliczanie ostatniego zbioru
        if (numberOfClassInstances > ConstStrings.ONE) {
            singleAttrValue = directConditionalEntropyCalc(decisionsInstances, numberOfClassInstances);
            finalValue += singleAttrValue;
        }
        return finalValue;
    }

    private double directConditionalEntropyCalc(int[] decisionsInstances, int numberOfClassInstances) {
        double singleAttrValue = ConstStrings.ZERO;
        for (int decisionsInstance : decisionsInstances) {
            double probability = ((double) decisionsInstance) / ((double) numberOfClassInstances);
            if (probability == ConstStrings.ZERO) {
                singleAttrValue += ConstStrings.ZERO;
            } else {
                double logarithm = (Math.log(probability) / Math.log(ConstStrings.TWO));
                singleAttrValue = singleAttrValue + (probability * logarithm);
            }
        }
        return singleAttrValue;
    }

    private void computeHeuristic(Vertice sk) {
        domc.sortingBy.add(sk.getIndex());
        sortedDataset.sort(domc);
        heuristicValues.put(sk, computeSignificanceFormula15());
        domc.sortingBy.remove((Integer) sk.getIndex());
    }

    private double computeSignificanceFormula15() {
        return (DataAccessor.getDecisionEntropy() - conditionalEntropyC()) - reducedDataset;
    }
}
