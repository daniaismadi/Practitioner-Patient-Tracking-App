package view;

import javax.swing.*;
import java.awt.event.ActionListener;

public class LogInView extends JFrame{
    JPanel rootPanel;
    JPanel northPanel;
    JPanel westPanel;
    JPanel southPanel;
    JButton logInButton;
    JLabel header;
    JPanel eastPanel;
    JTextField practitionerId;
    private JCheckBox fetchNewEncountersCheckBox;
    private JCheckBox fetchNewObservationsCheckBox;

    LogInView() {

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(rootPanel);
        this.setSize(600, 200);

        this.pack();
    }

    public String getIDText() {
        return practitionerId.getText();
    }

    public void addLogInListener(ActionListener listenForLogInButton) {
        logInButton.addActionListener(listenForLogInButton);
    }

    public boolean fetchNewEncounters() {
        return fetchNewEncountersCheckBox.isSelected();
    }

    public boolean fetchNewObservations() {
        return fetchNewObservationsCheckBox.isSelected();
    }

    public void displayErrorMessage(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage);
    }
}
