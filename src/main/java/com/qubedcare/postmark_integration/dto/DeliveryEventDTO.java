package com.qubedcare.postmark_integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Map;

public class DeliveryEventDTO {
    @JsonProperty("MessageID")
    private String messageId;

    @JsonProperty("Recipient")
    private String recipient;

    @JsonProperty("DeliveredAt")
    private Instant deliveredAt;

    @JsonProperty("Details")
    private String details;

    @JsonProperty("Tag")
    private String tag;

    @JsonProperty("ServerID")
    private Integer serverId;

    @JsonProperty("Metadata")
    private Map<String, String> metadata;

    @JsonProperty("RecordType")
    private String recordType;

    @JsonProperty("MessageStream")
    private String messageStream;

    // Getters and setters
    // ... (implement getters and setters for all fields)
    public String getMessageId() {
        return messageId;
    }
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    public String getRecipient() {
        return recipient;
    }
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    public Instant getDeliveredAt() {
        return deliveredAt;
    }
    public void setDeliveredAt(Instant deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
    public String getDetails() {
        return details;
    }
    public void setDetails(String details) {
        this.details = details;
    }
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    public Integer getServerId() {
        return serverId;
    }
    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }
    public Map<String, String> getMetadata() {
        return metadata;
    }
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
    public String getRecordType() {
        return recordType;
    }
    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }
    public String getMessageStream() {
        return messageStream;
    }
}