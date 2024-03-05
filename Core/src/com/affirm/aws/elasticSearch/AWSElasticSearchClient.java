package com.affirm.aws.elasticSearch;

import com.affirm.aws.AmazonSigner;
import com.affirm.common.model.EmailEventsReportItem;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service("awsElasticSearchClient")
public class AWSElasticSearchClient {

    @Autowired
    private UtilService utilService;

    private static final String AWSACCESSKEYID = System.getenv("AWS_ACCESS_KEY_ID");
    private static final String AWSSECRETKEY = System.getenv("AWS_SECRET_ACCESS_KEY");

    private static final String REGION = "us-east-1";
    private static final String SERVICE = "es";
    //    private static final String ALGORITHM = "AWS4-HMAC-SHA256";
    private static final String HOST = "xxxxxxxxxxxxxxxxxxxxx" + "." + REGION + "." + SERVICE + ".amazonaws.com";
    private static final String URI = "/_search";
    private static final String URL = "https://" + HOST + URI;
    private static final String QUERYSTRING = "";

    private final Map<String, String> mapOfEvents;

    public static  final String BOUNCE = "Rebotado";
    public static  final String COMPLAINT = "Queja";
    public static  final String DELIVERY = "Entrega";
    public static  final String SEND = "Envio";
    public static  final String OPEN = "Abierto";
    public static  final String CLICK = "Click";

    public AWSElasticSearchClient() {
        mapOfEvents = new HashMap<String, String>();
        mapOfEvents.put("Bounce", BOUNCE);
        mapOfEvents.put("Complaint", COMPLAINT);
        mapOfEvents.put("Delivery", DELIVERY);
        mapOfEvents.put("Send", SEND);
        mapOfEvents.put("Open", OPEN);
        mapOfEvents.put("Click", CLICK);
    }

    public Map<String, Date> getEmailEventsByPersonInteractionIds(int[] personInteractionIds) throws Exception {
        String commaSeparatedPersonInteractionIds = IntStream.of(personInteractionIds)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining(", "));

        String DATA = "{\n" +
                "\t\"query\": {\n" +
                "\t\t\"bool\": {\n" +
                "\t\t\t\"must\": [{\n" +
                "\t\t\t\t\t\"match\": {\n" +
                "\t\t\t\t\t\t\"mail.tags.person_interaction_id\": \"" + commaSeparatedPersonInteractionIds + "\"\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"match\": {\n" +
                "\t\t\t\t\t\t\"mail.tags.host_env\": \"" + Configuration.getEnvironmmentName() + "\"\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t}\n" +
                "\t\t\t]\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}";

