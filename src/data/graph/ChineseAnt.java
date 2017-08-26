/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.graph;

import static data.ConstStrings.SINGLE_STEP;
import data.DataAccessor;
import data.roughsets.Attribute;
import data.roughsets.DataObject;
import data.roughsets.DataObjectMultipleComparator;
import java.util.ArrayList;
import java.util.Arrays;
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
    private List<DataObject> sortedDataset; //dane posortowane
    private Map<Vertice,Double> heuristicValues; //informacja heurystyczna
    private List<Attribute> sortByAttributes; //posortowane atrybuty
    private DataObjectMultipleComparator domc; //komparator do zbioru
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
    public void initLists(List<Vertice> unpicked) {
        this.unpickedAttributes = new ArrayList<>();
        unpicked.forEach((x) -> {
            unpickedAttributes.add(x);
        });
        this.pickedAttributes = new ArrayList<>();
    }

    @Override
    public void run() {
        currentIter = 0;
        reducedDataset = calculateMutualInformation();
        while(currentIter < DataAccessor.getMaxList() - 1 && DataAccessor.getDatasetMutualInformation()!=reducedDataset){
            for (Vertice vertice : unpickedAttributes){
                computeHeuristic(vertice, pickedAttributes.get(pickedAttributes.size()-1));
            }
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
                if (DataAccessor.getDatasetMutualInformation()-reducedDataset==0)
                    foundSolution = true;
                else foundSolution = false;
                return;
            }
        }
        if (DataAccessor.getDatasetMutualInformation()-reducedDataset==0)
            foundSolution = true;
        else foundSolution = false;
    }
    
    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public List<Vertice> getPickedAttributes() {
        return pickedAttributes;
    }

    @Override
    public void setPickedAttributes(List<Vertice> pickedAttributes) {
        this.pickedAttributes = pickedAttributes;
    }

    @Override
    public List<Vertice> getUnpickedAttributes() {
        return unpickedAttributes;
    }

    @Override
    public void setUnpickedAttributes(List<Vertice> unpickedAttributes) {
        this.unpickedAttributes = unpickedAttributes;
    }

    @Override
    public Map<Vertice, Double> getProbabilities() {
        return probabilities;
    }

    @Override
    public void setProbabilities(Map<Vertice, Double> probabilities) {
        this.probabilities = probabilities;
    }

    @Override
    public boolean isFoundSolution() {
        return foundSolution;
    }

    @Override
    public void setFoundSolution(boolean foundSolution) {
        this.foundSolution = foundSolution;
    }

    @Override
    public void pickVertice(Vertice v) {
        pickedAttributes.add(v);
        unpickedAttributes.remove(v);
    }

    @Override
    public void addEdgeToSolution() {
        allEdges.stream().filter((x) -> (x.getStart().getName().equals(pickedAttributes.get(currentIter - 1).getName()) || x.getEnd().getName().equals(pickedAttributes.get(currentIter - 1).getName()))).filter((x) -> (x.getStart().getName().equals(pickedAttributes.get(currentIter).getName()) || x.getEnd().getName().equals(pickedAttributes.get(currentIter).getName()))).forEachOrdered((x) -> {
            chosenEdges.add(x);
        });
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
                        double test1=Math.pow(x.getPheromone(), DataAccessor.getPheromoneRelevance());
                        double test2=Math.pow(findHeuristicValue(v), DataAccessor.getEdgeRelevance());
                        double addedValue = test1*test2;
                        sumPheromone += test1*test2;
                        //sumPheromone += Math.pow(x.getPheromone(), DataAccessor.getPheromoneRelevance()) * Math.pow(findHeuristicValue(v), DataAccessor.getEdgeRelevance());
                        probabilities.put(v, addedValue);
                    }
                }
            }
        }
        return sumPheromone;
    }

    private double findHeuristicValue(Vertice v){
        for (Map.Entry<Vertice, Double> entry : heuristicValues.entrySet()){
            if (entry.getKey().getIndex()==v.getIndex())
                if (entry.getValue()<0.001){
                    return 0.001;
                }
                else return entry.getValue();
        }
        return 0;
    }
    
    @Override
    public Vertice pickVerticeByProbability() {
        double p = Math.random();
        double cumulativeProbability = 0;
        for (Map.Entry<Vertice, Double> x : probabilities.entrySet()) {
            cumulativeProbability += x.getValue();
            if (p <= cumulativeProbability) {
                unpickedAttributes.remove(x.getKey());
                return x.getKey();
            }
        }
        return null;
    }

    @Override
    public List<Edge> getAllEdges() {
        return allEdges;
    }

    @Override
    public void setAllEdges(List<Edge> allEdges) {
        this.allEdges = allEdges;
    }

    @Override
    public List<Edge> getChosenEdges() {
        return chosenEdges;
    }

    @Override
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
    
    private double calculateMutualInformation(){
        List<DataObject> originalSort = DataAccessor.getDataset();
        sortByAttributes.add(DataAccessor.verticeToAttribute(pickedAttributes.get(pickedAttributes.size()-1)));
        domc = new DataObjectMultipleComparator(sortByAttributes);
        Collections.sort(sortedDataset, domc);
        double mutualInformation=DataAccessor.getDecisionEntropy()-conditionalEntropyC();
        return mutualInformation;
    }
    
    private double conditionalEntropyC(){
        double finalValue=0;
        double singleAttrValue=0;
        int numberOfClassInstances=0;
        int[] decisionsInstances=new int[DataAccessor.getDecisionValues().size()];
        DataObject prev=null;
        for (int i=0; i<sortedDataset.size(); i++){
            if (prev==null){
                Arrays.fill(decisionsInstances, 0);
                prev=sortedDataset.get(i);
                numberOfClassInstances++;
                decisionsInstances[DataAccessor.getDecisionValues().indexOf(sortedDataset.get(i).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())]++;
            }
            else{
                boolean theSame=true;
                for (int j=0; j<domc.getSortingBy().size(); j++){
                    if (!prev.getAttributes().get(domc.getSortingBy().get(j)).getValue().equals
        (sortedDataset.get(i).getAttributes().get(domc.getSortingBy().get(j)).getValue())){
                        theSame=false;
                        break;
                    }
                }
                if (theSame){
                    numberOfClassInstances++;
                    decisionsInstances[DataAccessor.getDecisionValues().indexOf(sortedDataset.get(i).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())]++;
                }
                else{
                    singleAttrValue=directConditionalEntropyCalc(decisionsInstances, numberOfClassInstances);
                    singleAttrValue*=(-1)*((double)numberOfClassInstances)/((double)sortedDataset.size());
                    numberOfClassInstances=0;
                    finalValue+=singleAttrValue;
                    singleAttrValue=0;
                    i--;
                    theSame=true;
                    prev=null;
                }
            }
        }
        //obliczanie ostatniego zbioru
        if (numberOfClassInstances>1){
            singleAttrValue=directConditionalEntropyCalc(decisionsInstances, numberOfClassInstances);
            finalValue+=singleAttrValue;
            prev=null;
        }
        return finalValue;
    }
    
    private double directConditionalEntropyCalc(int[] decisionsInstances, int numberOfClassInstances){
        double singleAttrValue=0; 
        for (int k=0; k<decisionsInstances.length; k++){
                        double probability = ((double)decisionsInstances[k])/((double)numberOfClassInstances);
                        if (probability==0){
                            singleAttrValue+=0;
                        }
                        else {
                            double logarithm = (Math.log(probability)/Math.log(2));
                            singleAttrValue=singleAttrValue+(probability*logarithm);
                        }
                    }
        return singleAttrValue;
    }
    
    private void computeHeuristic(Vertice sk, Vertice ak){
        domc.sortingBy.add(sk.getIndex());
        Collections.sort(sortedDataset, domc);
        heuristicValues.put(sk, computeSignificanceFormula15());
        domc.sortingBy.remove((Integer)sk.getIndex());
    }
    
    private double computeSignificanceFormula15(){
        double test = (DataAccessor.getDecisionEntropy()-conditionalEntropyC())-reducedDataset;
        return test;
    }
}
