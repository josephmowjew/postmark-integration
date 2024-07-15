package com.qubedcare.postmark_integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.Instant;
import java.util.Map;

@Data
public class BounceEventDTO {
    @JsonProperty("RecordType")
    private String recordType;

    @JsonProperty("MessageStream")
    private String messageStream;

    @JsonProperty("ID")
    private Long id;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("TypeCode")
    private Integer typeCode;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Tag")
    private String tag;

    @JsonProperty("MessageID")
    private String messageId;

    @JsonProperty("Metadata")
    private Map<String, String> metadata;

    @JsonProperty("ServerID")
    private Integer serverId;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("Details")
    private String details;

    @JsonProperty("Email")
    private String email;

    @JsonProperty("From")
    private String from;

    @JsonProperty("BouncedAt")
    private Instant bouncedAt;

    @JsonProperty("DumpAvailable")
    private boolean dumpAvailable;

    @JsonProperty("Inactive")
    private boolean inactive;

    @JsonProperty("CanActivate")
    private boolean canActivate;

    @JsonProperty("Subject")
    private String subject;

    @JsonProperty("Content")
    private String content;
}