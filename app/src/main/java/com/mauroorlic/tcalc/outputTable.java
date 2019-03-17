package com.mauroorlic.tcalc;

import android.widget.TableLayout;

public class outputTable {
    TransportProblem transportProblem;
    TableLayout outputGrid;
    public outputTable(TransportProblem transportProblem, TableLayout outputGrid) {
        this.transportProblem = transportProblem;
        this.outputGrid = outputGrid;
    }
}
