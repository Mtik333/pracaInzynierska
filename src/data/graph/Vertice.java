/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.graph;

/**
 *
 * @author Mateusz
 */
public class Vertice {

    private String name; //nazwa cechy
    private int index; //indeks

    public Vertice(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public Vertice() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
