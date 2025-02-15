package controller;

import database.DBModel;
import observer.Observer;
import view.BloodPressureTableView;
import view.Patient;
import view.PatientsView;
import view.SBPGraphView;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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

import static javax.swing.BorderFactory.createLineBorder;

/***
 * Class that acts as a controller for BloodPressureView. Implements Observer to observe changes in patient
 * information.
 *
 */
public class BPTableController implements Observer {

    /***
     * The main view that this controller is connected to.
     */
    private PatientsView patientsView;

    /**
     * The view class that this controller controls.
     */
    private BloodPressureTableView bpView;

    /**
     * The model class that this controller communicates with.
     */
    private DBModel dbModel;

    /**
     * The subject that this class subscribes to in order to update patient measurements.
     */
    private PatientUpdater patientUpdater;

    /***
     * Initialises all required variables.
     *
     * @param bpView            the view that this controller controls
     * @param theModel          the model that provides the view with information
     * @param patientUpdater    the concrete subject class which grabs new information about the patient
     */
    public BPTableController(BloodPressureTableView bpView, DBModel theModel, PatientUpdater patientUpdater) {
        this.bpView = bpView;
        this.dbModel = theModel;
        this.patientUpdater = patientUpdater;
        this.patientUpdater.register(this);

        bpView.setDiastolicBP(Double.POSITIVE_INFINITY);
        bpView.setSystolicBP(Double.POSITIVE_INFINITY);

        this.bpView.addRemoveBtnListener(new RemoveBtnListener());
        this.bpView.addGenerateBpBtnListener(new GenerateSBPBtnListener());
        this.bpView.addTableListener(new TableSelectionListener());
        this.bpView.addMonitorHighSystolicListener(new HighSystolicTrackerListener());
        this.bpView.addMonitorHighDiastolicListener(new HighDiastolicTrackerListener());
    }

    /***
     * Connects this view that displays blood pressure information to the main patients view.
     *
     * @param patientsView  the main patients view
     */
    public void setPatientsView(PatientsView patientsView) {
        this.patientsView = patientsView;
        this.patientsView.addMonitorBtnListener(new BPMonitorBtnListener());
        this.patientsView.addSystolicBPBtnListener(new SystolicBPBtnListener());
        this.patientsView.addDiastolicBPBtnListener(new DiastolicBPBtnListener());
    }

