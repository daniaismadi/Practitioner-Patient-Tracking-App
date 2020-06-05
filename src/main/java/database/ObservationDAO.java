package database;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface ObservationDAO {

    void insertPatientObsByCode(String patientId, String code, String count);
    void insertCholesterolObs(String patientId, int count);
    void insertBPObs(String patientId, int count);
    List<Object[]> getCholesterolObs(String patientId, int count) throws ParseException;
    List<Object[]> getSystolicBPObs(String patientId, int count) throws ParseException;
    List<Object[]> getDiastolicBPObs(String patientId, int count) throws ParseException;

    void insertCholesObsByCodes(ArrayList<String> codes, String count, int pageCount);
//    void insertLatestCholesObs(String patientId);
//    void insertPatientLatestObsByCode(String patientId, String code);
//    void insertObs(String obsId);

}
