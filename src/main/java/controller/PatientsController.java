package controller;

import database.DBModel;
import view.Patient;
import view.PatientsView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class PatientsController implements Observer {

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

        this.patientsView.addQueryBtnListener(new QueryBtnListener());
    }

    private void createPatients(List<String> patientIds) {

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

