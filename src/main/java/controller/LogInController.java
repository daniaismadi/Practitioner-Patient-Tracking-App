package controller;

import com.mongodb.DB;
import database.DBModel;
import view.BloodPressureTableView;
import view.CholesterolTableView;
import view.LogInView;
import view.PatientsView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogInController {

    private LogInView theView;
    private DBModel theModel;

    public LogInController(LogInView theView, DBModel theModel) {
        this.theView = theView;
        this.theModel = theModel;

        this.theView.addLogInListener(new LogInListener());
        this.theView.setSize(400,300);
    }

    public void updateView() {
        theView.setVisible(true);
    }

    class LogInListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            try {
                String hPracId = theView.getIDText();

                // Query server for information about this practitioner.
                theModel.onStart(hPracId, theView.fetchNewEncounters(), theView.fetchNewObservations());

                // Move to next page by initialising patients view.
                PatientsView patientsView = new PatientsView(hPracId);

                // Initialise new patientGrabber.
                PatientUpdater patientUpdater = new PatientUpdater();

                JPanel cholesterolTableView = createCholesterolTableView(patientsView, theModel, patientUpdater);
                // Add to tab pane of original view.
                patientsView.addTabPane("Cholesterol Table", cholesterolTableView);

                JPanel bloodPressureTableView = createBPTableView(patientsView, theModel, patientUpdater);
                // Add to tab pane of original view.
                patientsView.addTabPane("Blood Pressure Table", bloodPressureTableView);

                // Initialise new Patients Controller.
                PatientsController patientsController = new PatientsController(patientsView, theModel, patientUpdater);

                // Set current visibility to false.
                theView.setVisible(false);

                // Set next view visibility to true.
                patientsView.setVisible(true);


            } catch (Exception ex) {
                ex.printStackTrace();
                theView.displayErrorMessage("Invalid input. Please try again.");
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
        public JPanel createCholesterolTableView(PatientsView patientsView, DBModel dbModel, PatientUpdater patientUpdater) {
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
        public JPanel createBPTableView(PatientsView patientsView, DBModel dbModel, PatientUpdater patientUpdater) {
            // Add new Blood Pressure view.
            BloodPressureTableView bloodPressureTableView = new BloodPressureTableView();
            BPTableController bpTableController = new BPTableController(bloodPressureTableView, theModel, patientUpdater);
            // Give Blood Pressure pane access to information in PatientsView.
            bpTableController.setPatientsView(patientsView);

            return bloodPressureTableView.getBPMonitor();
        }
    }
}
