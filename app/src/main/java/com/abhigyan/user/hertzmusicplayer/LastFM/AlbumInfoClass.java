package com.abhigyan.user.hertzmusicplayer.LastFM;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AlbumInfoClass {

    @SerializedName("results")
    @Expose
    private Results results;

    public Results getResults() {
        return results;
    }

    public void setResults(Results results) {
        this.results = results;
    }

    public class Album {

        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("artist")
        @Expose
        private String artist;
        @SerializedName("url")
        @Expose
        private String url;
        @SerializedName("image")
        @Expose
        private List<Image> image = null;
        @SerializedName("streamable")
        @Expose
        private String streamable;
        @SerializedName("mbid")
        @Expose
        private String mbid;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
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

        public String getMbid() {
            return mbid;
        }

        public void setMbid(String mbid) {
            this.mbid = mbid;
        }

    }

    public class Albummatches {

        @SerializedName("album")
        @Expose
        private List<Album> album = null;

        public List<Album> getAlbum() {
            return album;
        }

        public void setAlbum(List<Album> album) {
            this.album = album;
        }

    }


    public class Attr {

        @SerializedName("for")
        @Expose
        private String _for;

        public String getFor() {
            return _for;
        }

        public void setFor(String _for) {
            this._for = _for;
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


    public class OpensearchQuery {

        @SerializedName("#text")
        @Expose
        private String text;
        @SerializedName("role")
        @Expose
        private String role;
        @SerializedName("searchTerms")
        @Expose
        private String searchTerms;
        @SerializedName("startPage")
        @Expose
        private String startPage;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getSearchTerms() {
            return searchTerms;
        }

        public void setSearchTerms(String searchTerms) {
            this.searchTerms = searchTerms;
        }

        public String getStartPage() {
            return startPage;
        }

        public void setStartPage(String startPage) {
            this.startPage = startPage;
        }

    }
    public class Results {

        @SerializedName("opensearch:Query")
        @Expose
        private OpensearchQuery opensearchQuery;
        @SerializedName("opensearch:totalResults")
        @Expose
        private String opensearchTotalResults;
        @SerializedName("opensearch:startIndex")
        @Expose
        private String opensearchStartIndex;
        @SerializedName("opensearch:itemsPerPage")
        @Expose
        private String opensearchItemsPerPage;
        @SerializedName("albummatches")
        @Expose
        private Albummatches albummatches;
        @SerializedName("@attr")
        @Expose
        private Attr attr;

        public OpensearchQuery getOpensearchQuery() {
            return opensearchQuery;
        }

        public void setOpensearchQuery(OpensearchQuery opensearchQuery) {
            this.opensearchQuery = opensearchQuery;
        }

        public String getOpensearchTotalResults() {
            return opensearchTotalResults;
        }

        public void setOpensearchTotalResults(String opensearchTotalResults) {
            this.opensearchTotalResults = opensearchTotalResults;
        }

        public String getOpensearchStartIndex() {
            return opensearchStartIndex;
        }

        public void setOpensearchStartIndex(String opensearchStartIndex) {
            this.opensearchStartIndex = opensearchStartIndex;
        }

        public String getOpensearchItemsPerPage() {
            return opensearchItemsPerPage;
        }

        public void setOpensearchItemsPerPage(String opensearchItemsPerPage) {
            this.opensearchItemsPerPage = opensearchItemsPerPage;
        }

        public Albummatches getAlbummatches() {
            return albummatches;
        }

        public void setAlbummatches(Albummatches albummatches) {
            this.albummatches = albummatches;
        }

        public Attr getAttr() {
            return attr;
        }

        public void setAttr(Attr attr) {
            this.attr = attr;
        }

    }

}
