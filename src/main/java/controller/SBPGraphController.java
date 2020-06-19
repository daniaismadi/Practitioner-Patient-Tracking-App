package controller;

import observer.Observer;
import view.BloodPressureTableView;
import view.Patient;
import view.PatientsView;
import view.SBPGraphView;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The controller class that controls the Systolic BP Graph view.
 */

public class SBPGraphController implements Observer {

    /**
     * The view this controller depends on for the High Systolic BP values.
     */
    private BloodPressureTableView bpView;

    /**
     * The view this controller controls.
     */
    private SBPGraphView graphView;

    /**
     * The main view this controller depends on.
     */
    private PatientsView patientsView;

    /**
     * The subject that this class subscribes to in order to update patient measurements.
     */
    private PatientUpdater patientUpdater;

    /**
     * Constructor that initialises all required variables
     * and invokes 2 methods to initialise the charts and show them.
     * @param graphView   The view this controller controls.
     * @param view     The view this controller depends on for the High Systolic BP values.
     * @param patientUpdater     The subject that this class subscribes to in order to update patient measurements.
     * @param patientsView      The main view this controller depends on.
     */
    public SBPGraphController(SBPGraphView graphView, BloodPressureTableView view, PatientUpdater patientUpdater, PatientsView patientsView) {
        this.graphView = graphView;
        this.bpView = view;
        this.patientUpdater = patientUpdater;
        this.patientUpdater.register(this);
        this.patientsView = patientsView;
        makeViews();
        showCharts();
    }

    /**
     * This method will take each Patient object from Bp table view and store the
     * systolic bp values in an array list (of type Double).
     * For each patient, init chart in graph view is invoked to make a chart for that patient
     */
    private void makeViews() {

        ArrayList<Patient> array = this.bpView.getHighSystolicPatients();

        for (Patient p: array){

            ArrayList<Double> values = new ArrayList<>();
            List<Object[]> bps = p.getSystolicBPs();
            for (Object[] bp: bps){
                values.add((Double) bp[1]);
            }

            // The method that will initialise a chart for a particular patient with his/her Systolic values
            this.graphView.initChart(p.toString(),values);

        }

    }

    /**
     * This method invokes a method in the graph view, to make the frame visible.
     */
    private void showCharts(){
        this.graphView.showView();
    }

    /**
     * Updates graph view when patient's high systolic bp values are updated.
     */
    @Override
    public void update() {

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
