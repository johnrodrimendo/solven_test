package com.affirm.landing.controller;

import com.affirm.client.model.form.ContactForm;
import com.affirm.client.model.wordpress.GeneralWordPressPost;
import com.affirm.client.service.WordpressService;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.service.CountryContextService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.ValidatorUtil;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

/**
 * Created by renzodiaz on 6/19/17.
 */
@Controller
@Scope("request")
public class ExternalPagesController {
    @Autowired
    private UserDAO userDao;
    @Autowired
    private WordpressService wordpressService;
    @Autowired
    private CountryContextService countryContextService;

    @RequestMapping(value = "/blog", method = RequestMethod.GET)
    public String blogPath(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "category", required = false) Integer categoryId) throws Exception {

        ContactForm contactForm = new ContactForm();
        CountryParam currentCountry = countryContextService.getCountryParamsByRequest(request);

        int tagCountryId = currentCountry.getWpTagId() != null ? currentCountry.getWpTagId() : Configuration.WORDPRESS_DEFAULT_TAG_COUNTRY_ID;
        int totalPosts = wordpressService.getTotalPosts(tagCountryId);
        int perPage = Configuration.WORDPRESS_PER_PAGE;
        int pages = (totalPosts + perPage - 1) / perPage;
        page = (page == null) ? 1 : page;
        int offset = (page - 1) * perPage;

        List<GeneralWordPressPost> posts = wordpressService.getPosts(
                (search != null && !search.trim().isEmpty() ? "&search=" + URLEncoder.encode(search.trim(), "UTF-8") : "") +
                        (categoryId != null ? "&categories=" + categoryId : "") +
                        "&offset=" + offset + "&per_page=" + perPage + "&tags=" + tagCountryId + "&_embed");

        model.addAttribute("posts", posts);
        model.addAttribute("contactForm", contactForm);
        model.addAttribute("pages", pages);
        model.addAttribute("currentPage", page);
        model.addAttribute("mostRecentPosts", wordpressService.getMostRecentPosts(5));
        model.addAttribute("postCategories", wordpressService.getCategories(null));

        return "/page-blog";
    }

    @RequestMapping(value = "/centro-de-ayuda", method = RequestMethod.GET)
    public String faqsPath(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        String wordpressCountryCode = "";
        CountryParam currentCountry = countryContextService.getCountryParamsByRequest(request);
        if (currentCountry.getId().equals(CountryParam.COUNTRY_ARGENTINA)) {
            wordpressCountryCode = "argentina";
        } else if (currentCountry.getId().equals(CountryParam.COUNTRY_PERU)) {
            wordpressCountryCode = "";
        }

        model.addAttribute("faqs", wordpressService.getFaqs("per_page=100&country=" + wordpressCountryCode));
        model.addAttribute("contactForm", new ContactForm());
        return "/page-faqs";
    }

    @RequestMapping(value = "/{postType:blog}/{slug}", method = RequestMethod.GET)
    public String postDetail(
            ModelMap model, Locale locale, HttpServletRequest request,
            @PathVariable("postType") String postType,
            @PathVariable("slug") String slug) throws Exception {

        GeneralWordPressPost post = null;
        ContactForm contactForm = new ContactForm();
        model.addAttribute("contactForm", contactForm);
        switch (postType) {
            case "blog":
                post = wordpressService.getPostBySlug(slug);
                break;
            case "preguntas-frecuentes":
                post = wordpressService.getFaqBySlug(slug);
                break;
        }
        model.addAttribute("post", post);
        model.addAttribute("slug", slug);
        //return postType.equals("blog") ? "/external/blogDetails" : "/external/faqs";
        return postType.equals("blog") ? "/page-blog-detail" : "/page-faqs";
    }

    @RequestMapping(value = "/subscribe", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object subscribe(
            @RequestParam(value = "email", required = false) String email, Locale locale) throws Exception {

        StringFieldValidator validator = new StringFieldValidator(ValidatorUtil.EMAIL, email);
        validator.validate(locale);
        if (validator.isHasErrors()) {
            return AjaxResponse.errorMessage("Por favor, ingresa un email válido.");
        }

        userDao.registerSubscritor(email, true);

        return AjaxResponse.ok(null);
    }

}
