package com.deltacodex.memoryme.model;

public class TVShow {
    private String documentId;
    private String showName;
    private String watchedDate;
    private String description;
    private String episodes;
    private String thoughts;
    private String landscapeUrl;
    private String portraitUrl;
    private String trailerUrl;

    public TVShow() { } // empty constructor for Firebase

    public TVShow(String documentId, String showName, String watchedDate, String description, String episodes, String thoughts, String landscapeUrl, String portraitUrl, String trailerUrl) {
        this.showName = showName;
        this.watchedDate = watchedDate;
        this.description = description;
        this.episodes = episodes;
        this.thoughts = thoughts;
        this.landscapeUrl = landscapeUrl;
        this.portraitUrl = portraitUrl;
        this.trailerUrl = trailerUrl;
    }
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getWatchedDate() {
        return watchedDate;
    }

    public void setWatchedDate(String watchedDate) {
        this.watchedDate = watchedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEpisodes() {
        return episodes;
    }

    public void setEpisodes(String episodes) {
        this.episodes = episodes;
    }

    public String getThoughts() {
        return thoughts;
    }

    public void setThoughts(String thoughts) {
        this.thoughts = thoughts;
    }

    public String getLandscapeUrl() {
        return landscapeUrl;
    }

    public void setLandscapeUrl(String landscapeUrl) {
        this.landscapeUrl = landscapeUrl;
    }

    public String getPortraitUrl() {
        return portraitUrl;
    }

    public void setPortraitUrl(String portraitUrl) {
        this.portraitUrl = portraitUrl;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }
}
