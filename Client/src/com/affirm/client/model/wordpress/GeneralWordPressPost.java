package com.affirm.client.model.wordpress;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by renzodiaz on 6/20/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneralWordPressPost implements WordPressPost {

    @JsonProperty(value = "id", required = true)
    private long id;
    @JsonProperty("date")
    private String date;
    @JsonProperty("date_gmt")
    private String dateGmt;
    @JsonProperty("guid")
    private GeneralWordPressPostObject guid;
    @JsonProperty("modified")
    private String modified;
    @JsonProperty("modified_gmt")
    private String modifiedGmt;
    @JsonProperty("slug")
    private String slug;
    @JsonProperty("status")
    private String status;
    @JsonProperty("type")
    private String type;
    @JsonProperty("link")
    private String link;
    @JsonProperty(value = "title", required = true)
    private GeneralWordPressPostObject title;
    @JsonProperty(value = "content", required = true)
    private GeneralWordPressPostObject content;
    @JsonProperty("excerpt")
    private GeneralWordPressPostObject excerpt;
    @JsonProperty("author")
    private String[] author;
    @JsonProperty("featured_media")
    private String featuredMedia;
    @JsonProperty("feature_image_url")
    private String featuredImageUrl;
    @JsonProperty("comment_status")
    private String commentStatus;
    @JsonProperty("ping_status")
    private String pingStatus;
    @JsonProperty("sticky")
    private boolean sticky;
    @JsonProperty("template")
    private String template;
    @JsonProperty("format")
    private String format;
    @JsonProperty("categories")
    private String[] categories;
    @JsonProperty("meta")
    private String[] meta;
    private String featuredImageUrlThumbnail;
    private String featuredImageUrlMedium;
    private String featuredImageUrlMediumLarge;
    private String featuredImageUrlLarge;

    public String getFeaturedImageUrlNoHttps() {
        return featuredImageUrl != null ? featuredImageUrl.replaceFirst("https", "http") : null;
    }

    public Date getDateAsDate() throws Exception{
        if(getDate() == null)
            return null;
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(getDate());
    }

    public String getAuthorFirstName(){
        if(author == null || author.length == 0)
            return "";
        return author[0].split(" ")[0];
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateGmt() {
        return dateGmt;
    }

    public void setDateGmt(String dateGmt) {
        this.dateGmt = dateGmt;
    }

    public GeneralWordPressPostObject getGuid() {
        return guid;
    }

    public void setGuid(GeneralWordPressPostObject guid) {
        this.guid = guid;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getModifiedGmt() {
        return modifiedGmt;
    }

    public void setModifiedGmt(String modifiedGmt) {
        this.modifiedGmt = modifiedGmt;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public GeneralWordPressPostObject getTitle() {
        return title;
    }

    public void setTitle(GeneralWordPressPostObject title) {
        this.title = title;
    }

    public GeneralWordPressPostObject getContent() {
        return content;
    }

    public void setContent(GeneralWordPressPostObject content) {
        this.content = content;
    }

    public GeneralWordPressPostObject getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(GeneralWordPressPostObject excerpt) {
        this.excerpt = excerpt;
    }

    public String[] getAuthor() {
        return author;
    }

    public void setAuthor(String[] author) {
        this.author = author;
    }

    public String getFeaturedMedia() {
        return featuredMedia;
    }

    public void setFeaturedMedia(String featuredMedia) {
        this.featuredMedia = featuredMedia;
    }

    public String getFeaturedImageUrl() {
        return featuredImageUrl;
    }

    public void setFeaturedImageUrl(String featuredImageUrl) {
        this.featuredImageUrl = featuredImageUrl;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public String getPingStatus() {
        return pingStatus;
    }

    public void setPingStatus(String pingStatus) {
        this.pingStatus = pingStatus;
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String[] getMeta() {
        return meta;
    }

    public void setMeta(String[] meta) {
        this.meta = meta;
    }

    public String getFeaturedImageUrlThumbnail() {
        return featuredImageUrlThumbnail;
    }

    public void setFeaturedImageUrlThumbnail(String featuredImageUrlThumbnail) {
        this.featuredImageUrlThumbnail = featuredImageUrlThumbnail;
    }

    public String getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(String commentStatus) {
        this.commentStatus = commentStatus;
    }

    public String getFeaturedImageUrlMedium() {
        return featuredImageUrlMedium;
    }

    public void setFeaturedImageUrlMedium(String featuredImageUrlMedium) {
        this.featuredImageUrlMedium = featuredImageUrlMedium;
    }

    public String getFeaturedImageUrlMediumLarge() {
        return featuredImageUrlMediumLarge;
    }

    public void setFeaturedImageUrlMediumLarge(String featuredImageUrlMediumLarge) {
        this.featuredImageUrlMediumLarge = featuredImageUrlMediumLarge;
    }

    public String getFeaturedImageUrlLarge() {
        return featuredImageUrlLarge;
    }

    public void setFeaturedImageUrlLarge(String featuredImageUrlLarge) {
        this.featuredImageUrlLarge = featuredImageUrlLarge;
    }
}
