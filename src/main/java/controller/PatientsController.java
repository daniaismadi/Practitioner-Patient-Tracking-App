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

    private PatientsView patientsView;
    private DBModel dbModel;
    private java.util.Timer queryTimer;
    private PatientUpdater patientUpdater;


    public PatientsController(PatientsView patientsView, DBModel dbModel, PatientUpdater patientUpdater) {
        this.patientsView = patientsView;
        this.dbModel = dbModel;
        this.patientUpdater = patientUpdater;
        this.patientUpdater.register(this);

        this.patientsView.setSize(1500,800);
        // Set patients list model.
        this.patientsView.setPatientListModel(patientsView.getDefaultPatientList());
        // update patient list
        ArrayList<String> patientIds = dbModel.getPatientList(this.patientsView.gethPracId());
        createPatients(patientIds);

        // new query timer, set to 60 seconds at first
        queryTimer = new java.util.Timer();
        queryTimer.schedule(new QueryObs(), 0, 60*1000);

//        this.patientsView.addMonitorBtnListener(new MonitorBtnListener());
//        this.patientsView.addRemoveBtnListener(new RemoveBtnListener());
        this.patientsView.addQueryBtnListener(new QueryBtnListener());
    }

//    public void onStart(String hPracId) {
//
////        patientsView.setAvgCholesterol(Double.POSITIVE_INFINITY);
//
//        // update patient list
//        ArrayList<String> patientIds = dbModel.getPatientList(hPracId);
//        createPatients(patientIds);
//
////        setTableListener();
//    }

//    private void setTableListener(){
//        ListSelectionModel model = patientsView.getMonTable().getSelectionModel();
//        model.addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//                if (!model.isSelectionEmpty()){
//                    JTable table = patientsView.getMonTable();
//                    int row = table.getSelectedRow();
//                    Object name = table.getValueAt(row,0);
//                    String nameStr = name.toString();
//                    List<Patient> patients = patientsView.getMonitoredPatients();
//                    for (Patient p : patients) {
//                        if (p.toString().equals(nameStr)){
//                            patientsView.addExtraInfo(p.getBirthDate(),p.getGender(),p.getCountry(),p.getCity(),p.getState());
//                        }
//                    }
//                }
//            }
//        });
//    }

    private void createPatients(List<String> patientIds) {
//        ArrayList<String> monitoredIds = theModel.getMonitoredPatients(theView.gethPracId());

        for (int i = 0; i < patientIds.size(); i++) {
            String patientId = patientIds.get(i);

            // Initialise new patient.
            Patient patient = new Patient();

            // Set variables.
            patient.setId(patientId);
            patient.setGivenName(dbModel.getPatientFName(patientId));
            patient.setFamilyName(dbModel.getPatientLName(patientId));
            patient.setBirthDate(dbModel.getPatientBirthdate(patientId));
            patient.setGender(dbModel.getPatientGender(patientId));
            patient.setCountry(dbModel.getPatientAddressCountry(patientId));
            patient.setCity(dbModel.getPatientAddressCity(patientId));
            patient.setState(dbModel.getPatientAddressState(patientId));
            patient.setTotalCholesterol(dbModel.getPatientLatestCholes(patientId));
            patient.setLatestCholesterolDate(dbModel.getPatientLatestCholesDate(patientId));

            // set latest 5 systolic blood pressure measurements
            patient.setSystolicBPs(dbModel.getPatientSystolicBPs(patientId, 5));
            // set latest 5 diastolic blood pressure measurements
            patient.setDiastolicBPs(dbModel.getPatientDiastolicBPs(patientId, 5));

            // Add to default model list.
            patientsView.addToPutList(patient);
        }
    }

    @Override
    public void update(Patient patient) {
        ;
    }

//    private void calculateCholesAverage() {
////        int size = theView.getMonTableRowCount();
////
//        int totalPatients = 0;
//        double totalCholes = 0;
//
//        for (Patient p : patientsView.getMonitoredPatients()) {
//            totalCholes += p.getTotalCholesterol();
//            if (p.getLatestCholesterolDate() != null) {
//                totalPatients += 1;
//            }
//        }
//
//        // iterate through people already on the monitored list
////        for (int i = 0; i < size; i++) {
////            String cholesStr = (String) theView.getMonTableValueAt(i, 1);
////            cholesStr = cholesStr.replace(" mg/dL", "");
////
////            try {
////                double choles = Double.valueOf(cholesStr);
////                totalPatients += 1;
////                totalCholes += choles;
////
////            } catch (NumberFormatException ex) {
////                System.out.println("No cholesterol value found.");
////            }
////        }
//
//        // calculate total cholesterol
//        if (totalPatients > 1) {
//            patientsView.setAvgCholesterol(totalCholes/totalPatients);
//        } else {
//            patientsView.setAvgCholesterol(Double.POSITIVE_INFINITY);
//        }
//    }

