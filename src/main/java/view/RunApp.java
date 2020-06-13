package view;

import controller.LogInController;
import database.DBModel;
import database.Mongo;

/***
 * The driver class to run the application.
 */
public class RunApp {

    /***
     * The main driver method.
     *
     * @param args      The list of arguments.
     */
    public static void main(String[] args) {

        // Connect to MongoDB.
        Mongo.connect();

        // View to initialise.
        LogInView theView = new LogInView();
        // Model to initialise.
        DBModel theModel = new DBModel();

        // Set the controller for Log In.
        LogInController theController = new LogInController(theView, theModel);

        // Update the view.
        theController.updateView();

    }
}
