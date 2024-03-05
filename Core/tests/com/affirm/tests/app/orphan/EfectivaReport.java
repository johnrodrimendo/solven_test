package com.affirm.tests.app.orphan;

import org.json.JSONObject;
import org.json.XML;

import java.io.*;

/**
 * Created by dev5 on 13/02/18.
 */
public class EfectivaReport {

    public static int PRETTY_PRINT_INDENT_FACTOR = 4;

    public static void main(String [ ] args) {

        String csvFile = "/home/dev5/Descargas/data_efectiva.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        StringBuffer buffer = new StringBuffer();

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] csv = line.split(cvsSplitBy);

                if(csv.length > 0){

                    buffer.append(csv[0] != null ? csv[0] : "");
                    buffer.append(";");

                    try{
                        if(csv.length > 6){
                            JSONObject xmlJSONObj = org.json.XML.toJSONObject(csv[6]);
                            String jsonPrettyPrintString = xmlJSONObj.toString();
                            buffer.append(csv[6]);
                            buffer.append(";");
                            buffer.append(jsonPrettyPrintString);
                            buffer.append(";");

                        }
                    }catch (Exception e){
                        buffer.append(csv[6]);
                        buffer.append(";");
                        buffer.append("ERROR");
                        buffer.append(";");
                    }

                    try{
                        if(csv.length > 7){
                            JSONObject xmlJSONObj = XML.toJSONObject(csv[7]);
                            String jsonPrettyPrintString = xmlJSONObj.toString();
                            buffer.append(csv[7]);
                            buffer.append(";");
                            buffer.append(jsonPrettyPrintString);

                        }
                    }catch (Exception e){
                        buffer.append(csv[7]);
                        buffer.append(";");
                        buffer.append("ERROR");
                    }
                    buffer.append("\n");
                }

            }

            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter( new FileWriter("/home/dev5/Descargas/outputEfectiva.csv"));
                writer.write(buffer.toString());

            }catch ( IOException e) {
            }
            finally {
                try {
                    if ( writer != null)
                        writer.close( );
                }catch ( IOException e){
                }
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}

