import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UI2 {
    JPanel rootPanel;
    JPanel northPanel;
    JPanel westPanel;
    JPanel southPanel;
    JButton button1;
    JLabel header;
    JTextField textField1;
    JFrame currentFrame;
//    int input;


    public UI2() {
        button1.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
//                JOptionPane.showMessageDialog(null,textField1);
//                UI obj = new UI();
//                rootPanel.getParent().setVisible(false);
                currentFrame.setVisible(false);
                JFrame frame2 = new JFrame();
                frame2.setContentPane(new next_f(textField1.getText()).panel1);
                frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame2.pack();
                frame2.setVisible(true);




            }
        });
//        textField1.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                input = textField1.getText();
//            }
//        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("UI");
        UI2 ui2 = new UI2();
        ui2.currentFrame = frame;
//        JFrame frame = new JFrame("UI");
        frame.setContentPane(ui2.rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);


    }
}
