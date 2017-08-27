/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.graph;

import static data.ConstStrings.*;
import data.DataAccessor;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Mateusz
 */
public class NewAnt extends Ant {

    private String[][] discMatrix; //macierz rozroznialnosci (prywatna)

    public NewAnt(int index) {
        this.index = index;
        this.pickedAttributes = new ArrayList<>();
        this.unpickedAttributes = new ArrayList<>();
        this.discMatrix = DataAccessor.getIndiscMatrix();
        this.allEdges = DataAccessor.getGraph().getEdges();
        this.chosenEdges = new ArrayList<>();
    }

    @Override
    public void run() {
        currentIter = 0;
        boolean reducedMatrix = reduceMatrix();
        while (currentIter < DataAccessor.getMaxList() - 1 && !reducedMatrix) {
            double pheromoneSum = calculateSum();
            for (int i = 0; i < probabilities.size(); i++) {
                probabilities.computeIfPresent(unpickedAttributes.get(i), (t, u) -> {
                    return u / pheromoneSum; //To change body of generated lambdas, choose Tools | Templates.
                });
            }
            //System.out.println(probabilities.values().stream().mapToDouble(Number::doubleValue).sum());
            pickedAttributes.add(pickVerticeByProbability());
            reducedMatrix = reduceMatrix();
            currentIter++;
            addEdgeToSolution();
            if (DataAccessor.getCalculationMode().equals(SINGLE_STEP)) {
                foundSolution = empty_matrix(discMatrix);
                return;
            }
        }
        foundSolution = empty_matrix(discMatrix);
    }

    public String[][] getDiscMatrix() {
        return discMatrix;
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

    public void setDiscMatrix(String[][] matrix) {
        this.discMatrix = new String[matrix.length][matrix.length];
        StringBuilder myString = new StringBuilder();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] == null) {
                    discMatrix[i][j] = null;
                } else {
                    myString.append(matrix[i][j]);
                    discMatrix[i][j] = myString.toString();
                    myString.setLength(0);
                }
            }
        }
    }

    public boolean reduceMatrix() {
        StringBuilder stringCompare = new StringBuilder();
        for (int i = 0; i < discMatrix.length; i++) {
            for (int j = i + 1; j < discMatrix.length; j++) {
                if (discMatrix[i][j] != null) {
                    stringCompare.append(",").append(pickedAttributes.get(pickedAttributes.size() - 1).getIndex()).append(",");
                    if (discMatrix[i][j].contains(stringCompare.toString())) {
                        discMatrix[i][j] = null;
                    }
                    stringCompare.setLength(0);
                }
            }
        }
        return empty_matrix(discMatrix);
    }

    public boolean empty_matrix(String matrix[][]) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i; j < matrix.length; j++) {
                if (matrix[i][j] != null) {
                    return false;
                }
            }
        }
        return true;
    }

}
