package controller;

import controller.Observer;
import view.CholestrolChartView;
import view.Patient;

import java.util.ArrayList;

/**
 * The controller class that controls the Cholestrol Chart View
 */
public class CholesChartController implements Observer{

    // list to store patients being monitored
    private ArrayList<Patient> monitoredList;

    /**
     * Constructor for CholesChartController.
     * @param list has objects of type Patient (Patients currently being monitored)
     */
    public CholesChartController(ArrayList<Patient> list){
        this.monitoredList = list;
        initializeView(this.monitoredList);
    }

    /**
     * This method will initialize the Cholestrol Chart View. Upon invoking this method, the
     * application will show the chart to the user.
     * @param list has objects of type Patient (Patients currently being monitored)
     */
    private void initializeView(ArrayList<Patient> list){
        CholestrolChartView cholChartView = new CholestrolChartView(list);
    }

    @Override
    public void update(Patient patient) {
        ;
    }
}