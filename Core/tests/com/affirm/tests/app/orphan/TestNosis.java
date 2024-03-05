package com.affirm.tests.app.orphan;

import com.affirm.common.util.Marshall;
import com.affirm.nosis.NosisResult;
import com.affirm.nosis.client.NosisClient;

public class TestNosis {
    public static void main(String[] args)
    {
//        String nDoc = "23245453690";
        String nDoc = "27261759158";
        Integer nroConsulta = 15; // This must change when you change to other nDoc

        try {
            NosisResult result = NosisClient.getInfo(nDoc, nroConsulta);
            Marshall marshall = new Marshall();
            System.out.println(marshall.toJson(result));
            System.out.println(marshall.toXml(result));
            System.out.println("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
