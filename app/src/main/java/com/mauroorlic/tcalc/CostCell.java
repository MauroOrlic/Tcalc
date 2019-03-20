package com.mauroorlic.tcalc;

public class CostCell {
    Double cost;
    Double alloted;
    Integer positionRow;
    Integer positionColumn;

    /*public CostCell(Double cost, Double alloted) {
        this.cost = cost;
        this.alloted = alloted;
    }*/
    public CostCell(Double cost, Double alloted, Integer positionRow, Integer positionColumn) {
        this.cost = cost;
        this.alloted = alloted;
        this.positionRow = positionRow;
        this.positionColumn = positionColumn;
    }
    /*public CostCell(Double cost) {
        this.cost = cost;
        this.alloted = 0.0;
    }*/
    public CostCell(Double cost, Integer positionRow, Integer positionColumn) {
        this.cost = cost;
        this.alloted = 0.0;
        this.positionRow = positionRow;
        this.positionColumn = positionColumn;
    }
    public CostCell(CostCell costCell){
        this.cost = costCell.cost;
        this.alloted = costCell.alloted;
        this.positionRow = costCell.positionRow;
        this.positionColumn = costCell.positionColumn;
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
