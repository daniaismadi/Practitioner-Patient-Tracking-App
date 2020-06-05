package controller;

import database.DBModel;
import view.BloodPressureView;
import view.Patient;
import view.PatientsView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BPMonitorController {
    private PatientsView patientsView;
    private BloodPressureView bpView;
    private DBModel dbModel;

    public BPMonitorController(BloodPressureView bpView, DBModel theModel) {
        this.bpView = bpView;
        this.dbModel = theModel;
        bpView.setDiastolicBP(Double.POSITIVE_INFINITY);
        bpView.setSystolicBP(Double.POSITIVE_INFINITY);

        this.bpView.addRemoveBtnListener(new RemoveBtnListener());
    }

    public void setPatientsView(PatientsView patientsView) {
        this.patientsView = patientsView;
        this.patientsView.addMonitorBtnListener(new BPMonitorBtnListener());
        this.patientsView.addSystolicBPBtnListener(new SystolicBPBtnListener());
        this.patientsView.addDiastolicBPBtnListener(new DiastolicBPBtnListener());
    }

    private void addToBPTable(List<Patient> p) {

        for (int i = 0; i < p.size(); i++) {
            String systolicBP = "-";
            String diastolicBP = "-";

            String bpDate = "-";

            Patient patient = p.get(i);

            // add latest systolic blood pressure value
            try {
                systolicBP = (double)patient.getSystolicBPs().get(0)[1] + " mmHg";
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            // add latest diastolic blood pressure value
            try {
                diastolicBP = (double)patient.getDiastolicBPs().get(0)[1] + " mmHg";
                bpDate = convertDateToString((Date)patient.getDiastolicBPs().get(0)[0]);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            bpView.addRowToBPTable(new Object[]{patient.toString(), systolicBP, diastolicBP, bpDate});

        }
        bpView.updateSystolicColumn();
        bpView.updateDiastolicColumn();
    }

    private String convertDateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return dateFormat.format(date);
    }

    private boolean checkPatientAdded(Patient patient) {
        // Return true if patient has already been added to the blood pressure monitor list.
        List<Patient> patients = bpView.getMonitoredPatients();
        for (Patient p : patients) {
            if (p.equals(patient)) {
                return true;
            }
        }
        return false;
    }

    private class BPMonitorBtnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (patientsView.monitorBP()) {
                // Update the view.
                List<Patient> p = patientsView.getPatientList().getSelectedValuesList();
                List<Patient> newPatientsToMonitor = new ArrayList<>();
                for (Patient patient : p) {
                    if (!checkPatientAdded(patient)) {
                        bpView.addPatientToMonitor(patient);
                        newPatientsToMonitor.add(patient);
                    }
                }
                System.out.println(bpView.getMonitoredPatients());
                addToBPTable(newPatientsToMonitor);
            }
        }
    }

    private class RemoveBtnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int row = bpView.getBpTable().getSelectedRow();
                bpView.getBpTableModel().removeRow(row);
                // remove monitored patient
                Patient p = bpView.getMonitoredPatients().get(row);
                bpView.removePatientFromMonitor(p);
                System.out.println(bpView.getMonitoredPatients());
            }
            catch (Exception k){
            }
        }
    }

    private class SystolicBPBtnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                double systolicBP = Double.parseDouble(patientsView.getSystolicBPTxt());
                bpView.setSystolicBP(systolicBP);
                bpView.updateSystolicColumn();
            } catch (NumberFormatException ex) {
                patientsView.displayErrorMessage("Please enter a valid input for systolic blood pressure.");
            }
        }
    }

    private class DiastolicBPBtnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                double diastolicBP = Double.parseDouble(patientsView.getDiastolicBPTxt());
                bpView.setDiastolicBP(diastolicBP);
                bpView.updateDiastolicColumn();
            } catch (NumberFormatException ex) {
                patientsView.displayErrorMessage("Please enter a valid input for diastolic blood pressure.");
            }
        }
    }
}
