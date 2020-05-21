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

public class PatientsController{

    private PatientsView theView;
    private DBModel theModel;
    private java.util.Timer queryTimer;
    private java.util.Timer autosave;


    public PatientsController(PatientsView theView, DBModel theModel) {
        this.theView = theView;
        this.theModel = theModel;

        this.theView.setSize(400,300);

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

        // new autosave timer, save every 5 minutes
        autosave = new java.util.Timer();
        autosave.schedule(new AutoSave(), 0, 5*10000);

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
        ArrayList<String> monitoredIds = theModel.getMonitoredPatients(theView.gethPracId());

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

            // if id is in monitored list, add to monitored list
            if (monitoredIds.contains(patientId) && !checkPatientAdded(patient)) {
                theView.addMonitoredPatient(patient);
            }
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
        String choles;
        String strDate = "No Value Collected Yet";

        for (int i = 0; i < p.size(); i++) {
            Patient patient = p.get(i);
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
            // add monitored patient
            // theView.addMonitoredPatient(patient);
        }
        calculateCholesAverage();
        theView.updateColumnRenderer();
    }



    private class MonitorBtnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

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

    private class RemoveBtnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int row = theView.getMonTable().getSelectedRow();
                theView.getTableModel().removeRow(row);
                calculateCholesAverage();
                theView.updateColumnRenderer();
                // remove monitored patient
                Patient p = theView.getMonitoredPatients().get(row);
                theView.removeMonitoredPatient(row);
                theModel.removeMonitorPatient(theView.gethPracId(), p.getId());
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

                String patientId = p.getId();

                // update observations
                theModel.updateCholesObs(patientId);

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

    private class AutoSave extends TimerTask {

        @Override
        public void run() {
            System.out.println("Autosave executed");
            ArrayList<Patient> monitorPatients = theView.getMonitoredPatients();
            ArrayList<String> monitorIds = new ArrayList<>();

            // find unique patients to add
            ArrayList<String> toAdd = new ArrayList<>();
            for (Patient p : monitorPatients) {
                monitorIds.add(p.getId());

                if (!toAdd.contains(p.getId())) {
                    toAdd.add(p.getId());
                    theModel.insertMonitorPatient(theView.gethPracId(), p.getId());
                }
            }

            ArrayList<String> patientsInDB = theModel.getMonitoredPatients(theView.gethPracId());

            // remove patients
            for (String p : patientsInDB) {
                if (!monitorIds.contains(p)) {
                    theModel.removeMonitorPatient(theView.gethPracId(), p);
                }
            }

        }
    }
}

