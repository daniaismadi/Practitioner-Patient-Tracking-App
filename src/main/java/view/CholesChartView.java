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


public class CholesChartView extends ApplicationFrame {


    public CholesChartView(){
        super("Cholestrol Observations of Monitored Patients");

        JFreeChart barChart = chart();
        ChartPanel chartPanel = new ChartPanel(barChart);
        CategoryPlot categoryPlot = barChart.getCategoryPlot();
        BarRenderer br = (BarRenderer) categoryPlot.getRenderer();
        br.setMaximumBarWidth(.1); // set maximum width to 35% of chart
        setContentPane(chartPanel);
    }

    public JFreeChart chart(){
        JFreeChart barChart = ChartFactory.createBarChart("Total Cholestrol mg/dl", "","",createDataSet(), PlotOrientation.VERTICAL,false,true,false);
        return barChart;
    }


    public CategoryDataset createDataSet(){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(7,"","Junaid");
        dataset.addValue(10,"","Gaffar");
        dataset.addValue(4,"","Junaid2");
        dataset.addValue(30,"","Gaffar2");
        dataset.addValue(4,"","Junaid3");
        dataset.addValue(45,"","Gaffar3");

        return dataset;
    }

    public static void main(String[] args) {
        CholesChartView chart = new CholesChartView();
        chart.pack();
//        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);

    }
}
