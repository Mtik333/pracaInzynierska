/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import data.graph.Ant;
import data.graph.ChineseAnt;
import data.graph.Edge;
import data.graph.Graph;
import data.graph.Vertice;
import data.roughsets.Attribute;
import data.roughsets.DataObject;
import data.roughsets.DataObjectComparator;
import data.roughsets.DataObjectMultipleComparator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Mateusz
 */
public class ChineseLogic extends Logic {

    @Override
    public void generateGraph() {
        long startTime = new Date().getTime();
        coreCT2();
        long stopTime = new Date().getTime();
        DataAccessor.setElapsedTime(DataAccessor.getElapsedTime() + (((double) (stopTime - startTime)) / ConstStrings.THOUSAND));
        List<Attribute> test = DataAccessor.getAllAttributes();
        calculateMutualInformation();
        featureCore();
        List<Vertice> vertices = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < DataAccessor.getAllAttributes().size() - 1; i++) {
            if (!DataAccessor.getCoreAttributes().contains(DataAccessor.getAllAttributes().get(i))) {
                vertices.add(new Vertice(DataAccessor.getAllAttributes().get(i).getName(), i));
            }
        }
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = i + 1; j < vertices.size(); j++) {
                edges.add(new Edge(vertices.get(i), vertices.get(j))); //graf pelny, wiec krawdzie miedzy kazdymi wierzcholkami
            }
        }
        DataAccessor.setGraph(new Graph(vertices, edges));
        DataAccessor.setAntsNumber(vertices.size() / ConstStrings.TWO);
        DataAccessor.setMaxList(vertices.size());
    }

    @Override
    public void generateAntsPheromone() {
        if (DataAccessor.getCurrentReduct() == null) {
            generateBasicPheromone();
        }
        List<Ant> newAnts = new ArrayList<>();
        for (int i = 0; i < DataAccessor.getAntsNumber(); i++) {
            ChineseAnt ant = new ChineseAnt(i);
            ant.initLists(DataAccessor.getGraph().getVertices());
            newAnts.add(ant);
        }
        DataAccessor.setAllAnts(newAnts);
    }

    //zlicza ilosc konfliktow (w klasie)
    public int countConflictsRow(int[] ctRow) {
        double initialValue = Math.pow(Arrays.stream(ctRow).sum(), ConstStrings.TWO);
        for (int value : ctRow) {
            initialValue = initialValue - Math.pow(value, ConstStrings.TWO);
        }
        initialValue = initialValue * ConstStrings.HALF;
        return (int) initialValue;
    }

    //zlicza ilosc konfliktow ogolem
    public int countConflictsTotal(int[] ctTotal) {
        double finalValue = Math.pow(Arrays.stream(ctTotal).sum(), ConstStrings.TWO);
        for (int value : ctTotal) {
            finalValue = finalValue - Math.pow(value, ConstStrings.TWO);
        }
        finalValue = finalValue * ConstStrings.HALF;
        return (int) finalValue;
    }

    //caly algorytm CT
    public void coreCT2() {
        List<Attribute> foundCore = new ArrayList<>();
        int decisionClasses = countDecisionClasses();
        for (int i = 0; i < DataAccessor.getAllAttributes().size() - 1; i++) {
            int[] ctRow = new int[decisionClasses];
            int[] ctTotal = new int[decisionClasses];
            int confs = ConstStrings.ZERO;
            DataObject prev = null;
            boolean difference = false;
            Collections.sort(DataAccessor.getDataset(), new DataObjectComparator(i));
            for (int k = 0; k < DataAccessor.getDataset().size(); k++) {
                for (int j = 0; j < DataAccessor.getDataset().get(0).getAttributes().size() - 1; j++) {
                    if (j != i) {
                        if (prev != null) {
                            if (!prev.getAttributes().get(j).getValue().equals(DataAccessor.getDataset().get(k).getAttributes().get(j).getValue())) {
                                difference = true;
                                break;
                            }
                        }
                    }
                }
                if (difference == false) {
                    ctTotal[DataAccessor.getDecisionValues().indexOf(DataAccessor.getDataset().get(k).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())]++;
                    ctRow[DataAccessor.getDecisionValues().indexOf(DataAccessor.getDataset().get(k).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())]++;
                    prev = DataAccessor.getDataset().get(k);
                } else {
                    int confsRow = countConflictsRow(ctRow);
                    confs = confs + confsRow;
                    Arrays.fill(ctRow, ConstStrings.ZERO);
                    difference = false;
                    prev = null;
                    k--;
                }
            }
            int discdA = countConflictsTotal(ctTotal);
            int discdAminus = discdA - confs;
            if (discdAminus < discdA) {
                foundCore.add(DataAccessor.getAllAttributes().get(i));
            }
        }
        foundCore.forEach((a) -> {
        });
        if (foundCore.size() == DataAccessor.getAllAttributes().size() - ConstStrings.ONE) {
            DataAccessor.setCoreAttributes(new ArrayList<>());
        } else {
            DataAccessor.setCoreAttributes(foundCore);
        }
        Collections.sort(DataAccessor.getDataset());
    }

    //FUNKCJE DO LICZENIA ENTROPII
    private void calculateMutualInformation() {
        DataAccessor.setDatasetMutualInformation(informationEntropyD() - conditionalEntropyC());
    }

    private double informationEntropyD() {
        double value = ConstStrings.ZERO;
        for (String decision : DataAccessor.getDecisionValues()) {
            int instances = ConstStrings.ZERO;
            instances = DataAccessor.getDataset().stream().filter((dataObject) -> (dataObject.getAttributes().get(DataAccessor.getDecisionMaker()).getValue().equals(decision))).map((_item) -> ConstStrings.ONE).reduce(instances, Integer::sum);
            double probability = ((double) instances) / ((double) DataAccessor.getDataset().size());
            double logarithm = (Math.log(probability) / Math.log(ConstStrings.TWO));
            value += (ConstStrings.MINUS_ONE) * probability * logarithm;
        }
        DataAccessor.setDecisionEntropy(value);
        return value;
    }

    private double conditionalEntropyC() {
        double finalValue = ConstStrings.ZERO;
        double singleAttrValue = ConstStrings.ZERO;
        int numberOfClassInstances = ConstStrings.ZERO;
        int[] decisionsInstances = new int[DataAccessor.getDecisionValues().size()];
        DataObjectMultipleComparator domc = new DataObjectMultipleComparator(DataAccessor.getAllAttributes());
        Collections.sort(DataAccessor.getDataset(), domc);
        DataObject prev = null;
        for (int i = 0; i < DataAccessor.getDataset().size(); i++) {
            if (prev == null) {
                Arrays.fill(decisionsInstances, ConstStrings.ZERO);
                prev = DataAccessor.getDataset().get(i);
                numberOfClassInstances++;
                decisionsInstances[DataAccessor.getDecisionValues().indexOf(DataAccessor.getDataset().get(i).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())]++;
            } else {
                boolean theSame = true;
                for (int j = 0; j < prev.getAttributes().size() - 1; j++) {
                    if (!prev.getAttributes().get(j).getValue().equals(DataAccessor.getDataset().get(i).getAttributes().get(j).getValue())) {
                        theSame = false;
                        break;
                    }
                }
                if (theSame) {
                    numberOfClassInstances++;
                    decisionsInstances[DataAccessor.getDecisionValues().indexOf(DataAccessor.getDataset().get(i).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())]++;
                } else {
                    singleAttrValue = directConditionalEntropyCalc(decisionsInstances, numberOfClassInstances);
                    singleAttrValue *= (ConstStrings.MINUS_ONE) * ((double) numberOfClassInstances) / ((double) DataAccessor.getDataset().size());
                    numberOfClassInstances = ConstStrings.ZERO;
                    finalValue += singleAttrValue;
                    singleAttrValue = ConstStrings.ZERO;
                    i--;
                    theSame = true;
                    prev = null;
                }
            }
        }
        //obliczanie ostatniego zbioru
        if (numberOfClassInstances > ConstStrings.ONE) {
            singleAttrValue = directConditionalEntropyCalc(decisionsInstances, numberOfClassInstances);
            finalValue += singleAttrValue;
            prev = null;
        }
        return finalValue;
    }

    private double directConditionalEntropyCalc(int[] decisionsInstances, int numberOfClassInstances) {
        double singleAttrValue = ConstStrings.ZERO;
        for (int k = 0; k < decisionsInstances.length; k++) {
            double probability = ((double) decisionsInstances[k]) / ((double) numberOfClassInstances);
            if (probability == ConstStrings.ZERO) {
                singleAttrValue += ConstStrings.ZERO;
            } else {
                double logarithm = (Math.log(probability) / Math.log(ConstStrings.TWO));
                singleAttrValue = singleAttrValue + (probability * logarithm);
            }
        }
        return singleAttrValue;
    }

    public static boolean checkIfCoreIsReduct() {
        if (DataAccessor.getCoreAttributes().isEmpty()) {
            return false;
        }
        int numberOfClassInstances = ConstStrings.ZERO;
        int[] decisionsInstances = new int[DataAccessor.getDecisionValues().size()];
        DataObject prev = null;
        DataObjectMultipleComparator domc = new DataObjectMultipleComparator(DataAccessor.getCoreAttributes());
        Collections.sort(DataAccessor.getDataset(), domc);
        for (int i = 0; i < DataAccessor.getDataset().size(); i++) {
            if (prev == null) {
                Arrays.fill(decisionsInstances, ConstStrings.ZERO);
                prev = DataAccessor.getDataset().get(i);
                numberOfClassInstances++;
                decisionsInstances[DataAccessor.getDecisionValues().indexOf(DataAccessor.getDataset().get(i).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())]++;
            } else {
                boolean theSame = true;
                for (int j = 0; j < domc.getSortingBy().size(); j++) {
                    if (!prev.getAttributes().get(domc.getSortingBy().get(j)).getValue().equals(DataAccessor.getDataset().get(i).getAttributes().get(domc.getSortingBy().get(j)).getValue())) {
                        theSame = false;
                        break;
                    }
                }
                if (theSame) {
                    numberOfClassInstances++;
                    decisionsInstances[DataAccessor.getDecisionValues().indexOf(DataAccessor.getDataset().get(i).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())]++;
                } else {
                    int variousClasses = ConstStrings.ZERO;
                    for (int j = 0; j < decisionsInstances.length; j++) {
                        if (decisionsInstances[j] != ConstStrings.ZERO) {
                            variousClasses++;
                        }
                    }
                    if (variousClasses != ConstStrings.ONE) {
                        return false;
                    } else {
                        numberOfClassInstances = ConstStrings.ZERO;
                        i--;
                        theSame = true;
                        prev = null;
                    }
                }
            }
        }
        int variousClasses = ConstStrings.ZERO;
        for (int j = 0; j < decisionsInstances.length; j++) {
            if (decisionsInstances[j] != ConstStrings.ZERO) {
                variousClasses++;
            }
        }
        if (variousClasses != ConstStrings.ONE) {
            return false;
        }
        Collections.sort(DataAccessor.getDataset());
        DataAccessor.setCalculatedReductInIteration(true);
        return true;
    }

    public void featureCore(){
        List<Attribute> chineseCore = new ArrayList<>();
        double test2 = DataAccessor.getDecisionEntropy();
        double test = DataAccessor.getDatasetMutualInformation();
        for (int i=0; i<DataAccessor.getAllAttributes().size(); i++){
            if (mutualInformationWithoutAttr(DataAccessor.getAllAttributes().get(i), i)<test){
                chineseCore.add(DataAccessor.getAllAttributes().get(i));
            }
        }
        if (chineseCore.size() == DataAccessor.getAllAttributes().size() - ConstStrings.ONE) {
            DataAccessor.setCoreAttributes(new ArrayList<>());
        } else {
            DataAccessor.setCoreAttributes(chineseCore);
        }
        Collections.sort(DataAccessor.getDataset());
        int test5=5;
    }
    
    public double mutualInformationWithoutAttr(Attribute attribute, int i){
        double mutualInformation = DataAccessor.getDecisionEntropy()-conditionalEntropyCWithoutAttribute(attribute, i);
        return mutualInformation;
    }
    
    private double conditionalEntropyCWithoutAttribute(Attribute attribute, int index) {
        double finalValue = ConstStrings.ZERO;
        double singleAttrValue = ConstStrings.ZERO;
        int numberOfClassInstances = ConstStrings.ZERO;
        int[] decisionsInstances = new int[DataAccessor.getDecisionValues().size()];
        DataObject prev = null;
        Collections.sort(DataAccessor.getDataset(), new DataObjectComparator(index));
        for (int i = 0; i < DataAccessor.getDataset().size(); i++) {
            if (prev == null) {
                Arrays.fill(decisionsInstances, ConstStrings.ZERO);
                prev = DataAccessor.getDataset().get(i);
                numberOfClassInstances++;
                decisionsInstances[DataAccessor.getDecisionValues().indexOf(DataAccessor.getDataset().get(i).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())]++;
            } else {
                boolean theSame = true;
                for (int j = 0; j < prev.getAttributes().size() - 1; j++) {
                    if (!prev.getAttributes().get(j).getName().equals(attribute.getName())){
                        if (!prev.getAttributes().get(j).getValue().equals(DataAccessor.getDataset().get(i).getAttributes().get(j).getValue())) {
                            theSame = false;
                            break;
                        }
                    }
                }
                if (theSame) {
                    numberOfClassInstances++;
                    decisionsInstances[DataAccessor.getDecisionValues().indexOf(DataAccessor.getDataset().get(i).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())]++;
                } else {
                    singleAttrValue = directConditionalEntropyCalc(decisionsInstances, numberOfClassInstances);
                    singleAttrValue *= (ConstStrings.MINUS_ONE) * ((double) numberOfClassInstances) / ((double) DataAccessor.getDataset().size());
                    numberOfClassInstances = ConstStrings.ZERO;
                    finalValue += singleAttrValue;
                    singleAttrValue = ConstStrings.ZERO;
                    i--;
                    theSame = true;
                    prev = null;
                }
            }
        }
        //obliczanie ostatniego zbioru
        if (numberOfClassInstances > ConstStrings.ONE) {
            singleAttrValue = directConditionalEntropyCalc(decisionsInstances, numberOfClassInstances);
            finalValue += singleAttrValue;
            prev = null;
        }
        if (finalValue<0)
            finalValue*=-1;
        return finalValue;
    }
    
}
