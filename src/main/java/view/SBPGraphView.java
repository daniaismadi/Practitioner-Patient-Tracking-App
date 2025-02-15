package view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * A class for the view that will show the line graph consisting monitored
 * patients' high systolic bp values.
 */
public class SBPGraphView extends JFrame {

    /**
     * Main panel which will contain the tabbed panels.
     */
    private JPanel mainPanel = new JPanel();

    /**
     * Tabbed Panel, which will contain one or more panels, within which there will be chart panels.
     */
    private JTabbedPane tabPane = new JTabbedPane();

    /**
     * Name of the patient, for a particular set of values to be plotted.
     */
    private String patientName;

    /**
     * Array list which will contain the values to be plotted for a particular patient.
     */
    private ArrayList<Double> patientPlotValues = new ArrayList<>();

    /**
     * Constructor for the class.
     * Sets the JFrame layout to Flow Layout, adds the main panel to the JFrame and adds the
     * tabbed pane to the main panel.
     */
    public SBPGraphView(){

        setLayout(new FlowLayout());
        add(mainPanel);
        mainPanel.add(tabPane);

    }

    /**
     * This method initializes one chart (plotted based on the values and
     * name that have been passed in as arguments)
     * Once chart is initialized, it is put into a chart panel, and that
     * chart panel is put into a normal panel called pane.
     * The pane will be added into the tabbed pane, i.e new tab for the new chart.
     * @param name will be the name of the patient (for who the BP values belong).
     * @param values is the array list of Systolic BP values of integer type.
     */
    public void initChart(String name, ArrayList<Double> values){

        this.patientName = name;
        this.patientPlotValues = values;

        JFreeChart linegraph = graph();
        // putting the graph into the chart panel
        ChartPanel chartPanel = new ChartPanel(linegraph);
        // making a new panel, to add the chart panel into
        JPanel pane = new JPanel();
        // adding the chart panel into the new panel
        pane.add(chartPanel);
        // adding the new panel into the tabbed panel
        tabPane.add(this.patientName, pane);

    }

    /**
     * This method creates the graph using Chart Factory (JFreeChart library).
     * @return graph named linegraph of type JFreeChart.
     */
    private JFreeChart graph(){

        // This line invokes the createDataSet() method.
        JFreeChart lineGraph = ChartFactory.createLineChart(this.patientName, "", "",
                createDataSet(), PlotOrientation.VERTICAL,false,true,false);

        return lineGraph;
    }

    /**
     * This method creates the a dataset object with values which will be used
     * to plot the graph.
     * @return an object with the values needed to plot.
     */
    private CategoryDataset createDataSet(){

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Integer i = 1;

        for (Double value: this.patientPlotValues) {
            dataset.addValue(value,"Systolic Blood Pressure", i);
            i = i + 1;
        }

        return dataset;
    }

    /**
     * Sole purpose of this void method is to pack and show the JFrame.
     */
    public void showView(){

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.pack();
        this.setVisible(true);

    }

    /**
     * This method is a getter for the tabbed pane.
     * @return tabPane which will contain all the Panels, that will contain a chart
     */
    public JTabbedPane getTabPane() {
        return tabPane;
    }

    /**
     * This method is a getter for the main panel.
     * @return mainPanel  which will contain all the panels within
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }

    /**
     * This method sets the a tabbed pane to this.tabPane
     * @param tabPane  A tab pane which will contain panels (containing chart panels)
     */
    public void setTabPane(JTabbedPane tabPane) {
        this.tabPane = tabPane;
    }


}
