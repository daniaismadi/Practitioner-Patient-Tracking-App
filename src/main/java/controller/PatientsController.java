package controller;

import database.DBModel;
import view.Patient;
import view.PatientsView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

public class PatientsController{

    private PatientsView theView;
    private DBModel theModel;
    private java.util.Timer timer;

    public PatientsController(PatientsView theView, DBModel theModel) {
        this.theView = theView;
        this.theModel = theModel;

        this.theView.addMonitorBtnListener(new MonitorBtnListener());
        this.theView.addRemoveBtnListener(new RemoveBtnListener());
        this.theView.addQueryBtnListener(new QueryBtnListener());
    }

    public void onStart(String hPracId) {

        timer = new java.util.Timer();
        theView.setAvgCholes(Double.POSITIVE_INFINITY);
        theView.setPatientListModel(theView.getDefaultPatientList());

        ArrayList<String> patientIds = theModel.getPatientList(hPracId);
        System.out.println(patientIds);

        for (int i = 0; i < patientIds.size(); i++) {
            String patientId = patientIds.get(i);

            // Initialise new patient.
            Patient patient = new Patient();

            // Set variables.
            patient.setId(patientId);
            patient.setGivenName(theModel.getPatientFName(patientId));
            patient.setFamilyName(theModel.getPatientLName(patientId));
            patient.setBirthDate(theModel.getPatientBirthdate(patientId));
            patient.setGender(theModel.getPatientGender(patientId));
            patient.setCountry(theModel.getPatientAddressCountry(patientId));
            patient.setCity(theModel.getPatientAddressCity(patientId));
            patient.setState(theModel.getPatientAddressState(patientId));
            patient.setTotalCholesterol(theModel.getPatientLatestCholes(patientId));
            patient.setLatestCholesterolDate(theModel.getPatientLatestCholesDate(patientId));

            // Add to default model list.
            theView.addToPutList(patient);
        }
    }

    private void calculateCholesAverage() {
        int size = theView.getMonTableRowCount();

        int totalPatients = 0;
        double totalCholes = 0;

        // iterate through people already on the monitored list
        for (int i = 0; i < size; i++) {
            String cholesStr = (String) theView.getMonTableValueAt(i, 1);

            try {
                double choles = Double.valueOf(cholesStr);
                totalPatients += 1;
                totalCholes += choles;

            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }

        // calculate total cholesterol
        if (totalPatients > 1) {
            theView.setAvgCholes(totalCholes/totalPatients);
        } else {
            theView.setAvgCholes(Double.POSITIVE_INFINITY);
        }
    }

    private boolean checkPatientAdded(String patientName) {
        // Return true if patient has already been added to the monitor list.
        List<String> monitoredPatients = new ArrayList<>();
        int size = theView.getMonTableRowCount();

        for (int i = 0; i < size; i ++) {
            String name = (String) theView.getMonTableValueAt(i, 0);
            monitoredPatients.add(name);
        }

        return monitoredPatients.contains(patientName);

    }

    private class MonitorBtnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String choles;
            String strDate = "No Value Collected Yet";

            // Update the view.
            List<Patient> p = theView.getPatientList().getSelectedValuesList();
            for (int i = 0; i < p.size(); i++) {
                Patient patient = p.get(i);
                if (!checkPatientAdded(patient.toString())) {
                    try {
                        choles = String.valueOf(patient.getTotalCholesterol());

                        DateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY HH:mm:ss");
                        Date cholesDate = patient.getLatestCholesterolDate();
                        strDate = dateFormat.format(cholesDate);
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                        choles = "No Value Collected Yet";
                    }

                    theView.addRowToTableModel(new Object[]{patient.toString(), choles, strDate});
                }
            }
            calculateCholesAverage();
            theView.updateColumnRenderer();
        }
    }

    private class RemoveBtnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int row = theView.getMonTable().getSelectedRow();
                theView.getTableModel().removeRow(row);
                calculateCholesAverage();
                theView.updateColumnRenderer();
            }
            catch (Exception k){
            }
        }
    }

    private class QueryBtnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // cancel previous time
            timer.cancel();
            timer = new java.util.Timer();

            try {
                System.out.println(theView.getQueryTimeTxt());
                int n = Integer.valueOf(theView.getQueryTimeTxt());

                // update time
                timer.schedule(new QueryObs(), 0, n*1000);

            } catch (NumberFormatException ex){
                theView.displayErrorMessage("Please enter a valid input for query time.");
            }

        }

    }

    private class QueryObs extends TimerTask {

        @Override
        public void run() {
            System.out.println("Getting new observations.");

            ListModel patients = theView.getPatientList().getModel();
            for (int i = 0; i < patients.getSize(); i++) {
                Patient p = (Patient) patients.getElementAt(i);

                String patientId = p.getId();

                // update observations
                theModel.updateObservations(patientId);

                // update latest cholesterol value
                double latestCholes = theModel.getPatientLatestCholes(patientId);
                p.setTotalCholesterol(latestCholes);

                // update latest cholesterol date
                Date latestDate = theModel.getPatientLatestCholesDate(patientId);
                p.setLatestCholesterolDate(latestDate);
            }
            // calculate new average
            calculateCholesAverage();

            // update table
            theView.updateColumnRenderer();
        }
    }
}

