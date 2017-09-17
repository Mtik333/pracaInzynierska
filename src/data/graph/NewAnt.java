/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.graph;

import static data.ConstStrings.*;
import data.DataAccessor;
import data.roughsets.DataObject;
import data.roughsets.DataObjectMultipleComparator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 *
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
        currentIter = 0;
        boolean reducedMatrix = checkIfReduct();
        while (currentIter < DataAccessor.getMaxList() - 1 && !reducedMatrix) {
            double pheromoneSum = calculateSum();
            for (int i = 0; i < probabilities.size(); i++) {
                probabilities.computeIfPresent(unpickedAttributes.get(i), (t, u) -> {
                    return u / pheromoneSum; //To change body of generated lambdas, choose Tools | Templates.
                });
            }
            //System.out.println(probabilities.values().stream().mapToDouble(Number::doubleValue).sum());
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

    @Override
    public double calculateSum() {
        double sumPheromone = 0;
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

    public boolean checkIfReduct() {
        int numberOfClassInstances = 0;
        int[] decisionsInstances = new int[DataAccessor.getDecisionValues().size()];
        DataObject prev = null;
        sortByAttributes.add(DataAccessor.verticeToAttribute(pickedAttributes.get(pickedAttributes.size() - 1)));
        domc = new DataObjectMultipleComparator(sortByAttributes);
        Collections.sort(sortedDataset, domc);
        for (int i = 0; i < sortedDataset.size(); i++) {
            if (prev == null) {
                Arrays.fill(decisionsInstances, 0);
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
                    int variousClasses = 0;
                    for (int j = 0; j < decisionsInstances.length; j++) {
                        if (decisionsInstances[j] != 0) {
                            variousClasses++;
                        }
                    }
                    if (variousClasses != 1) {
                        return false;
                    } else {
                        numberOfClassInstances = 0;
                        i--;
                        theSame = true;
                        prev = null;
                    }
                }
            }
        }
        int variousClasses = 0;
        for (int j = 0; j < decisionsInstances.length; j++) {
            if (decisionsInstances[j] != 0) {
                variousClasses++;
            }
        }
        return variousClasses == 1;
    }

}
