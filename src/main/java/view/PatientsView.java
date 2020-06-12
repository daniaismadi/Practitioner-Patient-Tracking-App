package view;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class PatientsView extends JFrame {
    private JTabbedPane Monitor;
    JPanel rootPanel;
    private JList<Patient> patientList;
    private JButton monitorBtn;
    private JTextField queryTimeTxt;
    private JButton queryBtn;
    private JTextField diastolicBPTxt;
    private JButton setDiastolicBPBtn;
    private JTextField systolicBPTxt;
    private JButton setSystolicBPBtn;
    private JRadioButton monitorCholesterolBtn;
    private JRadioButton monitorBPBtn;
    private DefaultListModel<Patient> defaultPatientList;
    private String hPracId;

    public PatientsView(String hPracId) {

        this.hPracId = hPracId;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(rootPanel);
        this.pack();

        this.defaultPatientList = new DefaultListModel<Patient>();
    }

    public void addTabPane(String tabName, JPanel newPanel) {
        Monitor.add(tabName, newPanel);
    }

    public boolean monitorCholesterol() {
        return monitorCholesterolBtn.isSelected();
    }

    public boolean monitorBP() {
        return monitorBPBtn.isSelected();
    }

    public DefaultListModel getDefaultPatientList() {
        return this.defaultPatientList;
    }

    public JList<Patient> getPatientList() {
        return this.patientList;
    }

    public void addToPutList(Patient patient) {
        this.defaultPatientList.addElement(patient);
    }

    public String gethPracId() {
        return hPracId;
    }

    public void setPatientListModel(DefaultListModel model) {
        this.patientList.setModel(defaultPatientList);
    }


    public String getQueryTimeTxt() {
        return queryTimeTxt.getText();
    }

    public String getSystolicBPTxt() {
        return systolicBPTxt.getText();
    }

    public String getDiastolicBPTxt() {
        return diastolicBPTxt.getText();
    }

    public void addMonitorBtnListener(ActionListener listenForMonitorBtn) {
        monitorBtn.addActionListener(listenForMonitorBtn);
    }


    public void addQueryBtnListener(ActionListener listenForQueryBtn) {
        queryBtn.addActionListener(listenForQueryBtn);
    }

    public void addSystolicBPBtnListener(ActionListener listenForSystolicBPBtn) {
        setSystolicBPBtn.addActionListener(listenForSystolicBPBtn);
    }

    public void addDiastolicBPBtnListener(ActionListener listenForDiastolicBPBtn) {
        setDiastolicBPBtn.addActionListener(listenForDiastolicBPBtn);
    }

    public void displayErrorMessage(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage);
    }

}
