/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.roughsets;
import static data.ConstStrings.*;
/**
 *
 * @author Mateusz
 */
public class Attribute {
    private String name;
    private String value;
    private boolean decisionMaking;
    
    @Override
    public String toString() {
        if (!decisionMaking)
            return ATTRIBUTE_TO_STRING + ATTRIBUTE_TO_STRING_NAME + name + ATTRIBUTE_TO_STRING_VALUE + value + '}';
        else return ATTRIBUTE_TO_STRING + ATTRIBUTE_TO_STRING_NAME + name + ATTRIBUTE_TO_STRING_VALUE + value + ATTRIBUTE_TO_STRING_DECISIONMAKING + decisionMaking + '}';
    }
    public Attribute(String name){
        this.name=name;
    }
    
    public Attribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    public boolean isDecisionMaking() {
        return decisionMaking;
    }

    public void setDecisionMaking(boolean decisionMaking) {
        this.decisionMaking = decisionMaking;
    }
}
