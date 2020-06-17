package controller;

import observer.Observer;
import view.BloodPressureTableView;
import view.Patient;
import view.PatientsView;
import view.SBPGraphView;

import java.util.ArrayList;
import java.util.List;

public class SBPGraphController implements Observer {

    private ArrayList<ArrayList<String>> data;

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
        System.out.println(this.data);
        this.patientUpdater = patientUpdater;
        this.patientUpdater.register(this);
        this.patientsView = patientsView;
        makeViews();
        showCharts();
    }

    private void makeViews() {

//        for (ArrayList<String> list: data){
//            ArrayList<Integer> values = new ArrayList<>();
//            for (int i = 1; i<list.size(); i++){
//                values.add(Integer.valueOf(list.get(i)));
//            }
//            this.graphView.initChart(list.get(0), values);
//        }

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
            this.graphView.closeView();
            this.graphView.getTabPane().removeAll();
            makeViews();
            showCharts();
        }
    }

}
