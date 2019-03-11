package com.mauroorlic.tcalc;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    TableLayout tableLayout;
    List<EditText> costTable = new ArrayList<>();
    List<EditText> supplyTable = new ArrayList<>();
    List<EditText> demandTable = new ArrayList<>();

    int supply, demand;

    final int DEMAND_MAX = 10;
    final int CELL_WIDTH = 32;
    final int CELL_HEIGHT = 32;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tableLayout = findViewById(R.id.input_grid);

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
                if(demand > DEMAND_MAX){
                    Toast.makeText(MainActivity.this, "Too much demand", Toast.LENGTH_SHORT).show();
                    return;
                }
                generateGrid(supply, demand);
            }
        });

        Button calculateButton = findViewById(R.id.solve_button);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transform();
            }
        });

    }

    void generateGrid(int supply, int demand){

        tableLayout.removeAllViews();
        costTable.clear();

        for(int i=0; i<supply; i++){
            TableRow tableRow = new TableRow(this);
            tableLayout.addView(tableRow);
            tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            for(int j=0; j<demand; j++){
                EditText editText = createInputCell();

                tableRow.addView(editText);
                costTable.add(editText);
            }

            EditText editText = createInputCell();
            tableRow.addView(editText);
            supplyTable.add(editText);
        }

        TableRow tableRow = new TableRow(this);
        tableLayout.addView(tableRow);
        tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        for(int i=0; i<demand; i++){
            EditText editText = createInputCell();
            tableRow.addView(editText);
            demandTable.add(editText);
        }

        EditText editText = createEmptyCell();
        tableRow.addView(editText);

        //View v = (LayoutInflater.from(this).inflate(R.layout.activity_main, tableLayout, false));
        //v.findViewById(R.id.)
    }

    private EditText createInputCell(){
        EditText editText = new EditText(this);
        int px = toPx(CELL_WIDTH);
        editText.setWidth(px);
        editText.setHeight(px);
        editText.setBackground(ContextCompat.getDrawable(this, R.drawable.table_border));
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        return editText;
    }

    private EditText createEmptyCell(){
        EditText editText = new EditText(this);
        int px = toPx(CELL_WIDTH);
        editText.setWidth(px);
        editText.setHeight(px);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setEnabled(false);
        editText.setVisibility(View.INVISIBLE);

        return editText;
    }

    private int toPx(int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    void transform(){
        ArrayList<ArrayList<Double>> costTableList = new ArrayList<>();
        ArrayList<Double> supplyList = new ArrayList<>();
        ArrayList<Double> demandList = new ArrayList<>();


        for(int i=0; i<supply; i++){
            costTableList.add(new ArrayList<Double>());
            for(int j=0; j<demand; j++){
                //TODO: Try catch stuff
                costTableList.get(i).add(Double.parseDouble(costTable.get(i*demand + j).getText().toString()));
            }
        }

        for(int i=0; i<supplyTable.size(); i++){
            //TODO: Pokusaj, uhvati
            supplyList.add(Double.parseDouble(supplyTable.get(i).getText().toString()));
        }

        for(int i=0; i<demandTable.size(); i++){
            //TODO: Povuci potegni
            demandList.add(Double.parseDouble(demandTable.get(i).getText().toString()));
        }

        TransportProblem transportProblem = new TransportProblem(demandList, supplyList, costTableList);
        transportProblem.initialNorthWest(false, false);
        Log.d(TAG, "transform: " + transportProblem.costTable);
    }
}
