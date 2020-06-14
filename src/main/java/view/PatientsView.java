package view;

import javax.swing.*;
import java.awt.event.ActionListener;

/***
 * Class that shows list of patients of a certain practitioner and has the required controls to monitor new patients
 * and set query time and diastolic and systolic blood pressure thresholds.
 *
 */
public class PatientsView extends JFrame {

    /**
     * The pane that shows all practitioner patients and settings for monitoring each patient.
     */
    private JTabbedPane patientsMonitorSettings;

    /**
     * The root panel.
     */
    private JPanel rootPanel;

    /**
     * The list of patients of this practitioner.
     */
    private JList<Patient> patientList;

    /**
     * The monitor button to monitor the new patient.
     */
    private JButton monitorBtn;

    /**
     * The text field that contains the new query time, N, that updates patient observations every N seconds.
     */
    private JTextField queryTimeTxt;

    /**
     * The query button to set the new query time.
     */
    private JButton queryBtn;

    /**
     * The text field that contains the diastolic blood pressure measurement to monitor.
     */
    private JTextField diastolicBPTxt;

    /**
     * The button that sets the diastolic blood pressure threshold.
     */
    private JButton setDiastolicBPBtn;

    /**
     * The text field that contains the systolic blood pressure measurement to monitor.
     */
    private JTextField systolicBPTxt;

    /**
     * The button that sets the systolic blood pressure threshold.
     */
    private JButton setSystolicBPBtn;

    /**
     * The radio button to indicate whether the patient's cholesterol measurements should be monitored.
     */
    private JRadioButton monitorCholesterolBtn;

    /**
     * The radio button to indicate whether the patient's blood pressure measurements should be monitored.
     */
    private JRadioButton monitorBPBtn;

    /**
     * The patient list as a DefaultListModel.
     */
    private DefaultListModel<Patient> defaultPatientList;

    /**
     * The practitioner ID.
     */
    private String hPracId;

    /***
     * Initialises all the required variables of PatientView.
     *
     * @param hPracId
     */
    public PatientsView(String hPracId) {

        this.hPracId = hPracId;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(rootPanel);
        this.pack();

        // Set default patient list to a new list model.
        this.defaultPatientList = new DefaultListModel<Patient>();
    }

    /***
     * Add tab pane to this view.
     *
     * @param tabName       the name of the tab
     * @param newPanel      the new panel to add as a tab pane
     */
    public void addTabPane(String tabName, JPanel newPanel) {
        patientsMonitorSettings.add(tabName, newPanel);
    }

    /***
     * Return true if the radio button to monitor cholesterol button is checked, false otherwise.
     *
     * @return      true if the radio button to monitor cholesterol button is checked, false otherwise
     */
    public boolean monitorCholesterol() {
        return monitorCholesterolBtn.isSelected();
    }

    /***
     * Return true if the radio button to monitor blood pressure button is checked, false otherwise.
     *
     * @return      true if the radio button to monitor blood pressure button is checked, false otherwise
     */
    public boolean monitorBP() {
        return monitorBPBtn.isSelected();
    }

    /***
     * Return the patient list as a DefaultListModel.
     *
     * @return      the patient list as a DefaultListModel
     */
    public DefaultListModel getDefaultPatientList() {
        return this.defaultPatientList;
    }

    /***
     * Return the patient list as a JList.
     *
     * @return      the patient list as a JList
     */
    public JList<Patient> getPatientList() {
        return this.patientList;
    }

    /***
     * Add patient to patient list.
     *
     * @param patient       the patient to add
     */
    public void addToPatientList(Patient patient) {
        this.defaultPatientList.addElement(patient);
    }

    /***
     * Return the practitioner ID.
     *
     * @return  the practitioner ID
     */
    public String getHPracId() {
        return hPracId;
    }

    /***
     * Set the patient list model.
     *
     */
    public void setPatientListModel() {
        this.patientList.setModel(defaultPatientList);
    }

    /***
     * Return the new query time.
     *
     * @return      the query time that was set by the practitioner
     */
    public String getQueryTimeTxt() {
        return queryTimeTxt.getText();
    }

    /***
     * Return the new systolic blood pressure threshold.
     *
     * @return      the systolic blood pressure threshold that was set by the practitioner
     */
    public String getSystolicBPTxt() {
        return systolicBPTxt.getText();
    }

    /***
     * Return the new diastolic blood pressure threshold.
     *
     * @return      the diastolic blood pressure threshold that was set by the practitioner
     */
    public String getDiastolicBPTxt() {
        return diastolicBPTxt.getText();
    }

    /***
     * Add a listener for the monitor button.
     *
     * @param listenForMonitorBtn       the listener for the monitor button
     */
    public void addMonitorBtnListener(ActionListener listenForMonitorBtn) {
        monitorBtn.addActionListener(listenForMonitorBtn);
    }


    /***
     * Add a listener for the query button.
     *
     * @param listenForQueryBtn     the listener for the query button
     */
    public void addQueryBtnListener(ActionListener listenForQueryBtn) {
        queryBtn.addActionListener(listenForQueryBtn);
    }

    /***
     * Add a listener for the systolic blood pressure button.
     *
     * @param listenForSystolicBPBtn    the listener for setting the systolic blood pressure threshold
     */
    public void addSystolicBPBtnListener(ActionListener listenForSystolicBPBtn) {
        setSystolicBPBtn.addActionListener(listenForSystolicBPBtn);
    }

    /***
     * Add a listener for the diastolic blood pressure button.
     *
     * @param listenForDiastolicBPBtn   the listener for setting the diastolic blood pressure threshold
     */
    public void addDiastolicBPBtnListener(ActionListener listenForDiastolicBPBtn) {
        setDiastolicBPBtn.addActionListener(listenForDiastolicBPBtn);
    }

    /***
     * Display an error message to the view.
     *
     * @param errorMessage      the error mesage to display
     */
    public void displayErrorMessage(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage);
    }

}