    /***
     * Adds the patients in the list of patients p to the blood pressure table.
     *
     * @param p     list of patients to add to the blood pressure table
     */
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
                ;
            }

            // add latest diastolic blood pressure value
            try {
                diastolicBP = (double)patient.getDiastolicBPs().get(0)[1] + " mmHg";
                bpDate = convertDateToString((Date)patient.getDiastolicBPs().get(0)[0]);
            } catch (IndexOutOfBoundsException e) {
                ;
            }

            bpView.addRowToBPTable(new Object[]{patient.toString(), systolicBP, diastolicBP, bpDate});

        }
        bpView.updateSystolicColumn();
        bpView.updateDiastolicColumn();
    }

    /***
     * Helper function to convert date into a string format.
     *
     * @param date  the date to convert
     * @return      the date as a string
     */
    private String convertDateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return dateFormat.format(date);
    }

    /***
     * Return true if patient has already been added patients.
     *
     * @param patient   the patient to check for
     * @param patients  the list of patients to check
     * @return          true if patient has already been added to the blood monitor list, false otherwise
     */
    private boolean checkPatientAdded(Patient patient, List<Patient> patients) {
        for (Patient p : patients) {
            if (p.equals(patient)) {
                return true;
            }
        }
        return false;
    }

    /***
     * Add a text pane to the high systolic blood pressure tracker.
     *
     * @param jTextPanes    The text pane containing the information of the patient to add.
     */
    private void addHighSystolicBPObs(List<JTextPane> jTextPanes) {
        for (JTextPane jTextPane : jTextPanes) {
            bpView.addToHighSystolicBPObs(jTextPane);
        }
    }

    /***
     * Add a text pane to the high diastolic blood pressure tracker.
     *
     * @param jTextPanes    The text pane containing the information of the patient to add.
     */
    private void addHighDiastolicBPObs(List<JTextPane> jTextPanes) {
        for (JTextPane jTextPane : jTextPanes) {
            bpView.addToHighDiastolicBPObs(jTextPane);
        }
    }

    /***
     * Create a list of text panes containing information of the patient who are currently being tracked for either
     * high diastolic or high systolic blood pressure measurement.
     *
     * @param patientList       The list of patients to create text panes for.
     * @param type              The type of tracker to create text panes for.
     * @return                  The list of text panes that were created.
     */
    private List<JTextPane> createBPTracker(List<Patient> patientList, String type) {

        List<JTextPane> textPanes = new ArrayList<>();

        for (Patient p : patientList) {

            /**
             * This Array is the sub array that will contain the name of a patient
             * and his/her high systolic bp values.
             */

            List<Object[]> bps;

            if (type.equalsIgnoreCase("systolic")) {

                bps = p.getSystolicBPs();
            } else {
                bps = p.getDiastolicBPs();
            }

            // Create JTextPane.
            JTextPane textPane = new JTextPane();
            textPane.setText("\n     "+p.toString()+"\n");
            StyledDocument doc = textPane.getStyledDocument();


            for (Object[] observation : bps) {

                String date = "\n     Date: " + convertDateToString((Date)observation[0]);
                String value = ", Value: " + observation[1] + " mmHg     ";

                try {
                    doc.insertString(doc.getLength(), date + value, null);
                } catch (BadLocationException e) {
                    ;
                }
            }

            try {
                doc.insertString(doc.getLength(), "\n", null);
            } catch (BadLocationException e) {
                ;
            }

            textPane.setBorder(createLineBorder(Color.BLACK));
            textPane.setEditable(false);
            // Insert JTextPane to list.
            textPanes.add(textPane);
        }

        return textPanes;
    }

    /***
     * Update the blood pressure view to include patients who have systolic blood pressure measurements that are above
     * the currently set systolic blood pressure threshold.
     *
     */
    private void updateHighSystolicBPTracker() {
        // Update High Systolic BP Monitor.
        List<Patient> patientList = bpView.getHighSystolicPatients();
        List<JTextPane> textPanes = createBPTracker(patientList, "systolic");
        // clear current high systolic bp view.
        bpView.clearHighSystolicBPObs();
        // update view.
        addHighSystolicBPObs(textPanes);
    }

    /***
     * Update the blood pressure view to include patients who have diastolic blood pressure measurements that are
     * above the currently set diastolic blood pressure threshold.
     *
     */
    private void updateHighDiastolicBPTracker() {
        // Update High Diastolic BP Monitor.
        List<Patient> patientList = bpView.getHighDiastolicPatients();
        List<JTextPane> textPanes = createBPTracker(patientList, "diastolic");
        // clear current high systolic bp view.
        bpView.clearHighDiastolicBPObs();
        // update view.
        addHighDiastolicBPObs(textPanes);
    }

    /***
     * Overridden from the Observer interface method. Updates the view (blood pressure table and high blood pressure
     * tracker) with patient's new values.
     *
     */
    @Override
    public void update() {

        for (int i = 0; i < bpView.getMonitoredPatients().size(); i++) {
            // Get patient
            Patient patient = bpView.getMonitoredPatients().get(i);

            // update table values
            try {
                bpView.setBpTableValue(patient.getSystolicBPs().get(0)[1] + " mmHg", i, 1);
                bpView.setBpTableValue(patient.getDiastolicBPs().get(0)[1] + " mmHg", i, 2);
                bpView.setBpTableValue(convertDateToString((Date)patient.getSystolicBPs().get(0)[0]), i, 3);

                // Check if patient does not have high systolic BP anymore- remove from tracker accordingly.
                if (checkPatientAdded(patient, bpView.getHighSystolicPatients()) &&
                        (double) patient.getSystolicBPs().get(0)[1] < bpView.getSystolicBP()) {
                    bpView.getHighSystolicPatients().remove(patient);
                }
                updateHighSystolicBPTracker();

                // Check if patient does not have high diastolic BP anymore- remove from tracker accordingly.
                if (checkPatientAdded(patient, bpView.getHighDiastolicPatients()) &&
                        (double) patient.getDiastolicBPs().get(0)[1] < bpView.getDiastolicBP()) {
                    bpView.getHighDiastolicPatients().remove(patient);
                }
                updateHighDiastolicBPTracker();

            } catch (IndexOutOfBoundsException e) {
                ;
            }
        }

        // revalidate
        bpView.getBPMonitor().revalidate();
        bpView.getBPMonitor().repaint();
    }

    /***
     * A class to listen to the monitor button in patientsView.
     *
     */
    private class BPMonitorBtnListener implements ActionListener {

        /***
         * Invoked when the monitor button is clicked in the main view. Checks if the practitioner wants to monitor
         * blood pressure values for these patients as well and if yes, adds this patient to the blood pressure
         * monitor list. Updates the blood pressure table and the systolic and diastolic blood pressure trackers.
         *
         * @param e     the event that was performed
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (patientsView.monitorBP()) {
                // Update the view.
                List<Patient> p = patientsView.getPatientList().getSelectedValuesList();
                List<Patient> newPatientsToMonitor = new ArrayList<>();
                for (Patient patient : p) {
                    if (!checkPatientAdded(patient, bpView.getMonitoredPatients())) {
                        bpView.addPatientToMonitor(patient);
                        newPatientsToMonitor.add(patient);
                    }
                }
                // Add new patients to the blood pressure table.
                addToBPTable(newPatientsToMonitor);
            }
        }
    }

    /***
     * A class to listen to the remove button in the blood pressure view.
     *
     */
    private class RemoveBtnListener implements ActionListener {

        /***
         * Invoked when the remove button is clicked. Removes the corresponding patient from the blood pressure
         * monitor list and updates the blood pressure table and the systolic and diastolic blood pressure trackers.
         *
         * @param e     the event that was performed
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int row = bpView.getBpTable().getSelectedRow();
                bpView.getBpTableModel().removeRow(row);
                // remove monitored patient
                Patient p = bpView.getMonitoredPatients().get(row);
                bpView.removePatientFromMonitor(p);

                // Remove from high systolic and high diastolic patient list if they exist.
                bpView.getHighSystolicPatients().remove(p);
                bpView.getHighDiastolicPatients().remove(p);

                // Notify observers that view has changed.
                if (!patientsView.isUpdateFinished()) {
                    patientsView.setUpdateFinished(true);
                    patientUpdater.notifyObserver();
                    patientsView.setUpdateFinished(false);
                } else {
                    patientUpdater.notifyObserver();
                }

                updateHighSystolicBPTracker();
                updateHighDiastolicBPTracker();
                bpView.getBPMonitor().revalidate();
                bpView.getBPMonitor().repaint();
                // Reset patient info view state.
                bpView.resetPatientInfo();
            }
            catch (Exception k){
                System.out.println("No patient to remove.");
            }
        }
    }

    /**
     * A class to listen to the Generate Graph button in the BloodPressureTable View.
     */
    private class GenerateSBPBtnListener implements ActionListener {

        /***
         * Invoked when the generate graph button is clicked.
         * Initializes the Blood Pressure graph controller.
         *
         * @param e     the event that was performed
         */
        @Override
        public void actionPerformed(ActionEvent e) {

            // If high Systolic Blood Pressure values exist; a controller is initiated and the values are passed as arguments
            if ((bpView.getHighSystolicPatients().size() > 0)) {
                SBPGraphView sbpGraphView = new SBPGraphView();
                SBPGraphController BpGraphController = new SBPGraphController(sbpGraphView, bpView, patientUpdater, patientsView);
            }
            // Else displays error message
            else {
                bpView.displayErrorMessage("High Systolic Blood Pressure Monitor Empty. Kindly set Systolic BP " +
                        "threshold in Patients Tab.");
            }

        }
    }

    /***
     * A class to listen to systolic blood pressure button in patientsView.
     *
     */
    private class SystolicBPBtnListener implements ActionListener {

        /***
         * Gets the systolic blood pressure threshold set by the practitioner and updates the systolic blood pressure
         * tracker view.
         *
         * @param e     The event that was performed.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                double systolicBP = Double.parseDouble(patientsView.getSystolicBPTxt());
                bpView.setSystolicBP(systolicBP);
                bpView.updateSystolicColumn();

                // Remove patients that do not satisfy this new blood pressure requirement for high systolic list.
                ArrayList<Patient> highSystolicPatients = new ArrayList<>();
                for (Patient patient : bpView.getHighSystolicPatients()) {
                    if ((double)patient.getSystolicBPs().get(0)[1] > bpView.getSystolicBP()) {
                        highSystolicPatients.add(patient);
                    }
                }
                bpView.setHighSystolicPatients(highSystolicPatients);

                // Notify observers that view has changed.
                if (!patientsView.isUpdateFinished()) {
                    patientsView.setUpdateFinished(true);
                    patientUpdater.notifyObserver();
                    patientsView.setUpdateFinished(false);
                } else {
                    patientUpdater.notifyObserver();
                }

                // Update tracker view.
                updateHighSystolicBPTracker();

            } catch (NumberFormatException ex) {
                patientsView.displayErrorMessage("Please enter a valid input for systolic blood pressure.");
            }
        }
    }

    /***
     * A class to listen to the diastolic blood pressure button in patientsView.
     *
     */
    private class DiastolicBPBtnListener implements ActionListener {

        /***
         * Gets the diastolic blood pressure threshold set by the practitioner and updates the diastolic blood pressure
         * tracker view.
         *
         * @param e     The event that was performed.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                double diastolicBP = Double.parseDouble(patientsView.getDiastolicBPTxt());
                bpView.setDiastolicBP(diastolicBP);
                bpView.updateDiastolicColumn();

                // Remove patients that do not satisfy this new blood pressure requirement for high diastolic list.
                ArrayList<Patient> highDiastolicPatients = new ArrayList<>();
                for (Patient patient : bpView.getHighDiastolicPatients()) {
                    if ((double)patient.getDiastolicBPs().get(0)[1] > bpView.getDiastolicBP()) {
                        highDiastolicPatients.add(patient);
                    }
                }
                bpView.setHighDiastolicPatients(highDiastolicPatients);

                // Update BP Tracker.
                updateHighDiastolicBPTracker();

            } catch (NumberFormatException ex) {
                patientsView.displayErrorMessage("Please enter a valid input for diastolic blood pressure.");
            }
        }
    }

    /***
     * A class to listen to the blood pressure table selection.
     *
     */
    private class TableSelectionListener implements ListSelectionListener {

        /***
         *  Gets the information of the patient whose row is currently being selected and shows it onto the view.
         *
         * @param e     the event being performed
         */
        @Override
        public void valueChanged(ListSelectionEvent e) {
            try {
                assert !bpView.getBpTable().getSelectionModel().isSelectionEmpty();
                JTable table = bpView.getBpTable();
                int row = table.getSelectedRow();
                Object name = table.getValueAt(row,0);
                String nameStr = name.toString();
                List<Patient> patients = bpView.getMonitoredPatients();
                for (Patient p : patients) {
                    if (p.toString().equals(nameStr)) {
                        bpView.setPatientInfo(p.getBirthDate(), p.getGender(), p.getCountry(), p.getCity(), p.getState());
                    }
                }
            } catch (Exception ex) {
                System.out.println("No patient selected.");
            }
        }
    }

    /***
     * A class to listen to the monitor high systolic patient button in BloodPressureTableView.
     *
     */
    private class HighSystolicTrackerListener implements ActionListener {

        /***
         * Adds new patient to high systolic blood pressure tracker, if any and updates views accordingly.
         *
         * @param e     the event that was performed
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int row = bpView.getBpTable().getSelectedRow();
                // get monitored patient
                Patient p = bpView.getMonitoredPatients().get(row);

                if (p.getSystolicBPs().size() > 0 && (double)p.getSystolicBPs().get(0)[1] > bpView.getSystolicBP()) {
                    if (!checkPatientAdded(p, bpView.getHighSystolicPatients())) {
                        // Add patient to high systolic patients list.
                        bpView.getHighSystolicPatients().add(p);

                        // Notify views that something has changed.
                        if (!patientsView.isUpdateFinished()) {
                            patientsView.setUpdateFinished(true);
                            patientUpdater.notifyObserver();
                            patientsView.setUpdateFinished(false);
                        } else {
                            patientUpdater.notifyObserver();
                        }

                    } else {
                        bpView.displayErrorMessage("Patient has already been added to monitor.");
                    }
                } else {
                    bpView.displayErrorMessage("Patient does not have high systolic blood pressure measurement and" +
                            " cannot be added to the monitor.");
                }

                // Refresh view.
                updateHighSystolicBPTracker();
                bpView.getBPMonitor().revalidate();
                bpView.getBPMonitor().repaint();
            }
            catch (Exception k){
                System.out.println("No patient to add to high systolic tracker.");
            }
        }
    }

    /***
     * A class to listen to the monitor high diastolic patient button in BloodPressureTableView.
     *
     */
    private class HighDiastolicTrackerListener implements ActionListener {

        /***
         * Adds new patient to high diastolic blood pressure tracker, if any and updates views accordingly.
         *
         * @param e     the event that was performed
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int row = bpView.getBpTable().getSelectedRow();
                // get monitored patient
                Patient p = bpView.getMonitoredPatients().get(row);

                if (p.getDiastolicBPs().size() > 0 && (double)p.getDiastolicBPs().get(0)[1] > bpView.getDiastolicBP()) {
                    if (!checkPatientAdded(p, bpView.getHighDiastolicPatients())) {
                        // Add patient to high systolic patients list.
                        bpView.getHighDiastolicPatients().add(p);
                    } else{
                        bpView.displayErrorMessage("Patient has already been added to monitor.");
                    }
                } else {
                    bpView.displayErrorMessage("Patient does not have high diastolic blood pressure measurement and " +
                            "cannot be added to the monitor.");
                }

                // Refresh view.
                updateHighDiastolicBPTracker();
                bpView.getBPMonitor().revalidate();
                bpView.getBPMonitor().repaint();
            }
            catch (Exception k){
                System.out.println("No patient to add to high diastolic tracker.");
            }
        }
    }
}
