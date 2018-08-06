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

    public void initializeFish(){
        fishReduct=new ArrayList<>();
        fishReductSize=Integer.MAX_VALUE;
        fishReduct.addAll(DataAccessor.getAllAttributes());
        List<Fish> fishList = new ArrayList<>();
        for (int i=0; i< DataAccessor.getFishNumber(); i++){
            Fish fish = new Fish(i);
            fishList.add(fish);
        }
        DataAccessor.setAllFishes(fishList);
    }

//    public void testThings(){
//        DataAccessor.getAllFishes().get(0).searchingAlgorithm();
//        DataAccessor.getAllFishes().get(0).swarmingAlgorithm();
//    }

    public void findReduct(){
        List<Attribute> reduct = new ArrayList<>();
        reduct.addAll(DataAccessor.getAllAttributes());
        double globalDegree = DataAccessor.getGlobalDependencyDegree();
        int iteration=0;
        int additional=0;
        dependency = fullDependencyDegree(DataAccessor.getAllAttributes());
        long timeElapsed = (long) DataAccessor.getElapsedTime();
        while (iteration<=DataAccessor.getFishMaxCycle()){
            additional=0;
            initializeFish();
            do{
                if (additional>DataAccessor.getAllAttributes().size())
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
            iteration++;
        }
        DataAccessor.setElapsedTime(((double) timeElapsed / ConstStrings.THOUSAND));
        System.out.println(DataAccessor.getElapsedTime());
        System.out.println(DataAccessor.getTemporaryReduct().size());
    }

    public boolean evaluate(){
        boolean allFishFound=true;
        for (Fish fish : DataAccessor.getAllFishes()){
            if (!fish.hasReduct){
                double dependencyFish = fish.updateDependencyDegree(fish.getAttributeList());
                if (dependencyFish == dependency){
                    fish.setHasReduct(true);
                    if (fish.getAttributeList().size()<fishReductSize){
                        fishReduct.clear();
                        fishReduct.addAll(fish.getAttributeList());
                        fishReductSize=fishReduct.size();
                        DataAccessor.setTemporaryReduct(fishReduct);
                    }
                }
                else allFishFound=false;
            }
        }
        return allFishFound;
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
