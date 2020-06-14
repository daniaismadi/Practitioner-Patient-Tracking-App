package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/***
 * Table cell renderer for Cholesterol measurements.
 */
public class CholesterolRenderer extends DefaultTableCellRenderer {

    /**
     * Average cholesterol value of monitored patients.
     */
    private double cholesterolAvg;

    /***
     * Class constructor Cholesterol Renderer. Initialises the average cholesterol value to act as a threshold.
     *
     * @param cholesterolAvg    The average cholesterol value of monitored patients.
     */
    public CholesterolRenderer(double cholesterolAvg) {
        this.cholesterolAvg = cholesterolAvg;
    }

    /***
     * Returns the component used for drawing the cell. This method is used to configure the renderer appropriately
     * before drawing. Will highlight cholesterol measurements on the table that are above the threshold.
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

        // Get value at column 1 because column 1 is the cholesterol measurement column.
        String cholesStr = (String) table.getModel().getValueAt(row, 1);

        // Parse cholesterol value from table.
        cholesStr = cholesStr.replace(" mg/dL", "");

        try {
            double choles = Double.parseDouble(cholesStr);

            if (choles > cholesterolAvg) {
                // Highlight cell red if value is above the threshold.
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
