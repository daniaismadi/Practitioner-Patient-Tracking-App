package controller;

import database.DBModel;
import view.BloodPressureView;
import view.Patient;
import view.PatientsView;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;

import static javax.swing.BorderFactory.createLineBorder;

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

    private void addHighSysBPObs(List<JTextPane> jTextPanes) {
        for (JTextPane jTextPane : jTextPanes) {
            bpView.addToHighSystolicBP(jTextPane);
        }
    }

    private List<JTextPane> createSysBPTextPanes(List<Patient> patientList) {
        List<JTextPane> textPanes = new ArrayList<>();

        for (Patient p : patientList) {
            List<Object[]> systolicBPs = p.getSystolicBPs();

            // Create JTextPane.
            JTextPane textPane = new JTextPane();
            textPane.setText("\n"+p.toString()+"\n");
            StyledDocument doc = textPane.getStyledDocument();

            for (Object[] observation : systolicBPs) {
                String date = "\n\tDate: " + convertDateToString((Date)observation[0]);
                String value = ", Value: " + observation[1] + " mmHg";

                try {
                    doc.insertString(doc.getLength(), date + value, null);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }

            try {
                doc.insertString(doc.getLength(), "\n", null);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

            textPane.setBorder(createLineBorder(Color.BLACK));
            textPane.setEditable(false);
            // Insert JTextPane to list.
            textPanes.add(textPane);
        }

        return textPanes;
    }

    private List<Patient> getHighSystolicBPs(double systolicBPThreshold) {
        List<Patient> patientList = new ArrayList<>();
        ArrayList<Patient> monitoredPatients = bpView.getMonitoredPatients();

        for (Patient p : monitoredPatients) {
            // get latest systolic blood pressure measurements
            List<Object[]> systolicBPs = p.getSystolicBPs();

            // ensure patient has at least one systolic blood pressure measurement
            if (systolicBPs.size() > 0) {
                // get latest observation
                double systolicBP = (double)systolicBPs.get(0)[1];

                // if latestVal is higher then threshold, this person has high systolic BP
                if (systolicBP > systolicBPThreshold) {
                    patientList.add(p);
                }
            }
        }

        return patientList;
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
                updateHighSystolicBP();
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
                updateHighSystolicBP();
                bpView.getBPMonitor().revalidate();
                System.out.println(bpView.getMonitoredPatients());
            }
            catch (Exception k){
            }
        }
    }

    private void updateHighSystolicBP() {
        // Update High Systolic BP Monitor.
        List<Patient> patientList = getHighSystolicBPs(bpView.getSystolicBP());
        List<JTextPane> textPanes = createSysBPTextPanes(patientList);
        // clear current high systolic bp view.
        bpView.clearHighSystolicBP();
        // update view.
        addHighSysBPObs(textPanes);
    }

    private class SystolicBPBtnListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                double systolicBP = Double.parseDouble(patientsView.getSystolicBPTxt());
                bpView.setSystolicBP(systolicBP);
                bpView.updateSystolicColumn();

                updateHighSystolicBP();

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
