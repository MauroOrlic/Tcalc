package com.mauroorlic.tcalc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;

public class OutputTable {
    TransportProblem transportProblem;
    TableLayout outputTableReference;
    public OutputTable(TableLayout outputTableReference, TransportProblem transportProblem) {
        this.transportProblem = transportProblem;
        this.outputTableReference = outputTableReference;
        this.outputTableReference.removeAllViews();
    }
    public void buildTable(){
        DecimalFormat format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(false);
        //TODO: Ovo makni valjda, samo sam testirao
        transportProblem.initialNorthWest(false, false);

        LayoutInflater inflater = LayoutInflater.from(outputTableReference.getContext());
        for(int i = 0; i<transportProblem.numOfSupplies;i++){
            TableRow tableRow = new TableRow(outputTableReference.getContext());
            outputTableReference.addView(tableRow);
            tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            for(int j=0;j<transportProblem.numOfDemands;j++) {
                View v = inflater.inflate(R.layout.output_cost_cell, tableRow, false);
                tableRow.addView(v);
                //TODO: Provjeri dal je tocno
                CostCell costCell = transportProblem.costTable.get(i).get(j);

                TextView cost = v.findViewById(R.id.inflateable_outputCost);
                cost.setText(format.format(costCell.cost));
                TextView alloted = v.findViewById(R.id.inflateable_outputAlloted);
                alloted.setText(format.format(costCell.alloted));
            }
            View v = inflater.inflate(R.layout.output_resource_cell, tableRow, false);
            tableRow.addView(v);
            TextView resourceAmount = v.findViewById(R.id.resourceAmount);
            //TODO: Provjeri dal je tocno
            resourceAmount.setText(format.format(transportProblem.supply.get(i).total));
        }
        TableRow tableRow = new TableRow(outputTableReference.getContext());
        outputTableReference.addView(tableRow);
        tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        for(int i=0; i<transportProblem.numOfDemands; i++){
            View c = inflater.inflate(R.layout.output_resource_cell, tableRow, false);
            TextView resourceAmount = c.findViewById(R.id.resourceAmount);
            //TODO: Provjeri dal je tocno
            resourceAmount.setText(format.format(transportProblem.demand.get(i).total));
            tableRow.addView(c);
        }
        //TODO: Alternativno: umjesto input cell resource cell, ali mislim da su istih dimenzija pa je svejedno
        View c = inflater.inflate(R.layout.output_resource_cell, tableRow, false);
        tableRow.addView(c);
        c.setEnabled(false);
        c.setVisibility(View.INVISIBLE);
    }

}
