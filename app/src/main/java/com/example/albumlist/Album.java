package com.example.albumlist;

import androidx.annotation.Nullable;

import java.sql.Blob;

public class Album {
    private String albumName;
    private String actor;
    private byte[] picture;
    private Integer year;

    public Album(String albumName, String actor, byte[] picture, Integer year) {
        this.albumName = albumName;
        this.actor = actor;
        this.picture = picture;
        this.year = year;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
