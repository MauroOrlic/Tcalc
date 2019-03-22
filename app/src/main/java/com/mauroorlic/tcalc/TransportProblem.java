package com.mauroorlic.tcalc;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class TransportProblem {
    private static final String TAG = "TransportProblem";
    int numOfDemands;
    int numOfSupplies;
    List<ResourceCell> demand = new ArrayList<>();
    List<ResourceCell> supply = new ArrayList<>();
    List<ArrayList<CostCell>> costTable = new ArrayList<>();
    Double totalCost;

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
    public TransportProblem(TransportProblem transportProblem){
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

            costTable.add(new ArrayList<CostCell>());
            for (int i = 0; i < supply.size(); i++) {
                costTable.get(demand.size() - 1).add(new CostCell(0.0,costTable.size()-1,i));
            }

        } else if (totalDemand < totalSupply) {
            numOfDemands += 1;
            demand.add(new ResourceCell(totalSupply - totalDemand));

            /*for (ArrayList<CostCell> row : costTable) {
                row.add(new CostCell(0.0));
            }*/
            for(int i=0; i< numOfSupplies;i++){
                costTable.get(i).add(new CostCell(0.0, i, numOfDemands-1));
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
        getTotalCost();
    }

    public void initialLeastCost(Boolean optimizeMODI, Boolean optimizeSteppingStone) {
        clearSolution();
        balanceProblem();

        List<CostCell> costCellList = new ArrayList<>();
        for(ArrayList<CostCell> row : costTable){
            costCellList.addAll(row);
        }
        Comparator<CostCell> compareCostCellLC = (CostCell o1, CostCell o2)-> {
            int difference = o1.cost.compareTo(o2.cost);
            if(difference==0){
                Double o1MaxCapacity = Math.min(supply.get(o1.positionRow).remaining, demand.get(o1.positionColumn).remaining);
                Double o2MaxCapacity = Math.min(supply.get(o2.positionRow).remaining, demand.get(o2.positionColumn).remaining);
                difference = -o1MaxCapacity.compareTo(o2MaxCapacity);
                if(difference==0){
                    Double o1RemainingSupply = supply.get(o1.positionRow).remaining - o1MaxCapacity;
                    Double o2RemainingSupply = supply.get(o2.positionRow).remaining - o2MaxCapacity;
                    difference = -o1RemainingSupply.compareTo(o2RemainingSupply);
                    if(difference==0) {
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
        //TODO implement some sort of sorting algorithm for costCellList by cost. get it? some SORT of algorithm? :D
        Double alloted = 0.0;

            while(costCellList.size()!=0){
                CostCell costCell = costCellList.get(0);
                if(supply.get(costCell.positionRow).remaining!=0.0 && demand.get(costCell.positionColumn).remaining!=0.0){
                    alloted = Math.min(supply.get(costCell.positionRow).remaining, demand.get(costCell.positionColumn).remaining);
                    costCell.alloted = alloted;
                    supply.get(costCell.positionRow).remaining -=alloted;
                    demand.get(costCell.positionColumn).remaining -=alloted;
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

        List<ResourceCell> vSupply = new ArrayList<>();
        for(int i=0; i<numOfSupplies; i++){
            vSupply.add(new ResourceCell(supply.get(i)));
        }

        List<ResourceCell> vDemand = new ArrayList<>();
        for(int i=0; i<numOfDemands;i++){
            vDemand.add(new ResourceCell(demand.get(i)));
        }
        List<List<CostCell>> vCostTable = new ArrayList<>();
        for(int i=0; i<numOfSupplies; i++){
            vCostTable.add(new ArrayList<>());
            for(int j=0; j< numOfDemands;j++){
                vCostTable.get(i).add(new CostCell(costTable.get(i).get(j)));
            }
        }

        List<Double> supplyMaxPenalty = new ArrayList<>();
        List<Double> demandMaxPenalty = new ArrayList<>();



        List<CostCell> candidateCells = new ArrayList<>();
        List<Double> penaltyCandidates = new ArrayList<>();
        CostCell chosenCell;
        Double maxQuantity;
        int toDeleteSupplyIndex = -1;
        int toDeleteDemandIndex = -1;
        Double maxPenalty;

        Comparator<CostCell> compareCostCellV = (CostCell o1, CostCell o2) ->{
            int difference = o1.cost.compareTo(o2.cost);
            if(difference==0){
                Double o1MaxCapacity = Math.min(supply.get(o1.positionRow).remaining, demand.get(o1.positionColumn).remaining);
                Double o2MaxCapacity = Math.min(supply.get(o2.positionRow).remaining, demand.get(o2.positionColumn).remaining);
                difference = -o1MaxCapacity.compareTo(o2MaxCapacity);
                if(difference==0){
                    Double o1RemainingSupply = supply.get(o1.positionRow).remaining - o1MaxCapacity;
                    Double o2RemainingSupply = supply.get(o2.positionRow).remaining - o2MaxCapacity;
                    difference = -o1RemainingSupply.compareTo(o2RemainingSupply);
                    if(difference==0) {
                        difference = o1.positionRow.compareTo(o2.positionRow);
                        if (difference == 0) {
                            difference = o1.positionColumn.compareTo(o2.positionColumn);
                        }
                    }
                }
            }
            return difference;
        };

        while(vCostTable!=null){
            maxPenalty = 0.0;
            supplyMaxPenalty.clear();
            demandMaxPenalty.clear();
            //getting max penalties for supply
            for(int i = 0; i<vSupply.size();i++){
                penaltyCandidates.clear();
                for(int j=0; j<vDemand.size();j++){
                    penaltyCandidates.add(vCostTable.get(i).get(j).cost);
                }
                supplyMaxPenalty.add(getMaxPenalty(penaltyCandidates));
            }
            //getting max penalties for demand
            for(int i=0; i<vDemand.size();i++){
                penaltyCandidates.clear();
                for(int j=0; j<vSupply.size();j++){
                    penaltyCandidates.add(vCostTable.get(j).get(i).cost);
                }
                demandMaxPenalty.add(getMaxPenalty(penaltyCandidates));
            }

            maxPenalty = Math.max(maxPenalty, Collections.max(supplyMaxPenalty));
            maxPenalty = Math.max(maxPenalty, Collections.max(demandMaxPenalty));

            for(int i=0; i< supplyMaxPenalty.size();i++){
                if(supplyMaxPenalty.get(i).equals(maxPenalty)){
                    for(int j=0; j< demandMaxPenalty.size();j++){
                        candidateCells.add(vCostTable.get(i).get(j));
                    }
                }
            }
            for(int i=0; i< demandMaxPenalty.size(); i++){
                if(demandMaxPenalty.get(i).equals(maxPenalty)){
                    for(int j=0; j<supplyMaxPenalty.size();j++){
                        candidateCells.add(vCostTable.get(i).get(j));
                    }
                }
            }
            candidateCells.sort(compareCostCellV);
            chosenCell = candidateCells.get(0);
            //candidateCells.get(0) je odabran
            maxQuantity = Math.min(demand.get(chosenCell.positionColumn).remaining , supply.get(chosenCell.positionRow).remaining);

            costTable.get(chosenCell.positionRow).get(chosenCell.positionColumn).alloted += maxQuantity;
            supply.get(chosenCell.positionRow).remaining -= maxQuantity;
            demand.get(chosenCell.positionColumn).remaining -=maxQuantity;
            for(int i=0; i<vSupply.size();i++){
                for(int j=0; j<vDemand.size();j++){
                    if(vCostTable.get(i).get(j).equals(chosenCell)){
                        toDeleteSupplyIndex = j;
                        toDeleteDemandIndex = i;
                    }
                }
            }

            if(supply.get(chosenCell.positionRow).remaining<demand.get(chosenCell.positionColumn).remaining){
                vCostTable.remove(toDeleteDemandIndex);
                vSupply.remove(toDeleteDemandIndex);
            }
            else if(supply.get(chosenCell.positionRow).remaining>demand.get(chosenCell.positionColumn).remaining){
                for(int i=0; i< vSupply.size();i++){
                    vCostTable.get(i).remove(toDeleteSupplyIndex);
                }
                vDemand.remove(toDeleteSupplyIndex);
            }
            else if(supply.get(chosenCell.positionRow).remaining.equals(demand.get(chosenCell.positionColumn).remaining)){
                for(int i=0; i< vSupply.size();i++){
                    vCostTable.get(i).remove(toDeleteSupplyIndex);
                }
                vDemand.remove(toDeleteSupplyIndex);
                vCostTable.remove(toDeleteDemandIndex);
                vSupply.remove(toDeleteDemandIndex);
            }
        }


        if (optimizeMODI) {
            optimizeMODI();
        }
        if (optimizeSteppingStone) {
            optimizeSteppingStone();
        }
        getTotalCost();
    }
    private Double getMaxPenalty(List<Double> list){
        List<Double> nlist = new ArrayList<>(list);
        Double minCost = Collections.min(nlist);
        nlist.remove(Collections.min(nlist));
        return (Collections.min(nlist) - minCost);
    }

    private void optimizeMODI() {
        fixDegenerateCase();
        Double[] u = new Double[numOfSupplies];
        Double[] v = new Double[numOfDemands];
        Double[][] costMatrix = new Double[numOfSupplies][numOfDemands];
        Double[][] costMatrixInverted = new Double[numOfSupplies][numOfDemands];
        //reading costs of alloted cells
        for(int i=0; i<numOfSupplies;i++){
            for(int j=0; j<numOfDemands;j++) {
                if (costTable.get(i).get(j).alloted!=0.0) {
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
                            }else if( v[j] == null && u[i]==null){
                                tryAgain = true;
                            }


                    }
                }
            }
        }while (tryAgain);

        //building inverted cost matrix
        for(int i=0;i<u.length;i++){
            for(int j=0; j<v.length;j++){
                if(costMatrix[i][j]==null){

                    costMatrixInverted[i][j] = costTable.get(i).get(j).cost -(v[j] + u[i]);
                }
            }
        }
        //search for CostCell which can reduce the total transport cost
        boolean breakOut = false;

        for(int i=0; i<u.length;i++){
            for(int j=0;j<v.length; j++){
                if(costMatrixInverted[i][j] != null && costMatrixInverted[i][j]<0.0){
                    //stepping stone rotate
                    CostCell newSolutionCell = costTable.get(i).get(j);
                    CostCell[] path = getClosedPath(newSolutionCell);

                    double lowestQuantity = Integer.MAX_VALUE;
                    boolean plus = true;

                    for(CostCell currentCostCell : path){
                        if(!plus){
                            if(currentCostCell.alloted<lowestQuantity){
                                lowestQuantity=currentCostCell.alloted;
                            }
                        }
                        plus = !plus;
                    }
                    plus = true;

                    for(CostCell s : path){
                        if(plus){
                            s.alloted+=lowestQuantity;
                        }
                        else{
                            s.alloted-=lowestQuantity;
                        }
                        plus = !plus;
                    }
                    optimizeMODI();
                    breakOut = true;
                }

                if(breakOut){break;}
                //if(i==u.length-1 && j==v.length-1){return;}
            }
            if(breakOut){break;}
        }
        //optimizeMODI();
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
                        return;
                    }
                }
            }
        }
    }
}
