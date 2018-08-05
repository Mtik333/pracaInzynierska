package data.fishsearch;

import data.DataAccessor;
import data.roughsets.Attribute;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

public class FishLogic {

    public void initializeFish(){
        List<Fish> fishList = new ArrayList<>();
        for (int i=0; i< DataAccessor.getFishNumber(); i++){
            Fish fish = new Fish(i);
            fishList.add(fish);
        }
        DataAccessor.setAllFishes(fishList);
        //DataAccessor.updateCenterFish();
        DataAccessor.hammingDistance(fishList.get(0), fishList.get(1));
    }

    public void calculateFitness(Fish fish){
        double fitness = (DataAccessor.getFishAlphaRelevance() * fish.updateDependencyDegree(fish.getAttributeList()));
        double test = (double)(DataAccessor.getNonDecisionAttributesNumber()-fish.returnTrueSize())/((double)DataAccessor.getNonDecisionAttributesNumber());
        fitness = fitness + DataAccessor.getFishBetaRelevance() * test;
    }

    public void testThings(){
        DataAccessor.getAllFishes().get(0).searchingAlgorithm();
        DataAccessor.getAllFishes().get(0).swarmingAlgorithm();
    }

    public void findReduct(){
        List<Attribute> reduct = new ArrayList<>();
        reduct.addAll(DataAccessor.getAllAttributes());
        reduct.remove(reduct.size()-1);
        double globalDegree = DataAccessor.getGlobalDependencyDegree();
        int iteration=0;
        while (iteration<=DataAccessor.getFishMaxCycle()){

        }
    }



}
