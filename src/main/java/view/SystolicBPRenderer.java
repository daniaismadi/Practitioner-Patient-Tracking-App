package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/***
 * Table cell renderer for Systolic BP measurements.
 */
public class SystolicBPRenderer extends DefaultTableCellRenderer {

    /**
     * Systolic BP threshold.
     */
    private double systolicBP;

    /***
     * Class constructor for SystolicBPRenderer. Initialises a systolic blood pressure measurement to act as
     * a threshold.
     *
     * @param systolicBP       the systolic blood pressure threshold
     */
    public SystolicBPRenderer(double systolicBP) {
        this.systolicBP = systolicBP;
    }

    /***
     * Returns the component used for drawing the cell. This method is used to configure the renderer appropriately
     * before drawing. Will highlight systolic BP measurements on the table that are above the threshold.
     *
     * @param table         the JTable that is asking the renderer to draw; can be null
     * @param value         the value of the cell to be rendered
     * @param isSelected    true if the cell is to be rendered with the selection highlighted; otherwise false
     * @param hasFocus      if true, render cell appropriately
     * @param row           the row index of the cell being drawn
     * @param column        the column index of the cell being drawn
     * @return              the component used for drawing the cell
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Get value at column 1 because column 1 is the systolic blood pressure column.
        String bpVal = (String) table.getModel().getValueAt(row, 1);

        // Parse value on table.
        bpVal = bpVal.replace(" mmHg", "");

        try {
            double sysBp = Double.parseDouble(bpVal);

            if (sysBp > systolicBP) {
                // highlight colour to purple if systolic blood pressure is above the threshold
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
