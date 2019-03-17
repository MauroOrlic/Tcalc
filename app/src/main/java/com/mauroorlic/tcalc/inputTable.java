package com.mauroorlic.tcalc;

import android.widget.TableLayout;

public class inputTable {
    TableLayout inputTable;
    int numOfDemands;
    int numOfSupplies;
    public inputTable(TableLayout inputTable, int demands, int supplies) {
        this.inputTable = inputTable;
        this.numOfDemands = demands;
        this.numOfSupplies = supplies;
    }
}
