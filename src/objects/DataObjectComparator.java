/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;

import java.util.Comparator;

/**
 *
 * @author Mateusz
 */
public class DataObjectComparator implements Comparator<DataObject>{
    public int unusedAttribute=0;
    public void setUnusedAttribute(int unusedAttribute) {
        this.unusedAttribute = unusedAttribute;
    }
    public DataObjectComparator(int unusedAttribute){
        this.unusedAttribute=unusedAttribute;
    }
    @Override
    public int compare(DataObject t, DataObject t1) {
        int c=0;
        for (int i=0; i<t.getAttributes().size()-1; i++){
            if (i!=unusedAttribute)
                c = ((DataObject)t).getAttributes().get(i).getValue().compareTo(((DataObject)t1).getAttributes().get(i).getValue());
            if (c!=0)
                break;
        }
        return c;
    }
    
}
