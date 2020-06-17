package controller;

import view.BloodPressureTableView;
import view.Patient;
import view.PatientsView;
import view.SBPGraphView;

import javax.swing.*;
import java.util.ArrayList;

public class SBPGraphController implements Observer {

    private ArrayList<ArrayList<String>> data;
    private SBPGraphView graphView;

    public SBPGraphController(SBPGraphView graphView, ArrayList<ArrayList<String>> set) {
        this.graphView = graphView;
        this.data = set;
        makeViews();
        showCharts();
    }

    private void makeViews() {

        for (ArrayList<String> list: data){
            ArrayList<Integer> values = new ArrayList<>();
            for (int i = 1; i<list.size(); i++){
                values.add(Integer.valueOf(list.get(i)));
            }
            this.graphView.initChart(list.get(0), values);
        }

    }

    private void showCharts(){
        this.graphView.showView();
    }


    @Override
    public void update(Patient patient) {
        ;
    }

}
