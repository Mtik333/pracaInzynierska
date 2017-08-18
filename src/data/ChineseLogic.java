/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import data.graph.Edge;
import data.graph.Graph;
import data.graph.Vertice;
import data.roughsets.Attribute;
import data.roughsets.DataObject;
import data.roughsets.DataObjectComparator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Mateusz
 */
public class ChineseLogic {
    
    
    //generuje graf (wierzchołki i krawędzi)
    public void generateGraph() {
        coreCT2();
        List<Vertice> vertices = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < DataAccessor.getAllAttributes().size()-1; i++){
            if (!DataAccessor.getCoreAttributes().contains(DataAccessor.getAllAttributes().get(i)))
                vertices.add(new Vertice(DataAccessor.getAllAttributes().get(i).getName(), i));
        }
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = i + 1; j < vertices.size(); j++) {
                edges.add(new Edge(vertices.get(i), vertices.get(j))); //graf pelny, wiec krawdzie miedzy kazdymi wierzcholkami
            }
        }
        DataAccessor.setGraph(new Graph(vertices, edges));
        DataAccessor.setAntsNumber(vertices.size());
        DataAccessor.setMaxList(vertices.size());
        fillIndiscMatrix();
        String[][] indiscMatrix = DataAccessor.getIndiscMatrix();
        System.out.println();
    }
    
    //funkcje do algorytmu CORE-CT
    //zlicza ilosc klas decyzyjnych
    public int countDecisionClasses() {
        DataAccessor.setDecisionValues(new ArrayList<>());
        DataAccessor.getDataset().stream().filter((dataObject) -> (!DataAccessor.getDecisionValues().contains(dataObject.getAttributes().get(DataAccessor.getDecisionMaker()).getValue()))).forEachOrdered((dataObject) -> {
            DataAccessor.getDecisionValues().add(dataObject.getAttributes().get(DataAccessor.getDecisionMaker()).getValue());
        });
        return DataAccessor.getDecisionValues().size();
    }

    //zlicza ilosc konfliktow (w klasie)
    public int countConflictsRow(int[] ctRow) {
        double initialValue = Math.pow(Arrays.stream(ctRow).sum(), 2);
        for (int value : ctRow) {
            initialValue = initialValue - Math.pow(value, 2);
        }
        initialValue = initialValue * 0.5;
        return (int) initialValue;
    }

    //zlicza ilosc konfliktow ogolem
    public int countConflictsTotal(int[] ctTotal) {
        double finalValue = Math.pow(Arrays.stream(ctTotal).sum(), 2);
        for (int value : ctTotal) {
            finalValue = finalValue - Math.pow(value, 2);
        }
        finalValue = finalValue * 0.5;
        return (int) finalValue;
    }

//    public void coreDDM() {
//        List<String> foundCore = new ArrayList<>();
//        for (int i = 0; i < indiscMatrix.length; i++) {
//            for (int j = i; j < indiscMatrix[i].length; j++) {
//                if (indiscMatrix[i][j] != null) {
//                    if (indiscMatrix[i][j].chars().filter(ch -> ch == ',').count() == 2) {
//                        int singleton = (int)indiscMatrix[i][j].replace(",", "").charAt(0)-'0';
//                        if (!foundCore.contains(attributesAll.get(singleton).getName())){
//                            foundCore.add(attributesAll.get(singleton).getName());
//                        }
//                    }
//                }
//            }
//        }
//        for (String x : foundCore) {
//            System.out.print(x + ",");
//        }
//    }
    //caly algorytm CT
    public void coreCT2() {
        List<Attribute> foundCore = new ArrayList<>();
        int decisionClasses = countDecisionClasses();
        System.out.println(decisionClasses);
        for (int i = 0; i < DataAccessor.getAllAttributes().size() - 1; i++) {
            int[] ctRow = new int[decisionClasses];
            int[] ctTotal = new int[decisionClasses];
            int confs = 0;
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
                    Arrays.fill(ctRow, 0);
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
            System.out.print(a.getName() + ",");
        });
        DataAccessor.setCoreAttributes(foundCore);
    }
    
     //wypelnienie macierzy rozroznialnosci
    public void fillIndiscMatrix() {
        DataObject dat1, dat2;
        StringBuilder cellString = new StringBuilder();
        DataAccessor.setIndiscMatrix(new String[DataAccessor.getDataset().size()][DataAccessor.getDataset().size()]);
        for (int i = 0; i < DataAccessor.getDataset().size(); i++) {
            for (int j = (i + 1); j < DataAccessor.getDataset().size(); j++) {
                if (!DataAccessor.getDataset().get(i).getAttributes().get(DataAccessor.getDecisionMaker()).getValue().equals(DataAccessor.getDataset().get(j).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())) {
                    dat1 = DataAccessor.getDataset().get(i);
                    dat2 = DataAccessor.getDataset().get(j);
                    cellString.setLength(0);
                    cellString.append(",");
                    for (int k = 0; k < DataAccessor.getAllAttributes().size() - 1; k++) { //problem gdy decisionmaker nie na końcu
                        
                            if (!dat1.getAttributes().get(k).getValue().equals(dat2.getAttributes().get(k).getValue())) {
                                cellString.append(k).append(",");
                            }
                        
                    }
                    if (cellString.length() > 2) {
                        DataAccessor.getIndiscMatrix()[i][j] = cellString.toString();
                    }
                    /*if (cellString.length() > 0) {
                        cellString.deleteCharAt(cellString.length() - 1);
                    }*/
                }
            }
        }
    }
}
