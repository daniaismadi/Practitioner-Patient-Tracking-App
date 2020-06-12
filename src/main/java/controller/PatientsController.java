package controller;

import database.DBModel;
import view.Patient;
import view.PatientsView;

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
import java.util.TimerTask;

public class PatientsController implements Observer{

    private PatientsView theView;
    private DBModel theModel;
    private java.util.Timer queryTimer;
    private java.util.Timer autosave;
    private PatientUpdater patientUpdater;


    public PatientsController(PatientsView theView, DBModel theModel, PatientUpdater patientUpdater) {
        this.theView = theView;
        this.theModel = theModel;
        this.patientUpdater = patientUpdater;
        this.patientUpdater.register(this);

        this.theView.setSize(1500,800);

        this.theView.addMonitorBtnListener(new MonitorBtnListener());
        this.theView.addRemoveBtnListener(new RemoveBtnListener());
        this.theView.addQueryBtnListener(new QueryBtnListener());
    }

    public void onStart(String hPracId) {

        theView.setAvgCholes(Double.POSITIVE_INFINITY);
        theView.setPatientListModel(theView.getDefaultPatientList());

        // update patient list
        ArrayList<String> patientIds = theModel.getPatientList(hPracId);
        createPatients(patientIds);

        // update monitored table
        List<Patient> monitoredPatients = theView.getMonitoredPatients();
        addToMonitoredPatientTable(monitoredPatients);

        // new query timer, set to 30 seconds at first
        queryTimer = new java.util.Timer();
        queryTimer.schedule(new QueryObs(), 0, 30*1000);
        setTableListener();
    }

    private void setTableListener(){
        ListSelectionModel model = theView.getMonTable().getSelectionModel();
        model.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!model.isSelectionEmpty()){
                    JTable table = theView.getMonTable();
                    int row = table.getSelectedRow();
                    Object name = table.getValueAt(row,0);
                    String nameStr = name.toString();
                    List<Patient> patients = theView.getMonitoredPatients();
                    for (Patient p : patients) {
                        if (p.toString().equals(nameStr)){
                            theView.addExtraInfo(p.getBirthDate(),p.getGender(),p.getCountry(),p.getCity(),p.getState());
                        }
                    }
                }
            }
        });
    }

    private void createPatients(List<String> patientIds) {
//        ArrayList<String> monitoredIds = theModel.getMonitoredPatients(theView.gethPracId());

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

            // set latest 5 systolic blood pressure measurements
            patient.setSystolicBPs(theModel.getPatientSystolicBPs(patientId, 5));
            // set latest 5 diastolic blood pressure measurements
            patient.setDiastolicBPs(theModel.getPatientDiastolicBPs(patientId, 5));

            // Add to default model list.
            theView.addToPutList(patient);
        }
    }

    private void calculateCholesAverage() {
//        int size = theView.getMonTableRowCount();
//
        int totalPatients = 0;
        double totalCholes = 0;

        for (Patient p : theView.getMonitoredPatients()) {
            totalCholes += p.getTotalCholesterol();
            if (p.getLatestCholesterolDate() != null) {
                totalPatients += 1;
            }
        }

        // iterate through people already on the monitored list
//        for (int i = 0; i < size; i++) {
//            String cholesStr = (String) theView.getMonTableValueAt(i, 1);
//            cholesStr = cholesStr.replace(" mg/dL", "");
//
//            try {
//                double choles = Double.valueOf(cholesStr);
//                totalPatients += 1;
//                totalCholes += choles;
//
//            } catch (NumberFormatException ex) {
//                System.out.println("No cholesterol value found.");
//            }
//        }

        // calculate total cholesterol
        if (totalPatients > 1) {
            theView.setAvgCholes(totalCholes/totalPatients);
        } else {
            theView.setAvgCholes(Double.POSITIVE_INFINITY);
        }
    }

    private boolean checkPatientAdded(Patient patient) {
        // Return true if patient has already been added to the monitor list.
        List<Patient> patients = theView.getMonitoredPatients();
        for (Patient p : patients) {
            if (p.equals(patient)) {
                return true;
            }
        }
        return false;

    }

    private void addToMonitoredPatientTable(List<Patient> p) {

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

            theView.addRowToTableModel(new Object[]{patient.toString(), cholesterol, cholesterolDate});

        }
        calculateCholesAverage();
        theView.updateCholesterolColumn();
    }

    private String convertDateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return dateFormat.format(date);
    }

    @Override
    public void update(Patient patient) {
        System.out.println("Cholesterol view updated.");
        // calculate new average
        calculateCholesAverage();
        System.out.println(theView.getAvgCholes());

        // Get patient position in monitor list if they are on the monitor list
        int i = theView.getMonitoredPatients().indexOf(patient);

        // change value in cholesterol column
        try {
            if (patient.getTotalCholesterol() > 0) {
                theView.setMonTableValueAt(patient.getTotalCholesterol() + " mg/dL", i, 1);
                theView.setMonTableValueAt(convertDateToString(patient.getLatestCholesterolDate()), i, 2);
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("No cholesterol value to update on table.");
        }

        // update table
        theView.updateCholesterolColumn();

        // revalidate panel
        theView.getTabPane2().revalidate();
        theView.getTabPane2().repaint();
    }


    private class MonitorBtnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (theView.monitorCholesterol()) {
                // Update the view.
                List<Patient> p = theView.getPatientList().getSelectedValuesList();
                List<Patient> toAdd = new ArrayList<>();
                for (Patient patient : p) {
                    if (!checkPatientAdded(patient)) {
                        theView.addMonitoredPatient(patient);
                        toAdd.add(patient);
                    }
                }
                System.out.println(theView.getMonitoredPatients());
                addToMonitoredPatientTable(toAdd);
            }
        }
    }

    private class RemoveBtnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int row = theView.getMonTable().getSelectedRow();
                theView.getTableModel().removeRow(row);
                calculateCholesAverage();
                theView.updateCholesterolColumn();
                // remove monitored patient
                Patient p = theView.getMonitoredPatients().get(row);
                theView.removeMonitoredPatient(row);
                theView.extraInfoInitialState();
            }
            catch (Exception k){
            }
        }
    }

    private class QueryBtnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // cancel previous time
            queryTimer.cancel();
            queryTimer = new java.util.Timer();

            try {
                int n = Integer.valueOf(theView.getQueryTimeTxt());

                // update time
                queryTimer.schedule(new QueryObs(), 0, n*1000);

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
                patientUpdater.setPatient(p);
                patientUpdater.updatePatientCholesterol(theModel);
                patientUpdater.updatePatientDiastolicBP(theModel);
                patientUpdater.updatePatientSystolicBP(theModel);
            }
        }
    }
}

