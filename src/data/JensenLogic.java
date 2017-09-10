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
        countDecisionClasses();
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
        DataAccessor.setAntsNumber(vertices.size()/2);
        DataAccessor.setMaxList(vertices.size());
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
        DataAccessor.getAllAnts().forEach((ant) -> {
            int j = random.nextInt(DataAccessor.getGraph().getVertices().size()); //losowy wybór
            ant.pickVertice(ant.getUnpickedAttributes().get(j));
        });
        DataAccessor.setCurrentIter(1);
    }

}
