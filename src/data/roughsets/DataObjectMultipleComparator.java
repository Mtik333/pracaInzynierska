/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.roughsets;

import data.ConstStrings;
import data.DataAccessor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Mateusz
 */
public class DataObjectMultipleComparator implements Comparator<DataObject> {

    public final List<Integer> sortingBy;

    public DataObjectMultipleComparator(List<Attribute> attributes) {
        sortingBy = new ArrayList<>();
        attributes.forEach((attribute) -> sortingBy.add(DataAccessor.getAllAttributes().indexOf(attribute)));
    }

    public List<Integer> getSortingBy() {
        return sortingBy;
    }

    @Override
    public int compare(DataObject t, DataObject t1) {
        int c;
        int i = ConstStrings.ZERO;
        do {
            c = t.getAttributes().get(sortingBy.get(i)).getValue()
                    .compareTo(t1.getAttributes().get(sortingBy.get(i)).getValue());
            i++;
        } while (c == ConstStrings.ZERO && i < sortingBy.size());
        return c;
    }

}
