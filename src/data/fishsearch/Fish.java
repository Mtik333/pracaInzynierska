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
    int numberOfUsedAttributes=0;
    boolean[] values; //tablica atrybutow
    List<Attribute> attributeList;
    List<Attribute> notUsedAttributeList;

    public boolean isHasReduct() {
        return hasReduct;
    }

    public void setHasReduct(boolean hasReduct) {
        this.hasReduct = hasReduct;
    }

    boolean hasReduct=false;

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

    public Fish(int index, boolean[] values, int numberOfUsedAttributes, List<Attribute> attributes, List<Attribute> notUsed){
        this.index = index;
        this.values = Arrays.copyOf(values,values.length);
        this.attributeList = new ArrayList<>();
        attributeList.addAll(attributes);
        this.notUsedAttributeList = new ArrayList<>();
        notUsedAttributeList.addAll(notUsed);
        this.numberOfUsedAttributes=numberOfUsedAttributes;
    }

    public void initFish(){
        Random random = new Random();
        notUsedAttributeList.addAll(DataAccessor.getAllAttributes());
        notUsedAttributeList.remove(notUsedAttributeList.size()-1);
        int index = random.nextInt(DataAccessor.getNonDecisionAttributesNumber());
        Attribute attribute = DataAccessor.getAllAttributes().get(index);
        attributeList.add(attribute);
        notUsedAttributeList.remove(attribute);
        values[index] = true;
        this.numberOfUsedAttributes=returnTrueSize();
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
        if (attributes.size()==0)
            return 0;
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

    public void searchingAlgorithm(Fish myFish){
        double currentDependencyDegree = updateDependencyDegree(myFish.getAttributeList());
        List<Attribute> newlyUsed = new ArrayList<>();
        newlyUsed.addAll(myFish.attributeList);
        boolean[] temporarySearching = Arrays.copyOf(myFish.values,myFish.values.length);
        Random random = new Random();
        int maxSize=myFish.notUsedAttributeList.size();
        int i=0;
        do{
            int index = random.nextInt(myFish.notUsedAttributeList.size());
            Attribute attribute = myFish.notUsedAttributeList.get(index);
            newlyUsed.add(attribute);
            myFish.notUsedAttributeList.remove(attribute);
            int indexOf = DataAccessor.getAllAttributes().indexOf(attribute);
            temporarySearching[indexOf]=true;
            i++;
        }
        while(currentDependencyDegree>=updateDependencyDegree(newlyUsed) && i<maxSize);
        myFish.attributeList=newlyUsed;
        myFish.numberOfUsedAttributes=returnTrueSize();
        myFish.values=Arrays.copyOf(temporarySearching,temporarySearching.length);
    }

    public void swarmingAlgorithm(Fish myFish){
        int n=0;
        double v=0;
        List<Fish> friendFishes = new ArrayList<>();
        boolean[] temporarySwarming = new boolean[myFish.values.length];
        for (Fish fish : DataAccessor.getAllFishes()){
            if (fish!=myFish){
                if (DataAccessor.hammingDistance(myFish, fish)<DataAccessor.getFishVisual()){
                    n=n+1;
                    friendFishes.add(fish);
                    v=v+updateDependencyDegree(fish.getAttributeList());
                }
            }
        }
        Fish centerFish = DataAccessor.updateCenterFish(friendFishes);
        if (v/((double)(n))<(updateDependencyDegree(myFish.getAttributeList()))*DataAccessor.getFishDeltaRelevance()){
            myFish.attributeList=centerFish.getAttributeList();
            myFish.numberOfUsedAttributes=returnTrueSize();
            myFish.values=Arrays.copyOf(centerFish.values,centerFish.values.length);
        }
        else{
            searchingAlgorithm(myFish);
        }
    }

    public void swimmingAlgorithm(Fish myFish){
        int n=0;
        Fish bestFish=null;
        double dependencyMax=0;
        double vmax=0;
        for (Fish fish : DataAccessor.getAllFishes()){
            if (fish!=myFish) {
                if (DataAccessor.hammingDistance(myFish, fish) < DataAccessor.getFishVisual() && updateDependencyDegree(fish.getAttributeList()) > dependencyMax){
                    bestFish=fish;
                    dependencyMax=updateDependencyDegree(bestFish.getAttributeList());
                }
            }
        }
        if (bestFish==null)
            bestFish=myFish;
        for (Fish fish : DataAccessor.getAllFishes()){
            if (fish!=myFish) {
                if (DataAccessor.hammingDistance(bestFish, fish) < DataAccessor.getFishVisual()){
                    n=n+1;
                    vmax=vmax+updateDependencyDegree(fish.getAttributeList());
                }
            }
        }
        if (vmax/((double)(n))<(updateDependencyDegree(myFish.getAttributeList()))*DataAccessor.getFishDeltaRelevance()){
            myFish.attributeList=bestFish.getAttributeList();
            myFish.numberOfUsedAttributes=returnTrueSize();
            myFish.values=Arrays.copyOf(bestFish.values,bestFish.values.length);
        }
        else{
            searchingAlgorithm(myFish);
        }
    }

    @Override
    public void run() {
        Fish searchFish = new Fish(this.index, this.values, this.numberOfUsedAttributes, this.getAttributeList(), this.notUsedAttributeList);
        Fish swarmingFish = new Fish(this.index, this.values, this.numberOfUsedAttributes,this.getAttributeList(), this.notUsedAttributeList);
        Fish swimmingFish = new Fish(this.index, this.values, this.numberOfUsedAttributes,this.getAttributeList(), this.notUsedAttributeList);
        searchingAlgorithm(searchFish);
        swarmingAlgorithm(swarmingFish);
        swimmingAlgorithm(swimmingFish);
        double searchingFitness = calculateFitness(searchFish);
        double swarmingFitness = calculateFitness(swarmingFish);
        double swimmingFitness = calculateFitness(swimmingFish);
        if (searchingFitness > swarmingFitness && searchingFitness > swimmingFitness){
            this.attributeList=searchFish.getAttributeList();
            this.notUsedAttributeList=searchFish.notUsedAttributeList;
            this.values=searchFish.values;
            this.numberOfUsedAttributes=searchFish.numberOfUsedAttributes;
        }
        else if (swarmingFitness > searchingFitness && swarmingFitness > swimmingFitness){
            this.attributeList=searchFish.getAttributeList();
            this.notUsedAttributeList=searchFish.notUsedAttributeList;
            this.values=searchFish.values;
            this.numberOfUsedAttributes=searchFish.numberOfUsedAttributes;
        }
        else if (swimmingFitness > searchingFitness && swimmingFitness > swarmingFitness) {
            this.attributeList=searchFish.getAttributeList();
            this.notUsedAttributeList=searchFish.notUsedAttributeList;
            this.values=searchFish.values;
            this.numberOfUsedAttributes=searchFish.numberOfUsedAttributes;
        }
        int i=0;
    }

    public Double calculateFitness(Fish fish){
        double fitness = (DataAccessor.getFishAlphaRelevance() * fish.updateDependencyDegree(fish.getAttributeList()));
        double test = (double)(DataAccessor.getNonDecisionAttributesNumber()-fish.returnTrueSize())/((double)DataAccessor.getNonDecisionAttributesNumber());
        fitness = fitness + DataAccessor.getFishBetaRelevance() * test;
        return fitness;
    }
}
