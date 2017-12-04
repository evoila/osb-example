package de.evoila.cf.broker.controller.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiLocationInfo {
    @JsonProperty(value = "name", required = false)
    String name;
    @JsonProperty(value = "build", required = false)
    String build;
    @JsonProperty(value = "support", required = false)
    String support;
    @JsonProperty(value = "version", required = false)
    String version;
    @JsonProperty(value = "description", required = false)
    String description;
    @JsonProperty(value="authorization_endpoint", required = false)
    String authorizationEndpoint;
    @JsonProperty(value="token_endpoint", required = false)
    String tokenEndpoint;
    @JsonProperty(value="min_cli_version", required = false)
    String minCliVersion;
    @JsonProperty(value = "api_version", required = false)
    String apiVersion;

    public String getName () {
        return name;
    }

    public String getBuild () {
        return build;
    }

    public String getSupport () {
        return support;
    }

    public String getVersion () {
        return version;
    }

    public String getDescription () {
        return description;
    }

    public String getAuthorizationEndpoint () {
        return authorizationEndpoint;
    }

    public String getTokenEndpoint () {
        return tokenEndpoint;
    }

    public String getMinCliVersion () {
        return minCliVersion;
    }

    public String getApiVersion () {
        return apiVersion;
    }
}
