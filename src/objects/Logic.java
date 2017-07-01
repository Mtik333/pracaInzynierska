/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author Mateusz
 */
public class Logic {
    private Graph graph;
    private File loadedFile; //plik z danymi
    private List<DataObject> dataset; //zbior obiektów wczytanych
    private String[] attributesNames; //nazwy atrybutów
    private String[][] indiscMatrix; //macierz rozróznialności
    private int decisionMaker; //indeks atrybutu decyzyjnego

    public Logic(){}
    public int getDecisionMaker() {
        return decisionMaker;
    }

    public void setDecisionMaker(int decisionMaker) {
        this.decisionMaker = decisionMaker;
    }
    
    public File getFile(){
        return loadedFile;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }
    public void setFile(File f){
        this.loadedFile=new File(f.toURI());
    }

    public List<DataObject> getDataset() {
        return dataset;
    }

    public void setDataset(List<DataObject> dataset) {
        this.dataset = dataset;
    }

    public String[] getAttributesNames() {
        return attributesNames;
    }

    public void setAttributesNames(String[] attributesNames) {
        this.attributesNames = attributesNames;
    }
   
    public void fileToObjects(String separator) throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader(loadedFile.getPath()));
        setAttributesNames(br.readLine().split(separator));
        setDecisionMaker(getAttributesNames().length-1);
        int j=1; //ilosc obiektow
        String line;
        dataset=new ArrayList<>();
        while ((line = br.readLine()) != null) {
            String oneObject[] = line.split(separator);
            int i = 0; //ktory z kolei atrybut wczytywany
            List<Attribute> all_attributes = new ArrayList<>();
            for (String x : oneObject) {
                Attribute attribute = new Attribute(attributesNames[i], x);
                if (i != getDecisionMaker()) {
                    all_attributes.add(attribute);
                } else {
                    attribute.setDecisionMaking(true);
                    all_attributes.add(attribute);
                }
                i++;
            }
            DataObject newObject = new DataObject(""+j);
            newObject.setAttributes(all_attributes);
            dataset.add(newObject);
            j++;
        }
        if (getGraph()==null)
            generateGraph();
    }
    public void generateGraph(){
        List<Vertice> vertices = new ArrayList<>();
        List<Edge> edges=new ArrayList<>(); 
        for (String x : attributesNames){
            vertices.add(new Vertice(x)); //dodajemy wszystkie atrybuty
        }
        vertices.remove(decisionMaker); //usuwamy wierzcholek decyzyjny
        for (int i=0; i<vertices.size(); i++){
            for (int j=i+1; j<vertices.size(); j++){
                edges.add(new Edge(vertices.get(i),vertices.get(j))); //graf pelny, wiec krawdzie miedzy kazdymi wierzcholkami
            }
        }
        setGraph(new Graph(vertices, edges));
        System.out.println(graph.toString());
    }
    public void fillIndiscMatrix(){
        this.indiscMatrix = new String[getDataset().size()][getDataset().size()];
        
    }
}
