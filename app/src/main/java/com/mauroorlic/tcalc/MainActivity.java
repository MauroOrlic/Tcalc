package com.mauroorlic.tcalc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;

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

        inputTableLayout = findViewById(R.id.input_grid);
        outputTableLayout = findViewById(R.id.output_grid);
        final CheckBox usingMODI = findViewById(R.id.usingMODI);
        final CheckBox usingSteppingStone = findViewById(R.id.usingSteppingStone);

        final Spinner initialSolutionMethod = findViewById(R.id.initialSolutionMethod);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.initialSolutionMethods, R.layout.support_simple_spinner_dropdown_item);
        initialSolutionMethod.setAdapter(adapter);

        final EditText supplyInput = findViewById(R.id.supplyInput);
        final EditText demandInput = findViewById(R.id.demandInput);
        Button buildTableButton = findViewById(R.id.build_table);


        buildTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    supply = Integer.parseInt(supplyInput.getText().toString());
                    demand = Integer.parseInt(demandInput.getText().toString());
                } catch (NumberFormatException e){
                    Toast.makeText(MainActivity.this, "You didn't enter", Toast.LENGTH_SHORT).show();
                    return;
                }
                inputTable = new InputTable(inputTableLayout,demand, supply);
                inputTable.buildTable();
            }
        });

        Button calculateButton = findViewById(R.id.solve_button);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //transportProblem = new TransportProblem(inputTable.getTransportProblem());
                //TODO uncomment above and comment bellow line to make input actually work
                transportProblem = inputTable.generateAtozmathStock();

                String selectedInitialMethod = initialSolutionMethod.getSelectedItem().toString();

                if(selectedInitialMethod.contentEquals(adapter.getItem(0))){
                    transportProblem.initialNorthWest(usingMODI.isChecked(), usingSteppingStone.isChecked());
                }
                else if(selectedInitialMethod.contentEquals(adapter.getItem(1))){
                    transportProblem.initialLeastCost(usingMODI.isChecked(), usingSteppingStone.isChecked());
                }
                else if(selectedInitialMethod.contentEquals(adapter.getItem(3))){
                    transportProblem.initialVogel(usingMODI.isChecked(), usingSteppingStone.isChecked());
                }
                OutputTable outputTable = new OutputTable(outputTableLayout, transportProblem);
                outputTable.buildTable();
                transportProblem = inputTable.getTransportProblem();

                /*
                OutputTable outputTable = new OutputTable(outputTableLayout, inputTable.getTransportProblem());
                outputTable.buildTable();
                transportProblem = inputTable.getTransportProblem();
                */
            }
        });

    }
}