package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class DiastolicBPRenderer extends DefaultTableCellRenderer {

    double diastolicBP;

    public DiastolicBPRenderer(double diastolicBP) {
        this.diastolicBP = diastolicBP;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        String bpVal = (String) table.getModel().getValueAt(row, 4);
        bpVal = bpVal.replace(" mmHg", "");

        try {
            double sysBp = Double.valueOf(bpVal);

            if (sysBp > diastolicBP) {
                // highlight colour to blue
                cell.setForeground(new Color(100,149,237));
            } else {
                cell.setForeground(Color.black);
            }
        } catch (NumberFormatException e) {
            cell.setForeground(Color.black);
        }

        return cell;
    }
}
