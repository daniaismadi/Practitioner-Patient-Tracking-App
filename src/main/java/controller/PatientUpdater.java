package controller;

import database.DBModel;
import observer.Observer;
import observer.Subject;
import view.Patient;

import java.util.ArrayList;
import java.util.Date;

/***
 * Concrete subject class that implements Subject.
 * Updates patient measurements when triggered.
 *
 */
public class PatientUpdater implements Subject {

    /**
     * List of observers that subscribe to this PatientGrabber.
     */
    private ArrayList<Observer> observers;

    /**
     * Patient to update measurements for.
     */
    private Patient patient;

//    For testing
    private int i = 100;
    private double j = 100;
//    private static double k = 100;

    /***
     * Class constructor for PatientGrabber. Initialises observers list to an empty ArrayList.
     *
     */
    public PatientUpdater() {
        this.observers = new ArrayList<>();
    }

    /***
     * Register a new observer.
     *
     * @param newObserver   the new observer to add
     */
    @Override
    public void register(Observer newObserver) {
        observers.add(newObserver);
    }

    /***
     * Unregister this observer from the list of observers.
     *
     * @param observerToDelete  the observer to delete
     */
    @Override
    public void unregister(Observer observerToDelete) {
        observers.remove(observerToDelete);
    }


    /***
     * Notify observers that there has been a change in this patient's measurements.
     *
     */
    @Override
    public void notifyObserver() {
        for (Observer observer : observers) {
            observer.update();
        }
    }

    /***
     * Sets patient to update.
     *
     * @param patient   the patient to update measurements for
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    /***
     * Updates patient's cholesterol measurements.
     *
     * @param dbModel   connection to database to trigger the update of measurements and retrieve measurements
     */
    public void updatePatientCholesterol(DBModel dbModel) {
        // Trigger model to update cholesterol values.
        dbModel.updateCholesObs(patient.getId());

        // Set latest total cholesterol values.
        patient.setTotalCholesterol(dbModel.getPatientLatestCholes(patient.getId()));
        patient.setLatestCholesterolDate(dbModel.getPatientLatestCholesDate(patient.getId()));

//        Test
//        patient.setTotalCholesterol(i);
//        patient.setLatestCholesterolDate(new Date());
//        i += 1;
    }

    /***
     * Updates patient's systolic blood pressure measurements.
     *
     * @param dbModel   connection to database to trigger the update of measurements and retrieve measurements
     */
    public void updatePatientSystolicBP(DBModel dbModel) {

        // Trigger model to update blood pressure measurements.
        dbModel.updateBPObs(patient.getId());
        // Get 5 latest systolic BP measurements.
        patient.setSystolicBPs(dbModel.getPatientSystolicBPs(patient.getId(), 5));

//        Test
//        ArrayList<Object[]> lst = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            Object[] obj = new Object[]{new Date(), j};
//            j++;
//            lst.add(obj);
//        }
//
//        patient.setSystolicBPs(lst);
    }

    /***
     * Updates patient's diastolic blood pressure measurements.
     *
     * @param dbModel   connection to database to trigger the update of measurements and retrieve measurements
     */
    public void updatePatientDiastolicBP(DBModel dbModel) {
        // Trigger model to update blood pressure measurements.
        dbModel.updateBPObs(patient.getId());
        // Get 5 latest diastolic BP measurements.
        patient.setDiastolicBPs(dbModel.getPatientDiastolicBPs(patient.getId(), 5));

//        Test
//        Object[] obj = new Object[]{new Date(), k};
//        ArrayList<Object[]> lst = new ArrayList<>();
//        lst.add(obj);
//
//        patient.setDiastolicBPs(lst);
//        k += 1;
//        notifyObserver();
    }
}