        return mapAWSESEventTypeResult(new JSONObject(DATA));
    }

    public Map<String, Date> getEmailEventsByPersonInteractionId(Integer personInteractionId) throws Exception {
        String DATA = "{\n" +
                "\t\"query\": {\n" +
                "\t\t\"bool\": {\n" +
                "\t\t\t\"must\": [{\n" +
                "\t\t\t\t\t\"match\": {\n" +
                "\t\t\t\t\t\t\"mail.tags.person_interaction_id\": \"" + personInteractionId + "\"\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"match\": {\n" +
                "\t\t\t\t\t\t\"mail.tags.host_env\": \"" + Configuration.getEnvironmmentName() + "\"\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t}\n" +
                "\t\t\t]\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}";

        return mapAWSESEventTypeResult(new JSONObject(DATA));
    }

    private Map<String, Date> mapAWSESEventTypeResult(JSONObject data) throws Exception {
        Map<String, Date> map = null;

        AmazonSigner signer = new AmazonSigner(AWSACCESSKEYID, AWSSECRETKEY, REGION, SERVICE, URL, HOST);
        HttpPost request = signer.getSignedPostRequest(URI, QUERYSTRING, new HashMap<String, String>(), data.toString());
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        HttpClient client = HttpClientBuilder.create().build();
        client.execute(request);
        HttpResponse response = client.execute(request);

        if (response.getStatusLine().getStatusCode() == 200) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String line = "";

            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            String jsonResponse = result.toString();
            JSONObject jsonObject = new JSONObject(jsonResponse);
            map = new HashMap<String, Date>();
            JSONArray array = jsonObject.getJSONObject("hits").getJSONArray("hits");

            for (int i = 0; i < array.length(); i++) {
                JSONObject rootJson = array.getJSONObject(i).getJSONObject("_source");
                String event = rootJson.getString("eventType");
                String timestamp = null;
                if(event.equalsIgnoreCase("Send")){
                    timestamp = rootJson.getJSONObject("mail").getString("timestamp");
                }else{
                    JSONObject eventJson = rootJson.optJSONObject(event.toLowerCase());
                    if(eventJson != null && eventJson.has("timestamp")){
                        timestamp = eventJson.getString("timestamp");
                    }
                }

                if(timestamp != null){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                    Date date = sdf.parse(timestamp);
                    if(map.containsKey(translateAmazonEmailServiceEvents(event))){
                        if(map.get(translateAmazonEmailServiceEvents(event)).after(date))
                            map.put(translateAmazonEmailServiceEvents(event), date);
                    }else{
                        map.put(translateAmazonEmailServiceEvents(event), date);
                    }
                }
            }
        }

        return utilService.sortByValue(map);
    }

    public JSONObject getEmailEventsByFilter(int[] interactionContentId, int[] interactionScheduleHours, Date dateFrom, Date dateUntil, Integer size, Integer from) throws Exception {
        SimpleDateFormat p_sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        Calendar cal = Calendar.getInstance();

        cal.setTime(dateFrom);
        cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) + 5);
        Date dateFromB = cal.getTime();

        cal.setTime(dateUntil);
        cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) + 29);
        Date dateUntilB = cal.getTime();

        String interactionContentIdList = "";
        String interactionScheduleHoursList = "";

        if (interactionContentId.length > 0) {
            interactionContentIdList = "        {\n" +
                    "          \"terms\": {\n" +
                    "            \"mail.tags.interaction_content_id\": " + Arrays.toString(interactionContentId) + "\n" +
                    "          }\n" +
                    "        },\n";
        }

        if (interactionScheduleHours.length > 0) {
            interactionScheduleHoursList = "        {\n" +
                    "          \"terms\": {\n" +
                    "            \"mail.tags.interaction_schedule_hour\": " + Arrays.toString(interactionScheduleHours) + "\n" +
                    "          }\n" +
                    "        },\n";
        }

        String DATA = "{\n" +
                "  \"query\" : {\n" +
                "    \"bool\" :{\n" +
                "      \"must\": [\n" +
                interactionContentIdList +
                interactionScheduleHoursList +
                "        {\n" +
                "          \"range\": {\n" +
                "            \"mail.timestamp\": {\n" +
                "              \"gte\": \"" + p_sdf.format(dateFromB) + "\",\n" +
                "              \"lte\": \"" + p_sdf.format(dateUntilB) + "\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"match\": {\n" +
                "            \"mail.tags.host_env\": \"" + Configuration.getEnvironmmentName() + "\"\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"_source\": [\"mail.tags.person_interaction_id\", \"mail.tags.interaction_content_id\", \"eventType\", \"mail.tags.schedule\"]\n" +
                "}";

        AmazonSigner signer = new AmazonSigner(AWSACCESSKEYID, AWSSECRETKEY, REGION, SERVICE, URL, HOST);
        HttpPost request = signer.getSignedPostRequest(URI, QUERYSTRING, new HashMap<String, String>(), DATA);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        HttpClient client = HttpClientBuilder.create().build();
        client.execute(request);
        HttpResponse response = client.execute(request);

        List<EmailEventsReportItem> reportItems = new ArrayList<>();
        if (response.getStatusLine().getStatusCode() == 200) {

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            String jsonResponse = result.toString().replace("\\n", "\n");
            JSONObject jsonObject = new JSONObject(jsonResponse);

            return jsonObject;
        }
        return null;
    }


    public List<EmailEventsReportItem> getEmailReportByFilters(int[] interactionContentId, int[] interactionScheduleHours, Date dateFrom, Date dateUntil) throws Exception {

        final int hitsSize = 500;

        List<EmailEventsReportItem> reportItems = new ArrayList<>();
        reportItems.add(new EmailEventsReportItem(EmailEventsReportItem.EVENT_SEND));
        reportItems.add(new EmailEventsReportItem(EmailEventsReportItem.EVENT_DELIVERY));
        reportItems.add(new EmailEventsReportItem(EmailEventsReportItem.EVENT_OPEN));
        reportItems.add(new EmailEventsReportItem(EmailEventsReportItem.EVENT_CLICK));
        reportItems.add(new EmailEventsReportItem(EmailEventsReportItem.EVENT_SPAM));
        reportItems.add(new EmailEventsReportItem(EmailEventsReportItem.EVENT_BOUNCE));
//        reportItems.add(new EmailEventsReportItem(EmailEventsReportItem.EVENT_UNSUBSCRIBED));
        reportItems.add(new EmailEventsReportItem(EmailEventsReportItem.EVENT_TOTAL));

        JSONObject jsonResponse = getEmailEventsByFilter(interactionContentId, interactionScheduleHours, dateFrom, dateUntil, hitsSize, 0);
        JSONObject hitsJson = JsonUtil.getJsonObjectFromJson(jsonResponse, "hits", null);
        if (hitsJson != null && hitsJson.has("total")) {
            int totalHits = hitsJson.getInt("total");
            int loopRepeats = (int) Math.ceil((totalHits * 1.0) / (hitsSize * 1.0));

            JSONArray hitsArray = hitsJson.getJSONArray("hits");
            for (EmailEventsReportItem item : reportItems) {
                processEmailReportItem(hitsArray, item);
            }

            if (loopRepeats > 1) {
                for (int i = 2; i <= loopRepeats; i++) {
                    jsonResponse = getEmailEventsByFilter(interactionContentId, interactionScheduleHours, dateFrom, dateUntil, hitsSize, hitsSize * i);
                    hitsJson = JsonUtil.getJsonObjectFromJson(jsonResponse, "hits", null);
                    hitsArray = hitsJson.getJSONArray("hits");
                    for (EmailEventsReportItem item : reportItems) {
                        processEmailReportItem(hitsArray, item);
                    }
                }
            }

            EmailEventsReportItem totalReport = reportItems.stream().filter(r -> r.getEvent().equalsIgnoreCase(EmailEventsReportItem.EVENT_TOTAL)).findFirst().orElse(null);
            reportItems.stream().filter(r -> !r.getEvent().equalsIgnoreCase(EmailEventsReportItem.EVENT_TOTAL))
                    .forEach(r -> r.setPercentage((r.getUniqueHits() * 100.0) / (totalReport.getUniqueHits() * 1.0)));

            return reportItems;
        }

        return null;
    }

    private void processEmailReportItem(JSONArray arrayHits, EmailEventsReportItem emailEventsReportItem) {
        for (int i = 0; i < arrayHits.length(); i++) {
            JSONObject sourceJson = JsonUtil.getJsonObjectFromJson(arrayHits.getJSONObject(i), "_source", null);
            if (sourceJson != null) {
                String event = JsonUtil.getStringFromJson(sourceJson, "eventType", null);
                Integer personInteractionId = null;
                JSONObject mailJson = JsonUtil.getJsonObjectFromJson(sourceJson, "mail", null);
                if (mailJson != null) {
                    JSONObject tagsJson = JsonUtil.getJsonObjectFromJson(mailJson, "tags", null);
                    if (tagsJson != null) {
                        JSONArray personInteractionArrayJson = JsonUtil.getJsonArrayFromJson(tagsJson, "person_interaction_id", null);
                        if (personInteractionArrayJson != null && personInteractionArrayJson.length() > 0) {
                            personInteractionId = personInteractionArrayJson.optInt(0, -1);
                        }
                    }
                }

                if (event != null && personInteractionId != null && personInteractionId != -1) {
                    switch (emailEventsReportItem.getEvent()) {
                        case EmailEventsReportItem.EVENT_OPEN:
                            if (event.equalsIgnoreCase("Open")) {
                                emailEventsReportItem.addPersonInteractionHit(personInteractionId);
                            }
                            break;
                        case EmailEventsReportItem.EVENT_BOUNCE:
                            if (event.equalsIgnoreCase("Bounce")) {
                                emailEventsReportItem.addPersonInteractionHit(personInteractionId);
                            }
                            break;
                        case EmailEventsReportItem.EVENT_CLICK:
                            if (event.equalsIgnoreCase("Click")) {
                                emailEventsReportItem.addPersonInteractionHit(personInteractionId);
                            }
                            break;
                        case EmailEventsReportItem.EVENT_DELIVERY:
                            if (event.equalsIgnoreCase("Delivery")) {
                                emailEventsReportItem.addPersonInteractionHit(personInteractionId);
                            }
                            break;
                        case EmailEventsReportItem.EVENT_SEND:
                            if (event.equalsIgnoreCase("Send")) {
                                emailEventsReportItem.addPersonInteractionHit(personInteractionId);
                            }
                            break;
                        case EmailEventsReportItem.EVENT_SPAM:
                            if (event.equalsIgnoreCase("Complaint")) {
                                emailEventsReportItem.addPersonInteractionHit(personInteractionId);
                            }
                            break;
                        case EmailEventsReportItem.EVENT_UNSUBSCRIBED:
                            // TODO
                            break;
                        case EmailEventsReportItem.EVENT_TOTAL:
                            emailEventsReportItem.addPersonInteractionHit(personInteractionId);
                            break;
                    }
                }
            }
        }
    }

    private String translateAmazonEmailServiceEvents(String enEvent) {
        return mapOfEvents.entrySet().stream()
                .filter(x -> enEvent.equals(x.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.joining());
    }

}
