package controller;

import observer.Observer;
import view.BloodPressureTableView;
import view.Patient;
import view.PatientsView;
import view.SBPGraphView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class SBPGraphController implements Observer {

    private BloodPressureTableView bpView;

    private SBPGraphView graphView;

    private PatientsView patientsView;

    /**
     * The subject that this class subscribes to in order to update patient measurements.
     */
    private PatientUpdater patientUpdater;

    public SBPGraphController(SBPGraphView graphView, BloodPressureTableView view, PatientUpdater patientUpdater, PatientsView patientsView) {
        this.graphView = graphView;
        this.bpView = view;
        this.patientUpdater = patientUpdater;
        this.patientUpdater.register(this);
        this.patientsView = patientsView;
        makeViews();
        showCharts();
    }

    private void makeViews() {

        ArrayList<Patient> array = this.bpView.getHighSystolicPatients();
        for (Patient p: array){
            ArrayList<Double> values = new ArrayList<>();
            List<Object[]> bps = p.getSystolicBPs();
            for (Object[] bp: bps){
                values.add((Double) bp[1]);
            }
            this.graphView.initChart(p.toString(),values);
        }

    }

    private void showCharts(){
        this.graphView.showView();
    }


    @Override
    public void update(Patient patient) {

        if (patientsView.isUpdateFinished()){

            // remove old tab pane
            this.graphView.getMainPanel().remove(this.graphView.getTabPane());
            // set new tab pane
            this.graphView.setTabPane(new JTabbedPane());
            // add new tab pane
            this.graphView.getMainPanel().add(this.graphView.getTabPane());

            // make new view
            makeViews();
            this.graphView.getMainPanel().revalidate();
            this.graphView.getMainPanel().repaint();
        }
    }

}
