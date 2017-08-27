/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Mateusz
 */
public abstract class InterfaceAnt implements Runnable {

    protected int index; //indeks mrówki
    protected int currentIter; //obecny numer kroku w iteracji
    protected List<Vertice> pickedAttributes; //wybrane węzły
    protected List<Vertice> unpickedAttributes; //pozostałe węzły
    protected Map<Vertice, Double> probabilities; //mapa z prawdopodobieństwami
    protected List<Edge> allEdges; //lista krawedzi (wszystkich)
    protected List<Edge> chosenEdges; //lista krawedzi (wybranych)
    protected boolean foundSolution = false; //czy znaleziono rozwiazanie)

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

    public void initLists(List<Vertice> unpicked) {
        this.unpickedAttributes = new ArrayList<>();
        unpicked.forEach((x) -> {
            unpickedAttributes.add(x);
        });
        this.pickedAttributes = new ArrayList<>();
    }

    public void addEdgeToSolution() {
        allEdges.stream().filter((x) -> (x.getStart().getName().equals(pickedAttributes.get(currentIter - 1).getName()) || x.getEnd().getName().equals(pickedAttributes.get(currentIter - 1).getName()))).filter((x) -> (x.getStart().getName().equals(pickedAttributes.get(currentIter).getName()) || x.getEnd().getName().equals(pickedAttributes.get(currentIter).getName()))).forEachOrdered((x) -> {
            chosenEdges.add(x);
        });
    }

    public abstract double calculateSum();

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

}
