import database.Mongo;

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


    public UI2() {
        button1.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {

                currentFrame.setVisible(false);
                JFrame frame2 = new JFrame();
                frame2.setContentPane(new nextPage(textField1.getText()).rootPanel);
                frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame2.pack();
                frame2.setVisible(true);




            }
        });

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("UI");
        UI2 ui2 = new UI2();
        ui2.currentFrame = frame;
        frame.setContentPane(ui2.rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        Mongo.connect();

    }
}
