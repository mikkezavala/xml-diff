package com.mikkezavala.tools.domain;

public class Resource {
    private String id;
    private String fileName;
    private String location;

    public Resource(String id, String fileName, String location) {
        this.id = id;
        this.fileName = fileName;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
