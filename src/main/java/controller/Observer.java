package controller;

import view.Patient;

import java.util.ArrayList;

public interface Observer {

    void update(Patient patient);

}
