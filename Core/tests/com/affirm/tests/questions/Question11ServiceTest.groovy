package com.affirm.tests.questions

import com.affirm.common.dao.LoanApplicationDAO
import com.affirm.common.dao.UserDAO
import com.affirm.common.model.transactional.LoanApplication
import com.affirm.common.model.transactional.PhoneNumber
import com.affirm.common.service.UserService
import com.affirm.common.service.UtilService
import com.affirm.common.service.question.Question11Service
import com.affirm.common.service.question.Question54Service
import com.affirm.common.service.question.QuestionFlowService
import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class Question11ServiceTest extends BaseConfig {

    @Autowired
    private LoanApplicationDAO loanApplicationDAO
    @Autowired
    private UserDAO userDAO
    @Autowired
    private Question11Service question11Service

    @Test
    void getViewAttributes() {
        question11Service.getViewAttributes(QuestionFlowService.Type.LOANAPPLICATION, 635227, Configuration.defaultLocale, false, null);
    }

    @Test
    void getProbabilidades() {
        Random random = new Random();
        int countA = 0;
        int countB = 0;
       for (int i = 0; i < 1000; i++) {

           int randon = random.nextInt(10).intValue();

           switch (randon){
               case 0 :
               case 1 :
               case 2 :
               case 3 :
               case 4 :
               case 5 :
               case 6 :
               case 7 :
               case 8 :
                   countA += 1;
                   break;
               case 9: countB += 1;
                   break;
           }
        }

        println("MIL--- A: "+ countA + " ----- B: " + countB);



         countA = 0;
         countB = 0;
        for (int i = 0; i < 10000; i++) {

            int randon = random.nextInt(10).intValue();

            switch (randon){
                case 0 :
                case 1 :
                case 2 :
                case 3 :
                case 4 :
                case 5 :
                case 6 :
                case 7 :
                case 8 :
                    countA += 1;
                    break;
                case 9: countB += 1;
                    break;
            }
        }

        println("10MIL--- A: "+ countA + " ----- B: " + countB);

    }
}
