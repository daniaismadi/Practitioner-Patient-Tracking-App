package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/***
 * Table cell renderer for Diastolic BP measurements.
 */
public class DiastolicBPRenderer extends DefaultTableCellRenderer {

    /**
     *  Diastolic BP threshold.
     */
    private double diastolicBP;

    /***
     * Class constructor for DiastolicBPRenderer. Initialises a diastolic blood pressure measurement to act as
     * a threshold.
     *
     * @param diastolicBP       the diastolic blood pressure threshold
     */
    public DiastolicBPRenderer(double diastolicBP) {
        this.diastolicBP = diastolicBP;
    }

    /***
     * Returns the component used for drawing the cell. This method is used to configure the renderer appropriately
     * before drawing. Will highlight diastolic BP measurements on the table that are above the threshold.
     *
     * @param table         the JTable that is asking the renderer to draw; can be null
     * @param value         the value of the cell to be rendered
     * @param isSelected    true if the cell is to be rendered with the selection highlighted; otherwise false
     * @param hasFocus      if true, render cell appropriately
     * @param row           the row index of the cell basteing drawn
     * @param column        the column index of the cell being drawn
     * @return              the component used for drawing the cell
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Get value at column 2 because column 2 is the diastolic blood pressure column.
        String bpVal = (String) table.getModel().getValueAt(row, 2);

        // Parse value on table.
        bpVal = bpVal.replace(" mmHg", "");

        try {
            double diasBp = Double.parseDouble(bpVal);
            if (diasBp > diastolicBP) {
                // highlight colour to blue if diastolic blood pressure is above the threshold
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
