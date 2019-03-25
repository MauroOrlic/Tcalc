package com.mauroorlic.tcalc;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class InputTable {
    private TableLayout inputTableReference;
    private List<List<EditText>> inputCells = new ArrayList<>();
    private List<EditText> demandInputCells = new ArrayList<>();
    private List<EditText> supplyInputCells = new ArrayList<>();
    private int numOfDemands;
    private int numOfSupplies;
    private Integer currentInputCellID;
    public InputTable(TableLayout inputTableReference, int demands, int supplies) {
        this.inputTableReference = inputTableReference;
        this.numOfDemands = demands;
        this.numOfSupplies = supplies;
        this.inputTableReference.removeAllViews();
        this.currentInputCellID = 567044;
    }
    public void buildTable(){
        LayoutInflater inflater = LayoutInflater.from(inputTableReference.getContext());
        TableRow tableRow = new TableRow(inputTableReference.getContext());
        tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        inputTableReference.addView(tableRow);
        View lineTag;
        lineTag = inflater.inflate(R.layout.output_resource_cell, tableRow,false);
        tableRow.addView(lineTag);
        lineTag.setEnabled(false);
        lineTag.setVisibility(View.INVISIBLE);
        for(int i=0; i<numOfDemands;i++){
            lineTag = inflater.inflate(R.layout.output_resource_cell, tableRow,false);
            ((TextView)lineTag.findViewById(R.id.resourceAmount)).setText(Html.fromHtml(("D"+"<sub>"+(i+1)+"</sub>")));
            tableRow.addView(lineTag);
        }
        for(int i = 0; i<numOfSupplies;i++){
            inputCells.add(new ArrayList<EditText>());
            tableRow = new TableRow(inputTableReference.getContext());
            inputTableReference.addView(tableRow);
            lineTag = inflater.inflate(R.layout.output_resource_cell, tableRow,false);
            ((TextView)lineTag.findViewById(R.id.resourceAmount)).setText(Html.fromHtml(("S"+"<sub>"+(i+1)+"</sub>")));
            tableRow.addView(lineTag);

            tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            for(int j=0;j<numOfDemands;j++) {
                View v = inflater.inflate(R.layout.input_cell, tableRow, false);
                tableRow.addView(v);
                EditText a = v.findViewById(R.id.inflateable_inputCell);
                inputCells.get(i).add(a);
                inputCells.get(i).get(j).setImeOptions(EditorInfo.IME_ACTION_NEXT);
                inputCells.get(i).get(j).setId(currentInputCellID);
                if(i==0 && j==0){
                    inputCells.get(i).get(j).requestFocus();
                }
                else {
                    inputCells.get(i).get(j).setNextFocusForwardId(++currentInputCellID);
                }

            }
            View v = inflater.inflate(R.layout.input_cell, tableRow, false);
            tableRow.addView(v);
            EditText b = v.findViewById(R.id.inflateable_inputCell);
            supplyInputCells.add( (EditText) v.findViewById(R.id.inflateable_inputCell));
            supplyInputCells.get(i).setImeOptions(EditorInfo.IME_ACTION_NEXT);
            supplyInputCells.get(i).setId(currentInputCellID);
            supplyInputCells.get(i).setNextFocusForwardId(++currentInputCellID);
        }
        tableRow = new TableRow(inputTableReference.getContext());
        inputTableReference.addView(tableRow);
        tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        View c = inflater.inflate(R.layout.input_cell, tableRow, false);
        tableRow.addView(c);
        c.setEnabled(false);
        c.setVisibility(View.INVISIBLE);
        for(int i=0; i<numOfDemands; i++){
            c = inflater.inflate(R.layout.input_cell, tableRow, false);
            tableRow.addView(c);
            demandInputCells.add((EditText) c.findViewById(R.id.inflateable_inputCell));
            demandInputCells.get(i).setId(currentInputCellID);
            if(i<numOfDemands-1){
                demandInputCells.get(i).setImeOptions(EditorInfo.IME_ACTION_NEXT);
                demandInputCells.get(i).setNextFocusForwardId(++currentInputCellID);
            }

        }
        c = inflater.inflate(R.layout.input_cell, tableRow, false);
        tableRow.addView(c);
        c.setEnabled(false);
        c.setVisibility(View.INVISIBLE);
    }
    private List<List<Double>> getCostTable(){
        List<List<Double>> costTable = new ArrayList<>();
        String input;

        for(int i=0; i< numOfSupplies; i++){
            costTable.add(new ArrayList<Double>());
            for(int j=0; j< numOfDemands; j++){
                input = inputCells.get(i).get(j).getText().toString();
                if(!input.equals("")) {
                    costTable.get(i).add(Double.parseDouble(input));
                }
                else{
                    costTable.get(i).add(0.0);
                }
            }
        }
        return costTable;
    }
    private List<Double> getSupplyArray(){
        List<Double> supplyArray = new ArrayList<>();
        String input;
        for(int i=0;i<numOfSupplies;i++){
            input = supplyInputCells.get(i).getText().toString();
            if(!input.equals("")) {
                supplyArray.add(Double.parseDouble(input));
            }
            else {
                supplyArray.add(0.0);
            }
        }
        return supplyArray;
    }
    private List<Double> getDemandArray(){
        List<Double> demandArray = new ArrayList<>();
        String input;
        for(int i=0;i<numOfDemands;i++){
            input = demandInputCells.get(i).getText().toString();
            if(!input.equals("")) {
                demandArray.add(Double.parseDouble(input));
            }
            else{
                demandArray.add(0.0);
            }
        }
        return demandArray;
    }
    public TransportProblem getTransportProblem(){
        return new TransportProblem(getDemandArray(), getSupplyArray(), getCostTable());
    }
    public TransportProblem generateSpecial(){
        List<Double> supplyArray = new ArrayList<>();
        List<Double> demandArray = new ArrayList<>();
        List<List<Double>> costTable = new ArrayList<>();

        supplyArray.add(6.0);
        supplyArray.add(5.0);

        demandArray.add(6.0);
        demandArray.add(5.0);
        demandArray.add(2.0);
        demandArray.add(5.0);
        demandArray.add(3.0);

        costTable.add(new ArrayList<Double>());
        costTable.get(0).add(2.0);
        costTable.get(0).add(5.0);
        costTable.get(0).add(1.0);
        costTable.get(0).add(4.0);
        costTable.get(0).add(5.0);

        costTable.add(new ArrayList<Double>());
        costTable.get(1).add(3.0);
        costTable.get(1).add(5.0);
        costTable.get(1).add(5.0);
        costTable.get(1).add(7.0);
        costTable.get(1).add(4.0);

        return new TransportProblem(demandArray, supplyArray, costTable);
    }
    public TransportProblem generateAtozmathStock(){
        List<Double> supplyArray = new ArrayList<>();
        List<Double> demandArray = new ArrayList<>();
        List<List<Double>> costTable = new ArrayList<>();

        supplyArray.add(250.0);
        supplyArray.add(300.0);
        supplyArray.add(400.0);

        demandArray.add(200.0);
        demandArray.add(225.0);
        demandArray.add(275.0);
        demandArray.add(250.0);

        costTable.add(new ArrayList<Double>());
        costTable.get(0).add(11.0);
        costTable.get(0).add(13.0);
        costTable.get(0).add(17.0);
        costTable.get(0).add(14.0);

        costTable.add(new ArrayList<Double>());
        costTable.get(1).add(16.0);
        costTable.get(1).add(18.0);
        costTable.get(1).add(14.0);
        costTable.get(1).add(10.0);

        costTable.add(new ArrayList<Double>());
        costTable.get(2).add(21.0);
        costTable.get(2).add(24.0);
        costTable.get(2).add(13.0);
        costTable.get(2).add(10.0);

        return new TransportProblem(demandArray, supplyArray, costTable);
    }

}