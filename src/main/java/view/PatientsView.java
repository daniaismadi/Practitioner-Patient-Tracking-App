package view;

import controller.Observer;
import database.DBModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class PatientsView extends JFrame {
    private JTabbedPane Monitor;
    JPanel rootPanel;
    private JPanel tabPane1;
    private JPanel tabPane2;
    private JPanel westPanel;
    private JPanel northEastPanel;
    private JPanel southEastPanel;
    private JList<Patient> patientList;
    private JTable monTable;
    private JButton monBttn;
    private JButton remBttn;
    private JTextField queryTimeTxt;
    private JButton queryBtn;
    private JLabel dob;
    private JLabel gender;
    private JLabel country;
    private JLabel city;
    private JLabel state;
    private JTextField diastolicBPTxt;
    private JButton setDiastolicBPBtn;
    private JTextField systolicBPTxt;
    private JButton setSystolicBPBtn;
    private JRadioButton monitorCholesterolBtn;
    private JRadioButton monitorBPBtn;
    private DefaultListModel<Patient> defaultPatientList;
    private DefaultTableModel tableModel;
    private String arg;
    private JFrame currentFrame;
    private String hPracId;
    private double avgCholes;
    private int queryTime = 10;

    private ArrayList<Patient> monitoredPatients;

    public PatientsView(String hPracId) {

        this.hPracId = hPracId;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(rootPanel);
        this.pack();

        this.defaultPatientList = new DefaultListModel<Patient>();
        this.tableModel = new DefaultTableModel();
        this.monitoredPatients = new ArrayList<Patient>();

        // onStart(hPracId);

        monTable.setModel(tableModel);
        tableModel.addColumn("NAME");
        tableModel.addColumn("TOTAL CHOLESTEROL");
        tableModel.addColumn("TIME");
        tableModel.setRowCount(0);

    }

    public JPanel getTabPane2() {
        return tabPane2;
    }

    public double getAvgCholes() {
        return avgCholes;
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

    public ArrayList<Patient> getMonitoredPatients() {
        return monitoredPatients;
    }

    public void addMonitoredPatient(Patient patient) {
        this.monitoredPatients.add(patient);
    }

    public void removeMonitoredPatient(int position) {
        this.monitoredPatients.remove(position);
    }

    public DefaultTableModel getTableModel() {
        return this.tableModel;
    }

    public DefaultListModel getDefaultPatientList() {
        return this.defaultPatientList;
    }

    public void updateCholesterolColumn() {
        monTable.getColumnModel().getColumn(1).setCellRenderer(new HighlightCholesRenderer(avgCholes));
    }

    public JList<Patient> getPatientList() {
        return this.patientList;
    }

    public JTable getMonTable() {
        return this.monTable;
    }

    public int getMonTableRowCount() {
        return this.monTable.getRowCount();
    }

    public Object getMonTableValueAt(int row, int col) {
        return this.monTable.getValueAt(row, col);
    }

    public void setMonTableValueAt(Object object, int row, int col) {
        this.monTable.setValueAt(object, row, col);
    }

    public void addRowToTableModel(Object[] objects) {
        this.tableModel.addRow(objects);
    }

    public void addExtraInfo(String dob, String gender, String country, String city, String state){
        this.dob.setText(dob);
        this.gender.setText(gender);
        this.country.setText(country);
        this.city.setText(city);
        this.state.setText(state);
    }

    public void extraInfoInitialState(){
        this.dob.setText("Date of Birth");
        this.gender.setText("Gender");
        this.country.setText("Country");
        this.city.setText("City");
        this.state.setText("State");
    }

    public void addToPutList(Patient patient) {
        this.defaultPatientList.addElement(patient);
    }

    public void addMonitorBtnListener(ActionListener listenForMonitorBtn) {
        monBttn.addActionListener(listenForMonitorBtn);
    }

    public void addRemoveBtnListener(ActionListener listenForRemoveBtn) {
        remBttn.addActionListener(listenForRemoveBtn);
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

    public String gethPracId() {
        return hPracId;
    }

    public void setPatientListModel(DefaultListModel model) {
        this.patientList.setModel(defaultPatientList);
    }

    public void setAvgCholes(double avgCholes) {
        this.avgCholes = avgCholes;
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

    public void displayErrorMessage(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage);
    }

}
