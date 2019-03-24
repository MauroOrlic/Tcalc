package com.mauroorlic.tcalc;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TransportProblem {
    private static final String TAG = "TransportProblem";
    int numOfDemands;
    int numOfSupplies;
    List<ResourceCell> demand = new ArrayList<>();
    List<ResourceCell> supply = new ArrayList<>();
    List<ArrayList<CostCell>> costTable = new ArrayList<>();
    Double totalCost;
    int dummy = 0;

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

        this.totalCost = 0.0;
    }

    public TransportProblem(TransportProblem transportProblem) {
        this.numOfDemands = transportProblem.numOfDemands;
        this.numOfSupplies = transportProblem.numOfSupplies;
        this.demand = transportProblem.demand;
        this.supply = transportProblem.supply;
        this.costTable = transportProblem.costTable;
        this.totalCost = 0.0;
    }

    public int getNumOfDemands() {
        return numOfDemands;
    }

    public int getNumOfSupplies() {
        return numOfSupplies;
    }

    public void getTotalCost() {
        double totalCost = 0.0;
        for (ArrayList<CostCell> row : costTable) {
            for (CostCell e : row) {
                totalCost += e.cost * e.alloted;
            }
        }
        this.totalCost = totalCost;
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
            dummy = 1;

            costTable.add(new ArrayList<CostCell>());
            for (int i = 0; i < supply.size(); i++) {
                costTable.get(demand.size() - 1).add(new CostCell(0.0, costTable.size() - 1, i));
            }

        } else if (totalDemand < totalSupply) {
            numOfDemands += 1;
            demand.add(new ResourceCell(totalSupply - totalDemand));
            dummy = -1;

            /*for (ArrayList<CostCell> row : costTable) {
                row.add(new CostCell(0.0));
            }*/
            for (int i = 0; i < numOfSupplies; i++) {
                costTable.get(i).add(new CostCell(0.0, i, numOfDemands - 1));
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
        while (currentDemand < numOfDemands && currentSupply < numOfSupplies) {

            alloted = Math.min(demand.get(currentDemand).remaining, supply.get(currentSupply).remaining);
            costTable.get(currentSupply).get(currentDemand).alloted = alloted; //for certain problems throws ArrayIndexOutOfBounds exception, not sure why

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
        getTotalCost();
    }

    public void initialLeastCost(Boolean optimizeMODI, Boolean optimizeSteppingStone) {
        clearSolution();
        balanceProblem();

        List<CostCell> costCellList = new ArrayList<>();
        for (ArrayList<CostCell> row : costTable) {
            costCellList.addAll(row);
        }
        Comparator<CostCell> compareCostCellLC = (CostCell o1, CostCell o2) -> {
            int difference = o1.cost.compareTo(o2.cost);
            if (difference == 0) {
                Double o1MaxCapacity = Math.min(supply.get(o1.positionRow).remaining, demand.get(o1.positionColumn).remaining);
                Double o2MaxCapacity = Math.min(supply.get(o2.positionRow).remaining, demand.get(o2.positionColumn).remaining);
                difference = -o1MaxCapacity.compareTo(o2MaxCapacity);
                if (difference == 0) {
                    Double o1RemainingSupply = supply.get(o1.positionRow).remaining - o1MaxCapacity;
                    Double o2RemainingSupply = supply.get(o2.positionRow).remaining - o2MaxCapacity;
                    difference = -o1RemainingSupply.compareTo(o2RemainingSupply);
                    if (difference == 0) {
                        difference = o1.positionRow.compareTo(o2.positionRow);
                        if (difference == 0) {
                            difference = o1.positionColumn.compareTo(o2.positionColumn);
                        }
                    }
                }
            }
            return difference;
        };
        costCellList.sort(compareCostCellLC);
        Double alloted = 0.0;

        while (costCellList.size() != 0) {
            CostCell costCell = costCellList.get(0);
            if (supply.get(costCell.positionRow).remaining != 0.0 && demand.get(costCell.positionColumn).remaining != 0.0) {
                alloted = Math.min(supply.get(costCell.positionRow).remaining, demand.get(costCell.positionColumn).remaining);
                costCell.alloted = alloted;
                supply.get(costCell.positionRow).remaining -= alloted;
                demand.get(costCell.positionColumn).remaining -= alloted;
            }
            costCellList.remove(0);
            costCellList.sort(compareCostCellLC);
        }


        if (optimizeMODI) {
            optimizeMODI();
        }
        if (optimizeSteppingStone) {
            optimizeSteppingStone();
        }
        getTotalCost();
    }

    //DOESNT WORK dont know if I will be able to fix
    public void initialVogel(Boolean optimizeMODI, Boolean optimizeSteppingStone) {
        clearSolution();
        balanceProblem();

        Boolean[] supplyUsed = new Boolean[supply.size()];
        Boolean[] demandUsed = new Boolean[demand.size()];
        Arrays.fill(supplyUsed, false);
        Arrays.fill(demandUsed, false);
        Double[] supplyPenalty = new Double[supply.size()];
        Double[] demandPenalty = new Double[demand.size()];

        Comparator<CostCell> compareCostCellVogel = (CostCell o1, CostCell o2) -> {
            int difference = o1.cost.compareTo(o2.cost);
            if (difference == 0) {
                Double o1MaxCapacity = Math.min(supply.get(o1.positionRow).remaining, demand.get(o1.positionColumn).remaining);
                Double o2MaxCapacity = Math.min(supply.get(o2.positionRow).remaining, demand.get(o2.positionColumn).remaining);
                difference = -o1MaxCapacity.compareTo(o2MaxCapacity);
                if (difference == 0) {
                    Double o1RemainingSupply = supply.get(o1.positionRow).remaining - o1MaxCapacity;
                    Double o2RemainingSupply = supply.get(o2.positionRow).remaining - o2MaxCapacity;
                    difference = -o1RemainingSupply.compareTo(o2RemainingSupply);
                    if (difference == 0) {
                        difference = o1.positionRow.compareTo(o2.positionRow);
                        if (difference == 0) {
                            difference = o1.positionColumn.compareTo(o2.positionColumn);
                        }
                    }
                }
            }
            return difference;
        };

        int breakCounter = 0;
        while (containsFalse(supplyUsed) && containsFalse(demandUsed)) {

            List<Double> costGroup = new ArrayList<>();
            Arrays.fill(supplyPenalty, 0.0);
            Arrays.fill(demandPenalty, 0.0);

            //getting penalties for supply
            for (int i = 0; i < supply.size(); i++) {
                if (!supplyUsed[i]) {
                    costGroup.clear();
                    for (int j = 0; j < demand.size(); j++) {
                        if(!demandUsed[j]) {
                            costGroup.add(costTable.get(i).get(j).cost);
                        }
                    }
                    supplyPenalty[i] = getGroupPenalty(costGroup);
                }
            }
            //getting penalties for demand
            for (int j = 0; j < demand.size(); j++) {
                if (!demandUsed[j]) {
                    costGroup.clear();
                    for (int i = 0; i < supply.size(); i++) {
                        if(!supplyUsed[i]) {
                            costGroup.add(costTable.get(i).get(j).cost);
                        }
                    }
                    demandPenalty[j] = getGroupPenalty(costGroup);
                }
            }

            //find the maximum penalty in rows/columns
            Double maxPenalty = 0.0;
            for (int i = 0; i < supply.size(); i++) {
                maxPenalty = Math.max(maxPenalty, supplyPenalty[i]);
            }
            for (int i = 0; i < demand.size(); i++) {
                maxPenalty = Math.max(maxPenalty, demandPenalty[i]);
            }

            //getting all cells that are part a group that has penalty equal to maxPenalty
            List<CostCell> allocationCandidates = new ArrayList<>();
            for (int i = 0; i < supply.size(); i++) {
                for (int j = 0; j < demand.size(); j++) {
                    if ((!supplyUsed[i] && !demandUsed[j]) && (supplyPenalty[i].equals(maxPenalty) || demandPenalty[j].equals(maxPenalty))) {
                        allocationCandidates.add(costTable.get(i).get(j));
                    }
                }
            }
            //sorting cells from most to least desirable
            if(allocationCandidates.size()>1) {
                allocationCandidates.sort(compareCostCellVogel);
            }
            CostCell chosenCostCell = allocationCandidates.get(0);

            //allocating transport to chosen cell
            Double supplyMaxAllocation =supply.get(chosenCostCell.positionRow).remaining;
            Double demandMaxAllocation=demand.get(chosenCostCell.positionColumn).remaining;
            Double allocationAmount = Math.min(supplyMaxAllocation,demandMaxAllocation);
            chosenCostCell.alloted = allocationAmount;
            supply.get(chosenCostCell.positionRow).remaining = supplyMaxAllocation - allocationAmount;
            demand.get(chosenCostCell.positionColumn).remaining = demandMaxAllocation - allocationAmount;


            // marking supply as used if emptied
            if (supply.get(chosenCostCell.positionRow).remaining.equals(0.0)){
                supplyUsed[chosenCostCell.positionRow] = true;
            }
            //marking demand as used if emptied
            if(demand.get(chosenCostCell.positionColumn).remaining.equals(0.0)){
                demandUsed[chosenCostCell.positionColumn] = true;
            }
            if(breakCounter>15){
                break;
            }
            //breakCounter++;
        }


        if (optimizeMODI) {
            optimizeMODI();
        }
        if (optimizeSteppingStone) {
            optimizeSteppingStone();
        }
        getTotalCost();
    }
    private boolean containsFalse(Boolean[] a){
        for(int i=0; i<a.length;i++){
            if(!a[i]){
                return true;
            }
        }
        return false;
    }

    private Double getGroupPenalty(List<Double> list) {
        while(list.size()<2){
            list.add(0.0);
        }
        Collections.sort(list);
        return list.get(1) - list.get(0);
    }

    private void optimizeMODI() {
        fixDegenerateCase();
        Double[] u = new Double[numOfSupplies];
        Double[] v = new Double[numOfDemands];
        Double[][] costMatrix = new Double[numOfSupplies][numOfDemands];
        Double[][] costMatrixInverted = new Double[numOfSupplies][numOfDemands];
        //reading costs of alloted cells
        for (int i = 0; i < numOfSupplies; i++) {
            for (int j = 0; j < numOfDemands; j++) {
                if (costTable.get(i).get(j).alloted != 0.0) {
                    costMatrix[i][j] = costTable.get(i).get(j).cost;
                }
            }
        }

        List<Boolean> check = new ArrayList<>();

        u[0] = 0.0;
        boolean tryAgain;
        do {
            tryAgain = false;

            for (int i = 0; i < u.length; i++) {
                for (int j = 0; j < v.length; j++) {
                    if (costMatrix[i][j] != null) {
                        if (u[i] == null && v[j] != null) {
                            u[i] = costMatrix[i][j] - v[j];

                        } else if (v[j] == null && u[i] != null) {
                            v[j] = costMatrix[i][j] - u[i];
                        } else if (v[j] == null && u[i] == null) {
                            tryAgain = true;
                        }


                    }
                }
            }
        } while (tryAgain);

        //building inverted cost matrix
        for (int i = 0; i < u.length; i++) {
            for (int j = 0; j < v.length; j++) {
                if (costMatrix[i][j] == null) {

                    costMatrixInverted[i][j] = costTable.get(i).get(j).cost - (v[j] + u[i]);
                }
            }
        }
        //search for CostCell which can reduce the total transport cost
        boolean breakOut = false;

        for (int i = 0; i < u.length; i++) {
            for (int j = 0; j < v.length; j++) {
                if (costMatrixInverted[i][j] != null && costMatrixInverted[i][j] < 0.0) {
                    //stepping stone rotate
                    CostCell newSolutionCell = costTable.get(i).get(j);
                    CostCell[] path = getClosedPath(newSolutionCell);

                    double lowestQuantity = Integer.MAX_VALUE;
                    boolean plus = true;

                    for (CostCell currentCostCell : path) {
                        if (!plus) {
                            if (currentCostCell.alloted < lowestQuantity) {
                                lowestQuantity = currentCostCell.alloted;
                            }
                        }
                        plus = !plus;
                    }
                    plus = true;

                    for (CostCell s : path) {
                        if (plus) {
                            s.alloted += lowestQuantity;
                        } else {
                            s.alloted -= lowestQuantity;
                        }
                        plus = !plus;
                    }
                    optimizeMODI();
                    breakOut = true;
                }

                if (breakOut) {
                    break;
                }
            }
            if (breakOut) {
                break;
            }
        }
    }

    public void optimizeSteppingStone() {
        double maxReduction = 0;
        CostCell[] move = null;
        CostCell leaving = null;
        fixDegenerateCase();

        for (ArrayList<CostCell> row : costTable) {
            for (CostCell costCell : row) {
                if (costCell.alloted != 0.0) {
                    continue;
                }
                CostCell trial = new CostCell(costCell.cost, 0.0, costCell.positionRow, costCell.positionColumn);
                CostCell[] path = getClosedPath(trial);

                double reduction = 0;
                double lowestQuanity = Integer.MAX_VALUE;
                CostCell leavingCandidate = null;

                boolean plus = true;

                for (CostCell s : path) {
                    if (plus) {
                        reduction += s.cost;
                    } else {
                        reduction -= s.cost;
                        if (s.alloted < lowestQuanity) {
                            leavingCandidate = s;
                            lowestQuanity = s.alloted;
                        }
                    }
                    plus = !plus;
                }
                if (reduction < maxReduction) {
                    move = path;
                    leaving = leavingCandidate;
                    maxReduction = reduction;
                }
            }
        }
        if (move != null) {
            double q = leaving.alloted;
            boolean plus = true;
            for (CostCell s : move) {
                s.alloted += plus ? q : -q;
                //costTable.get(s.positionRow).set(s.positionColumn, s.alloted == 0 ? new CostCell(0.0,0.0, s.positionRow, s.positionColumn):s);
                costTable.get(s.positionRow).get(s.positionColumn).alloted = s.alloted;
                costTable.get(s.positionRow).get(s.positionColumn).cost = s.cost;
                plus = !plus;
            }
            optimizeSteppingStone();
        }
    }

    private CostCell[] getClosedPath(CostCell startingCostCell) {
        final List<CostCell> path = matrixToList();
        path.add(0, startingCostCell);
        while (path.removeIf(e -> {
            CostCell[] neighbors = getNeighbors(e, path);
            return neighbors[0] == null || neighbors[1] == null;
        })) ;

        CostCell[] stones = path.toArray(new CostCell[path.size()]);
        CostCell previous = startingCostCell;
        for (int i = 0; i < stones.length; i++) {
            stones[i] = previous;
            previous = getNeighbors(previous, path)[i % 2];
        }
        return stones;
    }

    private List<CostCell> matrixToList() {
        List<CostCell> costList = new ArrayList<>();
        for (ArrayList<CostCell> row : costTable) {
            for (CostCell costCell : row) {
                if (costCell.alloted != 0.0) {
                    costList.add(costCell);
                }
            }
        }
        return costList;
    }

    private CostCell[] getNeighbors(CostCell startingCostCell, List<CostCell> list) {
        CostCell[] neighbors = new CostCell[2];
        for (CostCell currentCostCell : list) {
            if (currentCostCell != startingCostCell) {
                if (currentCostCell.positionRow.equals(startingCostCell.positionRow) && neighbors[0] == null) {
                    neighbors[0] = currentCostCell;
                } else if (currentCostCell.positionColumn.equals(startingCostCell.positionColumn) && neighbors[1] == null) {
                    neighbors[1] = currentCostCell;
                }
                if (neighbors[0] != null && neighbors[1] != null) {
                    break;
                }
            }
        }

        return neighbors;
    }

    private void fixDegenerateCase() {
        final Double epsilon = Double.MIN_VALUE;

        int allotedCellCount = 0;
        for (ArrayList<CostCell> row : costTable) {
            for (CostCell costCell : row) {
                if (costCell.alloted != 0.0) {
                    allotedCellCount++;
                }
            }
        }
        if (allotedCellCount < numOfSupplies + numOfDemands - 1) {
            for (List<CostCell> row : costTable) {
                for (CostCell costCell : row) {
                    if (costCell.alloted == 0.0 && getClosedPath(costCell).length == 0) {
                        costCell.alloted = epsilon;
                        return;
                    }
                }
            }
        }
    }
}
