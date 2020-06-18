package controller;

import database.DBModel;
import observer.Observer;
import view.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/***
 * Class that acts as a controller for CholesterolTableView. Implements Observer to observe changes in patient
 * information. TC stands for total cholesterol.
 */
public class TCTableController implements Observer {

    /***
     * The main view that this controller is connected to.
     */
    private PatientsView patientsView;

    /**
     * The view class that this controller controls.
     */
    private CholesterolTableView tcView;

    /**
     * The model class that this controller communicates with.
     */
    private DBModel dbModel;

    /**
     * The subject that this class subscribes to in order to update patient measurements.
     */
    private PatientUpdater patientUpdater;

    /***
     * Initialises all required variables for the cholesterol table view.
     *
     * @param theView           the view that this controller controls
     * @param theModel          the model that provides the view with information
     * @param patientUpdater    the concrete subject class which grabs new information about the patient
     */
    public TCTableController(CholesterolTableView theView, DBModel theModel, PatientUpdater patientUpdater) {
        this.tcView = theView;
        this.dbModel = theModel;
        this.patientUpdater = patientUpdater;
        // Registers this class as an observer to patientUpdater.
        this.patientUpdater.register(this);

        // Initially set average cholesterol to infinity.
        theView.setAvgCholesterol(Double.POSITIVE_INFINITY);

        // Add remove button listener.
        this.tcView.addRemoveBtnListener(new RemoveBtnListener());
        // Add generate button listener
        this.tcView.addGenerateCholBtnListener(new GenerateCholesterolChartListener());
        // Add table selection listener.
        this.tcView.addTableListener(new TableSelectionListener());
    }

    /***
     * Calculate the average cholesterol of all the patients on the cholesterol monitor list.
     */
    private void calculateTCAverage() {
        int totalPatients = 0;
        double totalCholes = 0;

        for (Patient p : tcView.getMonitoredPatients()) {
            totalCholes += p.getTotalCholesterol();
            if (p.getLatestCholesterolDate() != null) {
                totalPatients += 1;
            }
        }

        // calculate total cholesterol
        if (totalPatients > 1) {
            tcView.setAvgCholesterol(totalCholes/totalPatients);
        } else {
            tcView.setAvgCholesterol(Double.POSITIVE_INFINITY);
        }
    }

    /***
     * Connects this view that displays cholesterol information to the main patients view.
     *
     * @param patientsView  the main patients view
     */
    public void setPatientsView(PatientsView patientsView) {
        this.patientsView = patientsView;
        this.patientsView.addMonitorBtnListener(new TCMonitorBtnListener());
    }

