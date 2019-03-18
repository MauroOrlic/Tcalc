package com.mauroorlic.tcalc;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TransportProblem {
    int numOfDemands;
    int numOfSupplies;
    List<ResourceCell> demand = new ArrayList<>();
    List<ResourceCell> supply = new ArrayList<>();
    List<ArrayList<CostCell>> costTable = new ArrayList<>();

    public TransportProblem(List<Double> demand, List<Double> supply, List<List<Double>> costTable) {
        //entering raw demands into demand arraylist
        for (Double d : demand) {
            this.demand.add(new ResourceCell(d));
        }
        //entering raw suppliers into supply arraylist
        for (Double s : supply) {
            this.supply.add(new ResourceCell(s));
        }
        //entering raw costs into costTable
        Integer rowCount = 0;
        Integer colCount = 0;
        for (List<Double> row : costTable) {
            colCount = 0;

            this.costTable.add(new ArrayList<CostCell>());
            for (Double inputCost : row) {
                this.costTable.get(this.costTable.size() - 1).add(new CostCell(inputCost, rowCount, colCount));
                colCount++;
            }
            rowCount++;
        }
        numOfDemands = this.demand.size();
        numOfSupplies = this.supply.size();

        balanceProblem();
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

    public void balanceProblem() {
        Double totalDemand = 0.0;
        Double totalSupply = 0.0;
        for (ResourceCell d : demand) {
            totalDemand += d.getTotal();
        }
        for (ResourceCell s : supply) {
            totalSupply += s.getTotal();
        }
        if (totalDemand.equals(totalSupply)) {
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
        balanceProblem();
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

    }

    public void optimizeSteppingStone() {

    }
    private List<CostCell> getClosedPath(CostCell s){
        List<CostCell> path = new ArrayList<CostCell>();

        return  path;
    }
    private List<CostCell> getNeighbors(CostCell c, ArrayList<CostCell> list){
        List<CostCell> neighbors = new ArrayList<CostCell>();

        return neighbors;
    }
    private void fixDegenerateCase(){
        final Double epsilon = Double.MIN_VALUE;

        Integer allotedCellCount = 0;
        for(ArrayList<CostCell> row : costTable){
            for(CostCell costCell : row){
                if(costCell.alloted==0.0){
                    allotedCellCount++;
                }
            }
        }
        if(allotedCellCount < numOfSupplies+numOfDemands-1){
            for(List<CostCell> row : costTable){
                for(CostCell costCell : row){
                    if(costCell.alloted==0.0 && getClosedPath(costCell).size()==0){
                        costCell.alloted = epsilon;
                        //TODO check if the return bellow actually messes up the solution
                        return;
                    }
                }
            }
        }
    }
}
