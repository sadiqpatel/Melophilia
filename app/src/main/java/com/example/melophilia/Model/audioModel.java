package com.example.melophilia.Model;

public class audioModel {

    public audioModel() {

    }

    public String getSongUri() {
        return songUri;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public String getSongWriter() {
        return songWriter;
    }

    public String songUri;
    public String songTitle;
    public String songWriter;
    public String songId;
    public String songKey;
    public String songImg;

    public String getSongImg() {
        return songImg;
    }

    public void setSongUri(String songUri) {
        this.songUri = songUri;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public void setSongWriter(String songWriter) {
        this.songWriter = songWriter;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public void setSongKey(String songKey) {
        this.songKey = songKey;
    }

    public void setSongImg(String songImg) {
        this.songImg = songImg;
    }

    public String getSongId() {
        return songId;
    }

    public String getSongKey() {
        return songKey;
    }

    public audioModel(String songUri,String songImg, String songTitle, String songWriter, String songId, String songKey) {
        this.songUri = songUri;
        this.songTitle = songTitle;
        this.songWriter = songWriter;
        this.songId = songId;
        this.songKey = songKey;
        this.songImg = songImg;
    }


}
