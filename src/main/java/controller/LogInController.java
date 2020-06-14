package controller;

import database.DBModel;
import view.BloodPressureTableView;
import view.CholesterolTableView;
import view.LogInView;
import view.PatientsView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/***
 * The controller class that controls LogInView.
 */
public class LogInController {

    /**
     * The view this controller controls.
     */
    private LogInView logInView;

    /**
     * The model class that this controller communicates with.
     */
    private DBModel dbModel;

    /***
     * Initialises all required variables.
     *
     * @param logInView     the view that this controller controls
     * @param dbModel       the model that provides the view with information
     */
    public LogInController(LogInView logInView, DBModel dbModel) {
        this.logInView = logInView;
        this.dbModel = dbModel;

        this.logInView.addLogInListener(new LogInListener());
        // Set preferred size.
        this.logInView.setSize(400,300);
    }

    /***
     * Set log in view to visible.
     */
    public void updateView() {
        logInView.setVisible(true);
    }

    /***
     * Listener to listen for the log in button,.
     */
    private class LogInListener implements ActionListener{

        /***
         * Invoked when the log in button is clicked. Creates required tab panes to monitor certain characteristics
         * and opens a new view that shows list of patients after the practitioner logs in.
         *
         * @param e     the event that was performed
         */
        @Override
        public void actionPerformed(ActionEvent e) {

            try {
                String hPracId = logInView.getIDText();

                // Query server for information about this practitioner.
                dbModel.onStart(hPracId, logInView.fetchNewEncounters(), logInView.fetchNewObservations());

                // Move to next page by initialising patients view.
                PatientsView patientsView = new PatientsView(hPracId);

                // Initialise new patientGrabber.
                PatientUpdater patientUpdater = new PatientUpdater();

                JPanel cholesterolTableView = createCholesterolTableView(patientsView, dbModel, patientUpdater);
                // Add to tab pane of original view.
                patientsView.addTabPane("Cholesterol Table", cholesterolTableView);

                JPanel bloodPressureTableView = createBPTableView(patientsView, dbModel, patientUpdater);
                // Add to tab pane of original view.
                patientsView.addTabPane("Blood Pressure Table", bloodPressureTableView);

                // Initialise new Patients Controller.
                PatientsController patientsController = new PatientsController(patientsView, dbModel, patientUpdater);

                // Set current visibility to false.
                logInView.setVisible(false);

                // Set next view visibility to true.
                patientsView.setVisible(true);


            } catch (Exception ex) {
                ex.printStackTrace();
                logInView.displayErrorMessage("Invalid input. Please try again.");
            }
        }

        /***
         * Create the Cholesterol Table view and attach it to the main view, patientsView.
         *
         * @param patientsView      The main view to attach this view to.
         * @param dbModel           The model that provides this view with information.
         * @param patientUpdater    The concrete subject class that updates information for this view.
         * @return                  The panel that this view is attached to.
         */
        private JPanel createCholesterolTableView(PatientsView patientsView, DBModel dbModel, PatientUpdater patientUpdater) {
            // Add new Cholesterol Table view.
            CholesterolTableView cholesterolTableView = new CholesterolTableView();
            TCTableController tcTableController = new TCTableController(cholesterolTableView, dbModel, patientUpdater);
            // Give Cholesterol Table pane access to information in PatientsView.
            tcTableController.setPatientsView(patientsView);
            // Add to tab pane of original view.

            return cholesterolTableView.getTCTableMonitor();
        }

        /***
         * Create the Blood Pressure Table view and attach it to the main view, patientsView.
         *
         * @param patientsView      The main view to attach this view to.
         * @param dbModel           The model that provides this view with information.
         * @param patientUpdater    The concrete subject class that updates information for this view.
         * @return                  The panel that this view is attached to.
         */
        private JPanel createBPTableView(PatientsView patientsView, DBModel dbModel, PatientUpdater patientUpdater) {
            // Add new Blood Pressure view.
            BloodPressureTableView bloodPressureTableView = new BloodPressureTableView();
            BPTableController bpTableController = new BPTableController(bloodPressureTableView, LogInController.this.dbModel, patientUpdater);
            // Give Blood Pressure pane access to information in PatientsView.
            bpTableController.setPatientsView(patientsView);

            return bloodPressureTableView.getBPMonitor();
        }
    }
}
