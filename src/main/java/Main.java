import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;

public class Main extends JFrame {
    private JRadioButton small, medium, large;

    private JButton button;

    public Main(String title) {
        super(title);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container contentPane = this.getContentPane();
        ButtonGroup group = new ButtonGroup();
        small = new JRadioButton("small");
        medium = new JRadioButton("medium");
        large = new JRadioButton("large");

        group.add(small);
        group.add(medium);
        group.add(large);
        button = new JButton("Click here.");
        button.setBounds(100, 50, 100, 50);
        JPanel center = new JPanel();
        center.setLayout(null);
        center.add(button);
        contentPane.add(center, BorderLayout.CENTER);

        JPanel north = new JPanel();
        north.add(small);
        north.add(medium);
        north.add(large);
        contentPane.add(north, BorderLayout.NORTH);

        ChangeSize listener = new ChangeSize(button);
        small.addItemListener(listener);
        medium.addItemListener(listener);
        large.addItemListener(listener);
    }

    public static void main(String[] args) {
        JFrame f = new Main("JRadioButtonDemo");
        f.setSize(300, 200);
        f.setVisible(true);
    }
}
class ChangeSize implements ItemListener {
    private Component component;

    public ChangeSize(Component c) {
        component = c;
    }

    public void itemStateChanged(ItemEvent e) {
        String size = (String) e.getItem();
        if (size.equals("small")) {
            component.setSize(75, 20);
        } else if (size.equals("medium")) {
            component.setSize(100, 50);
        } else if (size.equals("large")) {
            component.setSize(150, 75);
        }
    }
}