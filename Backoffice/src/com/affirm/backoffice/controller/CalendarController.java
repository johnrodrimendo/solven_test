package com.affirm.backoffice.controller;

import com.affirm.common.dao.BotDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.transactional.HolidaysResult;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.jsoup.Jsoup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Controller
public class CalendarController {

    @Autowired
    private BotDAO botDAO;

    @RequestMapping(value = "/sync_calendar", method = RequestMethod.GET)
    public Object syncCalendar(ModelMap model, Locale locale, @RequestParam(value = "year", required = false) Integer year) throws Exception {
        TimeAndDateScrapper calendar = new TimeAndDateScrapper();

        if (year == null) {
            year = 1900 + new Date().getYear();
        }
        //HolidaysResult holidaysResult = calendar.getData(year,TimeAndDateScrapper.URL_PERU);
        //botDAO.registerHolidays(holidaysResult,CountryParam.COUNTRY_PERU);
        HolidaysResult holidaysResult = calendar.getData(year,TimeAndDateScrapper.URL_ARGENTINA);
        botDAO.registerHolidays(holidaysResult,CountryParam.COUNTRY_ARGENTINA);
        model.addAttribute("dates", holidaysResult);
        return "calendar";
    }
}

class TimeAndDateScrapper {

    public static final String URL_PERU = "https://www.timeanddate.com/holidays/peru/";
    public static final String URL_ARGENTINA = "https://www.timeanddate.com/holidays/argentina/";

    public HolidaysResult getData(Integer year,String url) throws Exception {
        try {
            HolidaysResult holidays = new HolidaysResult();
            Document doc = Jsoup.connect(url + year).get();
            Element table = doc.getElementsByClass("zebra").first();
            Element body = table.getElementsByTag("tbody").first();
            Elements rows = body.getElementsByTag("tr");

            for (Element row : rows) {
                Element holidayType = row.select("td").last();
                if (holidayType.text().contains("holiday")) {
                    Element date = row.select("th").first();
                    holidays.addHoliday(new SimpleDateFormat("yyyy MMM d", Locale.US).parse(year + " " +  date.text()));
                }
            }

            return holidays;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) throws Exception {
        TimeAndDateScrapper t= new TimeAndDateScrapper();
        HolidaysResult hr=t.getData(2018,TimeAndDateScrapper.URL_PERU);
        System.out.println(hr);
        hr=t.getData(2018,TimeAndDateScrapper.URL_ARGENTINA);
        System.out.println(hr);
    }
}
