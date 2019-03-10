package com.mauroorlic.tcalc;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TransportProblem {
    int numOfDemands;
    int numOfSupplies;
    ArrayList<ResourceCell> demand;
    ArrayList<ResourceCell> supply;
    ArrayList<ArrayList<CostCell>> costTable;
    boolean isDegenerated;

    public TransportProblem(ArrayList<Double> demand, ArrayList<Double> supply, ArrayList<ArrayList<Double>> costTable) {
        //entering raw demands into demand arraylist
        for(Double d : demand){
            this.demand.add(new ResourceCell(d));
        }
        //entering raw suppliers into supply arraylist
        for(Double s: supply){
            this.supply.add(new ResourceCell(s));
        }
        //entering raw costs into costTable
        for(ArrayList<Double> row : costTable){

            this.costTable.add(new ArrayList<CostCell>());
            for(Double e : row){
                this.costTable.get(this.costTable.size()-1).add(new CostCell(e));
            }
        }
        numOfDemands = this.demand.size();
        numOfSupplies = this.supply.size();
    }
    public void InitialNorthWest(){

    }
}
