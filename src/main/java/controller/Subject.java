package controller;

/**
 * Interface for Subject.
 *
 */
public interface Subject {

    /***
     * Registers a new observer to this subject.
     *
     * @param newObserver   the new observer to register
     */
    void register(Observer newObserver);

    /***
     * Unregisters this observer from this subject.
     *
     * @param observerToDelete  the observer to delete
     */
    void unregister(Observer observerToDelete);

    /***
     * Notifies observers subscribed to this subject that there has been a change.
     *
     */
    void notifyObserver();

}
