package view;

import javax.swing.*;
import java.awt.event.ActionListener;

/***
 * A class for the Log In view which extends JFrame.
 *
 */
public class LogInView extends JFrame{

    /***
     * The root panel of this view.
     */
    JPanel rootPanel;

    /**
     * The log in button.
     */
    JButton logInButton;

    /**
     * The text field where the practitioner ID is entered.
     */
    JTextField practitionerId;

    /***
     * Checkbox to indicate to fetch new encounters for this practitioner.
     */
    private JCheckBox fetchNewEncountersCheckBox;

    /***
     * Checkbox to indicate to fetch new observations for the patients of this practitioner.
     */
    private JCheckBox fetchNewObservationsCheckBox;

    /***
     * Constructor for LogInView. Initialises all the required attributes.
     */
    LogInView() {

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(rootPanel);
        // Set size of the view window.
        this.setSize(600, 200);

        this.pack();
    }

    /***
     * Get the practitioner ID from the text field.
     *
     * @return      the practitioner ID
     */
    public String getIDText() {
        return practitionerId.getText();
    }

    /***
     * Add a listener for the log in button.
     *
     * @param listenForLogInButton      the listener for the log in button
     */
    public void addLogInListener(ActionListener listenForLogInButton) {
        logInButton.addActionListener(listenForLogInButton);
    }

    /***
     * Return true if fetchNewEncounters checkbox is selected, otherwise return false.
     *
     * @return      true if fetchNewEncounters checkbox is selected, otherwise return false
     */
    public boolean fetchNewEncounters() {
        return fetchNewEncountersCheckBox.isSelected();
    }

    /***
     * Return true if fetchNewObservations checkbox is selected, otherwise return false.
     *
     * @return      true if fetchNewObservations checkbox is selected, otherwise return false
     */
    public boolean fetchNewObservations() {
        return fetchNewObservationsCheckBox.isSelected();
    }

    /***
     * Display error message to the view.
     *
     * @param errorMessage      the error message to display
     */
    public void displayErrorMessage(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage);
    }
}
