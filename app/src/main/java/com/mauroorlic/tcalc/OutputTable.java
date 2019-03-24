package com.mauroorlic.tcalc;

import android.text.Html;
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

        TableRow tableRow = new TableRow(outputTableReference.getContext());
        tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        outputTableReference.addView(tableRow);
        LayoutInflater inflater = LayoutInflater.from(outputTableReference.getContext());
        View lineTag;
        lineTag = inflater.inflate(R.layout.output_resource_cell, tableRow,false);
        tableRow.addView(lineTag);
        lineTag.setEnabled(false);
        lineTag.setVisibility(View.INVISIBLE);
        for(int i=0; i<transportProblem.numOfDemands;i++){
            lineTag = inflater.inflate(R.layout.output_resource_cell, tableRow,false);
            if(i==transportProblem.numOfDemands-1 && transportProblem.dummy ==-1){
                ((TextView) lineTag.findViewById(R.id.resourceAmount)).setText(Html.fromHtml(("D" + "<sub>d</sub>")));
            }
            else {
                ((TextView) lineTag.findViewById(R.id.resourceAmount)).setText(Html.fromHtml(("D" + "<sub>" + (i + 1) + "</sub>")));
            }
            tableRow.addView(lineTag);
        }

        for(int i = 0; i<transportProblem.numOfSupplies;i++){
            tableRow = new TableRow(outputTableReference.getContext());
            outputTableReference.addView(tableRow);
            lineTag = inflater.inflate(R.layout.output_resource_cell, tableRow,false);
            if(i==transportProblem.numOfSupplies-1 && transportProblem.dummy ==1){
                ((TextView) lineTag.findViewById(R.id.resourceAmount)).setText(Html.fromHtml(("S" + "<sub>d</sub>")));
            }
            else {
                ((TextView) lineTag.findViewById(R.id.resourceAmount)).setText(Html.fromHtml(("S" + "<sub>" + (i + 1) + "</sub>")));
            }
            tableRow.addView(lineTag);
            tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            for(int j=0;j<transportProblem.numOfDemands;j++) {
                View v = inflater.inflate(R.layout.output_cost_cell, tableRow, false);
                tableRow.addView(v);
                CostCell costCell = transportProblem.costTable.get(i).get(j);

                TextView cost = v.findViewById(R.id.inflateable_outputCost);
                cost.setText(format.format(costCell.cost));
                TextView alloted = v.findViewById(R.id.inflateable_outputAlloted);
                alloted.setText(format.format(costCell.alloted));
            }
            View v = inflater.inflate(R.layout.output_resource_cell, tableRow, false);
            tableRow.addView(v);
            TextView resourceAmount = v.findViewById(R.id.resourceAmount);
            resourceAmount.setText(format.format(transportProblem.supply.get(i).total));
        }
        tableRow = new TableRow(outputTableReference.getContext());
        outputTableReference.addView(tableRow);
        tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        View c = inflater.inflate(R.layout.output_resource_cell, tableRow, false);
        tableRow.addView(c);
        c.setEnabled(false);
        c.setVisibility(View.INVISIBLE);

        for(int i=0; i<transportProblem.numOfDemands; i++){
            c = inflater.inflate(R.layout.output_resource_cell, tableRow, false);
            TextView resourceAmount = c.findViewById(R.id.resourceAmount);
            resourceAmount.setText(format.format(transportProblem.demand.get(i).total));
            tableRow.addView(c);
        }
        c = inflater.inflate(R.layout.output_resource_cell, tableRow, false);
        tableRow.addView(c);
        c.setEnabled(false);
        c.setVisibility(View.INVISIBLE);
    }

}
