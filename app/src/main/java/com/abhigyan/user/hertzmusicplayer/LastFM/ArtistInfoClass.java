package com.abhigyan.user.hertzmusicplayer.LastFM;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ArtistInfoClass {

    @SerializedName("artist")
    @Expose
    private Artist artist;

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public class Artist {

        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("mbid")
        @Expose
        private String mbid;
        @SerializedName("url")
        @Expose
        private String url;
        @SerializedName("image")
        @Expose
        private List<Image> image = null;
        @SerializedName("streamable")
        @Expose
        private String streamable;
        @SerializedName("ontour")
        @Expose
        private String ontour;

        @SerializedName("bio")
        @Expose
        private Bio bio;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMbid() {
            return mbid;
        }

        public void setMbid(String mbid) {
            this.mbid = mbid;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public List<Image> getImage() {
            return image;
        }

        public void setImage(List<Image> image) {
            this.image = image;
        }

        public String getStreamable() {
            return streamable;
        }

        public void setStreamable(String streamable) {
            this.streamable = streamable;
        }

        public String getOntour() {
            return ontour;
        }

        public void setOntour(String ontour) {
            this.ontour = ontour;
        }

        public Bio getBio() {
            return bio;
        }

        public void setBio(Bio bio) {
            this.bio = bio;
        }

    }

    public class Artist_ {

        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("url")
        @Expose
        private String url;
        @SerializedName("image")
        @Expose
        private List<Image_> image = null;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public List<Image_> getImage() {
            return image;
        }

        public void setImage(List<Image_> image) {
            this.image = image;
        }

    }

    public class Bio {


        @SerializedName("published")
        @Expose
        private String published;
        @SerializedName("summary")
        @Expose
        private String summary;
        @SerializedName("content")
        @Expose
        private String content;

        public String getPublished() {
            return published;
        }

        public void setPublished(String published) {
            this.published = published;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

    }

    public class Image {

        @SerializedName("#text")
        @Expose
        private String text;
        @SerializedName("size")
        @Expose
        private String size;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

    }

    public class Image_ {

        @SerializedName("#text")
        @Expose
        private String text;
        @SerializedName("size")
        @Expose
        private String size;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }
    }
}

