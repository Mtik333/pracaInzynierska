/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.graph;

import data.DataAccessor;
import data.roughsets.DataObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Mateusz
 */
public class ChineseAnt extends InterfaceAnt{
    
    private int index; //indeks mrówki
    private int currentIter; //obecny numer kroku w iteracji
    private List<Vertice> pickedAttributes; //wybrane węzły
    private List<Vertice> unpickedAttributes; //pozostałe węzły
    private Map<Vertice, Double> probabilities; //mapa z prawdopodobieństwami
    private List<Edge> allEdges; //lista krawedzi (wszystkich)
    private List<Edge> chosenEdges; //lista krawedzi (wybranych)
    private boolean foundSolution = false; //czy znaleziono rozwiazanie)
    private List<DataObject> sortedDataset;
    
    public ChineseAnt(int index) {
        this.index = index;
        this.pickedAttributes = new ArrayList<>();
        this.unpickedAttributes = new ArrayList<>();
        this.allEdges = DataAccessor.getGraph().getEdges();
        this.chosenEdges = new ArrayList<>();
        this.sortedDataset = new ArrayList<>();
        Collections.copy(DataAccessor.getDataset(), sortedDataset);
    }
    
    public void initLists(List<Vertice> unpicked) {
        this.unpickedAttributes = new ArrayList<>();
        unpicked.forEach((x) -> {
            unpickedAttributes.add(x);
        });
        this.pickedAttributes = new ArrayList<>();
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    public void addEdgeToSolution() {
        allEdges.stream().filter((x) -> (x.getStart().getName().equals(pickedAttributes.get(currentIter - 1).getName()) || x.getEnd().getName().equals(pickedAttributes.get(currentIter - 1).getName()))).filter((x) -> (x.getStart().getName().equals(pickedAttributes.get(currentIter).getName()) || x.getEnd().getName().equals(pickedAttributes.get(currentIter).getName()))).forEachOrdered((x) -> {
            chosenEdges.add(x);
        });
    }

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

    @Override
    public String[][] getDiscMatrix() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDiscMatrix(String[][] matrix) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean reduceMatrix() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean empty_matrix(String[][] matrix) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
