package view;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/***
 * A class for the view that monitors cholesterol measurements for patients and displays it onto a table.
 */
public class CholesterolTableView {

    /**
     * The cholesterol table (tc is short for Total Cholesterol).
     */
    private JTable tcTable;

    /**
     * The cholesterol table model.
     */
    private DefaultTableModel tcTableModel;

    /**
     * The list of patients currently being monitored for their cholesterol measurements.
     */
    private ArrayList<Patient> monitoredPatients;

    /**
     * The remove button to remove monitored patients.
     */
    private JButton removeButton;

    /***
     * Date of birth label for patient selected.
     */
    private JLabel patientDOB;

    /**
     * Gender label for patient selected.
     */
    private JLabel patientGender;

    /**
     * Country label for patient selected.
     */
    private JLabel patientCountry;

    /**
     * City label for patient selected.
     */
    private JLabel patientCity;

    /**
     * State label for patient selected.
     */
    private JLabel patientState;

    /**
     * The entire view- the cholesterol monitor panel.
     */
    private JPanel TCTableMonitor;

    /**
     * The average cholesterol of all monitored patients.
     */
    private double avgCholesterol;

    /***
     * Initialise the cholesterol table view.
     */
    public CholesterolTableView () {

        // Add the columns on the cholesterol table.
        this.tcTableModel = new DefaultTableModel();
        this.monitoredPatients = new ArrayList<Patient>();

        tcTable.setModel(tcTableModel);
        tcTableModel.addColumn("NAME");
        tcTableModel.addColumn("TOTAL CHOLESTEROL");
        tcTableModel.addColumn("TIME");
        tcTableModel.setRowCount(0);
    }

    /***
     * Get the entire view.
     *
     * @return      Return the entire view.
     */
    public JPanel getTCTableMonitor() {
        return this.TCTableMonitor;
    }

    /**
     * Get the average cholesterol of the monitored patients.
     *
     * @return      the average cholesterol of the monitored patients
     */
    public double getAvgCholesterol() {
        return avgCholesterol;
    }

    /***
     * Set the average cholesterol of the monitored patients.
     *
     * @param avgCholesterol    the average cholesterol of the monitored patients
     */
    public void setAvgCholesterol(double avgCholesterol) {
        this.avgCholesterol = avgCholesterol;
    }

    /***
     * Monitor this patient's blood pressure measurements by adding this patient to the current monitor list.
     *
     * @param patient       The patient to add to the current monitor list.
     */
    public void addPatientToMonitor(Patient patient) {
        monitoredPatients.add(patient);
    }

    /***
     * Remove this patient from the list of patients who are currently being monitored for their blood pressure
     * measurements.
     *
     * @param patient     The patient to remove.
     */
    public void removePatientFromMonitor(Patient patient) {
        monitoredPatients.remove(patient);
    }

    /***
     * Update the cholesterol column to highlight the values that are above the average cholesterol.
     */
    public void updateCholesterolColumn() {
        tcTable.getColumnModel().getColumn(1).setCellRenderer(new CholesterolRenderer(avgCholesterol));
    }

    /***
     * Show patient information for patient selected.
     *
     * @param patientDOB        The date of birth of the patient.
     * @param patientGender     The gender of the patient.
     * @param patientCountry    The country where the patient is from.
     * @param patientCity       The city where the patient is from.
     * @param patientState      The state the patient is from.
     */
    public void setPatientInfo(String patientDOB, String patientGender, String patientCountry,
                               String patientCity, String patientState) {

        this.patientDOB.setText(patientDOB);
        this.patientGender.setText(patientGender);
        this.patientCountry.setText(patientCountry);
        this.patientCity.setText(patientCity);
        this.patientState.setText(patientState);
    }

    /***
     * Reset patient information to original state.
     */
    public void resetPatientInfo() {
        this.patientDOB.setText("Date of Birth");
        this.patientGender.setText("Gender");
        this.patientCountry.setText("Country");
        this.patientCity.setText("City");
        this.patientState.setText("State");
    }

    /***
     * Add a listener for the remove button in the view.
     *
     * @param listenForRemoveBtn    the listener to add for the remove button
     */
    public void addRemoveBtnListener(ActionListener listenForRemoveBtn) {
        removeButton.addActionListener(listenForRemoveBtn);
    }

    /***
     * Set the value in row, column to the total cholesterol table to object.
     *
     * @param object    The object to update the table row, column to.
     * @param row       The row of the table.
     * @param col       The column of the table.
     */
    public void setTcTableValue(Object object, int row, int col) {
        this.tcTable.setValueAt(object, row, col);
    }

    /***
     * Get the total cholesterol table.
     *
     * @return      The total cholesterol table.
     */
    public   JTable getTcTable() {
        return tcTable;
    }

    /***
     * Get the total cholesterol table model.
     *
     * @return      The total cholesterol table model.
     */
    public DefaultTableModel getTcTableModel() {
        return tcTableModel;
    }

    /***
     * Add this array of objects as a row in the total cholesterol table.
     *
     * @param objects       The objects to add to the table.
     */
    public void addRowToTcTable(Object[] objects) {
        this.tcTableModel.addRow(objects);
    }

    /***
     * Get the list of patients who are currently being monitored for their total cholesterol measurements.
     *
     * @return      The list of patients who are currently being monitored for their total cholesterol measurements.
     */
    public ArrayList<Patient> getMonitoredPatients() {
        return monitoredPatients;
    }

    /***
     * Add a listener for the table model.
     *
     * @param listenForTableClick   the listener to add for the table
     */
    public void addTableListener(ListSelectionListener listenForTableClick) {
        tcTable.getSelectionModel().addListSelectionListener(listenForTableClick);
    }
}
