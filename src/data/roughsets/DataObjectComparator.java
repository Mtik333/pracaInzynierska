/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.roughsets;

import data.ConstStrings;

import java.util.Comparator;

/**
 * @author Mateusz
 */
public class DataObjectComparator implements Comparator<DataObject> {

    private int unusedAttribute; //nieuzywane atrybuty

    public DataObjectComparator(int unusedAttribute) {
        this.unusedAttribute = unusedAttribute;
    }

    @Override
    public int compare(DataObject t, DataObject t1) {
        int c = ConstStrings.ZERO;
        if (t.getAttributes().size() == ConstStrings.TWO) {
            unusedAttribute = ConstStrings.MINUS_ONE;
        }
        for (int i = 0; i < t.getAttributes().size() - 1; i++) {
            if (i != unusedAttribute) {
                c = t.getAttributes().get(i).getValue().compareTo(t1.getAttributes().get(i).getValue());
            }
            if (c != ConstStrings.ZERO) {
                break;
            }
        }
        return c;
    }

}
