package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class BloodPressureView {
    private JTable bpTable;
    private DefaultTableModel bpTableModel;
    private ArrayList<Patient> monitoredPatients;
    private JScrollPane highSystolicBP;
    private JPanel BPMonitor;
    private JButton removeBtn;
    private double systolicBP;
    private double diastolicBP;

    public BloodPressureView() {
        // Initialise columns on table.
        this.bpTableModel = new DefaultTableModel();
        bpTable.setModel(bpTableModel);
        bpTableModel.addColumn("NAME");
        bpTableModel.addColumn("SYSTOLIC BLOOD PRESSURE");
        bpTableModel.addColumn("DIASTOLIC BLOOD PRESSURE");
        bpTableModel.addColumn("TIME");
        bpTableModel.setRowCount(0);

        this.monitoredPatients = new ArrayList<>();
    }

    public JPanel getBPMonitor() {
        return BPMonitor;
    }

    public double getSystolicBP() {
        return systolicBP;
    }

    public void setSystolicBP(double systolicBP) {
        this.systolicBP = systolicBP;
    }

    public double getDiastolicBP() {
        return diastolicBP;
    }

    public void setDiastolicBP(double diastolicBP) {
        this.diastolicBP = diastolicBP;
    }

    public JTable getBpTable() {
        return bpTable;
    }

    public DefaultTableModel getBpTableModel() {
        return bpTableModel;
    }

    public void addPatientToMonitor(Patient patient) {
        monitoredPatients.add(patient);
    }

    public void removePatientFromMonitor(Patient p) {
        monitoredPatients.remove(p);
    }

    public ArrayList<Patient> getMonitoredPatients() {
        return monitoredPatients;
    }

    public void addRowToBPTable(Object[] objects) {
        this.bpTableModel.addRow(objects);
    }

    public void updateSystolicColumn() {
        bpTable.getColumnModel().getColumn(1).setCellRenderer(new SystolicBPRenderer(systolicBP));
    }

    public void updateDiastolicColumn() {
        bpTable.getColumnModel().getColumn(2).setCellRenderer(new DiastolicBPRenderer(diastolicBP));
    }

    public void addRemoveBtnListener(ActionListener listenForRemoveBtn) {
        removeBtn.addActionListener(listenForRemoveBtn);
    }

}
