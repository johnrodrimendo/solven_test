package com.affirm.tests

import com.affirm.common.model.form.ShortProcessQuestion1Form
import groovy.transform.CompileStatic
import org.apache.http.Header
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair

import java.security.SecureRandom
@CompileStatic
abstract class HttpClientConfig {

    static String getTokenFromResponse(HttpResponse response) {
        String token = ''
        for (Header header : response.getAllHeaders()) {

            if (header.getName().contains('Set-Cookie') && header.getValue().contains('fibonacci13=')) {
                String value = header.getValue()
                token = value.substring(12, value.indexOf(';'))
                break;
            }
        }
        token
    }

    static List<NameValuePair> getEncodedLoanApplicationForm() {
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>()
        urlParameters.add(new BasicNameValuePair('amount', '10000'))
        urlParameters.add(new BasicNameValuePair('reason', '7'))
        urlParameters.add(new BasicNameValuePair('docType', '1'))
        urlParameters.add(new BasicNameValuePair('gaClientID', '1380719033.1535399068'))
        urlParameters.add(new BasicNameValuePair('source', ''))
        urlParameters.add(new BasicNameValuePair('medium', ''))
        urlParameters.add(new BasicNameValuePair('campaign', ''))
        urlParameters.add(new BasicNameValuePair('term', ''))
        urlParameters.add(new BasicNameValuePair('content', ''))
        urlParameters.add(new BasicNameValuePair('gclid', ''))
        urlParameters.add(new BasicNameValuePair('birthday', ''))
        urlParameters.add(new BasicNameValuePair('documentNumber', String.valueOf(randomInt(40000000, 80000000))))
        urlParameters.add(new BasicNameValuePair('email', randomAlphabeticString(6) + '@' + randomAlphabeticString(5) + '.com'))
        urlParameters.add(new BasicNameValuePair('phone', '9' + String.valueOf(randomInt(40000000, 80000000))))
        urlParameters
    }

    static int randomInt(int min, int max) {
        Random r = new Random()
        return r.nextInt((max - min) + 1) + min
    }

    static String randomAlphabeticString(int n) {
        SecureRandom rnd = new SecureRandom()
        final String AB = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'
        StringBuilder sb = new StringBuilder(n)
        for (int i = 0; i < n; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())))
        sb.toString()
    }

    ShortProcessQuestion1Form getFormLandingPage15() {
        ShortProcessQuestion1Form shortProcessQuestion1Form = new ShortProcessQuestion1Form()
        shortProcessQuestion1Form.setDocumentNumber(String.valueOf(randomInt(40000000, 80000000)))
        shortProcessQuestion1Form.setEmail(randomAlphabeticString(6) + '@' + randomAlphabeticString(5) + '.com')
        shortProcessQuestion1Form.setReason(15)
        shortProcessQuestion1Form.setDocType(1)
        shortProcessQuestion1Form.setPhone('9' + String.valueOf(randomInt(40000000, 80000000)))
        shortProcessQuestion1Form.setSource('')
        shortProcessQuestion1Form.setMedium('')
        shortProcessQuestion1Form.setCampaign('')
        shortProcessQuestion1Form.setTerm('')
        shortProcessQuestion1Form.setContent('')
        shortProcessQuestion1Form.setBirthday('')
        shortProcessQuestion1Form.setContent(null)
        shortProcessQuestion1Form.setPep(null)
        shortProcessQuestion1Form.setAmount(5000)
        shortProcessQuestion1Form.setCountryId(null)
        shortProcessQuestion1Form.setAgentId(null)
        shortProcessQuestion1Form.setCategoryUrl(null)
        shortProcessQuestion1Form.setExternalParams(null)
        shortProcessQuestion1Form
    }
}
