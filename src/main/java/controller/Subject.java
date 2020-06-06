package controller;

public interface Subject {

    void register(Observer newObserver);
    void unregister(Observer observerToDelete);
    void notifyObserver();

}
