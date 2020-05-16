package view;

import database.DBModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class PatientsView extends JFrame{
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

    public void updateColumnRenderer() {
        monTable.getColumnModel().getColumn(0).setCellRenderer(new HighlightCholesRenderer(avgCholes));
        monTable.getColumnModel().getColumn(1).setCellRenderer(new HighlightCholesRenderer(avgCholes));
        monTable.getColumnModel().getColumn(2).setCellRenderer(new HighlightCholesRenderer(avgCholes));
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

    void addRowSelectionInterval(int i, int j) {
        this.monTable.addRowSelectionInterval(i, j);
    }

    void removeRowSelectionInterval(int i, int j) {
        this.monTable.removeRowSelectionInterval(i, j);
    }

    void setTableForegroundColor(Color color) {
        this.monTable.setForeground(color);
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

    public String gethPracId() {
        return hPracId;
    }

    public void sethPracId(String hPracId) {
        this.hPracId = hPracId;
    }

    public void setPatientListModel(DefaultListModel model) {
        this.patientList.setModel(defaultPatientList);
    }

    public double getAvgCholes() {
        return avgCholes;
    }

    public void setAvgCholes(double avgCholes) {
        this.avgCholes = avgCholes;
    }


    public String getQueryTimeTxt() {
        return queryTimeTxt.getText();
    }

    public void displayErrorMessage(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage);
    }
}
