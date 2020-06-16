package view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A class for the view that will show the line graph consisting monitored
 * patients' high systolic bp values.
 */
public class SBPGraphView extends JDialog {

    private String name;
    private ArrayList<Integer> plotValues = new ArrayList<>();

    public SBPGraphView(String name, ArrayList<Integer> values){

        this.name = name;
        this.plotValues = values;
        JFreeChart linegraph = chart();
        ChartPanel chartPanel = new ChartPanel(linegraph);
        chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
        setContentPane(chartPanel);
        setDefaultCloseOperation(CholestrolChartView.HIDE_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }

    public JFreeChart chart(){

        JFreeChart lineGraph = ChartFactory.createLineChart(this.name, "", "", createDataSet(), PlotOrientation.VERTICAL,false,true,false);

        return lineGraph;
    }

    /**
     * This method creates the a dataset object with values which will be used
     * to plot the graph.
     * @return an object with the values needed to plot.
     */
    public CategoryDataset createDataSet(){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Integer i = 1;
        for (Integer value: this.plotValues) {
            dataset.addValue(value,"Systolic Blood Pressure", i);
//            dataset.addValue(value, "", i);
            i = i + 1;
//            System.out.println(value);
        }

        return dataset;
    }


}
