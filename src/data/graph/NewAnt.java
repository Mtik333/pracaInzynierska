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

import static data.ConstStrings.SINGLE_STEP;

/**
 * @author Mateusz
 */
public class NewAnt extends Ant {

    public NewAnt(int index) {
        this.index = index;
        this.pickedAttributes = new ArrayList<>();
        this.unpickedAttributes = new ArrayList<>();
        this.allEdges = DataAccessor.getGraph().getEdges();
        this.chosenEdges = new ArrayList<>();
        this.sortedDataset = new ArrayList<>();
        this.sortByAttributes = new ArrayList<>();
        sortedDataset.addAll(DataAccessor.getDataset());
    }

    @Override
    public void run() {
        currentIter = ConstStrings.ZERO;
        boolean reducedMatrix = checkIfReduct();
        while (currentIter < DataAccessor.getMaxList() - 1 && !reducedMatrix) {
            double pheromoneSum = calculateSum();
            for (int i = 0; i < probabilities.size(); i++) {
                probabilities.computeIfPresent(unpickedAttributes.get(i), (t, u) -> u / pheromoneSum);
            }
            pickedAttributes.add(pickVerticeByProbability());
            reducedMatrix = checkIfReduct();
            currentIter++;
            addEdgeToSolution();
            if (DataAccessor.getCalculationMode().equals(SINGLE_STEP)) {
                foundSolution = checkIfReduct();
                return;
            }
        }
        foundSolution = checkIfReduct();
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
                        sumPheromone += Math.pow(x.getPheromone(), DataAccessor.getPheromoneRelevance()) * Math.pow(x.getWeight(), DataAccessor.getEdgeRelevance());
                        probabilities.put(v, x.getPheromone());
                    }
                }
            }
        }
        return sumPheromone;
    }

    private boolean checkIfReduct() {
        int[] decisionsInstances = new int[DataAccessor.getDecisionValues().size()];
        DataObject prev = null;
        sortByAttributes.add(DataAccessor.verticeToAttribute(pickedAttributes.get(pickedAttributes.size() - ConstStrings.ONE)));
        domc = new DataObjectMultipleComparator(sortByAttributes);
        sortedDataset.sort(domc);
        for (int i = 0; i < sortedDataset.size(); i++) {
            if (prev == null) {
                Arrays.fill(decisionsInstances, ConstStrings.ZERO);
                prev = sortedDataset.get(i);
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
                    decisionsInstances[DataAccessor.getDecisionValues().indexOf(sortedDataset.get(i).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())]++;
                } else {
                    int variousClasses = ConstStrings.ZERO;
                    for (int decisionsInstance : decisionsInstances) {
                        if (decisionsInstance != ConstStrings.ZERO) {
                            variousClasses++;
                        }
                    }
                    if (variousClasses != ConstStrings.ONE) {
                        return false;
                    } else {
                        i--;
                        prev = null;
                    }
                }
            }
        }
        int variousClasses = ConstStrings.ZERO;
        for (int decisionsInstance : decisionsInstances) {
            if (decisionsInstance != ConstStrings.ZERO) {
                variousClasses++;
            }
        }
        return variousClasses == ConstStrings.ONE;
    }

}
