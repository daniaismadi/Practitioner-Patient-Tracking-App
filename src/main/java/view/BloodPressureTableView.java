package view;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/***
 * A class for the view that monitors blood pressure for patients.
 *
 */
public class BloodPressureTableView extends JFrame {

    /**
     * The blood pressure table.
     */
    private JTable bpTable;

    /**
     * The blood pressure table model.
     */
    private DefaultTableModel bpTableModel;

    /**
     * The list of patients currently being monitored for their blood pressure measurements.
     */
    private ArrayList<Patient> monitoredPatients;

    /**
     * The entire view- the blood pressure panel.
     */
    private JPanel BPMonitor;

    /**
     * The high systolic blood pressure tracker panel.
     */
    private JPanel highSystolicBP;

    /**
     * The remove button to remove monitored patients.
     */
    private JButton removeBtn;

    /**
     * The high diastolic blood pressure tracker panel.
     */
    private JPanel highDiastolicBP;

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
     * Button to generate high systolic blood pressure.
     */
    private JButton generateBpButton;

    /**
     * Panel for the blood pressure table.
     */
    private JScrollPane tablePanel;

    /**
     * Panel for the systolic tracker.
     */
    private JScrollPane systolicTracker;

    /**
     * Panel for the diastolic tracker.
     */
    private JScrollPane diastolicTracker;

    /**
     * This Array will contain multiple arrays, which will contain the High Systolic Blood Pressure Values to be plotted.
     */
    private ArrayList<Patient> HighSystolicPatients;

    /**
     * The systolic blood pressure threshold.
     */
    private double systolicBP;

    /**
     * The diastolic blood pressure threshold.
     */
    private double diastolicBP;

    /***
     * Initialise the blood pressure view.
     *
     */
    public BloodPressureTableView() {
        // Initialise columns on table.
        this.bpTableModel = new DefaultTableModel();
        bpTable.setModel(bpTableModel);
        bpTableModel.addColumn("NAME");
        bpTableModel.addColumn("SYSTOLIC BLOOD PRESSURE");
        bpTableModel.addColumn("DIASTOLIC BLOOD PRESSURE");
        bpTableModel.addColumn("TIME");
        bpTableModel.setRowCount(0);

        this.monitoredPatients = new ArrayList<>();

        highSystolicBP.setLayout(new GridLayout(monitoredPatients.size(), 1));
        highDiastolicBP.setLayout(new GridLayout(monitoredPatients.size(), 1));

        // Setting the width for the appropriate panels.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int tableWidth = (int) (Math.round(screenSize.width*0.40));
        int systolicTrackerWidth = (int) (Math.round(screenSize.width*0.30));
        int diastolicTrackerWidth = (int) (Math.round(screenSize.width*0.30));

        this.tablePanel.setPreferredSize(new Dimension(tableWidth, -1));
        this.systolicTracker.setPreferredSize(new Dimension(systolicTrackerWidth, -1));
        this.diastolicTracker.setPreferredSize(new Dimension(diastolicTrackerWidth, -1));
    }

    /**
     * Set the new dataset with new patient values.
     *
     * @param highSystolicPatients   the data set to set
     */
    public void setHighSystolicPatients(ArrayList<Patient> highSystolicPatients) {
        this.HighSystolicPatients = highSystolicPatients;
    }

