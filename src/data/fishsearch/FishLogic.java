package data.fishsearch;

import data.ConstStrings;
import data.DataAccessor;
import data.roughsets.Attribute;
import data.roughsets.DataObject;
import data.roughsets.DataObjectMultipleComparator;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FishLogic {

    public List<Attribute> fishReduct;
    public int fishReductSize=Integer.MAX_VALUE;
    public double dependency=0;

    public void initializeFish(boolean core){
        fishReduct=new ArrayList<>();
        fishReductSize=Integer.MAX_VALUE;
        fishReduct.addAll(DataAccessor.getAllAttributes());
        DataAccessor.setFishNumber(DataAccessor.getAllAttributes().size()/2);
        List<Fish> fishList = new ArrayList<>();
        for (int i=0; i< DataAccessor.getFishNumber(); i++){
            Fish fish = new Fish(i, core);
            fishList.add(fish);
        }
        DataAccessor.setAllFishes(fishList);
    }

//    public void testThings(){
//        DataAccessor.getAllFishes().get(0).searchingAlgorithm();
//        DataAccessor.getAllFishes().get(0).swarmingAlgorithm();
//    }

    public void findReduct(boolean core){
        List<Attribute> reduct = new ArrayList<>();
        DataAccessor.setListOfReducts(new ArrayList<>());
        if (DataAccessor.isCalculatedReductInIteration()){
            System.out.println(DataAccessor.getElapsedTime());
            System.out.println(DataAccessor.getCurrentReduct().size());
            return;
        }
        DataAccessor.setCurrentReduct(DataAccessor.getAllAttributes());
        reduct.addAll(DataAccessor.getAllAttributes());
        reduct.remove(reduct.size()-1);
        double globalDegree = DataAccessor.getGlobalDependencyDegree();
        int iteration=0;
        int additional=0;
        dependency = fullDependencyDegree(reduct);
        long timeElapsed = (long) DataAccessor.getElapsedTime();
        while (iteration<=DataAccessor.getLoopLimit()){
            //System.out.println("Iter"+iteration);
            additional=1;
            initializeFish(core);
            do{
                if (additional>=DataAccessor.getCurrentReduct().size())
                    break;
                ExecutorService executor = Executors.newFixedThreadPool(DataAccessor.getAllFishes().size());
                long startTime = new Date().getTime();
                DataAccessor.getAllFishes().forEach(executor::execute);
                executor.shutdown();
                while (!executor.isTerminated()) {
                    int b=0;
                }
                long stopTime = new Date().getTime();
                timeElapsed = timeElapsed + (stopTime - startTime);
                additional++;
            }
            while(!evaluate());
            DataAccessor.getListOfReducts().add(DataAccessor.getCurrentReduct());
            iteration++;
            DataAccessor.setPerformedIterations(iteration);
            if (checkFruitlessSearches())
                break;
        }
        DataAccessor.setElapsedTime(((double) timeElapsed / ConstStrings.THOUSAND));
        System.out.println(DataAccessor.getElapsedTime());
        System.out.println(DataAccessor.getCurrentReduct().size());
    }

    public boolean evaluate(){
        boolean allFishFound=true;
        for (Fish fish : DataAccessor.getAllFishes()){
            if (!fish.hasReduct){
                if (fish.getAttributeList().size()<DataAccessor.getCurrentReduct().size()){
                    double dependencyFish = fish.updateDependencyDegree(fish.getAttributeList());
                    if (dependencyFish == dependency){
                        fish.setHasReduct(true);
                        fishReduct.clear();
                        fishReduct.addAll(fish.getAttributeList());
                        fishReductSize=fishReduct.size();
                        DataAccessor.setCurrentReduct(fishReduct);
                    }
                    else allFishFound=false;
                }
            }
        }
        return allFishFound;
    }

    private boolean checkFruitlessSearches() {
        if (DataAccessor.getListOfReducts().size() > DataAccessor.getFruitlessSearches()) {
            int performedIterations = DataAccessor.getPerformedIterations();
            int size = DataAccessor.getListOfReducts().get(performedIterations - ConstStrings.ONE).size();
            for (int i = 2; i <= DataAccessor.getFruitlessSearches(); i++) {
                if (DataAccessor.getListOfReducts().get(performedIterations - i - 1).size() != size) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public double calculateFitness(){
        double fitness = (DataAccessor.getFishAlphaRelevance() * dependency);
        double test = (double)(DataAccessor.getNonDecisionAttributesNumber()-DataAccessor.getCurrentReduct().size())/((double)DataAccessor.getNonDecisionAttributesNumber());
        fitness = fitness + DataAccessor.getFishBetaRelevance() * test;
        return fitness;
    }

    public double fullDependencyDegree(List<Attribute> attributes){
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


}
