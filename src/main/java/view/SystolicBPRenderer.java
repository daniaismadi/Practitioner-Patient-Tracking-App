package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class SystolicBPRenderer extends DefaultTableCellRenderer {

    double systolicBP;

    public SystolicBPRenderer(double systolicBP) {
        this.systolicBP = systolicBP;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        String bpVal = (String) table.getModel().getValueAt(row, 3);
        bpVal = bpVal.replace(" mmHg", "");

        try {
            double sysBp = Double.valueOf(bpVal);

            if (sysBp > systolicBP) {
                // highlight colour to purple
                cell.setForeground(new Color(138, 43, 226));
            } else {
                cell.setForeground(Color.black);
            }
        } catch (NumberFormatException e) {
            cell.setForeground(Color.black);
        }

        return cell;
    }
}
