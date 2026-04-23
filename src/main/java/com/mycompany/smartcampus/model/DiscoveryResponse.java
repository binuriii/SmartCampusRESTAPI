/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampus.model;

import java.util.Map;

/**
 *
 * @author binuripiyathma
 */

public class DiscoveryResponse {

    private String version;
    private String contact;
    private Map<String, String> resources;

    public DiscoveryResponse() {}

    public DiscoveryResponse(String version, String contact, Map<String, String> resources) {
        this.version = version;
        this.contact = contact;
        this.resources = resources;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Map<String, String> getResources() {
        return resources;
    }

    public void setResources(Map<String, String> resources) {
        this.resources = resources;
    }
}
