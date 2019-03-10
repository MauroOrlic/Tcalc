package com.mauroorlic.tcalc;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TransportProblem {
    int numOfDemands;
    int numOfSupplies;
    ArrayList<ResourceCell> demand;
    ArrayList<ResourceCell> supply;
    ArrayList<ArrayList<CostCell>> costTable;

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

        fixDegeneracy();
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

    public void fixDegeneracy() {
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
        int currentDemand = 0;
        int currentSupply = 0;
        Double alloted = 0.0;
        for (int step = 0; step < demand.size() + supply.size() - 1; step++) {

            alloted = Math.min(demand.get(currentDemand).remaining, supply.get(currentSupply).remaining);
            costTable.get(currentSupply).get(currentDemand).setAlloted(alloted);

            supply.get(currentSupply).remaining -= alloted;
            demand.get(currentDemand).remaining -= alloted;

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

    private void optimizeMODI() {

    }

    public void optimizeSteppingStone() {

    }
}
