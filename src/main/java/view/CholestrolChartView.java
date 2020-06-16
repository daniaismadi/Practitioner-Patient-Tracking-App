package view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.util.List;

/**
 * A class for the view that will show the bar chart consisting monitored
 * patients' cholestrol values.
 */
public class CholestrolChartView extends JDialog {

    /**
     * List of objects (of type Patient)
     */
    List<Patient> patientList;

    /**
     * Constructor for CholestrolChartView class.
     * It uses a JFreeChart library which gives us multiple options
     * of graphs to display.
     * @param patientsList will contain the list of patients (Object type Patient) currently being monitored.
     */
    public CholestrolChartView(List<Patient> patientsList){


        this.patientList = patientsList;
        JFreeChart barChart = chart();
        ChartPanel chartPanel = new ChartPanel(barChart);
        setContentPane(chartPanel);

        // If frame is closed, application doesn't close.
        setDefaultCloseOperation(CholestrolChartView.HIDE_ON_CLOSE);

        this.pack();
        this.setVisible(true);
    }

    /**
     * This method creates a bar chart using the JFreeChart library.
     * @return chart named barChart of type JFreeChart.
     */
    private JFreeChart chart(){

        // A createDataSet method is being invoked in the arguments, to set the values to be plotted in the graph
        JFreeChart barChart = ChartFactory.createBarChart("Total Cholestrol mg/dl", "","",createDataSet(), PlotOrientation.VERTICAL,false,true,false);

        return barChart;
    }

    /**
     * This method creates the a dataset object with values which will be used
     * to plot the graph.
     * @return an object with the values needed to plot.
     */
    private CategoryDataset createDataSet(){

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Patient patient: patientList) {

            // If patient's cholestrol value is not 0, then it is added into the data set
            if (!(patient.getTotalCholesterol() == 0.0)) {
                dataset.addValue(patient.getTotalCholesterol(), patient.getId(), patient.toString());
            }
        }

        return dataset;
    }

}
