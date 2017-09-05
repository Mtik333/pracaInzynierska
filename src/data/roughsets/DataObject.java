/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.roughsets;

import java.util.List;

/**
 *
 * @author Mateusz
 */
public class DataObject implements Comparable<DataObject> {

    private String name; //nazwa obiektu (z reguly indeks jako ciag znakow)
    private List<Attribute> attributes; //lista atrybutow

    @Override
    public String toString() {
        StringBuilder returnString = new StringBuilder();
        attributes.forEach((x) -> {
            returnString.append(x.getValue()).append(", ");
        });
        returnString.delete(returnString.length() - 2, returnString.length());
        returnString.append(';');
        return "" + name + ": " + returnString.toString() + "\n";
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

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public int compareTo(DataObject other) {
        Integer myName = Integer.parseInt(name);
        Integer otherName = Integer.parseInt(other.name);
        return myName.compareTo(otherName);
    }
}
