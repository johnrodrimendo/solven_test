package com.affirm.tests.service

import com.affirm.acquisition.controller.questions.AreYouReady20Question
import com.affirm.tests.BaseAcquisitionConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

import static org.junit.jupiter.api.Assertions.assertThrows

class ErrorServiceTest extends BaseAcquisitionConfig {

    @Autowired
    AreYouReady20Question areYouReady20Question

    void throwsNullPointerExceptionMethod() throws NullPointerException{
        areYouReady20Question.getQuestion(null, null, null, null, null, null, null, null)
    }

    @Test
    void ShouldThrowNullPointerExceptionAndLogErrorApplicationNameAsCLIENT() {
        assertThrows(NullPointerException.class, throwsNullPointerExceptionMethod())
    }
}
