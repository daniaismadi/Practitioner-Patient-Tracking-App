import javax.swing.*;
import java.util.ArrayList;

public class next_f {
    JPanel panel1;
    private JLabel labelid;
    private JPanel northPanel;
    private JList list1;
    private JPanel westPanel;
    private JPanel eastPanel;
    DefaultListModel<String> patientList = new DefaultListModel();
    String id;

    public next_f(String text){
        this.id = text;
        labelid.setText(this.id);
        showList();
    }

    public void showList(){
        patientList.addElement("Kit Kat");
        patientList.addElement("Toblerone");
        patientList.addElement("Snickers");
        list1.setModel(patientList);

    }




    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setContentPane(new next_f("500").panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
