package observer;

import view.Patient;

import java.util.ArrayList;

/***
 * Interface for Observer.
 *
 */
public interface Observer {

    /***
     * Notifies all observers that there has been a change.
     *
     */
    void update();

}
