/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.roughsets;

import data.ConstStrings;

import static data.ConstStrings.*;

/**
 * @author Mateusz
 */
public class Attribute {

    private final String name; //nazwa atrybutu
    private String value; //wartosc atrybutu
    private boolean decisionMaking; //czy decyzyjny

    public Attribute(String name) {
        this.name = name;
    }

    public Attribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        if (!decisionMaking) {
            return ATTRIBUTE_TO_STRING + ATTRIBUTE_TO_STRING_NAME + name + ATTRIBUTE_TO_STRING_VALUE + value + ConstStrings.CURLY_BRACKET_CLOSE;
        } else {
            return ATTRIBUTE_TO_STRING + ATTRIBUTE_TO_STRING_NAME + name + ATTRIBUTE_TO_STRING_VALUE + value + ATTRIBUTE_TO_STRING_DECISIONMAKING + true + ConstStrings.CURLY_BRACKET_CLOSE;
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    public void setDecisionMaking(boolean decisionMaking) {
        this.decisionMaking = decisionMaking;
    }
}
