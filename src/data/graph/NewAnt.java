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
import java.util.List;
import java.util.Map;

/**
 *
 * @author Mateusz
 */
public class NewAnt implements Runnable{
    private int index;
    private int currentIter;
    private List<Vertice> pickedAttributes; //wybrane węzły
    private List<Vertice> unpickedAttributes; //pozostałe węzły
    private Map<Vertice, Double> probabilities; //mapa z prawdopodobieństwami
    private List<Edge> allEdges;
    private List<Edge> chosenEdges;
    private String[][] discMatrix;
    private boolean foundSolution=false;
    
    public NewAnt(int index) {
        this.index = index;
        this.pickedAttributes = new ArrayList<Vertice>();
        this.unpickedAttributes = new ArrayList<Vertice>();
        this.discMatrix = DataAccessor.getIndiscMatrix();
        this.allEdges = DataAccessor.getGraph().getEdges();
        this.chosenEdges = new ArrayList<>();
    }
    
    @Override
    public void run() {
        currentIter = 0;
        boolean reducedMatrix=reduceMatrix();
        while (currentIter<DataAccessor.getMaxList()-1 && !reducedMatrix){
            double pheromoneSum = calculateSum();
            for (int i = 0; i < probabilities.size(); i++) {
                probabilities.computeIfPresent(unpickedAttributes.get(i), (t, u) -> {
                    return u / pheromoneSum; //To change body of generated lambdas, choose Tools | Templates.
                });
            }
            //System.out.println(probabilities.values().stream().mapToDouble(Number::doubleValue).sum());
            pickedAttributes.add(pickVerticeByProbability());
            reducedMatrix=reduceMatrix();
            currentIter++;
            addEdgeToSolution();
            if (DataAccessor.getCalculationMode().equals(SINGLE_STEP)){
                return;
            }
        }
        if (empty_matrix(discMatrix))
            foundSolution=true;
        else foundSolution=false;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<Vertice> getPickedAttributes() {
        return pickedAttributes;
    }

    public void setPickedAttributes(List<Vertice> pickedAttributes) {
        this.pickedAttributes = pickedAttributes;
    }

    public List<Vertice> getUnpickedAttributes() {
        return unpickedAttributes;
    }

    public void setUnpickedAttributes(List<Vertice> unpickedAttributes) {
        this.unpickedAttributes = unpickedAttributes;
    }

    public Map<Vertice, Double> getProbabilities() {
        return probabilities;
    }

    public void setProbabilities(Map<Vertice, Double> probabilities) {
        this.probabilities = probabilities;
    }

    public String[][] getDiscMatrix() {
        return discMatrix;
    }

    public boolean isFoundSolution() {
        return foundSolution;
    }

    public void setFoundSolution(boolean foundSolution) {
        this.foundSolution = foundSolution;
    }
    
    public void pickVertice(Vertice v) {
        pickedAttributes.add(v);
        unpickedAttributes.remove(v);
    }
    public void initLists(List<Vertice> unpicked) {
        this.unpickedAttributes = new ArrayList<Vertice>();
        for (Vertice x : unpicked) {
            unpickedAttributes.add(x);
        }
        this.pickedAttributes = new ArrayList<Vertice>();
    }
    
    private void addEdgeToSolution(){
        for (Edge x : allEdges){
            if (x.getStart().getName().equals(pickedAttributes.get(currentIter-1).getName()) || x.getEnd().getName().equals(pickedAttributes.get(currentIter-1).getName())){
                if (x.getStart().getName().equals(pickedAttributes.get(currentIter).getName()) || x.getEnd().getName().equals(pickedAttributes.get(currentIter).getName())){
                    chosenEdges.add(x);
                }
            }
        }
    }
    
    private double calculateSum() {
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
                        sumPheromone += Math.pow(x.getPheromone(), DataAccessor.getPheromoneRelevance())*Math.pow(x.getWeight(),DataAccessor.getEdgeRelevance());
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
    
    public Vertice pickVerticeByProbability() {
        double p = Math.random();
        double cumulativeProbability = 0.0;
        for (Map.Entry<Vertice, Double> x : probabilities.entrySet()) {
            cumulativeProbability += x.getValue();
            if (p <= cumulativeProbability) {
                unpickedAttributes.remove(x.getKey());
                return x.getKey();
            }
        }
        return null;
    }
    
    public boolean reduceMatrix() {
        StringBuilder stringCompare = new StringBuilder();
        for (int i = 0; i < discMatrix.length; i++) {
            for (int j = i+1; j < discMatrix.length; j++) {
                if (discMatrix[i][j]!=null){
                    stringCompare.append(",").append(pickedAttributes.get(pickedAttributes.size()-1).getIndex()).append(",");
                    if (discMatrix[i][j].contains(stringCompare.toString()))
                        discMatrix[i][j]=null;
                    stringCompare.setLength(0);
                }
            }
        }
        if (empty_matrix(discMatrix))
            return true;
        else return false;
    }
    
    public boolean empty_matrix(String matrix[][]) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i; j < matrix.length; j++) {
                if (matrix[i][j]!=null) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<Edge> getAllEdges() {
        return allEdges;
    }

    public void setAllEdges(List<Edge> allEdges) {
        this.allEdges = allEdges;
    }

    public List<Edge> getChosenEdges() {
        return chosenEdges;
    }

    public void setChosenEdges(List<Edge> chosenEdges) {
        this.chosenEdges = chosenEdges;
    }
}
