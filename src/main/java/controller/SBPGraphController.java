package controller;

import view.Patient;
import view.SBPGraphView;

import java.util.ArrayList;

public class SBPGraphController implements Observer {

    private ArrayList<ArrayList<String>> data;

    public SBPGraphController(ArrayList<ArrayList<String>> set) {
        this.data = set;
        System.out.println(set);
        getViews();
    }

    public void getViews() {
        for (ArrayList<String> list: data){
            ArrayList<Integer> values = new ArrayList<>();
            for (int i = 1; i<list.size(); i++){
                values.add(Integer.valueOf(list.get(i)));
            }
            SBPGraphView graphView = new SBPGraphView(list.get(0),values);
        }

    }


    @Override
    public void update(Patient patient) {
        ;
    }

}