    /**
     * Get the current data set of patients with high systolic values.
     *
     * @return  the data set
     */
    public ArrayList<Patient> getHighSystolicPatients() {
        return HighSystolicPatients;
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
     * Add a new text pane to the high systolic blood pressure tracker.
     *
     * @param textPane
     */
    public void addToHighSystolicBPObs(JTextPane textPane) {
        highSystolicBP.add(textPane);
    }

    /***
     * Add a new text pane to the high diastolic blood pressure tracker.
     *
     * @param textPane
     */
    public void addToHighDiastolicBPObs(JTextPane textPane) {
        highDiastolicBP.add(textPane);
    }

    /***
     * Clear the high systolic blood pressure tracker view.
     *
     */
    public void clearHighSystolicBPObs() {
        highSystolicBP.getComponentCount();
        for (int i = highSystolicBP.getComponentCount()-1; i >= 0; i--) {
            highSystolicBP.remove(i);
        }
    }

    /***
     * Clear the high diastolic blood pressure tracker view.
     *
     */
    public void clearHighDiastolicBPObs() {
        highDiastolicBP.getComponentCount();
        for (int i = highDiastolicBP.getComponentCount()-1; i >= 0; i--) {
            highDiastolicBP.remove(i);
        }
    }

    /***
     * Get the entire view.
     *
     * @return      Return the entire view.
     */
    public JPanel getBPMonitor() {
        return BPMonitor;
    }

    /***
     * Get the systolic blood pressure threshold.
     *
     * @return      The systolic blood pressure threshold.
     */
    public double getSystolicBP() {
        return systolicBP;
    }

    /***
     * Set the systolic blood pressure threshold.
     *
     * @param systolicBP    The systolic blood pressure threshold.
     */
    public void setSystolicBP(double systolicBP) {
        this.systolicBP = systolicBP;
    }

    /***
     * Get the diastolic blood pressure threshold.
     *
     * @return      The diastolic blood pressure threshold.
     */
    public double getDiastolicBP() {
        return diastolicBP;
    }

    /***
     * Set the diastolic blood pressure threshold.
     *
     * @param diastolicBP   The diastolic blood pressure threshold.
     */
    public void setDiastolicBP(double diastolicBP) {
        this.diastolicBP = diastolicBP;
    }

    /***
     * Set the value in row, column to the blood pressure table to object.
     *
     * @param object    The object to update the table row, column to.
     * @param row       The row of the table.
     * @param col       The column of the table.
     */
    public void setBpTableValue(Object object, int row, int col) {
        this.bpTable.setValueAt(object, row, col);
    }

    /***
     * Get the blood pressure table.
     *
     * @return      The blood pressure table.
     */
    public JTable getBpTable() {
        return bpTable;
    }

    /***
     * Get the blood pressure table model.
     *
     * @return      The blood pressure table model.
     */
    public DefaultTableModel getBpTableModel() {
        return bpTableModel;
    }

    /***
     * Add this array of objects as a row in the blood pressure table.
     *
     * @param objects       The objects to add to the table.
     */
    public void addRowToBPTable(Object[] objects) {
        this.bpTableModel.addRow(objects);
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
     * Get the list of patients who are currently being monitored for their blood pressure measurements.
     *
     * @return      The list of patients who are currently being monitored for their blood pressure measurements.
     */
    public ArrayList<Patient> getMonitoredPatients() {
        return monitoredPatients;
    }

    /***
     * Update the systolic blood pressure column in the blood pressure table to be highlighted if it is above the
     * set systolic blood pressure threshold.
     *
     */
    public void updateSystolicColumn() {
        bpTable.getColumnModel().getColumn(1).setCellRenderer(new SystolicBPRenderer(systolicBP));
    }

    /***
     * Update the diastolic blood pressure column in the blood pressure table to be highlighted if it is above
     * the set diastolic blood pressure threshold.
     *
     */
    public void updateDiastolicColumn() {
        bpTable.getColumnModel().getColumn(2).setCellRenderer(new DiastolicBPRenderer(diastolicBP));
    }

    /***
     * Add a listener for the remove button in the view.
     *
     * @param listenForRemoveBtn    the listener to add for the remove button
     */
    public void addRemoveBtnListener(ActionListener listenForRemoveBtn) {
        removeBtn.addActionListener(listenForRemoveBtn);
    }

    /**
     * Add a listener for the generate graph button in the view.
     *
     * @param listenForGenerateBplBtn the listener to add for the generate graph button
     */
    public void addGenerateBpBtnListener(ActionListener listenForGenerateBplBtn) {
        generateBpButton.addActionListener(listenForGenerateBplBtn);
    }

    /***
     * Add a listener for the table model.
     *
     * @param listenForTableClick   the listener to add for the table
     */
    public void addTableListener(ListSelectionListener listenForTableClick) {
        bpTable.getSelectionModel().addListSelectionListener(listenForTableClick);
    }

    /***
     * Display error message to the view.
     *
     * @param errorMessage      the error message to display
     */
    public void displayErrorMessage(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage);
    }

}
