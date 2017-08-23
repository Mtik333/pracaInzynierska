/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.graph;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Mateusz
 */
public abstract class InterfaceAnt implements Runnable{
    
    public abstract int getIndex();
    public abstract void setIndex(int index);
    public abstract List<Vertice> getPickedAttributes();
    public abstract void setPickedAttributes(List<Vertice> pickedAttributes);
    public abstract List<Vertice> getUnpickedAttributes();
    public abstract void setUnpickedAttributes(List<Vertice> unpickedAttributes);
    public abstract Map<Vertice, Double> getProbabilities();
    public abstract void setProbabilities(Map<Vertice, Double> probabilities);
    public abstract String[][] getDiscMatrix();
    public abstract boolean isFoundSolution();
    public abstract void setFoundSolution(boolean foundSolution);
    public abstract void pickVertice(Vertice v);
    public abstract void initLists(List<Vertice> unpicked);
    public abstract void addEdgeToSolution();
    public abstract double calculateSum();
    public abstract void setDiscMatrix(String[][] matrix);
    public abstract Vertice pickVerticeByProbability();
    public abstract boolean reduceMatrix();
    public abstract boolean empty_matrix(String matrix[][]);
    public abstract List<Edge> getAllEdges();
    public abstract void setAllEdges(List<Edge> allEdges);
    public abstract List<Edge> getChosenEdges();
    public abstract void setChosenEdges(List<Edge> chosenEdges);
    
}
