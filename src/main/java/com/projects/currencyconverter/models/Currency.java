package com.projects.currencyconverter.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Currency {
    @JsonProperty("currencies")
    private Map<String,String> currencies;
    @JsonProperty("success")
    private boolean success;
}
