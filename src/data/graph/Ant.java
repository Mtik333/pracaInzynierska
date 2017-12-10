/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.graph;

import data.ConstStrings;
import data.roughsets.Attribute;
import data.roughsets.DataObject;
import data.roughsets.DataObjectMultipleComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Mateusz
 */
public abstract class Ant implements Runnable {

    int index; //indeks mrówki
    int currentIter; //obecny numer kroku w iteracji
    List<Vertice> pickedAttributes; //wybrane węzły
    List<Vertice> unpickedAttributes; //pozostałe węzły
    Map<Vertice, Double> probabilities; //mapa z prawdopodobieństwami
    List<Edge> allEdges; //lista krawedzi (wszystkich)
    List<Edge> chosenEdges; //lista krawedzi (wybranych)
    boolean foundSolution = false; //czy znaleziono rozwiazanie)
    List<Attribute> sortByAttributes; //posortowane atrybuty
    DataObjectMultipleComparator domc; //komparator do zbioru
    List<DataObject> sortedDataset; //dane posortowane

    public int getIndex() {
        return index;
    }

    public List<Vertice> getPickedAttributes() {
        return pickedAttributes;
    }

    public List<Vertice> getUnpickedAttributes() {
        return unpickedAttributes;
    }

    public boolean isFoundSolution() {
        return foundSolution;
    }

    public void setFoundSolution(boolean foundSolution) {
        this.foundSolution = foundSolution;
    }

    public void pickVertice(Vertice v) {
        if (v==null){
            System.out.println("a");
        }
        pickedAttributes.add(v);
        unpickedAttributes.remove(v);
    }

    public void initLists(List<Vertice> unpicked) {
        this.unpickedAttributes = new ArrayList<>();
        unpickedAttributes.addAll(unpicked);
        this.pickedAttributes = new ArrayList<>();
    }

    void addEdgeToSolution() {
        allEdges.stream().filter((x) -> (x.getStart().getName().equals(pickedAttributes.get(currentIter - ConstStrings.ONE).getName()) || x.getEnd().getName().equals(pickedAttributes.get(currentIter - ConstStrings.ONE).getName()))).filter((x) -> (x.getStart().getName().equals(pickedAttributes.get(currentIter).getName()) || x.getEnd().getName().equals(pickedAttributes.get(currentIter).getName()))).forEachOrdered((x) -> chosenEdges.add(x));
    }

    Vertice pickVerticeByProbability() {
        double p = Math.random();
        double cumulativeProbability = ConstStrings.DOUBLE_ZERO;
        for (Map.Entry<Vertice, Double> x : probabilities.entrySet()) {
            cumulativeProbability += x.getValue();
            if (p <= cumulativeProbability) {
                unpickedAttributes.remove(x.getKey());
                return x.getKey();
            }
        }
        return null;
    }

    public List<Edge> getChosenEdges() {
        return chosenEdges;
    }

}
