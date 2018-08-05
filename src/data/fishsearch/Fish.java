package data.fishsearch;

import data.ConstStrings;
import data.DataAccessor;
import data.roughsets.Attribute;
import data.roughsets.DataObject;
import data.roughsets.DataObjectMultipleComparator;

import java.util.*;
import java.util.stream.IntStream;

public class Fish implements Runnable {
    int index; //indeks mrówki
    int currentIter=0; //obecny numer kroku w iteracji
    boolean[] values; //tablica atrybutow
    List<Attribute> attributeList;
    List<Attribute> notUsedAttributeList;

    public List<Attribute> getNotUsedAttributeList() {
        return notUsedAttributeList;
    }

    public void setNotUsedAttributeList(List<Attribute> notUsedAttributeList) {
        this.notUsedAttributeList = notUsedAttributeList;
    }

    public List<Attribute> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<Attribute> attributeList) {
        this.attributeList = attributeList;
    }

    public List<DataObject> getSortedDataset() {
        return sortedDataset;
    }

    public void setSortedDataset(List<DataObject> sortedDataset) {
        this.sortedDataset = sortedDataset;
    }

    List<DataObject> sortedDataset; //dane posortowane

    public Fish(int index){
        this.index = index;
        this.values = new boolean[DataAccessor.getNonDecisionAttributesNumber()];

        this.attributeList = new ArrayList<>();
        this.notUsedAttributeList = new ArrayList<>();
        initFish();
    }

    public void initFish(){
        Random random = new Random();
        int bound = this.values.length;
        for (int i = 0; i < bound; i++) {
            values[i] = random.nextBoolean();
            if (values[i]){
                attributeList.add(DataAccessor.getAllAttributes().get(i));
            }
            else
                notUsedAttributeList.add(DataAccessor.getAllAttributes().get(i));
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getCurrentIter() {
        return currentIter;
    }

    public void setCurrentIter(int currentIter) {
        this.currentIter = currentIter;
    }

    public boolean[] getValues() {
        return values;
    }

    public void setValues(boolean[] values) {
        this.values = values;
    }

    public int returnTrueSize(){
        int trueNumber=0;
        for (int i=0; i<this.getValues().length; i++){
            if (this.getValues()[i])
                trueNumber++;
        }
        return trueNumber;
    }

    public double updateDependencyDegree(List<Attribute> attributes){
        int[] decisionsInstances = new int[DataAccessor.getDecisionValues().size()];
        int goodObjs=0;
        DataObject prev = null;
        DataObjectMultipleComparator domc = new DataObjectMultipleComparator(attributes);
        List<DataObject> sortedDataset = new ArrayList<>();
        sortedDataset.addAll(DataAccessor.getDataset());
        sortedDataset.sort(domc);
        for (int i = 0; i < sortedDataset.size(); i++) {
            if (prev == null) {
                Arrays.fill(decisionsInstances, ConstStrings.ZERO);
                prev = sortedDataset.get(i);
                decisionsInstances[DataAccessor.getDecisionValues().indexOf(sortedDataset.get(i).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())]++;
            } else {
                boolean theSame = true;
                for (int j = 0; j < domc.getSortingBy().size(); j++) {
                    if (!prev.getAttributes().get(domc.getSortingBy().get(j)).getValue().equals(sortedDataset.get(i).getAttributes().get(domc.getSortingBy().get(j)).getValue())) {
                        theSame = false;
                        break;
                    }
                }
                if (theSame) {
                    decisionsInstances[DataAccessor.getDecisionValues().indexOf(sortedDataset.get(i).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())]++;
                } else {
                    int variousClasses = ConstStrings.ZERO;
                    for (int decisionsInstance : decisionsInstances) {
                        if (decisionsInstance != ConstStrings.ZERO) {
                            variousClasses++;
                        }
                    }
                    if (variousClasses != ConstStrings.ONE) {
                        i--;
                        prev = null;
                    } else {
                        for (int decisionsInstance : decisionsInstances) {
                            goodObjs+=decisionsInstance;
                        }
                        i--;
                        prev = null;
                    }
                }
            }
        }
        int variousClasses = ConstStrings.ZERO;
        for (int decisionsInstance : decisionsInstances) {
            if (decisionsInstance != ConstStrings.ZERO) {
                variousClasses++;
            }
        }
        if (variousClasses==ConstStrings.ONE){
            for (int decisionsInstance : decisionsInstances) {
                goodObjs+=decisionsInstance;
            }
        }
        return ((double)goodObjs)/((double)sortedDataset.size());
    }

    public void searchingAlgorithm(){
        double currentDependencyDegree = updateDependencyDegree(getAttributeList());
        List<Attribute> newlyUsed = new ArrayList<>();
        newlyUsed.addAll(attributeList);
        boolean[] temporarySearching = Arrays.copyOf(values,values.length);
        Random random = new Random();
        int maxSize=notUsedAttributeList.size();
        int i=0;
        do{
            int index = random.nextInt(notUsedAttributeList.size());
            Attribute attribute = notUsedAttributeList.get(index);
            newlyUsed.add(attribute);
            notUsedAttributeList.remove(attribute);
            int indexOf = DataAccessor.getAllAttributes().indexOf(attribute);
            temporarySearching[indexOf]=true;
            i++;
        }
        while(currentDependencyDegree>=updateDependencyDegree(newlyUsed) && i<maxSize);
        attributeList=newlyUsed;
    }

    public void swarmingAlgorithm(){
        int n=0;
        double v=0;
        List<Fish> friendFishes = new ArrayList<>();
        boolean[] temporarySwarming = new boolean[values.length];
        for (Fish fish : DataAccessor.getAllFishes()){
            if (fish!=this){
                if (DataAccessor.hammingDistance(this, fish)<DataAccessor.getFishVisual()){
                    n=n+1;
                    friendFishes.add(fish);
                    v=v+updateDependencyDegree(fish.getAttributeList());
                }
            }
        }
        Fish centerFish = DataAccessor.updateCenterFish(friendFishes);
        if (v/((double)(n))<(updateDependencyDegree(this.getAttributeList()))*DataAccessor.getFishDeltaRelevance()){
            attributeList=centerFish.getAttributeList();
        }
        else{
            searchingAlgorithm();
        }
    }

    public void swimmingAlgorithm(){
        int n=0;
        Fish bestFish=null;
        double dependencymax=0;
        double vmax=0;
        double currentDependencyDegree = updateDependencyDegree(getAttributeList());
        for (Fish fish : DataAccessor.getAllFishes()){
            if (fish!=this) {
                if (DataAccessor.hammingDistance(this, fish) < DataAccessor.getFishVisual() && updateDependencyDegree(fish.getAttributeList()) > currentDependencyDegree)
                    bestFish=fish;
            }
        }
        for (Fish fish : DataAccessor.getAllFishes()){
            if (fish!=this) {
                if (DataAccessor.hammingDistance(bestFish, fish) < DataAccessor.getFishVisual()){
                    n=n+1;
                    vmax=vmax+updateDependencyDegree(fish.getAttributeList());
                }
            }
        }
        if (vmax/((double)(n))<(updateDependencyDegree(this.getAttributeList()))*DataAccessor.getFishDeltaRelevance()){
            attributeList=bestFish.getAttributeList();
        }
        else{
            searchingAlgorithm();
        }
    }

    @Override
    public void run() {

    }
}
