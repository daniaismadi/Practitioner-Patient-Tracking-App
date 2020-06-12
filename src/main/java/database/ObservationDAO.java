package database;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/***
 *
 */
public interface ObservationDAO {

    /***
     *
     * @param patientId
     * @param code
     * @param count
     */
    void insertPatientObsByCode(String patientId, String code, String count);

    /***
     *
     * @param patientId
     * @param count
     */
    void insertCholesterolObs(String patientId, int count);

    /***
     *
     * @param patientId
     * @param count
     */
    void insertBPObs(String patientId, int count);

    /***
     *
     * @param patientId
     * @param count
     * @return
     * @throws ParseException
     */
    List<Object[]> getCholesterolObs(String patientId, int count) throws ParseException;

    /***
     *
     * @param patientId
     * @param count
     * @return
     * @throws ParseException
     */
    List<Object[]> getSystolicBPObs(String patientId, int count) throws ParseException;

    /***
     *
     * @param patientId
     * @param count
     * @return
     * @throws ParseException
     */
    List<Object[]> getDiastolicBPObs(String patientId, int count) throws ParseException;

    /***
     *
     * @param codes
     * @param count
     * @param pageCount
     */
    void insertCholesObsByCodes(ArrayList<String> codes, String count, int pageCount);

//    void insertLatestCholesObs(String patientId);
//    void insertPatientLatestObsByCode(String patientId, String code);
//    void insertObs(String obsId);

}
