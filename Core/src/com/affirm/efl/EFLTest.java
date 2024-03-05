package com.affirm.efl;

import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.efl.model.EFLTokens;
import com.affirm.efl.model.ScoreRequest;
import com.affirm.efl.util.EFLEncrypDecrypt;
import com.affirm.efl.util.EFLServiceCall;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;

/**
 * Created by dev5 on 11/12/17.
 */
public class EFLTest {

    public static void main(String[] args){
        try{
            JSONObject jsonResponse = null;

            EFLEncrypDecrypt encryptUtil = new EFLEncrypDecrypt();
            EFLServiceCall serviceEFL = new EFLServiceCall();
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            EFLTokens tokens = encryptUtil.getTokens(false);
            System.out.println(tokens.toString());

            LoanApplication loanApplication = new LoanApplication();
            loanApplication.setId(28248);

            ScoreRequest scoreRequest = new ScoreRequest(loanApplication, tokens);
            String jsonScoreRequest = gson.toJson(scoreRequest);
            System.out.println("SCORE REQUEST : " + jsonScoreRequest);
            jsonResponse = null/*serviceEFL.call(jsonScoreRequest, "https://api.eflglobal.com/api/v1/scores/subject.json?auth_type=1")*/;

            System.out.println(jsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
