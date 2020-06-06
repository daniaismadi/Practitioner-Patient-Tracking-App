package controller;

import database.DBModel;
import view.Patient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PatientGrabber implements Subject {
    private ArrayList<Observer> observers;
    private Patient patient;
//    private static int i = 100;
//    private static double j = 100;
//    private static double k = 100;

    public PatientGrabber() {
        this.observers = new ArrayList<>();
    }

    @Override
    public void register(Observer newObserver) {
        observers.add(newObserver);
    }

    @Override
    public void unregister(Observer observerToDelete) {
        observers.remove(observerToDelete);
    }


    @Override
    public void notifyObserver() {
        for (Observer observer : observers) {
            observer.update(patient);
        }
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void updatePatientCholesterol(DBModel dbModel) {
        dbModel.updateCholesObs(patient.getId());
        patient.setTotalCholesterol(dbModel.getPatientLatestCholes(patient.getId()));
        patient.setLatestCholesterolDate(dbModel.getPatientLatestCholesDate(patient.getId()));
        notifyObserver();

        // Test
//        patient.setTotalCholesterol(i);
//        patient.setLatestCholesterolDate(new Date());
//        i += 1;
    }

    public void updatePatientSystolicBP(DBModel dbModel) {
        // Trigger model to update blood pressure measurements.
        dbModel.updateBPObs(patient.getId());
        // Get 5 latest systolic BP measurements.
        patient.setSystolicBPs(dbModel.getPatientSystolicBPs(patient.getId(), 5));
        notifyObserver();

//        Test
//        Object[] obj = new Object[]{new Date(), j};
//        ArrayList<Object[]> lst = new ArrayList<>();
//        lst.add(obj);
//
//        patient.setSystolicBPs(lst);
//        j += 1;
    }

    public void updatePatientDiastolicBP(DBModel dbModel) {
//        Test
//        Object[] obj = new Object[]{new Date(), k};
//        ArrayList<Object[]> lst = new ArrayList<>();
//        lst.add(obj);
//
//        patient.setDiastolicBPs(lst);
//        k += 1;
//        notifyObserver();

        // Trigger model to update blood pressure measurements.
        dbModel.updateBPObs(patient.getId());
        // Get 5 latest diastolic BP measurements.
        patient.setDiastolicBPs(dbModel.getPatientDiastolicBPs(patient.getId(), 5));
        notifyObserver();
    }
}
