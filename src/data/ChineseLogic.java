/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import data.graph.Edge;
import data.graph.Vertice;
import data.roughsets.Attribute;
import data.roughsets.DataObject;
import data.roughsets.DataObjectComparator;
import data.roughsets.DataObjectMultipleComparator;

import java.util.*;

/**
 * @author Mateusz
 */
public class ChineseLogic extends Logic {

    public static boolean checkIfCoreIsReduct() {
        if (DataAccessor.getCoreAttributes().isEmpty()) {
            return false;
        }
        int[] decisionsInstances = new int[DataAccessor.getDecisionValues().size()];
        DataObject prev = null;
        DataObjectMultipleComparator domc = new DataObjectMultipleComparator(DataAccessor.getCoreAttributes());
        DataAccessor.getDataset().sort(domc);
        for (int i = 0; i < DataAccessor.getDataset().size(); i++) {
            if (prev == null) {
                Arrays.fill(decisionsInstances, ConstStrings.ZERO);
                prev = DataAccessor.getDataset().get(i);
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
                    decisionsInstances[DataAccessor.getDecisionValues().indexOf(DataAccessor.getDataset().get(i).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())]++;
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
        if (variousClasses != ConstStrings.ONE) {
            return false;
        }
        Collections.sort(DataAccessor.getDataset());
        DataAccessor.setCalculatedReductInIteration(true);
        return true;
    }

    @Override
    public void generateGraph() {
        long startTime = new Date().getTime();
        calculateMutualInformation();
        featureCore();
        long stopTime = new Date().getTime();
        if (DataAccessor.getElapsedTime()==0)
            DataAccessor.setElapsedTime(DataAccessor.getElapsedTime() + (((double) (stopTime - startTime)) / ConstStrings.THOUSAND));
        else DataAccessor.setElapsedTime((((double) (stopTime - startTime)) / ConstStrings.THOUSAND));
        List<Vertice> vertices = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < DataAccessor.getAllAttributes().size() - 1; i++) {
            if (!DataAccessor.getCoreAttributes().contains(DataAccessor.getAllAttributes().get(i))) {
                vertices.add(new Vertice(DataAccessor.getAllAttributes().get(i).getName(), i));
            }
        }
        generateEdges(vertices, edges);
    }

    //FUNKCJE DO LICZENIA ENTROPII
    private void calculateMutualInformation() {
        countDecisionClasses();
        DataAccessor.setDatasetMutualInformation(informationEntropyD() - universalConditionalEntropyC(null, -1));
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

    private double directConditionalEntropyCalc(int[] decisionsInstances, int numberOfClassInstances) {
        double singleAttrValue = ConstStrings.ZERO;
        for (int decisionsInstance : decisionsInstances) {
            double probability = ((double) decisionsInstance) / ((double) numberOfClassInstances);
            if (probability == ConstStrings.ZERO) {
                singleAttrValue += ConstStrings.ZERO;
            } else {
                double logarithm = (Math.log(probability) / Math.log(ConstStrings.TWO));
                singleAttrValue = singleAttrValue + (probability * logarithm);
            }
        }
        return singleAttrValue;
    }

    private void featureCore() {
        List<Attribute> chineseCore = new ArrayList<>();
        double test = DataAccessor.getDatasetMutualInformation();
        for (int i = 0; i < DataAccessor.getAllAttributes().size(); i++) {
            if (mutualInformationWithoutAttr(DataAccessor.getAllAttributes().get(i), i) < test) {
                chineseCore.add(DataAccessor.getAllAttributes().get(i));
            }
        }
        if (chineseCore.size() == DataAccessor.getAllAttributes().size() - ConstStrings.ONE) {
            DataAccessor.setCoreAttributes(new ArrayList<>());
        } else {
            DataAccessor.setCoreAttributes(chineseCore);
        }
        Collections.sort(DataAccessor.getDataset());
    }

    private double mutualInformationWithoutAttr(Attribute attribute, int i) {
        return DataAccessor.getDecisionEntropy() - universalConditionalEntropyC(attribute, i);
    }

    private boolean isTheSame(DataObject prev, int i) {
        for (int j = 0; j < prev.getAttributes().size() - 1; j++) {
            if (!prev.getAttributes().get(j).getValue().equals(DataAccessor.getDataset().get(i).getAttributes().get(j).getValue())) {
                return false;
            }
        }
        return true;
    }

    private boolean isTheSameWithoutAttribute(Attribute attribute, DataObject prev, int i) {
        for (int j = 0; j < prev.getAttributes().size() - 1; j++) {
            if (!prev.getAttributes().get(j).getName().equals(attribute.getName())) {
                if (!prev.getAttributes().get(j).getValue().equals(DataAccessor.getDataset().get(i).getAttributes().get(j).getValue())) {
                    return false;
                }
            }
        }
        return true;
    }

    private double getFinalValue(double finalValue, int numberOfClassInstances, int[] decisionsInstances) {
        double singleAttrValue;
        if (numberOfClassInstances > ConstStrings.ONE) {
            singleAttrValue = directConditionalEntropyCalc(decisionsInstances, numberOfClassInstances);
            finalValue += singleAttrValue;
        }
        return finalValue;
    }

    private double universalConditionalEntropyC(Attribute attribute, int index) {
        double finalValue = ConstStrings.ZERO;
        double singleAttrValue;
        int numberOfClassInstances = ConstStrings.ZERO;
        int[] decisionsInstances = new int[DataAccessor.getDecisionValues().size()];
        DataObject prev = null;
        if (index == -1)
            DataAccessor.getDataset().sort(new DataObjectMultipleComparator(DataAccessor.getAllAttributes()));
        else DataAccessor.getDataset().sort(new DataObjectComparator(index));
        for (int i = 0; i < DataAccessor.getDataset().size(); i++) {
            if (prev == null) {
                Arrays.fill(decisionsInstances, ConstStrings.ZERO);
                prev = DataAccessor.getDataset().get(i);
                numberOfClassInstances++;
                decisionsInstances[DataAccessor.getDecisionValues().indexOf(DataAccessor.getDataset().get(i).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())]++;
            } else {
                boolean theSame;
                if (index == -1)
                    theSame = isTheSame(prev, i);
                else theSame = isTheSameWithoutAttribute(attribute, prev, i);
                if (theSame) {
                    numberOfClassInstances++;
                    decisionsInstances[DataAccessor.getDecisionValues().indexOf(DataAccessor.getDataset().get(i).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())]++;
                } else {
                    singleAttrValue = directConditionalEntropyCalc(decisionsInstances, numberOfClassInstances);
                    singleAttrValue *= (ConstStrings.MINUS_ONE) * ((double) numberOfClassInstances) / ((double) DataAccessor.getDataset().size());
                    numberOfClassInstances = ConstStrings.ZERO;
                    finalValue += singleAttrValue;
                    i--;
                    prev = null;
                }
            }
        }
        //obliczanie ostatniego zbioru
        finalValue = getFinalValue(finalValue, numberOfClassInstances, decisionsInstances);
        if (index != -1) {
            if (finalValue < 0)
                finalValue *= -1;
        }
        return finalValue;
    }

}