    /***
     * Helper function to convert date into a string format.
     *
     * @param date  the date to convert
     * @return      the date as a string
     */
    private String convertDateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return dateFormat.format(date);
    }

    /***
     * Return true if patient has already been added to the cholesterol monitor list.
     *
     * @param patient   the patient to check for
     * @return          true if patient has already been added to the cholesterol monitor list, false otherwise
     */
    private boolean checkPatientAdded(Patient patient) {
        List<Patient> patients = tcView.getMonitoredPatients();
        for (Patient p : patients) {
            if (p.equals(patient)) {
                return true;
            }
        }
        return false;
    }

    /***
     * Overridden from the Observer interface method. Updates the view (cholesterol table) with patient's
     * new values.
     *
     */
    @Override
    public void update() {
        // calculate new average
        calculateTCAverage();

        for (int i = 0; i < tcView.getMonitoredPatients().size(); i++) {
            Patient patient = tcView.getMonitoredPatients().get(i);

            // change value in cholesterol column
            try {
                if (patient.getTotalCholesterol() > 0) {
                    tcView.setTcTableValue(patient.getTotalCholesterol() + " mg/dL", i, 1);
                    tcView.setTcTableValue(convertDateToString(patient.getLatestCholesterolDate()), i, 2);
                }
            } catch (IndexOutOfBoundsException e) {
//            System.out.println("No cholesterol value to update on table.");
            }

        }

        // update table
        tcView.updateCholesterolColumn();

        // revalidate and repaint panel
        tcView.getTCTableMonitor().revalidate();
        tcView.getTCTableMonitor().repaint();
    }

    /***
     * Adds the patients in the list of patients p to the cholesterol table.
     *
     * @param p     list of patients to add to the cholesterol table.
     */
    private void addToTCTable(List<Patient> p) {

        for (int i = 0; i < p.size(); i++) {
            String cholesterol;

            String cholesterolDate = "-";

            Patient patient = p.get(i);

            // add latest cholesterol value
            try {
                cholesterol = patient.getTotalCholesterol() + " mg/dL";
                cholesterolDate = convertDateToString(patient.getLatestCholesterolDate());
            } catch (NullPointerException ex) {
                cholesterol = "-";
                ex.printStackTrace();
            }

            tcView.addRowToTcTable(new Object[]{patient.toString(), cholesterol, cholesterolDate});

        }
        calculateTCAverage();
        tcView.updateCholesterolColumn();
    }

    /***
     * A class to listen to the monitor button in patientsView.
     *
     */
    private class TCMonitorBtnListener implements ActionListener {

        /***
         * Invoked when the monitor button is clicked in the main view. Checks if the practitioner wants to monitor
         * cholesterol values for these patients as well and if yes, adds this patient to the cholesterol
         * monitor list. Updates the cholesterol table view.
         *
         * @param e     the event that was performed
         */
        @Override
        public void actionPerformed(ActionEvent e) {

            if (patientsView.monitorCholesterol()) {
                // Update the view.
                List<Patient> p = patientsView.getPatientList().getSelectedValuesList();
                List<Patient> toAdd = new ArrayList<>();
                for (Patient patient : p) {
                    if (!checkPatientAdded(patient)) {
                        tcView.addPatientToMonitor(patient);
                        toAdd.add(patient);
                    }
                }
                addToTCTable(toAdd);
            }
        }

    }

    /***
     * A class to listen to the remove button in the cholesterol table view.
     *
     */
    private class RemoveBtnListener implements ActionListener {

        /***
         * Invoked when the remove button is clicked. Removes the corresponding patient from the cholesterol
         * monitor list and updates the cholesterol table.
         *
         * @param e     the event that was performed
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int row = tcView.getTcTable().getSelectedRow();
                tcView.getTcTableModel().removeRow(row);
                // remove monitored patient
                Patient p = tcView.getMonitoredPatients().get(row);
                tcView.removePatientFromMonitor(p);

                calculateTCAverage();
                tcView.updateCholesterolColumn();
                // Reset patient info view state.
                tcView.resetPatientInfo();
            }
            catch (Exception k){
                System.out.println("No patient to remove.");
            }
        }
    }

    /***
     * A class to listen to the generate chart button in the cholesterol table view.
     *
     */
    private class GenerateCholesterolChartListener implements ActionListener {

        /***
         * Invoked when the generate chart button is clicked. Generates a bar chart with
         * total cholesterol values.
         *
         * @param e     the event that was performed
         */
        @Override
        public void actionPerformed(ActionEvent e) {

            if (tcView.getMonitoredPatients().size() == 0) {
                tcView.displayErrorMessage("No patients currently being monitored for cholesterol measurements.");
            } else {
                // Initialise new chart view with current monitored patients.
                CholesterolChartView chartView = new CholesterolChartView(tcView.getMonitoredPatients());
                TCChartController chartController = new TCChartController(patientsView,
                        chartView, patientUpdater);

                // Build the view.
                chartController.buildView();
            }
        }
    }

    /***
     * A class to listen to the blood pressure table selection.
     *
     */
    private class TableSelectionListener implements ListSelectionListener {

        /***
         *  Gets the information of the patient whose row is currently being selected and shows it onto the view.
         *
         * @param e     the event being performed
         */
        @Override
        public void valueChanged(ListSelectionEvent e) {
            try {
                assert !tcView.getTcTable().getSelectionModel().isSelectionEmpty();
                JTable table = tcView.getTcTable();
                int row = table.getSelectedRow();
                Object name = table.getValueAt(row,0);
                String nameStr = name.toString();
                List<Patient> patients = tcView.getMonitoredPatients();
                for (Patient p : patients) {
                    if (p.toString().equals(nameStr)) {
                        tcView.setPatientInfo(p.getBirthDate(), p.getGender(), p.getCountry(), p.getCity(), p.getState());
                    }
                }
            } catch (Exception ex) {
                System.out.println("No patient selected.");
            }
        }
    }
}
