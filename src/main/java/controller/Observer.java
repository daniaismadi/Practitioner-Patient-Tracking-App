package controller;

import view.Patient;

import java.util.ArrayList;

/***
 * Interface for Observer.
 *
 */
public interface Observer {

    /***
     * Updates patient with new measurements.
     *
     * @param patient   the patient to update
     */
    void update(Patient patient);

}
