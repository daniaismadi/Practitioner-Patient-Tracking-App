package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class HighlightCholesRenderer extends DefaultTableCellRenderer {

    double cholesAvg;

    public HighlightCholesRenderer(double cholesAvg) {
        this.cholesAvg = cholesAvg;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        String cholesStr = (String) table.getModel().getValueAt(row, 1);

        try {
            double choles = Double.valueOf(cholesStr);

            if (choles > cholesAvg) {
                cell.setForeground(new Color(255, 99, 71));
            } else {
                cell.setForeground(Color.black);
            }
        } catch (NumberFormatException e) {
            cell.setForeground(Color.black);
        }

        return cell;
    }

}
