
package com.affirm.tests.stress

import com.affirm.tests.HttpClientConfig
import groovy.transform.CompileStatic
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClientBuilder
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class HttpLoanApplicationTests extends HttpClientConfig {


    static void main (String []args){
        int numOfThreads = 10

        for (int i = 0; i < numOfThreads; i++) {
            final int index = i
            final HttpLoanApplicationTests httpLoanApplicationTests = new HttpLoanApplicationTests()
            Thread t = new Thread(new Runnable() {
                void run() {
                    httpLoanApplicationTests.performSubmission()
                    println 'Thread number '+ index
                }
            })
            t.start()
        }
    }

    @Test
    @Disabled
    void loanAplicationSubmissionTestSuccess() throws InterruptedException {
        int numOfThreads = 10

        for (int i = 0; i < numOfThreads; i++) {
            final int index = i
            final HttpLoanApplicationTests httpLoanApplicationTests = new HttpLoanApplicationTests()
            Thread t = new Thread(new Runnable() {
                void run() {
                    httpLoanApplicationTests.performSubmission()
                    println 'Thread number '+ index
                }
            })
            t.start()
            t.join() //in sequence
        }
    }

    void performSubmission() {
        def url = 'http://localhost:8080/credito-de-consumo-5'
        //def url = 'https://dev-solven-c.herokuapp.com/credito-de-consumo-5'
        HttpClient client = HttpClientBuilder.create().build()

        HttpGet getRequest = new HttpGet(url)
        HttpResponse responseGet = client.execute(getRequest)
         assertEquals(200, responseGet.getStatusLine().statusCode)

        HttpPost postRequest = new HttpPost(url)
        postRequest.setHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8')
        postRequest.setHeader('x-csrf-token', getTokenFromResponse(responseGet))
        postRequest.setEntity(new UrlEncodedFormEntity(getEncodedLoanApplicationForm()))
        HttpResponse responsePost = client.execute(postRequest)
        assertEquals(308, responsePost.getStatusLine().statusCode)
    }
}

