/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.roughsets;

import data.DataAccessor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Mateusz
 */
public class DataObjectMultipleComparator implements Comparator<DataObject> {

    public DataObjectMultipleComparator(List<Attribute> attributes){
        sortingBy = new ArrayList<Integer>();
        for (Attribute attribute : attributes){
            sortingBy.add(DataAccessor.getAllAttributes().indexOf(attribute));
        }
    }
    
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
        int i=0;
        do{
            c = ((DataObject) t).getAttributes().get(sortingBy.get(i)).getValue()
                    .compareTo(((DataObject) t1).getAttributes().get(sortingBy.get(i)).getValue());
            i++;
        }while(c==0 && i < sortingBy.size());
//        
//        for (Integer integer : sortingBy){
//            c = ((DataObject) t).getAttributes().get(integer).getValue()
//                    .compareTo(((DataObject) t1).getAttributes().get(integer).getValue());
//        }
        return c;
    }
    
}
