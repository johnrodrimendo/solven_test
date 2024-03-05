package com.affirm.client.service.impl;

import com.affirm.client.model.wordpress.GeneralWordPressPost;
import com.affirm.client.model.wordpress.WordPressCategory;
import com.affirm.client.model.wordpress.WordPressPost;
import com.affirm.client.model.wordpress.WordPressTransformer;
import com.affirm.client.service.WordpressService;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jrodriguez
 */

@Service("wordpressService")
public class WordpressServiceImpl implements WordpressService {

    private static final Logger logger = Logger.getLogger(WordpressServiceImpl.class);

    @Override
    public List<GeneralWordPressPost> getPosts(String queryParams) throws Exception {
        return (List<GeneralWordPressPost>) getPosts("posts", queryParams, new WordPressTransformer() {
            @Override
            public List transform(String var1) throws Exception {
                List<GeneralWordPressPost> posts = new ObjectMapper().readValue(var1, new TypeReference<List<GeneralWordPressPost>>() {
                });
                JSONArray postsArray = new JSONArray(var1);
                for (int i = 0; i < postsArray.length(); i++) {
                    JSONObject jsonPost = postsArray.getJSONObject(i);
                    if (postsArray.getJSONObject(i).optJSONObject("_embedded") != null
                            && postsArray.getJSONObject(i).getJSONObject("_embedded").optJSONArray("wp:featuredmedia") != null
                            && postsArray.getJSONObject(i).getJSONObject("_embedded").getJSONArray("wp:featuredmedia").getJSONObject(0).optJSONObject("media_details") != null
                            && postsArray.getJSONObject(i).getJSONObject("_embedded").getJSONArray("wp:featuredmedia").getJSONObject(0).getJSONObject("media_details").optJSONObject("sizes") != null) {
                        JSONObject mediaDetailsSizes = postsArray.getJSONObject(i).getJSONObject("_embedded").getJSONArray("wp:featuredmedia").getJSONObject(0).getJSONObject("media_details").getJSONObject("sizes");
                        posts.stream().filter(p -> p.getId() == jsonPost.getInt("id")).findFirst().ifPresent(p -> {
                            p.setFeaturedImageUrlThumbnail(JsonUtil.getStringFromJson(mediaDetailsSizes.optJSONObject("thumbnail"), "source_url", null));
                            p.setFeaturedImageUrlMedium(JsonUtil.getStringFromJson(mediaDetailsSizes.optJSONObject("medium"), "source_url", null));
                            p.setFeaturedImageUrlMediumLarge(JsonUtil.getStringFromJson(mediaDetailsSizes.optJSONObject("medium_large"), "source_url", null));
                            p.setFeaturedImageUrlLarge(JsonUtil.getStringFromJson(mediaDetailsSizes.optJSONObject("large"), "source_url", null));
                        });
                    }

                    List<String> categories = new ArrayList<>();
                    if (postsArray.getJSONObject(i).optJSONObject("_embedded") != null
                            && postsArray.getJSONObject(i).optJSONObject("_embedded").optJSONArray("wp:term") != null) {
                        JSONArray wpTerms = postsArray.getJSONObject(i).optJSONObject("_embedded").optJSONArray("wp:term");
                        for (int j = 0; j < wpTerms.length(); j++) {
                            JSONArray innerWpTerms = wpTerms.optJSONArray(j);
                            for (int k = 0; k < innerWpTerms.length(); k++) {
                                JSONObject wpTerm = innerWpTerms.optJSONObject(k);
                                if (wpTerm != null && wpTerm.optString("taxonomy") != null && wpTerm.optString("taxonomy").equals("category")) {
                                    categories.add(wpTerm.optString("name"));
                                }
                            }
                        }
                    }
                    if (!categories.isEmpty()) {
                        posts.stream().filter(p -> p.getId() == jsonPost.getInt("id")).findFirst().ifPresent(p -> p.setCategories(categories.toArray(new String[categories.size()])));
                    }
                }
                return posts;
            }
        });
    }

    @Override
    public List<GeneralWordPressPost> getFaqs(String queryParams) throws Exception {
        return (List<GeneralWordPressPost>) getPosts("faq", queryParams, new WordPressTransformer() {
            @Override
            public List transform(String var1) throws Exception {
                return new ObjectMapper().readValue(var1, new TypeReference<List<GeneralWordPressPost>>() {
                });
            }
        });
    }

