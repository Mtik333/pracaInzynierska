/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import data.roughsets.Attribute;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import javafx.stage.Stage;
import data.roughsets.DataObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collector;

/**
 *
 * @author Mateusz
 */
public class DataAccessor {
    public static Stage primaryStage;
    public static String separator=",";
    public static boolean loadedData=false;
    public static File file;
    public static List<DataObject> dataset; //zbior obiektów wczytanych
    public static List<Attribute> allAttributes;
    public static int decisionMaker;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static String getSeparator() {
        return separator;
    }

    public static boolean isLoadedData() {
        return loadedData;
    }

    public static File getFile() {
        return file;
    }

    public static List<DataObject> getDataset() {
        return dataset;
    }

    public static List<Attribute> getAllAttributes() {
        return allAttributes;
    }

    public static int getDecisionMaker() {
        return decisionMaker;
    }

    public static void setDecisionMaker(int decisionMaker) {
        DataAccessor.decisionMaker = decisionMaker;
    }
    
    public static void setLoadedData(boolean loadedData) {
        DataAccessor.loadedData = loadedData;
    }

    public static void setSeparator(String separator) {
        DataAccessor.separator = separator;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        DataAccessor.primaryStage = primaryStage;
    }
    
    public static void setFile(File file){
        DataAccessor.file = file;
    }
    
    public static boolean parseFile() throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
        stringToAttribute(br.readLine().split(separator));
        if (allAttributes.size()==1){
            allAttributes=null;
            file=null;
            return false;
        }
        int j = 1; //ilosc obiektow
        String line;
        dataset = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            String oneObject[] = line.split(separator);
            int i = 0; //ktory z kolei atrybut wczytywany
            List<Attribute> singleObjectAttributes = new ArrayList<>();
            for (String x : oneObject) {
                Attribute attribute = new Attribute(allAttributes.get(i).getName(), x);
                if (i != getDecisionMaker()) {
                    singleObjectAttributes.add(attribute);
                } else {
                    attribute.setDecisionMaking(true);
                    singleObjectAttributes.add(attribute);
                }
                i++;
            }
            DataObject newObject = new DataObject("" + j);
            newObject.setAttributes(singleObjectAttributes);
            dataset.add(newObject);
            j++;
        }
        return true;
    }
    
    private static void stringToAttribute(String[] attributes){
        allAttributes = new ArrayList<Attribute>();
        setDecisionMaker(attributes.length-1);
        for (String attribute : attributes) {
            Attribute myAttribute = new Attribute(attribute);
            allAttributes.add(myAttribute);
        }
        allAttributes.get(decisionMaker).setDecisionMaking(true);
    }
    
}
