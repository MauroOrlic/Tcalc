package com.mauroorlic.tcalc;

public class CostCell {
    Double cost;
    Double alloted;

    public CostCell(Double cost, Double alloted) {
        this.cost = cost;
        this.alloted = alloted;
    }
    public CostCell(Double cost) {
        this.cost = cost;
        this.alloted = 0.0;
    }
    public CostCell() {
        this.cost = 0.0;
        this.alloted = 0.0;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getAlloted() {
        return alloted;
    }

    public void setAlloted(Double alloted) {
        this.alloted = alloted;
    }
}
