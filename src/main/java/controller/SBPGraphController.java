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

    private PatientsView patientsView;

    /**
     * The subject that this class subscribes to in order to update patient measurements.
     */
    private PatientUpdater patientUpdater;

    public SBPGraphController(SBPGraphView graphView, ArrayList<ArrayList<String>> set, PatientUpdater patientUpdater, PatientsView patientsView) {
        this.graphView = graphView;
        this.data = set;
        this.patientUpdater = patientUpdater;
        this.patientUpdater.register(this);
        this.patientsView = patientsView;
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
        if (patientsView.isUpdateFinished()){
            this.graphView.closeView();
            this.graphView.getTabPane().removeAll();
            makeViews();
            showCharts();
        }
    }

}
