package com.mikkezavala.tools.domain;

public class Job {
    private String fileBase;
    private String resourceA;
    private String resourceB;

    public Job(String fileBase, String resourceA, String resourceB) {
        this.fileBase = fileBase;
        this.resourceA = resourceA;
        this.resourceB = resourceB;
    }

    public String getResourceA() {
        return resourceA;
    }

    public void setResourceA(String resourceA) {
        this.resourceA = resourceA;
    }

    public String getResourceB() {
        return resourceB;
    }

    public void setResourceB(String resourceB) {
        this.resourceB = resourceB;
    }

    public String getFileBase() {
        return fileBase;
    }

    public void setFileBase(String fileBase) {
        this.fileBase = fileBase;
    }

}
