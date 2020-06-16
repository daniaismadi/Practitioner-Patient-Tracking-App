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

//    private JScrollPane scrollPane = new JScrollPane();
    private JPanel mainPane = new JPanel();
    private JTabbedPane tabPane = new JTabbedPane();
    private String patientName;
    private ArrayList<Integer> patientPlotValues = new ArrayList<>();

    public SBPGraphView(){

        setLayout(new FlowLayout());
        add(mainPane);
        mainPane.add(tabPane);

    }

    public void makeInitChart(String name, ArrayList<Integer> values){

        this.patientName = name;
        this.patientPlotValues = values;
        JFreeChart linegraph = chart();
        ChartPanel chartPanel = new ChartPanel(linegraph);
//        chartPanel.setPreferredSize( new java.awt.Dimension( 400 , 350 ) );
        JPanel pane = new JPanel();
        pane.add(chartPanel);
        tabPane.add(this.patientName, pane);

    }

    public JFreeChart chart(){

        JFreeChart lineGraph = ChartFactory.createLineChart(this.patientName, "", "", createDataSet(), PlotOrientation.VERTICAL,false,true,false);

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

        for (Integer value: this.patientPlotValues) {
            dataset.addValue(value,"Systolic Blood Pressure", i);
            i = i + 1;
        }

        return dataset;
    }

    public void showView(){

        setDefaultCloseOperation(CholestrolChartView.HIDE_ON_CLOSE);
        this.pack();
        this.setVisible(true);

    }

    public static void main(String[] args) {
        SBPGraphView frame = new SBPGraphView();
        ArrayList<Integer> list1 = new ArrayList<>();
        list1.add(22);
        list1.add(33);
        list1.add(99);
        frame.makeInitChart("Junaid", list1);

        ArrayList<Integer> list2 = new ArrayList<>();
        list2.add(22);
        list2.add(33);
        list2.add(99);
        frame.makeInitChart("Junaid", list2);

        ArrayList<Integer> list3 = new ArrayList<>();
        list3.add(22);
        list3.add(33);
        list3.add(99);
        frame.makeInitChart("Junaid", list3);

        ArrayList<Integer> list4 = new ArrayList<>();
        list4.add(22);
        list4.add(33);
        list4.add(99);
        frame.makeInitChart("Junaid", list4);
        frame.showView();
    }


}
