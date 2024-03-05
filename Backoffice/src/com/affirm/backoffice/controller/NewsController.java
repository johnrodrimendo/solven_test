package com.affirm.backoffice.controller;

import com.affirm.common.dao.NewDAO;
import com.affirm.common.model.New;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.FileService;
import com.affirm.common.util.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
@Scope("request")
public class NewsController {

    private static Logger logger = Logger.getLogger(NewsController.class);

    @Autowired
    private NewDAO newDao;

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private FileService fileService;

    @RequestMapping(value = "/news", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String getNews(ModelMap model, Locale locale)
    {
        List<New> news = newDao.getNews();
        List<CountryParam> countries = catalogService.getCountryParams();
        List<String> pressMediums = news.stream().map(pm -> pm.getPressMedium()).distinct().collect(Collectors.toList());

        model.addAttribute("news", news);
        model.addAttribute("countries", countries);
        model.addAttribute("registerForm", new RegisterNewForm());
        model.addAttribute("pressMediums", pressMediums);

        return "news";
    }

    @RequestMapping(value = "/news/register", method = RequestMethod.POST)
    public Object registerNew(ModelMap model, Locale locale, RegisterNewForm form, @RequestParam(value = "newLogo", required = false) MultipartFile fileLogo) throws Exception {
        form.getValidator().validate(locale);

        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        if(fileLogo != null){
            BufferedImage bufferedImage = ImageIO.read(fileLogo.getInputStream());
            Integer width = bufferedImage.getWidth();
            Integer height = bufferedImage.getHeight();

            if (width != 202 && height != 81) {
                return AjaxResponse.errorMessage("Las dimensiones de la imagen no son válidas");
            }
        }

        // Check if exists and imgUrl for the press medium
        New _tempNew = newDao.getNews().stream().filter(n -> n.getPressMedium() != null && n.getPressMedium().equals(form.getPressMedium())).distinct().findFirst().orElse(null);

        String imgUrl = "";
        String altImgUrl = "";
        String pressMedium = "";
        if (_tempNew != null) {
            pressMedium = form.getPressMedium();
            imgUrl = _tempNew.getLogo();
            altImgUrl = _tempNew.getAltLogo();
        } else {
            // Save image and new pressMedium
            if(fileLogo != null){
                pressMedium = form.getNewPressMedium();
                String fileNamePressMedium = pressMedium.toLowerCase().replaceAll(" ", "_").concat(FilenameUtils.getExtension(fileLogo.getOriginalFilename()));
                imgUrl = fileService.bucketSafeToPublicBucket("img/", fileNamePressMedium, fileLogo.getBytes());
                altImgUrl = pressMedium.toLowerCase().replaceAll(" ", "").concat(".png");
            }else{
                return AjaxResponse.errorMessage("El medio de prensa no tiene una imagen asociada.");
            }
        }

        newDao.registerNew(form.getCountry(), form.getTitle(), form.getSummary(), form.getLink(), pressMedium, new SimpleDateFormat("dd/MM/yyyy").parse(form.getDate()), imgUrl, altImgUrl);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/news/list", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String getListNews(ModelMap model, Locale locale)
    {
        List<New> news = newDao.getNews();
        model.addAttribute("news", news);
        return "news :: list";
    }

    public static class RegisterNewForm extends FormGeneric implements Serializable {

        Integer country;
        String pressMedium;
        String newPressMedium;
        String link;
        String date;
        String title;
        String summary;


        public RegisterNewForm() {
            this.setValidator(new RegisterNewForm.Validator());
        }

        public class Validator extends FormValidator implements Serializable {

            public IntegerFieldValidator country;
            public StringFieldValidator pressMedium;
            public StringFieldValidator link;
            public StringFieldValidator date;
            public StringFieldValidator title;
            public StringFieldValidator summary;

            public final static String URL_REGEX = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

            public Validator() {
                addValidator(country = new IntegerFieldValidator().setRequired(true));
                addValidator(pressMedium = new StringFieldValidator().setRequired(true));
                addValidator(link = new StringFieldValidator().setRequired(true).setValidRegex(URL_REGEX));
                addValidator(date = new StringFieldValidator().setRequired(true).setValidRegex(DateFieldValidator.REGEXP_DDMMYYYY_SLASH_DATE));
                addValidator(summary = new StringFieldValidator().setRequired(true).setMaxCharacters(200));
                addValidator(title = new StringFieldValidator().setRequired(true).setMaxCharacters(72));
            }

            @Override
            protected void setDynamicValidations() { }

            @Override
            protected Object getSubclass() {
                return this;
            }

            @Override
            protected Object getFormClass() {
                return RegisterNewForm.this;
            }
        }

        public Integer getCountry() { return country; }

        public void setCountry(Integer country) { this.country = country; }

        public String getLink() { return link; }

        public void setLink(String link) { this.link = link; }

        public String getPressMedium() { return pressMedium; }

        public void setPressMedium(String pressMedium) { this.pressMedium = pressMedium; }

        public String getNewPressMedium() { return newPressMedium; }

        public void setNewPressMedium(String newPressMedium) { this.newPressMedium = newPressMedium; }

        public String getDate() { return date; }

        public void setDate(String date) { this.date = date; }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

    }
}
