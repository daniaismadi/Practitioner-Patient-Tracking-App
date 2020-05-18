package controller;

import database.DBModel;
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
                theModel.onStart(hPracId);

                // Move to next page.
                PatientsView newView = new PatientsView(hPracId);
                PatientsController patientsController = new PatientsController(newView, theModel);
                patientsController.onStart(hPracId);

                // Set current visibility to false.
                theView.setVisible(false);

                // Set next view visibility to true.
                newView.setVisible(true);


            } catch (Exception ex) {
                ex.printStackTrace();
                theView.displayErrorMessage("Invalid input. Please try again.");
            }
        }
    }
}
