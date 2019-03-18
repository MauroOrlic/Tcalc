package com.mauroorlic.tcalc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.List;

public class InputTable {
    TableLayout inputTableReference;
    List<List<EditText>> inputCells = new ArrayList<>();
    List<EditText> demandInputCells = new ArrayList<>();
    List<EditText> supplyInputCells = new ArrayList<>();
    int numOfDemands;
    int numOfSupplies;
    public InputTable(TableLayout inputTableReference, int demands, int supplies) {
        this.inputTableReference = inputTableReference;
        this.numOfDemands = demands;
        this.numOfSupplies = supplies;
        this.inputTableReference.removeAllViews();
    }
    public void buildTable(){
        LayoutInflater inflater = LayoutInflater.from(inputTableReference.getContext());
        for(int i = 0; i<numOfSupplies;i++){
            inputCells.add(new ArrayList<EditText>());
            TableRow tableRow = new TableRow(inputTableReference.getContext());
            inputTableReference.addView(tableRow);
            tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            for(int j=0;j<numOfDemands;j++) {
                View v = inflater.inflate(R.layout.input_cell, tableRow, false);
                tableRow.addView(v);
                EditText a = v.findViewById(R.id.inflateable_inputCell);
                //TODO implement function that creates an edittext and links focuses
                inputCells.get(i).add(a);
                inputCells.get(i).get(j).setId(102000 + i*10 + j);
                inputCells.get(i).get(j).setImeOptions(EditorInfo.IME_ACTION_NEXT);
                if(i==0 && j==0){
                    inputCells.get(i).get(j).requestFocus();
                }
                else if(j==0){
                    inputCells.get(i-1).get(numOfDemands-1).setNextFocusForwardId(inputCells.get(i).get(j).getId());
                }
                else {
                    inputCells.get(i).get(j - 1).setNextFocusForwardId(inputCells.get(i).get(j).getId());
                }
            }
            View v = inflater.inflate(R.layout.input_cell, tableRow, false);
            tableRow.addView(v);
            EditText b = v.findViewById(R.id.inflateable_inputCell);
            supplyInputCells.add( (EditText) v.findViewById(R.id.inflateable_inputCell));
        }
        TableRow tableRow = new TableRow(inputTableReference.getContext());
        inputTableReference.addView(tableRow);
        tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        for(int i=0; i<numOfDemands; i++){
            View c = inflater.inflate(R.layout.input_cell, tableRow, false);
            tableRow.addView(c);
            demandInputCells.add((EditText) c.findViewById(R.id.inflateable_inputCell));
        }
        View c = inflater.inflate(R.layout.input_cell, tableRow, false);
        tableRow.addView(c);
        c.setEnabled(false);
        c.setVisibility(View.INVISIBLE);
    }
    public List<List<Double>> getCostTable(){
        List<List<Double>> costTable = new ArrayList<>();

        for(int i=0; i< numOfSupplies; i++){
            costTable.add(new ArrayList<Double>());
            for(int j=0; j< numOfDemands; j++){
                costTable.get(i).add(Double.parseDouble(inputCells.get(i).get(j).getText().toString()));
            }
        }
        return costTable;
    }
    public List<Double> getSupplyArray(){
        List<Double> supplyArray = new ArrayList<>();
        for(int i=0;i<numOfSupplies;i++){
            supplyArray.add(Double.parseDouble(supplyInputCells.get(i).getText().toString()));
        }
        return supplyArray;
    }
    public List<Double> getDemandArray(){
        List<Double> demandArray = new ArrayList<>();
        for(int i=0;i<numOfDemands;i++){
            demandArray.add(Double.parseDouble(demandInputCells.get(i).getText().toString()));
        }
        return demandArray;
    }
    public TransportProblem getTransportProblem(){
        return new TransportProblem(getDemandArray(), getSupplyArray(), getCostTable());
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