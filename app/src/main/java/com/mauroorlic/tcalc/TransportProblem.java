package com.mauroorlic.tcalc;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TransportProblem {
    private static final String TAG = "TransportProblem";
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
            Log.d("activated", "optimizeSteppingStone launched");
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
        double maxReduction = 0;
        CostCell[] move = null;
        CostCell leaving = null;
        fixDegenerateCase();

        for(ArrayList<CostCell> row : costTable){
            for(CostCell costCell : row){
                if(costCell.alloted !=0.0){
                    continue;
                }
                CostCell trial = new CostCell(costCell.cost,0.0,costCell.positionRow, costCell.positionColumn);
                CostCell[] path = getClosedPath(trial);

                double reduction = 0;
                double lowestQuanity = Integer.MAX_VALUE;
                CostCell leavingCandidate = null;

                boolean plus = true;

                for(CostCell s : path){
                    if(plus) {
                        reduction += s.cost;
                    }
                    else{
                        reduction -= s.cost;
                        if(s.alloted<lowestQuanity){
                            leavingCandidate = s;
                            lowestQuanity = s.alloted;
                        }
                    }
                    plus = !plus;
                }
                if(reduction<maxReduction){
                    move = path;
                    leaving = leavingCandidate;
                    maxReduction = reduction;
                }
            }
        }
        if(move!=null){
            double q = leaving.alloted;
            boolean plus = true;
            for(CostCell s : move){
                s.alloted+= plus ? q: -q;
                //costTable.get(s.positionRow).set(s.positionColumn, s.alloted == 0 ? new CostCell(0.0,0.0, s.positionRow, s.positionColumn):s);
                costTable.get(s.positionRow).get(s.positionColumn).alloted = s.alloted;
                costTable.get(s.positionRow).get(s.positionColumn).cost = s.cost;
                plus = !plus;
            }
            optimizeSteppingStone();
        }
    }
    private CostCell[] getClosedPath(CostCell startingCostCell){
        final List<CostCell> path = matrixToList();
        path.add(0, startingCostCell);
        //(path.removeIf(e -> {Shipment[] nbrs = getNeighbors(e, path);
        while (path.removeIf(e -> {
            CostCell[] neighbors = getNeighbors(e,path);
            return  neighbors[0] == null || neighbors[1] ==null;
        }));

        CostCell[] stones = path.toArray(new CostCell[path.size()]);
        CostCell previous = startingCostCell;
        for(int i=0; i< stones.length;i++){
            //Log.d(TAG, "getClosedPath: "+i+ "|"+previous);
            stones[i] = previous;
            previous = getNeighbors(previous,path)[i % 2];
        }
        return  stones;
    }
    private List<CostCell> matrixToList(){
        List<CostCell> costList = new ArrayList<>();
        for(ArrayList<CostCell> row :costTable){
            for(CostCell costCell : row){
                if(costCell.alloted!=0.0){
                    costList.add(costCell);
                }
            }
        }
        return costList;
    }
    private CostCell[] getNeighbors(CostCell startingCostCell, List<CostCell> list){
        //Log.d(TAG, "getNeighbors: "+"||"+startingCostCell+list.size());
        CostCell[] neighbors = new CostCell[2];
        for(CostCell currentCostCell : list){
            //TODO implement object Comparator or ??operator overloading??
            /*if(currentCostCell.cost != startingCostCell.cost && currentCostCell.alloted != startingCostCell.alloted && currentCostCell.positionColumn != startingCostCell.positionColumn && currentCostCell.positionRow != startingCostCell.positionRow){
              */
            if(currentCostCell != startingCostCell){
                if(currentCostCell.positionRow.equals(startingCostCell.positionRow) && neighbors[0]==null){
                    neighbors[0] = currentCostCell;
                }
                else if(currentCostCell.positionColumn.equals(startingCostCell.positionColumn)&& neighbors[1]==null){
                    neighbors[1]=currentCostCell;
                }
                if(neighbors[0]!=null && neighbors[1]!= null){
                    break;
                }
            }
        }

        return neighbors;
    }
    private void fixDegenerateCase(){
        final Double epsilon = Double.MIN_VALUE;

        int allotedCellCount = 0;
        for(ArrayList<CostCell> row : costTable){
            for(CostCell costCell : row){
                if(costCell.alloted!=0.0){
                    allotedCellCount++;
                }
            }
        }
        if(allotedCellCount < numOfSupplies+numOfDemands-1){
            for(List<CostCell> row : costTable){
                for(CostCell costCell : row){
                    if(costCell.alloted==0.0 && getClosedPath(costCell).length==0){
                        costCell.alloted = epsilon;
                        //TODO check if the return bellow actually messes up the solution
                        return;
                    }
                }
            }
        }
    }
}
