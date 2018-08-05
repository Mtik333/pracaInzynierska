package data.fishsearch;

import data.DataAccessor;
import data.roughsets.Attribute;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

public class FishLogic {

    public List<Attribute> fishReduct=new ArrayList<>();
    public int fishReductSize=0;

    public void initializeFish(){
        fishReduct.addAll(DataAccessor.getAllAttributes());
        List<Fish> fishList = new ArrayList<>();
        for (int i=0; i< DataAccessor.getFishNumber(); i++){
            Fish fish = new Fish(i);
            fishList.add(fish);
        }
        DataAccessor.setAllFishes(fishList);
    }

    public void calculateFitness(Fish fish){
        double fitness = (DataAccessor.getFishAlphaRelevance() * fish.updateDependencyDegree(fish.getAttributeList()));
        double test = (double)(DataAccessor.getNonDecisionAttributesNumber()-fish.returnTrueSize())/((double)DataAccessor.getNonDecisionAttributesNumber());
        fitness = fitness + DataAccessor.getFishBetaRelevance() * test;
    }

//    public void testThings(){
//        DataAccessor.getAllFishes().get(0).searchingAlgorithm();
//        DataAccessor.getAllFishes().get(0).swarmingAlgorithm();
//    }

    public void findReduct(){
        List<Attribute> reduct = new ArrayList<>();
        reduct.addAll(DataAccessor.getAllAttributes());
        reduct.remove(reduct.size()-1);
        double globalDegree = DataAccessor.getGlobalDependencyDegree();
        int iteration=0;
        int additional=0;
        while (iteration<=DataAccessor.getFishMaxCycle()){
            initializeFish();
            do{
                if (additional==DataAccessor.getAllAttributes().size())
                    break;
                for (Fish fish : DataAccessor.getAllFishes()){
                    if (!fish.hasReduct){
                        fish.run();
                    }
                }
                additional++;
            }
            while(!evaluate());
            iteration++;
        }
        System.out.println(fishReduct);
        System.out.println(fishReductSize);
    }

    public boolean evaluate(){
        boolean allFishFound=true;
        for (Fish fish : DataAccessor.getAllFishes()){
            if (fish.updateDependencyDegree(fish.getAttributeList()) == 1){
                fish.setHasReduct(true);
                if (fish.getAttributeList().size()<fishReduct.size()){
                    fishReduct.clear();
                    fishReduct.addAll(fish.getAttributeList());
                    fishReductSize=fishReduct.size();
                }
            }
            else allFishFound=false;
        }
        return allFishFound;
    }



}
