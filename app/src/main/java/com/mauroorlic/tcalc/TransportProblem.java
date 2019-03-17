package com.mauroorlic.tcalc;

import java.util.ArrayList;

public class TransportProblem {
    int numOfDemands;
    int numOfSupplies;
    ArrayList<ResourceCell> demand = new ArrayList<>();
    ArrayList<ResourceCell> supply = new ArrayList<>();
    ArrayList<ArrayList<CostCell>> costTable = new ArrayList<>();

    public TransportProblem(ArrayList<Double> demand, ArrayList<Double> supply, ArrayList<ArrayList<Double>> costTable) {
        //entering raw demands into demand arraylist
        for (Double d : demand) {
            this.demand.add(new ResourceCell(d));
        }
        //entering raw suppliers into supply arraylist
        for (Double s : supply) {
            this.supply.add(new ResourceCell(s));
        }
        //entering raw costs into costTable
        for (ArrayList<Double> row : costTable) {

            this.costTable.add(new ArrayList<CostCell>());
            for (Double e : row) {
                this.costTable.get(this.costTable.size() - 1).add(new CostCell(e));
            }
        }
        numOfDemands = this.demand.size();
        numOfSupplies = this.supply.size();

        closeProblem();
    }
    public TransportProblem(TransportProblem transportProblem){
        this.numOfDemands = transportProblem.numOfDemands;
        this.numOfSupplies = transportProblem.numOfSupplies;
        this.demand = transportProblem.demand;
        this.supply = transportProblem.supply;
        this.costTable = transportProblem.costTable;
    }

    public int getNumOfDemands() {
        return numOfDemands;
    }

    public int getNumOfSupplies() {
        return numOfSupplies;
    }

    public Double getTotalCost() {
        double totalCost = 0.0;
        for (ArrayList<CostCell> row : costTable) {
            for (CostCell e : row) {
                totalCost += e.cost * e.alloted;
            }
        }
        return totalCost;
    }

    public void closeProblem() {
        Double totalDemand = 0.0;
        Double totalSupply = 0.0;
        for (ResourceCell d : demand) {
            totalDemand += d.getTotal();
        }
        for (ResourceCell s : supply) {
            totalSupply += s.getTotal();
        }
        if (totalDemand == totalSupply) {
            return;
        } else if (totalDemand > totalSupply) {
            numOfSupplies += 1;
            supply.add(new ResourceCell(totalDemand - totalSupply));

            costTable.add(new ArrayList<CostCell>());
            for (int i = 0; i < supply.size(); i++) {
                costTable.get(costTable.size() - 1).add(new CostCell(0.0));
            }

        } else if (totalDemand < totalSupply) {
            numOfDemands += 1;
            demand.add(new ResourceCell(totalSupply - totalDemand));

            for (ArrayList<CostCell> row : costTable) {
                row.add(new CostCell(0.0));
            }
        }

    }

    public void clearSolution() {
        for (ResourceCell e : supply) {
            e.remaining = e.total;
        }
        for (ResourceCell e : demand) {
            e.remaining = e.total;
        }
        for (ArrayList<CostCell> row : costTable) {
            for (CostCell e : row) {
                e.alloted = 0.0;
            }
        }
    }

    public void initialNorthWest(Boolean optimizeMODI, Boolean optimizeSteppingStone) {
        clearSolution();
        closeProblem();
        int currentDemand = 0;
        int currentSupply = 0;
        Double alloted = 0.0;
        while (currentDemand<numOfDemands && currentSupply<numOfSupplies) {

            alloted = Math.min(demand.get(currentDemand).remaining, supply.get(currentSupply).remaining);
            costTable.get(currentSupply).get(currentDemand).setAlloted(alloted);

            supply.get(currentSupply).remaining -= alloted;
            demand.get(currentDemand).remaining -= alloted;

            int counter = 0;
            if (supply.get(currentSupply).remaining == 0.0) {
                currentSupply += 1;
            }
            if (demand.get(currentDemand).remaining == 0.0) {
                currentDemand += 1;
            }
        }


        if (optimizeMODI) {
            optimizeMODI();
        }
        if (optimizeSteppingStone) {
            optimizeSteppingStone();
        }

    }

    public void initialLeastCost(Boolean optimizeMODI, Boolean optimizeSteppingStone) {


        if (optimizeMODI) {
            optimizeMODI();
        }
        if (optimizeSteppingStone) {
            optimizeSteppingStone();
        }
    }

    public void initialVogel(Boolean optimizeMODI, Boolean optimizeSteppingStone) {


        if (optimizeMODI) {
            optimizeMODI();
        }
        if (optimizeSteppingStone) {
            optimizeSteppingStone();
        }
    }

    //takes an empty cell, scans for all occupied cells on same row, for each cell found, scan for other occupied cells in same column, for each cell found, scan for other occupied cells in same row... if a scanned cell is initial, close loop
    //always check if selected cells are already in the list, no more than 2 selected cells in same row/column
    private void optimizeMODI() {

        Double[][] OpportunityCost = new Double[supply.size()][demand.size()];

//        CostCell[][] costMatrix = new CostCell[supply.size()][demand.size()];
//        for(int i=0; i<supply.size();i++){
//            for(int j=0; j<demand.size(); j++){
//                costMatrix[i][j] = costTable.get(i).get(j);
//            }
//        }

        for(int i =0; i<supply.size();i++){
            for(int j=0;j<demand.size();j++){
                if(costTable.get(i).get(j).alloted==0.0){



                }
            }
        }
    }

    public void optimizeSteppingStone() {

    }
}
