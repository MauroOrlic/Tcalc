package com.mauroorlic.tcalc;

import android.widget.TableLayout;

public class OutputTable {
    TransportProblem transportProblem;
    TableLayout outputTableReference;
    public OutputTable(TableLayout outputTableReference, TransportProblem transportProblem) {
        this.transportProblem = transportProblem;
        this.outputTableReference = outputTableReference;
    }

}
