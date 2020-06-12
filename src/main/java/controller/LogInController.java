package controller;

import database.DBModel;
import view.BloodPressureTableView;
import view.CholesterolTableView;
import view.LogInView;
import view.PatientsView;

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

                // Move to next page.
                PatientsView patientsView = new PatientsView(hPracId);

                // Initialise new patientGrabber.
                PatientUpdater patientUpdater = new PatientUpdater();

                // Add new Cholesterol Table view.
                CholesterolTableView cholesterolTableView = new CholesterolTableView();
                TCTableController tcTableController = new TCTableController(cholesterolTableView, theModel, patientUpdater);
                // Give Cholesterol Table pane access to information in PatientsView.
                tcTableController.setPatientsView(patientsView);
                // Add to tab pane of original view.
                patientsView.addTabPane("Cholesterol Table", cholesterolTableView.getTCTableMonitor());

                // Add new Blood Pressure view.
                BloodPressureTableView bloodPressureTableView = new BloodPressureTableView();
                BPTableController bpTableController = new BPTableController(bloodPressureTableView, theModel, patientUpdater);
                // Give Blood Pressure pane access to information in PatientsView.
                bpTableController.setPatientsView(patientsView);
                // Add to tab pane of original view.
                patientsView.addTabPane("Blood Pressure", bloodPressureTableView.getBPMonitor());

                PatientsController patientsController = new PatientsController(patientsView, theModel, patientUpdater);
//                patientsController.onStart(hPracId);

                // Set current visibility to false.
                theView.setVisible(false);

                // Set next view visibility to true.
                patientsView.setVisible(true);


            } catch (Exception ex) {
                ex.printStackTrace();
                theView.displayErrorMessage("Invalid input. Please try again.");
            }
        }
    }
}
