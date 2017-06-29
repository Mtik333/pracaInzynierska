/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mateusz
 */
public class DataObject {
    private String name;
    private ArrayList<Attribute> attributes;
    
    @Override
    public String toString() {
        StringBuilder returnString=new StringBuilder();
        for (Attribute x : attributes){
            returnString.append(x.getValue()+", ");
        }
        returnString.delete(returnString.length()-2, returnString.length());
        returnString.append(';');
        return ""+name+": "+returnString.toString()+"\n";
    }

    public DataObject(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
    }

}