package com.affirm.client.service;


import com.affirm.client.model.wordpress.GeneralWordPressPost;
import com.affirm.client.model.wordpress.WordPressCategory;

import java.util.List;

/**
 * @author jrodriguez
 */
public interface WordpressService {
    List<GeneralWordPressPost> getPosts(String queryParams) throws Exception;

    List<GeneralWordPressPost> getFaqs(String queryParams) throws Exception;

    GeneralWordPressPost getPostBySlug(String slug) throws Exception;

    GeneralWordPressPost getFaqBySlug(String slug) throws Exception;

    List<GeneralWordPressPost> getMostRecentPosts(int count) throws Exception;

    List<WordPressCategory> getCategories(String queryParams) throws Exception;

    Integer getTotalPosts(int tagCountryId) throws Exception;
}
