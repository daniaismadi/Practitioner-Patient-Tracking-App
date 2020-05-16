package view;

import controller.LogInController;
import database.DBModel;
import database.Mongo;

public class RunApp {

    public static void main(String[] args) {

        Mongo.connect();
        LogInView theView = new LogInView();
        DBModel theModel = new DBModel();

        LogInController theController = new LogInController(theView, theModel);
        theController.updateView();

    }
}
