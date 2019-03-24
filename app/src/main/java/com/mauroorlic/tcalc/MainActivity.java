package com.mauroorlic.tcalc;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    TableLayout inputTableLayout;
    TableLayout outputTableLayout;
    InputTable inputTable;
    TransportProblem transportProblem;
    int supply, demand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DecimalFormat format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(false);


        inputTableLayout = findViewById(R.id.input_grid);
        outputTableLayout = findViewById(R.id.output_grid);
        final CheckBox usingMODI = findViewById(R.id.usingMODI);
        final CheckBox usingSteppingStone = findViewById(R.id.usingSteppingStone);

        final Spinner initialSolutionMethod = findViewById(R.id.initialSolutionMethod);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.initialSolutionMethods, R.layout.support_simple_spinner_dropdown_item);
        initialSolutionMethod.setAdapter(adapter);/*
        for(int i=0; i< initialSolutionMethod.getChildCount();i++){
            ((EditText) initialSolutionMethod.getChildAt(i)).setTextColor(Color.rgb(255,255,255));
        }*/

        final EditText supplyInput = findViewById(R.id.supplyInput);
        final EditText demandInput = findViewById(R.id.demandInput);
        Button buildTableButton = findViewById(R.id.build_table);
        final TextView displayTotalCost = findViewById(R.id.displayTotalCost);
        Button calculateButton = findViewById(R.id.solve_button);
        calculateButton.setEnabled(false);


        buildTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    supply = Integer.parseInt(supplyInput.getText().toString());
                    demand = Integer.parseInt(demandInput.getText().toString());
                } catch (NumberFormatException e){
                    Toast.makeText(MainActivity.this, "You didn't enter ", Toast.LENGTH_SHORT).show();
                    return;
                }
                inputTable = new InputTable(inputTableLayout,demand, supply);
                inputTable.buildTable();
                calculateButton.setEnabled(true);
            }
        });

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    transportProblem = new TransportProblem(inputTable.getTransportProblem());
                    //TODO uncomment above and comment bellow line to make input actually work
                    //transportProblem = inputTable.generateAtozmathStock();

                    for(ResourceCell demandCell : transportProblem.demand){
                        if(demandCell.total.equals(0.0)){
                            throw new IllegalArgumentException();
                        }
                    }
                    for(ResourceCell supplyCell : transportProblem.supply){
                        if(supplyCell.total.equals(0.0)){
                            throw new IllegalArgumentException();
                        }
                    }

                    String selectedInitialMethod = initialSolutionMethod.getSelectedItem().toString();

                    if (selectedInitialMethod.contentEquals(adapter.getItem(0))) {
                        transportProblem.initialNorthWest(usingMODI.isChecked(), usingSteppingStone.isChecked());
                    } else if (selectedInitialMethod.contentEquals(adapter.getItem(1))) {
                        transportProblem.initialLeastCost(usingMODI.isChecked(), usingSteppingStone.isChecked());
                    } else if (selectedInitialMethod.contentEquals(adapter.getItem(2))) {
                        transportProblem.initialVogel(usingMODI.isChecked(), usingSteppingStone.isChecked());
                    }
                    OutputTable outputTable = new OutputTable(outputTableLayout, transportProblem);
                    outputTable.buildTable();

                    displayTotalCost.setText(("Minimum total cost = " + format.format(transportProblem.totalCost)));
                    displayTotalCost.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    displayTotalCost.requestFocus();
                }
                catch (IllegalArgumentException e){
                    Toast.makeText(MainActivity.this, "Supply/Demand values cannot be zero", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }
}