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
    DataObjectMultipleComparator domc; //komparator do zbioru
    List<DataObject> sortedDataset; //dane posortowane
    boolean hasReduct=false;
    double currentDependency=0;

    public double getCurrentDependency() {
        return currentDependency;
    }

    public void setCurrentDependency(double currentDependency) {
        this.currentDependency = currentDependency;
    }

    public boolean isHasReduct() {
        return hasReduct;
    }

    public void setHasReduct(boolean hasReduct) {
        this.hasReduct = hasReduct;
    }

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

    public Fish(int index, boolean core){
        this.index = index;
        this.values = new boolean[DataAccessor.getNonDecisionAttributesNumber()];
        this.attributeList = new ArrayList<>();
        this.notUsedAttributeList = new ArrayList<>();
        this.sortedDataset = new ArrayList<>();
        this.sortedDataset.addAll(DataAccessor.getDataset());
        initFish(core);
    }

    public Fish(int index, boolean[] values, int numberOfUsedAttributes, List<Attribute> attributes, List<Attribute> notUsed, List<DataObject> dataset, DataObjectMultipleComparator domc){
        this.index = index;
        this.values = Arrays.copyOf(values,values.length);
        this.attributeList = new ArrayList<>();
        attributeList.addAll(attributes);
        this.notUsedAttributeList = new ArrayList<>();
        notUsedAttributeList.addAll(notUsed);
        this.numberOfUsedAttributes=numberOfUsedAttributes;
        this.sortedDataset=dataset;
        this.domc=domc;
    }

    public void initFish(boolean core){
        Random random = new Random();
        notUsedAttributeList.addAll(DataAccessor.getAllAttributes());
        notUsedAttributeList.remove(DataAccessor.getAllAttributes().size()-1);
        if (core){
            for (Attribute attribute : DataAccessor.getCoreAttributes()){
                attributeList.add(attribute);
                notUsedAttributeList.remove(attribute);
                int index = DataAccessor.getAllAttributes().indexOf(attribute);
                values[index]=true;
            }
            setCurrentDependency(DataAccessor.getCoreDependency());
            this.numberOfUsedAttributes = returnTrueSize();
        }
        else {
            int index = random.nextInt(DataAccessor.getNonDecisionAttributesNumber());
            Attribute attribute = DataAccessor.getAllAttributes().get(index);
            attributeList.add(attribute);
            notUsedAttributeList.remove(attribute);
            values[index] = true;
            this.numberOfUsedAttributes = returnTrueSize();
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
        if (attributes.size()==0)
            return 0;
        int[] decisionsInstances = new int[DataAccessor.getDecisionValues().size()];
        int goodObjs=0;
        DataObject prev = null;
        this.domc = new DataObjectMultipleComparator(attributes);
        this.sortedDataset.sort(this.domc);
        for (int i = 0; i < sortedDataset.size(); i++) {
            if (prev == null) {
                Arrays.fill(decisionsInstances, ConstStrings.ZERO);
                prev = sortedDataset.get(i);
                decisionsInstances[DataAccessor.getDecisionValues().indexOf(sortedDataset.get(i).getAttributes().get(DataAccessor.getDecisionMaker()).getValue())]++;
            } else {
                boolean theSame = true;
                for (int j = 0; j < this.domc.getSortingBy().size(); j++) {
                    if (!prev.getAttributes().get(this.domc.getSortingBy().get(j)).getValue().equals(this.sortedDataset.get(i).getAttributes().get(this.domc.getSortingBy().get(j)).getValue())) {
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

    public void searchingAlgorithm(Fish myFish, double myDependency){
        double currentDependencyDegree = myDependency;
        List<Attribute> newlyUsed = new ArrayList<>();
        newlyUsed.addAll(myFish.attributeList);
        boolean[] temporarySearching = Arrays.copyOf(myFish.values,myFish.values.length);
        Random random = new Random();
        boolean betterDependency=false;
        double newDependency=myDependency;
        int step=0;
        while (!betterDependency){
            step++;
            if (myFish.notUsedAttributeList.size()!=0) {
                int index = random.nextInt(myFish.notUsedAttributeList.size());
                Attribute attribute = myFish.notUsedAttributeList.get(index);
                myFish.attributeList.add(attribute);
                myFish.notUsedAttributeList.remove(attribute);
                int indexOf = DataAccessor.getAllAttributes().indexOf(attribute);
                temporarySearching[indexOf] = true;
                newDependency = updateDependencyDegree(myFish.getAttributeList());
                if (newDependency>currentDependencyDegree)
                    betterDependency=true;
                else if (step==myFish.notUsedAttributeList.size()){
                    betterDependency=true;
                }
                else{
                    myFish.attributeList.remove(attribute);
                    myFish.notUsedAttributeList.add(attribute);
                    temporarySearching[indexOf] = false;
                }
            }
        }
//
//        do{
//                    if (myFish.notUsedAttributeList.size()!=0)
//                    {
//                    int index = random.nextInt(myFish.notUsedAttributeList.size());
//                    Attribute attribute = myFish.notUsedAttributeList.get(index);
//                    myFish.attributeList.add(attribute);
//                    myFish.notUsedAttributeList.remove(attribute);
//                    int indexOf = DataAccessor.getAllAttributes().indexOf(attribute);
//                    temporarySearching[indexOf] = true;
//                    }
//        }
//        while(currentDependencyDegree>=updateDependencyDegree(myFish.getAttributeList()));
        myFish.numberOfUsedAttributes=returnTrueSize();
        myFish.values=Arrays.copyOf(temporarySearching,temporarySearching.length);
        myFish.setNotUsedAttributeList(DataAccessor.returnNonUsedAttributes(myFish.attributeList));
        myFish.setCurrentDependency(newDependency);
    }

    public void swarmingAlgorithm(Fish myFish, double myDependency){
        int n=0;
        double v=0;
        List<Fish> friendFishes = new ArrayList<>();
        for (Fish fish : DataAccessor.getAllFishes()){
            if (!fish.equals(myFish)){
                if (DataAccessor.hammingDistance(myFish, fish)<DataAccessor.getFishVisual()){
                    n=n+1;
                    friendFishes.add(fish);
                    v=v+fish.getCurrentDependency();
                    //v=v+updateDependencyDegree(fish.getAttributeList());
                }
            }
        }
        Fish centerFish = DataAccessor.updateCenterFish(friendFishes);
        if (v/((double)(n))<(myDependency)*DataAccessor.getFishDeltaRelevance()){
            myFish.attributeList=centerFish.getAttributeList();
            myFish.numberOfUsedAttributes=returnTrueSize();
            myFish.values=Arrays.copyOf(centerFish.values,centerFish.values.length);
            myFish.setNotUsedAttributeList(DataAccessor.returnNonUsedAttributes(myFish.attributeList));
            myFish.setCurrentDependency(updateDependencyDegree(centerFish.getAttributeList()));
        }
        else{
            searchingAlgorithm(myFish, myDependency);
        }
    }

    public void swimmingAlgorithm(Fish myFish, double myDependency){
        int n=0;
        Fish bestFish=null;
        double dependencyMax=0;
        double vmax=0;
        for (Fish fish : DataAccessor.getAllFishes()){
            if (!fish.equals(myFish)) {
                if (DataAccessor.hammingDistance(myFish, fish) < DataAccessor.getFishVisual()){
                    double dependencyTemp=fish.getCurrentDependency();
                    //double dependencyTemp=updateDependencyDegree(fish.getAttributeList());
                    if (dependencyTemp>dependencyMax){
                        bestFish=fish;
                        dependencyMax=dependencyTemp;
                    }
                }
            }
        }
        if (bestFish==null){
            bestFish=myFish;
            dependencyMax=myDependency;
        }
        for (Fish fish : DataAccessor.getAllFishes()){
            if (!fish.equals(bestFish)) {
                if (DataAccessor.hammingDistance(bestFish, fish) < DataAccessor.getFishVisual()){
                    n=n+1;
                    vmax=vmax+fish.getCurrentDependency();
                    //vmax=vmax+updateDependencyDegree(fish.getAttributeList());
                }
            }
        }
        double test1 = (vmax)/((double)n);
        double test2=(myDependency)*DataAccessor.getFishDeltaRelevance();
        if (test1<test2){
            myFish.attributeList=bestFish.getAttributeList();
            myFish.numberOfUsedAttributes=returnTrueSize();
            myFish.values=Arrays.copyOf(bestFish.values,bestFish.values.length);
            myFish.setNotUsedAttributeList(DataAccessor.returnNonUsedAttributes(myFish.attributeList));
            myFish.setCurrentDependency(dependencyMax);
        }
        else{
            searchingAlgorithm(myFish, myDependency);
        }
    }

    @Override
    public void run() {
        if (DataAccessor.getCurrentReduct().size()<this.attributeList.size())
            return;
        double dependency = getCurrentDependency();
        setCurrentDependency(dependency);
        if (this.hasReduct){
            return;
        }
        else if (DataAccessor.getGlobalDependencyDegree()==dependency){
            this.hasReduct=true;
            return;
        }
        Fish searchFish = new Fish(this.index, this.values, this.numberOfUsedAttributes, this.getAttributeList(), this.notUsedAttributeList, this.sortedDataset, this.domc);
        Fish swarmingFish = new Fish(this.index, this.values, this.numberOfUsedAttributes,this.getAttributeList(), this.notUsedAttributeList, this.sortedDataset, this.domc);
        Fish swimmingFish = new Fish(this.index, this.values, this.numberOfUsedAttributes,this.getAttributeList(), this.notUsedAttributeList, this.sortedDataset, this.domc);
        searchingAlgorithm(searchFish, dependency);
        swarmingAlgorithm(swarmingFish, dependency);
        swimmingAlgorithm(swimmingFish, dependency);
        double searchingFitness = calculateFitness(searchFish);
        double swarmingFitness = calculateFitness(swarmingFish);
        double swimmingFitness = calculateFitness(swimmingFish);
        if (searchingFitness > swarmingFitness && searchingFitness > swimmingFitness){
            this.attributeList=searchFish.getAttributeList();
            this.notUsedAttributeList=searchFish.notUsedAttributeList;
            this.values=searchFish.values;
            this.numberOfUsedAttributes=searchFish.numberOfUsedAttributes;
            this.currentDependency=searchFish.getCurrentDependency();
        }
        else if (swarmingFitness > searchingFitness && swarmingFitness > swimmingFitness){
            this.attributeList=swarmingFish.getAttributeList();
            this.notUsedAttributeList=swarmingFish.notUsedAttributeList;
            this.values=swarmingFish.values;
            this.numberOfUsedAttributes=swarmingFish.numberOfUsedAttributes;
            this.currentDependency=swarmingFish.getCurrentDependency();
        }
        else {
            this.attributeList=swimmingFish.getAttributeList();
            this.notUsedAttributeList=swimmingFish.notUsedAttributeList;
            this.values=swimmingFish.values;
            this.numberOfUsedAttributes=swimmingFish.numberOfUsedAttributes;
            this.currentDependency=swimmingFish.getCurrentDependency();
        }
        int i=0;
    }

    public Double calculateFitness(Fish fish){
        double fitness = (DataAccessor.getFishQualityRelevance() * fish.getCurrentDependency());
        //double fitness = (DataAccessor.getFishAlphaRelevance() * fish.updateDependencyDegree(fish.getAttributeList()));
        double test = (double)(DataAccessor.getNonDecisionAttributesNumber()-fish.returnTrueSize())/((double)DataAccessor.getNonDecisionAttributesNumber());
        fitness = fitness + DataAccessor.getFishSubsetRelevance() * test;
        return fitness;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fish fish = (Fish) o;
        return index == fish.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
}
