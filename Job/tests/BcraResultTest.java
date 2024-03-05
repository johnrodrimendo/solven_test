/**
 * Created by solven9 on 31/01/18.
 */


import com.affirm.common.model.transactional.BcraResult;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class BcraResultTest {

    //@DisplayName("")
    @Test
    @Disabled
    public void testUnserializeBcraJson() {
        System.out.println("[Test 1] Test unserialize BCRA result JSON");
        BcraResult bcraResult = new BcraResult();
        String jsonData = "{\"js_deudor\":[{\"denominacionDeudor\":\"BARREIRO JUAN IGNACIO\",\"entidad\":\"BANCO SUPERVIELLE S.A.\",\"periodo\":\"12/17\",\"situacion\":\"1\",\"monto\":\"28\",\"diasRetraso\":\"N/A\",\"observaciones\":\"-\"},{\"denominacionDeudor\":\"BARREIRO JUAN IGNACIO\",\"entidad\":\"BBVA BANCO FRANCES S.A.\",\"periodo\":\"12/17\",\"situacion\":\"1\",\"monto\":\"12\",\"diasRetraso\":\"N/A\",\"observaciones\":\"-\"},{\"denominacionDeudor\":\"BARREIRO JUAN IGNACIO\",\"entidad\":\"CENCOSUD S.A.\",\"periodo\":\"12/17\",\"situacion\":\"1\",\"monto\":\"2\",\"diasRetraso\":\"N/A\",\"observaciones\":\"-\"}],\"js_cheque\":[],\"js_deudas\":[{\"nombre\":\"BBVA BANCO FRANCES S.A.\",\"historial\":[{\"periodo\":\"11/2017\",\"monto\":\"1\",\"situacion\":\"15,0\"},{\"periodo\":\"10/2017\",\"monto\":\"1\",\"situacion\":\"14,0\"},{\"periodo\":\"09/2017\",\"monto\":\"1\",\"situacion\":\"15,0\"},{\"periodo\":\"08/2017\",\"monto\":\"1\",\"situacion\":\"19,0\"},{\"periodo\":\"07/2017\",\"monto\":\"1\",\"situacion\":\"18,0\"},{\"periodo\":\"06/2017\",\"monto\":\"1\",\"situacion\":\"15,1\"},{\"periodo\":\"05/2017\",\"monto\":\"1\",\"situacion\":\"18,7\"},{\"periodo\":\"04/2017\",\"monto\":\"1\",\"situacion\":\"19,6\"},{\"periodo\":\"03/2017\",\"monto\":\"1\",\"situacion\":\"6,2\"},{\"periodo\":\"02/2017\",\"monto\":\"1\",\"situacion\":\"6,6\"},{\"periodo\":\"01/2017\",\"monto\":\"1\",\"situacion\":\"7,7\"},{\"periodo\":\"12/2016\",\"monto\":\"1\",\"situacion\":\"7,0\"},{\"periodo\":\"11/2016\",\"monto\":\"1\",\"situacion\":\"8,3\"},{\"periodo\":\"10/2016\",\"monto\":\"1\",\"situacion\":\"17,6\"},{\"periodo\":\"09/2016\",\"monto\":\"1\",\"situacion\":\"10,0\"},{\"periodo\":\"08/2016\",\"monto\":\"1\",\"situacion\":\"9,1\"},{\"periodo\":\"07/2016\",\"monto\":\"1\",\"situacion\":\"2,0\"},{\"periodo\":\"06/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"05/2016\",\"monto\":\"1\",\"situacion\":\"7,8\"},{\"periodo\":\"04/2016\",\"monto\":\"1\",\"situacion\":\"7,7\"},{\"periodo\":\"03/2016\",\"monto\":\"1\",\"situacion\":\"4,7\"},{\"periodo\":\"02/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"01/2016\",\"monto\":\"1\",\"situacion\":\"17,6\"},{\"periodo\":\"12/2015\",\"monto\":\"1\",\"situacion\":\"12,7\"}]},{\"nombre\":\"BANCO SUPERVIELLE S.A.\",\"historial\":[{\"periodo\":\"11/2017\",\"monto\":\"1\",\"situacion\":\"31,0\"},{\"periodo\":\"10/2017\",\"monto\":\"1\",\"situacion\":\"26,0\"},{\"periodo\":\"09/2017\",\"monto\":\"1\",\"situacion\":\"41,0\"},{\"periodo\":\"08/2017\",\"monto\":\"1\",\"situacion\":\"53,0\"},{\"periodo\":\"07/2017\",\"monto\":\"1\",\"situacion\":\"55,0\"},{\"periodo\":\"06/2017\",\"monto\":\"1\",\"situacion\":\"61,0\"},{\"periodo\":\"05/2017\",\"monto\":\"1\",\"situacion\":\"62,7\"},{\"periodo\":\"04/2017\",\"monto\":\"1\",\"situacion\":\"51,6\"},{\"periodo\":\"03/2017\",\"monto\":\"1\",\"situacion\":\"107,1\"},{\"periodo\":\"02/2017\",\"monto\":\"1\",\"situacion\":\"56,9\"},{\"periodo\":\"01/2017\",\"monto\":\"1\",\"situacion\":\"66,3\"},{\"periodo\":\"12/2016\",\"monto\":\"1\",\"situacion\":\"70,3\"},{\"periodo\":\"11/2016\",\"monto\":\"1\",\"situacion\":\"66,2\"},{\"periodo\":\"10/2016\",\"monto\":\"1\",\"situacion\":\"58,1\"},{\"periodo\":\"09/2016\",\"monto\":\"1\",\"situacion\":\"29,7\"},{\"periodo\":\"08/2016\",\"monto\":\"1\",\"situacion\":\"26,2\"},{\"periodo\":\"07/2016\",\"monto\":\"1\",\"situacion\":\"3,6\"},{\"periodo\":\"06/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"05/2016\",\"monto\":\"1\",\"situacion\":\"1,8\"},{\"periodo\":\"04/2016\",\"monto\":\"1\",\"situacion\":\"1,8\"},{\"periodo\":\"03/2016\",\"monto\":\"1\",\"situacion\":\"2,2\"},{\"periodo\":\"02/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"01/2016\",\"monto\":\"1\",\"situacion\":\"15,0\"},{\"periodo\":\"12/2015\",\"monto\":\"1\",\"situacion\":\"3,1\"}]},{\"nombre\":\"BANCO SANTANDER RIO S.A.\",\"historial\":[{\"periodo\":\"11/2017\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"10/2017\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"09/2017\",\"monto\":\"1\",\"situacion\":\"1,0\"},{\"periodo\":\"08/2017\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"07/2017\",\"monto\":\"1\",\"situacion\":\"1,0\"},{\"periodo\":\"06/2017\",\"monto\":\"1\",\"situacion\":\"1,8\"},{\"periodo\":\"05/2017\",\"monto\":\"1\",\"situacion\":\"0,6\"},{\"periodo\":\"04/2017\",\"monto\":\"1\",\"situacion\":\"0,9\"},{\"periodo\":\"03/2017\",\"monto\":\"1\",\"situacion\":\"1,5\"},{\"periodo\":\"02/2017\",\"monto\":\"1\",\"situacion\":\"1,6\"},{\"periodo\":\"01/2017\",\"monto\":\"1\",\"situacion\":\"0,8\"},{\"periodo\":\"12/2016\",\"monto\":\"1\",\"situacion\":\"0,7\"},{\"periodo\":\"11/2016\",\"monto\":\"1\",\"situacion\":\"1,3\"},{\"periodo\":\"10/2016\",\"monto\":\"1\",\"situacion\":\"2,6\"},{\"periodo\":\"09/2016\",\"monto\":\"1\",\"situacion\":\"1,7\"},{\"periodo\":\"08/2016\",\"monto\":\"1\",\"situacion\":\"0,6\"},{\"periodo\":\"07/2016\",\"monto\":\"1\",\"situacion\":\"0,6\"},{\"periodo\":\"06/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"05/2016\",\"monto\":\"1\",\"situacion\":\"0,5\"},{\"periodo\":\"04/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"03/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"02/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"01/2016\",\"monto\":\"1\",\"situacion\":\"6,1\"},{\"periodo\":\"12/2015\",\"monto\":\"1\",\"situacion\":\"0,5\"}]},{\"nombre\":\"CENCOSUD S.A.\",\"historial\":[{\"periodo\":\"11/2017\",\"monto\":\"1\",\"situacion\":\"4,0\"},{\"periodo\":\"10/2017\",\"monto\":\"1\",\"situacion\":\"8,0\"},{\"periodo\":\"09/2017\",\"monto\":\"1\",\"situacion\":\"10,0\"},{\"periodo\":\"08/2017\",\"monto\":\"1\",\"situacion\":\"12,0\"},{\"periodo\":\"07/2017\",\"monto\":\"1\",\"situacion\":\"20,0\"},{\"periodo\":\"06/2017\",\"monto\":\"1\",\"situacion\":\"25,0\"},{\"periodo\":\"05/2017\",\"monto\":\"1\",\"situacion\":\"28,0\"},{\"periodo\":\"04/2017\",\"monto\":\"1\",\"situacion\":\"5,1\"},{\"periodo\":\"03/2017\",\"monto\":\"1\",\"situacion\":\"3,6\"},{\"periodo\":\"02/2017\",\"monto\":\"1\",\"situacion\":\"5,0\"},{\"periodo\":\"01/2017\",\"monto\":\"1\",\"situacion\":\"3,8\"},{\"periodo\":\"12/2016\",\"monto\":\"1\",\"situacion\":\"1,8\"},{\"periodo\":\"11/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"10/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"09/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"08/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"07/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"06/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"05/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"04/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"03/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"02/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"01/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"12/2015\",\"monto\":\"N/A\",\"situacion\":\"0,0\"}]}],\"originDate\":\"10/2010\",\"comparatorMonto\":{}}";
        try {
            JSONObject jo = new JSONObject(jsonData);

            bcraResult.fillFromDb(jo);

            System.out.println("[Test 1] Query ID:" + bcraResult.getQueryId());
            System.out.println("[Test 1] Document number:" + bcraResult.getInDocumentNumber());
            System.out.println("[Test 1] Document type:" + bcraResult.getInDocumentType());
            System.out.println("[Test 1] Origin date:" + bcraResult.getOriginDate());

            System.out.println("[Test 1] Numero de Deurores:" + bcraResult.getDeudores().size());
            System.out.println("[Test 1] Numero de Cheques :" + bcraResult.getCheques().size());
            System.out.println("[Test 1] Numero de Historial de deudas :" + bcraResult.getHistorialDeudas().size());
/*
            for(BcraResult.DeudaBanco db:bcraResult.getHistorialDeudas()){
                System.out.println("Banco: " +db.getNombre()+" historial size: "+db.getHistorial().size());
                for (BcraResult.DeudaBanco.RegistroDeuda rd : db.getHistorial()){
                    System.out.println("periodo: "+rd.getPeriodo() + " monto: "+rd.getMonto()+ " situacion: "+rd.getSituacion());
                }
            }
*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assertEquals(3, bcraResult.getDeudores().size());
        assertEquals(0, bcraResult.getCheques().size());
        assertEquals(4, bcraResult.getHistorialDeudas().size());

    }


    @Test
    @Disabled
    public void addFirstToResult() {
        System.out.println("[Test 2] Test BCRA result with additional deuda");
        BcraResult bcraResult = new BcraResult();
        String jsonData = "{\"js_deudor\":[{\"denominacionDeudor\":\"BARREIRO JUAN IGNACIO\",\"entidad\":\"BANCO SUPERVIELLE S.A.\",\"periodo\":\"12/17\",\"situacion\":\"1\",\"monto\":\"28\",\"diasRetraso\":\"N/A\",\"observaciones\":\"-\"},{\"denominacionDeudor\":\"BARREIRO JUAN IGNACIO\",\"entidad\":\"BBVA BANCO FRANCES S.A.\",\"periodo\":\"12/17\",\"situacion\":\"1\",\"monto\":\"12\",\"diasRetraso\":\"N/A\",\"observaciones\":\"-\"},{\"denominacionDeudor\":\"BARREIRO JUAN IGNACIO\",\"entidad\":\"CENCOSUD S.A.\",\"periodo\":\"12/17\",\"situacion\":\"1\",\"monto\":\"2\",\"diasRetraso\":\"N/A\",\"observaciones\":\"-\"}],\"js_cheque\":[],\"js_deudas\":[{\"nombre\":\"BBVA BANCO FRANCES S.A.\",\"historial\":[{\"periodo\":\"11/2017\",\"monto\":\"1\",\"situacion\":\"15,0\"},{\"periodo\":\"10/2017\",\"monto\":\"1\",\"situacion\":\"14,0\"},{\"periodo\":\"09/2017\",\"monto\":\"1\",\"situacion\":\"15,0\"},{\"periodo\":\"08/2017\",\"monto\":\"1\",\"situacion\":\"19,0\"},{\"periodo\":\"07/2017\",\"monto\":\"1\",\"situacion\":\"18,0\"},{\"periodo\":\"06/2017\",\"monto\":\"1\",\"situacion\":\"15,1\"},{\"periodo\":\"05/2017\",\"monto\":\"1\",\"situacion\":\"18,7\"},{\"periodo\":\"04/2017\",\"monto\":\"1\",\"situacion\":\"19,6\"},{\"periodo\":\"03/2017\",\"monto\":\"1\",\"situacion\":\"6,2\"},{\"periodo\":\"02/2017\",\"monto\":\"1\",\"situacion\":\"6,6\"},{\"periodo\":\"01/2017\",\"monto\":\"1\",\"situacion\":\"7,7\"},{\"periodo\":\"12/2016\",\"monto\":\"1\",\"situacion\":\"7,0\"},{\"periodo\":\"11/2016\",\"monto\":\"1\",\"situacion\":\"8,3\"},{\"periodo\":\"10/2016\",\"monto\":\"1\",\"situacion\":\"17,6\"},{\"periodo\":\"09/2016\",\"monto\":\"1\",\"situacion\":\"10,0\"},{\"periodo\":\"08/2016\",\"monto\":\"1\",\"situacion\":\"9,1\"},{\"periodo\":\"07/2016\",\"monto\":\"1\",\"situacion\":\"2,0\"},{\"periodo\":\"06/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"05/2016\",\"monto\":\"1\",\"situacion\":\"7,8\"},{\"periodo\":\"04/2016\",\"monto\":\"1\",\"situacion\":\"7,7\"},{\"periodo\":\"03/2016\",\"monto\":\"1\",\"situacion\":\"4,7\"},{\"periodo\":\"02/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"01/2016\",\"monto\":\"1\",\"situacion\":\"17,6\"},{\"periodo\":\"12/2015\",\"monto\":\"1\",\"situacion\":\"12,7\"}]},{\"nombre\":\"BANCO SUPERVIELLE S.A.\",\"historial\":[{\"periodo\":\"11/2017\",\"monto\":\"1\",\"situacion\":\"31,0\"},{\"periodo\":\"10/2017\",\"monto\":\"1\",\"situacion\":\"26,0\"},{\"periodo\":\"09/2017\",\"monto\":\"1\",\"situacion\":\"41,0\"},{\"periodo\":\"08/2017\",\"monto\":\"1\",\"situacion\":\"53,0\"},{\"periodo\":\"07/2017\",\"monto\":\"1\",\"situacion\":\"55,0\"},{\"periodo\":\"06/2017\",\"monto\":\"1\",\"situacion\":\"61,0\"},{\"periodo\":\"05/2017\",\"monto\":\"1\",\"situacion\":\"62,7\"},{\"periodo\":\"04/2017\",\"monto\":\"1\",\"situacion\":\"51,6\"},{\"periodo\":\"03/2017\",\"monto\":\"1\",\"situacion\":\"107,1\"},{\"periodo\":\"02/2017\",\"monto\":\"1\",\"situacion\":\"56,9\"},{\"periodo\":\"01/2017\",\"monto\":\"1\",\"situacion\":\"66,3\"},{\"periodo\":\"12/2016\",\"monto\":\"1\",\"situacion\":\"70,3\"},{\"periodo\":\"11/2016\",\"monto\":\"1\",\"situacion\":\"66,2\"},{\"periodo\":\"10/2016\",\"monto\":\"1\",\"situacion\":\"58,1\"},{\"periodo\":\"09/2016\",\"monto\":\"1\",\"situacion\":\"29,7\"},{\"periodo\":\"08/2016\",\"monto\":\"1\",\"situacion\":\"26,2\"},{\"periodo\":\"07/2016\",\"monto\":\"1\",\"situacion\":\"3,6\"},{\"periodo\":\"06/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"05/2016\",\"monto\":\"1\",\"situacion\":\"1,8\"},{\"periodo\":\"04/2016\",\"monto\":\"1\",\"situacion\":\"1,8\"},{\"periodo\":\"03/2016\",\"monto\":\"1\",\"situacion\":\"2,2\"},{\"periodo\":\"02/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"01/2016\",\"monto\":\"1\",\"situacion\":\"15,0\"},{\"periodo\":\"12/2015\",\"monto\":\"1\",\"situacion\":\"3,1\"}]},{\"nombre\":\"BANCO SANTANDER RIO S.A.\",\"historial\":[{\"periodo\":\"11/2017\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"10/2017\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"09/2017\",\"monto\":\"1\",\"situacion\":\"1,0\"},{\"periodo\":\"08/2017\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"07/2017\",\"monto\":\"1\",\"situacion\":\"1,0\"},{\"periodo\":\"06/2017\",\"monto\":\"1\",\"situacion\":\"1,8\"},{\"periodo\":\"05/2017\",\"monto\":\"1\",\"situacion\":\"0,6\"},{\"periodo\":\"04/2017\",\"monto\":\"1\",\"situacion\":\"0,9\"},{\"periodo\":\"03/2017\",\"monto\":\"1\",\"situacion\":\"1,5\"},{\"periodo\":\"02/2017\",\"monto\":\"1\",\"situacion\":\"1,6\"},{\"periodo\":\"01/2017\",\"monto\":\"1\",\"situacion\":\"0,8\"},{\"periodo\":\"12/2016\",\"monto\":\"1\",\"situacion\":\"0,7\"},{\"periodo\":\"11/2016\",\"monto\":\"1\",\"situacion\":\"1,3\"},{\"periodo\":\"10/2016\",\"monto\":\"1\",\"situacion\":\"2,6\"},{\"periodo\":\"09/2016\",\"monto\":\"1\",\"situacion\":\"1,7\"},{\"periodo\":\"08/2016\",\"monto\":\"1\",\"situacion\":\"0,6\"},{\"periodo\":\"07/2016\",\"monto\":\"1\",\"situacion\":\"0,6\"},{\"periodo\":\"06/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"05/2016\",\"monto\":\"1\",\"situacion\":\"0,5\"},{\"periodo\":\"04/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"03/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"02/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"01/2016\",\"monto\":\"1\",\"situacion\":\"6,1\"},{\"periodo\":\"12/2015\",\"monto\":\"1\",\"situacion\":\"0,5\"}]},{\"nombre\":\"CENCOSUD S.A.\",\"historial\":[{\"periodo\":\"11/2017\",\"monto\":\"1\",\"situacion\":\"4,0\"},{\"periodo\":\"10/2017\",\"monto\":\"1\",\"situacion\":\"8,0\"},{\"periodo\":\"09/2017\",\"monto\":\"1\",\"situacion\":\"10,0\"},{\"periodo\":\"08/2017\",\"monto\":\"1\",\"situacion\":\"12,0\"},{\"periodo\":\"07/2017\",\"monto\":\"1\",\"situacion\":\"20,0\"},{\"periodo\":\"06/2017\",\"monto\":\"1\",\"situacion\":\"25,0\"},{\"periodo\":\"05/2017\",\"monto\":\"1\",\"situacion\":\"28,0\"},{\"periodo\":\"04/2017\",\"monto\":\"1\",\"situacion\":\"5,1\"},{\"periodo\":\"03/2017\",\"monto\":\"1\",\"situacion\":\"3,6\"},{\"periodo\":\"02/2017\",\"monto\":\"1\",\"situacion\":\"5,0\"},{\"periodo\":\"01/2017\",\"monto\":\"1\",\"situacion\":\"3,8\"},{\"periodo\":\"12/2016\",\"monto\":\"1\",\"situacion\":\"1,8\"},{\"periodo\":\"11/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"10/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"09/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"08/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"07/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"06/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"05/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"04/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"03/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"02/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"01/2016\",\"monto\":\"N/A\",\"situacion\":\"0,0\"},{\"periodo\":\"12/2015\",\"monto\":\"N/A\",\"situacion\":\"0,0\"}]}],\"originDate\":\"10/2010\",\"comparatorMonto\":{}}";
        try {
            JSONObject jo = new JSONObject(jsonData);

            bcraResult.fillFromDb(jo);

            System.out.println("[Test 2] Numero de Deurores:" + bcraResult.getDeudores().size());
            System.out.println("[Test 2] Numero de Cheques :" + bcraResult.getCheques().size());
            System.out.println("[Test 2] Numero de Historial de deudas :" + bcraResult.getHistorialDeudas().size());
            System.out.println("");
            List<BcraResult.DeudaBanco> dbList = bcraResult.getHistorialDeudas();
            for (BcraResult.DeudaBanco db : dbList) {
                System.out.println("[Test 2] Cantidad de deudas del banco " + db.getNombre() + " antes de agregar la deuda: " + db.getHistorial().size());
            }
            //System.out.println("Cantidad de deudas antes de agregar la deuda:"+bcraResult.getHistorialDeudas().get(0).getHistorial().size());
            assertEquals("11/2017", bcraResult.getHistorialDeudas().get(0).getHistorial().get(0).getPeriodo());
            bcraResult.addDepthToHistorical();
            assertEquals("12/2017", bcraResult.getHistorialDeudas().get(0).getHistorial().get(0).getPeriodo());
            //System.out.println("Cantidad de deudas luego de agregar la deuda:"+bcraResult.getHistorialDeudas().get(1).getHistorial().size());
            System.out.println("");
            for (BcraResult.DeudaBanco db : dbList) {
                System.out.println("[Test 2] Cantidad de deudas del banco " + db.getNombre() + " luego de agregar la deuda: " + db.getHistorial().size());
            }

            assertEquals(25, bcraResult.getHistorialDeudas().get(0).getHistorial().size());
            assertEquals(25, bcraResult.getHistorialDeudas().get(1).getHistorial().size());
            assertEquals(24, bcraResult.getHistorialDeudas().get(2).getHistorial().size());
            assertEquals(25, bcraResult.getHistorialDeudas().get(3).getHistorial().size());
/*
            for(BcraResult.DeudaBanco db:bcraResult.getHistorialDeudas()){
                System.out.println("Banco: " +db.getNombre()+" historial size: "+db.getHistorial().size());
                for (BcraResult.DeudaBanco.RegistroDeuda rd : db.getHistorial()){
                    System.out.println("periodo: "+rd.getPeriodo() + " monto: "+rd.getMonto()+ " situacion: "+rd.getSituacion());
                }
            }
*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Disabled
    public void formatDate() {
        BcraResult b = new BcraResult();

        String ans = b.formatDate("20/2017");
        System.out.println("ans:" + ans);
        assertEquals("20/2017", ans);

        ans = b.formatDate("20/017");
        System.out.println("ans:" + ans);
        assertEquals("20/2017", ans);

        ans = b.formatDate("20/17");
        System.out.println("ans:" + ans);
        assertEquals("20/2017", ans);

        ans = b.formatDate("20/07");
        System.out.println("ans:" + ans);
        assertEquals("20/2007", ans);
    }


}