    @Override
    public GeneralWordPressPost getPostBySlug(String slug) throws Exception {
        List<GeneralWordPressPost> generalWordPressPosts = (List<GeneralWordPressPost>) getPosts("posts", "slug=" + slug + "&_embed", new WordPressTransformer() {
            @Override
            public List transform(String var1) throws Exception {
                return new ObjectMapper().readValue(var1, new TypeReference<List<GeneralWordPressPost>>() {
                });
            }
        });
        if(generalWordPressPosts != null && generalWordPressPosts.size() > 0) return generalWordPressPosts.get(0);
        return null;
    }

    @Override
    public GeneralWordPressPost getFaqBySlug(String slug) throws Exception {
        return (GeneralWordPressPost) getPosts("faq", "slug=" + slug, new WordPressTransformer() {
            @Override
            public List transform(String var1) throws Exception {
                return new ObjectMapper().readValue(var1, new TypeReference<List<GeneralWordPressPost>>() {
                });
            }
        }).get(0);
    }

    @Override
    public List<GeneralWordPressPost> getMostRecentPosts(int count) throws Exception {
        return (List<GeneralWordPressPost>) getPosts("posts", "orderby=date&order=desc&per_page=" + count + "&_embed", new WordPressTransformer() {
            @Override
            public List transform(String var1) throws Exception {
                return new ObjectMapper().readValue(var1, new TypeReference<List<GeneralWordPressPost>>() {
                });
            }
        });
    }

    @Override
    public List<WordPressCategory> getCategories(String queryParams) throws Exception {
        return (List<WordPressCategory>) getCategories(queryParams, new WordPressTransformer() {
            @Override
            public List transform(String var1) throws Exception {
                return new ObjectMapper().readValue(var1, new TypeReference<List<WordPressCategory>>() {
                });
            }
        });
    }

    private List<? extends WordPressPost> getPosts(String path, String queryParams, WordPressTransformer transformer) throws Exception {
        Client jaxrsClient = ClientBuilder.newClient();

        String endpoint = Configuration.WORDPRESS_BASE_URL + path + (queryParams != null ? "?" + (queryParams + "&status=publish") : "?status=publish");
        Response response = jaxrsClient
                .target(endpoint)
                .request(new String[]{"application/json"}).get();

        if (Response.Status.OK.getStatusCode() == response.getStatusInfo().getStatusCode()) {
            return transformer.transform(readInputStreamToString((InputStream) response.getEntity()));
        } else {
            throw new RuntimeException(String.format("Did not get an OK status code from WordPress API at %s, rather: %s",
                    new Object[]{endpoint, response != null ? response.getStatusInfo() : ""}));
        }
    }

    private List<? extends WordPressCategory> getCategories(String queryParams, WordPressTransformer transformer) throws Exception {
        Client jaxrsClient = ClientBuilder.newClient();

        String endpoint = Configuration.WORDPRESS_BASE_URL + "categories" + (queryParams != null ? "?" + (queryParams + "&status=publish" ): "?status=publish");
        Response response = jaxrsClient
                .target(endpoint)
                .request(new String[]{"application/json"}).get();

        if (Response.Status.OK.getStatusCode() == response.getStatusInfo().getStatusCode()) {
            return transformer.transform(readInputStreamToString((InputStream) response.getEntity()));
        } else {
            throw new RuntimeException(String.format("Did not get an OK status code from WordPress API at %s, rather: %s",
                    new Object[]{endpoint, response != null ? response.getStatusInfo() : ""}));
        }
    }

    private String readInputStreamToString(InputStream inputStream) {
        StringWriter writer = new StringWriter();

        try {
            IOUtils.copy(inputStream, writer, Charset.forName("UTF-8"));
        } catch (IOException var4) {
            throw new RuntimeException(var4);
        }

        return writer.toString();
    }

    public Integer getTotalPosts(int tagCountryId) throws Exception {
        Client jaxrsClient = ClientBuilder.newClient();
        String endpoint = Configuration.WORDPRESS_BASE_URL + "posts?per_page=1&tags=" + tagCountryId + "&status=publish";
        Response response = jaxrsClient.target(endpoint).request(new String[]{"application/json"}).get();
        return Integer.parseInt(response.getHeaders().getFirst("X-WP-Total").toString());
    }
}
