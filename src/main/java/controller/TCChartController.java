package controller;

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
import view.CholesterolChartView;
import view.Patient;
import view.PatientsView;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * The controller class that controls the Cholesterol Chart View
 */
public class TCChartController implements Observer, ChangeListener {

    /**
     * The view this controller controls.
     */
    private CholesterolChartView cholesterolChartView;

    /**
     * The main view this controller depends on.
     */
    private PatientsView patientsView;

    /**
     * The frame that this controller creates.
     */
    private JFrame frame;

    /**
     * The subject that this class subscribes to in order to update patient measurements.
     */
    private PatientUpdater patientUpdater;


    /***
     * Initialises all required variables.
     *
     * @param patientsView          the main view that this controller depends on
     * @param cholesterolChartView  the view this controller controls
     * @param patientUpdater        the concrete subject class which grabs new information about the patient
     */
    public TCChartController(PatientsView patientsView, CholesterolChartView cholesterolChartView, PatientUpdater patientUpdater){
        this.cholesterolChartView = cholesterolChartView;
        this.patientUpdater = patientUpdater;
        this.patientsView = patientsView;
        this.frame = new JFrame("Cholesterol Chart");

        // Register this controller as an observer.
        this.patientUpdater.register(this);
    }

    /***
     * Builds a new view with the new graph and displays frame.
     *
     */
    public void buildView() {
        frame.setDefaultCloseOperation(frame.HIDE_ON_CLOSE);
        // Build the graph view.
        buildGraph();
        frame.add(cholesterolChartView.getCholesterolPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /***
     * Builds the full cholesterol chart to display.
     *
     */
    public void buildGraph() {
        // Refresh panel.
        cholesterolChartView.getBarPanel().removeAll();

        // Create data set.
        cholesterolChartView.setDataset(new SlidingCategoryDataset(createDataSet(), 0, 5));

        // Create new chart with new values.
        JFreeChart barChart = createChart(cholesterolChartView.getDataset());

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(800, 700));

        // Put new chart in BarPanel.
        cholesterolChartView.getBarPanel().setLayout(new BorderLayout());
        cholesterolChartView.getBarPanel().add(chartPanel);

        // Build scroller for this graph.
        buildScroller(countValidData());
    }


    /***
     * Builds the scroller to display for the cholesterol chart.
     *
     * @param max   the maximum amount of data that this scroller can scroll to.
     */
    public void buildScroller(int max) {
        // Refresh scroll panel.
        cholesterolChartView.getScrollPanel().removeAll();

        // Create new scroller with extent to 1.
        cholesterolChartView.setScroller(new JScrollBar(Adjustable.HORIZONTAL, 0, 1, 0, max));
        cholesterolChartView.getScroller().getModel().addChangeListener(this);

        // Add scroller to panel.
        cholesterolChartView.getScrollPanel().add(cholesterolChartView.getScroller());
    }

    /***
     * Create a new cholesterol chart with dataset.
     *
     * @param dataset   the dataset to display in the chart
     * @return          the cholesterol chart
     */
    public JFreeChart createChart(CategoryDataset dataset){

        // A createDataSet method is being invoked in the arguments, to set the values to be plotted in the graph
        JFreeChart barChart = ChartFactory.createBarChart(
                "Total Cholesterol",
                "",
                "mg/dL",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false);

        CategoryPlot plot = (CategoryPlot) barChart.getPlot();

        // Rotate label positions so full name can be seen.
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 5.0));

        // Set bars to yellow.
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(
                255, 255, 153));

        return barChart;
    }

    /**
     * This method creates the a dataset object with values which will be used
     * to plot the graph.
     *
     * @return an object with the values needed to plot.
     */
    public CategoryDataset createDataSet(){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Patient patient: cholesterolChartView.getPatientList()) {
            if (!(patient.getTotalCholesterol() == 0)) {
                dataset.addValue(patient.getTotalCholesterol(), "Patient", patient.toString());
            }
        }

        return dataset;
    }

    /***
     * Count how many patients have valid measurements to be able to display onto the chart.
     *
     * @return  the amount of patients with valid cholesterol measurements
     */
    public int countValidData() {
        int count = 0;

        for (Patient patient : cholesterolChartView.getPatientList()) {
            if (!(patient.getTotalCholesterol() == 0)) {
                count++;
            }
        }

        return count;
    }

    /***
     * Updates the chart view when all patient measurements have been updated.
     *
     * @param patient   the patient that is being updated
     */
    @Override
    public void update(Patient patient) {
        if (patientsView.isUpdateFinished()) {
            buildGraph();
            SwingUtilities.updateComponentTreeUI(frame);
        }
    }

    /***
     * Listens to the scroller and changes the view of the graph based on the position of the scroller.
     *
     * @param e     the event in which the scroller changes
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        cholesterolChartView.getDataset().setFirstCategoryIndex(cholesterolChartView.getScroller().getValue());
    }
}