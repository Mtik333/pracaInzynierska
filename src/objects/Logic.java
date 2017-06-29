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

/**
 *
 * @author Mateusz
 */
public class Logic {
    private File loadedFile; //plik z danymi
    public ArrayList<DataObject> dataset; //zbior obiektów wczytanych
    private String[] attributesNames;
    public Logic(){}
    public File getFile(){
        return loadedFile;
    }
    public void setFile(File f){
        this.loadedFile=new File(f.toURI());
    }

    public ArrayList<DataObject> getDataset() {
        return dataset;
    }

    public void setDataset(ArrayList<DataObject> dataset) {
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
        int j=1; //ilosc obiektow
        String line;
        dataset=new ArrayList<>();
        while ((line = br.readLine()) != null) {
            String oneObject[] = line.split(separator);
            int i = 0; //ktory z kolei atrybut wczytywany
            ArrayList<Attribute> all_attributes = new ArrayList<Attribute>() {
            };
            for (String x : oneObject) {
                Attribute attribute = new Attribute(attributesNames[i], x);
                if (i != oneObject.length - 1) {
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
    }
}
