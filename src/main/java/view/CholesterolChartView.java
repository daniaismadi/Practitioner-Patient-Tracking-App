package view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.SlidingCategoryDataset;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/***
 * View for cholesterol chart.
 */
public class CholesterolChartView extends JFrame {

    /**
     * The frame that this controller creates.
     */
    private JFrame frame;

    /**
     * The scroller for this chart view.
     */
    private JScrollBar scroller;

    /**
     * The entire cholesterol panel that contains the chart and the scroller.
     */
    private JPanel cholesterolPanel;

    /**
     * The panel that contains the chart.
     */
    private JPanel barPanel;

    /**
     * The panel that contains the scroller.
     */
    private JPanel scrollPanel;

    /**
     * The dataset for the chart.
     */
    private SlidingCategoryDataset dataset;

    /**
     * The list of patients in the chart.
     */
    private List<Patient> patientList;

    /***
     * Initialise a new cholesterol chart with data from patients in patientList.
     *
     * @param patientsList  the list of patients to display data for
     */
    public CholesterolChartView(ArrayList<Patient> patientsList) {
        this.patientList = patientsList;
        this.frame = new JFrame("Cholesterol Chart");
        scrollPanel.setLayout(new BorderLayout());
    }

    /***
     * Return the frame that holds the chart.
     *
     * @return      the frame that holds the chart
     */
    public JFrame getFrame() {
        return frame;
    }

    /***
     * Set the scroller for this view.
     *
     * @param scroller  the new scroller to set
     */
    public void setScroller(JScrollBar scroller) {
        this.scroller = scroller;
    }

    /***
     * Get the current scroller for this view.
     *
     * @return  the current scroller
     */
    public JScrollBar getScroller() {
        return scroller;
    }

    /***
     * Get the panel that displays the chart.
     *
     * @return  the panel that displays the chart
     */
    public JPanel getBarPanel() {
        return barPanel;
    }

    /***
     * Get the panel that displays the scroller.
     *
     * @return  the panel that displays the scroller
     */
    public JPanel getScrollPanel() {
        return scrollPanel;
    }

    /***
     * Get the dataset that this chart displays.
     *
     * @return  the dataset that this chart displays
     */
    public SlidingCategoryDataset getDataset() {
        return dataset;
    }

    /***
     * Set the dataset that this chart displays.
     *
     * @param dataset   the new dataset that this chart will display
     */
    public void setDataset(SlidingCategoryDataset dataset) {
        this.dataset = dataset;
    }

    /***
     * Get the list of patients that this chart displays.
     *
     * @return  the list of patients that this chart displays
     */
    public List<Patient> getPatientList() {
        return patientList;
    }

    /***
     * Get the entire panel.
     *
     * @return  the entire panel
     */
    public JPanel getCholesterolPanel() {
        return cholesterolPanel;
    }
}
