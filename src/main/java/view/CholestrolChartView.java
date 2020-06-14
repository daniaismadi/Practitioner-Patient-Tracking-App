package view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.util.List;


public class CholestrolChartView extends JDialog {
    List<Patient> patientList;

    public CholestrolChartView(List<Patient> patientsList){

//        super("Cholestrol Observations of Monitored Patients");
        this.patientList = patientsList;
        JFreeChart barChart = chart();
        ChartPanel chartPanel = new ChartPanel(barChart);
        CategoryPlot categoryPlot = barChart.getCategoryPlot();
        BarRenderer br = (BarRenderer) categoryPlot.getRenderer();
        br.setMaximumBarWidth(.1); // set maximum width to 35% of chart
        setContentPane(chartPanel);
        setDefaultCloseOperation(CholestrolChartView.HIDE_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }

    public JFreeChart chart(){
        JFreeChart barChart = ChartFactory.createBarChart("Total Cholestrol mg/dl", "","",createDataSet(), PlotOrientation.VERTICAL,false,true,false);
        return barChart;
    }


    public CategoryDataset createDataSet(){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Patient patient: patientList) {
            if (!(patient.getTotalCholesterol() == 0.0)) {
                dataset.addValue(patient.getTotalCholesterol(), patient.getId(), patient.toString());
            }
        }

        return dataset;
    }

}
