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

    public String getSongId() {
        return songId;
    }

    public String getSongKey() {
        return songKey;
    }

    public audioModel(String songUri, String songTitle, String songWriter, String songId, String songKey) {
        this.songUri = songUri;
        this.songTitle = songTitle;
        this.songWriter = songWriter;
        this.songId = songId;
        this.songKey = songKey;
    }


}
