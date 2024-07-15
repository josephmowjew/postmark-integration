package com.qubedcare.postmark_integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
public class OpenEventDTO {
    @JsonProperty("RecordType")
    private String recordType;

    @JsonProperty("MessageStream")
    private String messageStream;

    @JsonProperty("FirstOpen")
    private boolean firstOpen;

    @JsonProperty("Client")
    private ClientInfo client;

    @JsonProperty("OS")
    private OSInfo os;

    @JsonProperty("Platform")
    private String platform;

    @JsonProperty("UserAgent")
    private String userAgent;

    @JsonProperty("Geo")
    private GeoInfo geo;

    @JsonProperty("MessageID")
    private String messageId;

    @JsonProperty("Metadata")
    private Map<String, String> metadata;

    @JsonProperty("ReceivedAt")
    private Instant receivedAt;

    @JsonProperty("Tag")
    private String tag;

    @JsonProperty("Recipient")
    private String recipient;

    @Data
    @NoArgsConstructor
    public static class ClientInfo {
        @JsonProperty("Name")
        private String name;

        @JsonProperty("Company")
        private String company;

        @JsonProperty("Family")
        private String family;
    }

    @Data
    @NoArgsConstructor
    public static class OSInfo {
        @JsonProperty("Name")
        private String name;

        @JsonProperty("Company")
        private String company;

        @JsonProperty("Family")
        private String family;
    }

    @Data
    @NoArgsConstructor
    public static class GeoInfo {
        @JsonProperty("CountryISOCode")
        private String countryIsoCode;

        @JsonProperty("Country")
        private String country;

        @JsonProperty("RegionISOCode")
        private String regionIsoCode;

        @JsonProperty("Region")
        private String region;

        @JsonProperty("City")
        private String city;

        @JsonProperty("Zip")
        private String zip;

        @JsonProperty("Coords")
        private String coords;

        @JsonProperty("IP")
        private String ip;
    }
}
