package com.abhigyan.user.hertzmusicplayer.Utility;

public class Song {

    private long albumID;
    private String trackName;
    private String albumName;
    private String songLink;
    private String artistName;
    private String songDuration;
    private String composerName;
    private String songSize;

    public Song(long albumID, String trackName, String albumName, String songLink, String artistName, String songDuration, String composerName, String songSize) {
        this.albumID = albumID;
        this.trackName = trackName;
        this.albumName = albumName;
        this.songLink = songLink;
        this.artistName = artistName;
        this.songDuration = songDuration;
        this.composerName = composerName;
        this.songSize = songSize;
    }

    public long getAlbumID() {
        return albumID;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getSongLink() {
        return songLink;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public String getComposerName() {
        return composerName;
    }

    public String getSongSize() {
        return songSize;
    }
}
