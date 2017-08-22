/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.roughsets;

import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Mateusz
 */
public class DataObjectMultipleComparator implements Comparator<DataObject> {

    public List<Integer> sortingBy;

    public List<Integer> getSortingBy() {
        return sortingBy;
    }

    public void setSortingBy(List<Integer> sortingBy) {
        this.sortingBy = sortingBy;
    }
    
    @Override
    public int compare(DataObject t, DataObject t1) {
        int c = 0;
        for (Integer integer : sortingBy){
            c = ((DataObject) t).getAttributes().get(integer).getValue()
                    .compareTo(((DataObject) t1).getAttributes().get(integer).getValue());
            if (c != 0) {
                break;
            }
        }
        return c;
    }
    
}