//    private boolean checkPatientAdded(Patient patient) {
//        // Return true if patient has already been added to the monitor list.
//        List<Patient> patients = patientsView.getMonitoredPatients();
//        for (Patient p : patients) {
//            if (p.equals(patient)) {
//                return true;
//            }
//        }
//        return false;
//
//    }
//
//    private void addToTCTable(List<Patient> p) {
//
//        for (int i = 0; i < p.size(); i++) {
//            String cholesterol;
//
//            String cholesterolDate = "-";
//
//            Patient patient = p.get(i);
//
//            // add latest cholesterol value
//            try {
//                cholesterol = patient.getTotalCholesterol() + " mg/dL";
//                cholesterolDate = convertDateToString(patient.getLatestCholesterolDate());
//            } catch (NullPointerException ex) {
//                cholesterol = "-";
//                ex.printStackTrace();
//            }
//
//            patientsView.addRowToTableModel(new Object[]{patient.toString(), cholesterol, cholesterolDate});
//
//        }
//        calculateCholesAverage();
//        patientsView.updateCholesterolColumn();
//    }

//    private String convertDateToString(Date date) {
//        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//        return dateFormat.format(date);
//    }
//
//    @Override
//    public void update(Patient patient) {
//        System.out.println("Cholesterol view updated.");
//        // calculate new average
//        calculateCholesAverage();
//        System.out.println(patientsView.getAvgCholesterol());
//
//        // Get patient position in monitor list if they are on the monitor list
//        int i = patientsView.getMonitoredPatients().indexOf(patient);
//
//        // change value in cholesterol column
//        try {
//            if (patient.getTotalCholesterol() > 0) {
//                patientsView.setMonTableValueAt(patient.getTotalCholesterol() + " mg/dL", i, 1);
//                patientsView.setMonTableValueAt(convertDateToString(patient.getLatestCholesterolDate()), i, 2);
//            }
//        } catch (IndexOutOfBoundsException e) {
//            System.out.println("No cholesterol value to update on table.");
//        }
//
//        // update table
//        patientsView.updateCholesterolColumn();
//
//        // revalidate panel
//        patientsView.getTabPane2().revalidate();
//        patientsView.getTabPane2().repaint();
//    }
//
//
//    private class MonitorBtnListener implements ActionListener {
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//
//            if (patientsView.monitorCholesterol()) {
//                // Update the view.
//                List<Patient> p = patientsView.getPatientList().getSelectedValuesList();
//                List<Patient> toAdd = new ArrayList<>();
//                for (Patient patient : p) {
//                    if (!checkPatientAdded(patient)) {
//                        patientsView.addPatientToMonitor(patient);
//                        toAdd.add(patient);
//                    }
//                }
//                System.out.println(patientsView.getMonitoredPatients());
//                addToTCTable(toAdd);
//            }
//        }
//    }
//
//    private class RemoveBtnListener implements ActionListener {
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            try {
//                int row = patientsView.getMonTable().getSelectedRow();
//                patientsView.getTableModel().removeRow(row);
//                calculateCholesAverage();
//                patientsView.updateCholesterolColumn();
//                // remove monitored patient
//                Patient p = patientsView.getMonitoredPatients().get(row);
//                patientsView.removePatientFromMonitor(row);
//                patientsView.extraInfoInitialState();
//            }
//            catch (Exception k){
//            }
//        }
//    }

    private class QueryBtnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // cancel previous time
            queryTimer.cancel();
            queryTimer = new java.util.Timer();

            try {
                int n = Integer.valueOf(patientsView.getQueryTimeTxt());

                // update time
                queryTimer.schedule(new QueryObs(), 0, n*1000);

            } catch (NumberFormatException ex){
                patientsView.displayErrorMessage("Please enter a valid input for query time.");
            }

        }

    }

    private class QueryObs extends TimerTask {

        @Override
        public void run() {
            System.out.println("Getting new observations.");

            ListModel patients = patientsView.getPatientList().getModel();
            for (int i = 0; i < patients.getSize(); i++) {
                Patient p = (Patient) patients.getElementAt(i);
                patientUpdater.setPatient(p);
                patientUpdater.updatePatientCholesterol(dbModel);
                patientUpdater.updatePatientDiastolicBP(dbModel);
                patientUpdater.updatePatientSystolicBP(dbModel);
            }
        }
    }
}

