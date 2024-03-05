package com.affirm.backoffice.controller;

import com.affirm.aws.elasticSearch.AWSElasticSearchClient;
import com.affirm.common.dao.InteractionDAO;
import com.affirm.common.model.EmailEventsReportItem;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.catalog.InteractionContent;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.ReportsService;
import com.affirm.security.model.SysUser;
import org.apache.shiro.SecurityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@Scope("request")
public class FollowUpPerformanceReportController {

    private static final String CATEGORY_COMMERCIAL = "Commercial";
    private static final String CATEGORY_COLLECTION = "Collection";

    @Autowired
    private InteractionDAO interactionDao;
    @Autowired
    private AWSElasticSearchClient awsElasticSearchClient;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private ReportsService reportsService;

    @RequestMapping(value = "/followUpReport", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String getFollowUpReport(ModelMap model, Locale locale) throws Exception {

        JSONArray arrayInteractionContents = interactionDao.getPersonInteractionContentForFilter();
        JSONArray arrayHours = interactionDao.getPersonInteractionHourOfDayForFilter();

        List<Integer> hoursCommercial = new ArrayList<>();
        List<Integer> hoursCollection = new ArrayList<>();

        for (int i = 0; i < arrayHours.length(); i++) {
            JSONObject jsonObject = arrayHours.getJSONObject(i);

            if (CATEGORY_COMMERCIAL.equals(jsonObject.getString("initcap"))) {
                if (!jsonObject.isNull("hour_of_day") && !hoursCommercial.contains(jsonObject.get("hour_of_day"))) {
                    hoursCommercial.add((Integer.valueOf((String) jsonObject.get("hour_of_day"))));
                }
            } else if (CATEGORY_COLLECTION.equals(jsonObject.getString("initcap"))) {
                if (!jsonObject.isNull("hour_of_day") && !hoursCollection.contains(jsonObject.get("hour_of_day"))) {
                    hoursCollection.add((Integer.valueOf((String) jsonObject.get("hour_of_day"))));
                }
            }
        }

        List<Integer> subcategoriesCommercial = new ArrayList<>();
        List<Integer> subcategoriesCollection = new ArrayList<>();

        List<InteractionContent> subcategoriesInteractionCommercial = new ArrayList<>();
        List<InteractionContent> subcategoriesInteractionCollection = new ArrayList<>();

        Integer activeCountry = ((SysUser) SecurityUtils.getSubject().getPrincipal()).getActiveCountries().get(0);

        for (int i = 0; i < arrayInteractionContents.length(); i++) {
            JSONObject jsonObject = arrayInteractionContents.getJSONObject(i);

            if (CATEGORY_COMMERCIAL.equals(jsonObject.getString("initcap"))) {
                if (!jsonObject.isNull("interaction_content_id") && !subcategoriesCommercial.contains(jsonObject.get("interaction_content_id"))) {
                    Integer interactionContentId = (Integer) jsonObject.get("interaction_content_id");
                    subcategoriesCommercial.add(interactionContentId);
                    subcategoriesInteractionCommercial.add(catalogService.getInteractionContent(interactionContentId, activeCountry));
                }
            } else if (CATEGORY_COLLECTION.equals(jsonObject.getString("initcap"))) {
                if (!jsonObject.isNull("interaction_content_id") && !subcategoriesCollection.contains(jsonObject.get("interaction_content_id"))) {
                    Integer interactionContentId = (Integer) jsonObject.get("interaction_content_id");
                    subcategoriesCollection.add(interactionContentId);
                    subcategoriesInteractionCollection.add(catalogService.getInteractionContent(interactionContentId, activeCountry));
                }
            }
        }

        Map<String, List<Integer>> hoursByCategory = new HashMap<>();
        hoursByCategory.put(CATEGORY_COMMERCIAL, hoursCommercial);
        hoursByCategory.put(CATEGORY_COLLECTION, hoursCollection);

        Map<String, List<InteractionContent>> subcategoriesByCategory = new HashMap<>();
        subcategoriesByCategory.put(CATEGORY_COMMERCIAL, subcategoriesInteractionCommercial);
        subcategoriesByCategory.put(CATEGORY_COLLECTION, subcategoriesInteractionCollection);

        model.addAttribute("hoursByCategory", hoursByCategory);
        model.addAttribute("subcategoriesByCategory", subcategoriesByCategory);

        return "followUpReport";
    }

    @RequestMapping(value = "/followUpReport", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public String processReport(ModelMap model,
                                @RequestParam(value = "interactionContentIds[]", required = false) String interactionContentIds,
                                @RequestParam(value = "schedule[]", required = false) String schedule,
                                @RequestParam(value = "fromDate[]", required = false) String fromDate,
                                @RequestParam(value = "toDate[]", required = false) String toDate) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date from = sdf.parse(fromDate);
        Date to = sdf.parse(toDate);

        int[] interactions = new int[0];
        if (!interactionContentIds.isEmpty()) {
            String[] interactionContentIdsArray = interactionContentIds.split(",");
            interactions = new int[interactionContentIdsArray.length];

            for (int i = 0; i < interactionContentIdsArray.length; i++) {
                interactions[i] = Integer.parseInt(interactionContentIdsArray[i]);
            }
        }

        int[] schedules = new int[0];
        if (!schedule.isEmpty()) {
            String[] schedulessArray = schedule.split(",");
            schedules = new int[schedulessArray.length];

            for (int i = 0; i < schedulessArray.length; i++) {
                schedules[i] = Integer.parseInt(schedulessArray[i]);
            }
        }

        List<EmailEventsReportItem> reportItems = awsElasticSearchClient.getEmailReportByFilters(interactions, schedules, from, to);
        model.addAttribute("events", reportItems);
        return "followUpReport :: results-table";
    }

    @RequestMapping(value = "/followUpReport/download", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.OTHER)
    @ResponseBody
    public void downloadReport(ModelMap model, Locale locale, HttpServletResponse response,
                                 @RequestParam("event") String event,
                                 @RequestParam("personInteractionIds") String personInteractionIds) throws Exception {

        byte[] report = reportsService.createPersoninteractionFollowUpReports(event, new JSONArray(personInteractionIds));

        response.setHeader("Content-disposition", "attachment; filename=followupreport.xlsx");
//        response.setContentType(contentType.getType());
        response.getOutputStream().write(report);
    }

}
