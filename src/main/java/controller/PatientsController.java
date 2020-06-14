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

/***
 * The controller class that controls PatientsView.
 */
public class PatientsController implements Observer {

    /**
     * The view this controller controls.
     */
    private PatientsView patientsView;

    /**
     * The model class that this controller communicates with.
     */
    private DBModel dbModel;

    /**
     * The query timer that will allow new observations to be updated automatically.
     */
    private java.util.Timer queryTimer;

    /**
     * The subject that this class subscribes to in order to update patient measurements.
     */
    private PatientUpdater patientUpdater;

    /***
     * Initialises all required variables for the Patients View.
     *
     * @param patientsView      the view that this controller controls
     * @param dbModel           the model that provides the view with information
     * @param patientUpdater    the concrete subject class which grabs new information about the patient
     */
    public PatientsController(PatientsView patientsView, DBModel dbModel, PatientUpdater patientUpdater) {
        this.patientsView = patientsView;
        this.dbModel = dbModel;
        this.patientUpdater = patientUpdater;
        this.patientUpdater.register(this);

        this.patientsView.setSize(1500,800);
        // Set patients list model.
        this.patientsView.setPatientListModel();
        // update patient list
        ArrayList<String> patientIds = dbModel.getPatientList(this.patientsView.getHPracId());
        createPatients(patientIds);

        // new query timer, set to 60 seconds at first
        queryTimer = new java.util.Timer();
        queryTimer.schedule(new QueryObs(), 0, 60*1000);

        this.patientsView.addQueryBtnListener(new QueryBtnListener());
    }

    /***
     * Create patient objects for this list of patients.
     *
     * @param patientIds    the IDs of the patients to create
     */
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
            patientsView.addToPatientList(patient);
        }
    }

    /***
     * Update patient measurements.
     *
     * @param patient   the patient to update
     */
    @Override
    public void update(Patient patient) {
        ;
    }

    /***
     * THe listener for the query button.
     */
    private class QueryBtnListener implements ActionListener {

        /***
         * Invoked when the query button is clicked. Will set the new query time to the time set by the practitioner.
         * The patients will then be updated automatically based on this new query time.
         *
         * @param e     the event that was performed
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            // cancel previous time
            queryTimer.cancel();
            queryTimer = new java.util.Timer();

            try {
                int n = Integer.parseInt(patientsView.getQueryTimeTxt());

                // update time
                queryTimer.schedule(new QueryObs(), 0, n*1000);

            } catch (NumberFormatException ex){
                patientsView.displayErrorMessage("Please enter a valid input for query time.");
            }

        }

    }

    /***
     * Class that will allow patient observations to be updated automatically.
     */
    private class QueryObs extends TimerTask {

        /***
         * Invoked every N seconds (based on the value of query timer set by the practitioner). Calls upon
         * patientUpdater to update observations for every one of the practitioner's patients.
         *
         */
        @Override
        public void run() {
            System.out.println("Getting new observations.");

             ListModel<Patient> patients = patientsView.getPatientList().getModel();

            for (int i = 0; i < patients.getSize(); i++) {
                Patient p = patients.getElementAt(i);
                patientUpdater.setPatient(p);
                patientUpdater.updatePatientCholesterol(dbModel);
                patientUpdater.updatePatientDiastolicBP(dbModel);
                patientUpdater.updatePatientSystolicBP(dbModel);
            }
        }
    }
}

