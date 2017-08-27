/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import data.graph.Ant;
import data.graph.Edge;
import data.graph.Graph;
import data.graph.NewAnt;
import data.graph.Vertice;
import data.roughsets.DataObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Mateusz
 */
public class JensenLogic extends Logic {

    @Override
    public void generateGraph() {
        List<Vertice> vertices = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < DataAccessor.getAllAttributes().size(); i++) {
            vertices.add(new Vertice(DataAccessor.getAllAttributes().get(i).getName(), i));
        }
        vertices.remove(DataAccessor.getDecisionMaker()); //usuwamy wierzcholek decyzyjny
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
        //System.out.println(graph.toString());
    }

    @Override
    public void generateAntsPheromone() {
        if (DataAccessor.getCurrentReduct() == null) {
            generateBasicPheromone();
        }
        List<Ant> newAnts = new ArrayList<>();
        for (int i = 0; i < DataAccessor.getAntsNumber(); i++) {
            NewAnt ant = new NewAnt(i);
            ant.initLists(DataAccessor.getGraph().getVertices());
            newAnts.add(ant);
        }
        DataAccessor.setAllAnts(newAnts);
    }

    @Override
    public void initializeAntsRandom() {
        DataAccessor.setCalculatedReductInIteration(false);
        generateAntsPheromone();
        Random random = new Random();
        DataAccessor.getAllAnts().stream().map((ant) -> {
            int j = random.nextInt(DataAccessor.getGraph().getVertices().size()); //losowy wybór
            ant.pickVertice(ant.getUnpickedAttributes().get(j));
            return ant;
        }).forEachOrdered((ant) -> {
            ((NewAnt) ant).setDiscMatrix(DataAccessor.getIndiscMatrix());
        });
        DataAccessor.setCurrentIter(1);
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
