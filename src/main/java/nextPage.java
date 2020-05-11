import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class nextPage {
    private JTabbedPane Monitor;
    JPanel rootPanel;
    private JPanel tabPane1;
    private JPanel tabPane2;
    private JPanel westPanel;
    private JPanel northEastPanel;
    private JPanel southEastPanel;
    //    private JList patientList;
//    private DefaultListModel toPut = new DefaultListModel();
    private JList<Patient> patientList;
    private JTable monTable;
    private JLabel extraInfo;
    private JButton monBttn;
    private JButton remBttn;
    private DefaultListModel<Patient> toPut = new DefaultListModel();
    private DefaultTableModel tableModel = new DefaultTableModel();
    private String arg;
    private JFrame currentFrame;
    private int average;


    public nextPage(String identification) {
        this.arg = identification;
        searchId(this.arg);
        monTable.setModel(tableModel);
        tableModel.addColumn("NAME");
        tableModel.addColumn("TOTAL CHOLESTEROL");
        tableModel.addColumn("TIME");
        tableModel.setRowCount(0);


        monBttn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Patient> p = patientList.getSelectedValuesList();
                for (Patient patient: p){
                    tableModel.addRow(new Object[]{patient.toString(), String.valueOf(patient.getTotalCholesterol()), "Time"});
                }
                List<String> cholesterolValuesStr = new ArrayList<String>();
                for (int r=0; r<tableModel.getRowCount(); r++){
                    cholesterolValuesStr.add((String) tableModel.getValueAt(r,1));
                }

                int total = 0;
                for (String value: cholesterolValuesStr) {
//                    cholesterolValuesInt.add(Integer.parseInt(value));
                    total = total + Integer.parseInt(value);
                }
                average = total/cholesterolValuesStr.size();
            }
        });
//        monTable.setDefaultRenderer(String.class, new TableTest.CustomTableRenderer());

        remBttn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int row = monTable.getSelectedRow();
                    tableModel.removeRow(row);

                }
                catch (Exception k){
                }
            }
        });

    }


    public void searchId(String id){
        /*
        This is a temporary method
         */
//        System.out.println(id);
        patientList.setModel(toPut);
        for (int i=0; i<6; i++){
            Patient patient = new Patient();
            patient.setGivenName("Junaid"+i);
            patient.setFamilyName("Althaf"+i);
            patient.setBirthDate("18th May"+i);
            patient.setGender("Male"+i);
            patient.setCountry("Sri Lanka"+i);
            patient.setCity("Colombo"+i);
            patient.setState("Western Province"+i);
            patient.setTotalCholesterol(14+i);
            toPut.addElement(patient);
        }

    }



    private class Patient{
        String familyName;
        String givenName;
        String birthDate;
        String gender;
        String city;
        String state;
        String country;
        int totalCholesterol;

        public Patient(){
        }

        public String getFamilyName() {
            return familyName;
        }

        public void setFamilyName(String familyName) {
            this.familyName = familyName;
        }

        public String getGivenName() {
            return givenName;
        }

        public void setGivenName(String givenName) {
            this.givenName = givenName;
        }

        public String getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(String birthDate) {
            this.birthDate = birthDate;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public int getTotalCholesterol() {
            return totalCholesterol;
        }

        public void setTotalCholesterol(int totalCholesterol) {
            this.totalCholesterol = totalCholesterol;
        }

        @Override
        public String toString() {
            return getGivenName()+" "+getFamilyName();
        }
    }







    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Next Page");
        nextPage page = new nextPage("800");
        page.currentFrame = frame;
        frame.setContentPane(page.rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
}
