package br.com.fiap.wtchat.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "campaigns")
public class Campaign {

    @Id
    private String id;

    private String title;

    private String body;

    private String url;

    private String segmentId;

    private List<Message.MessageAction> actions;

    private Map<String, String> actionUrls;

    private CampaignStatus status = CampaignStatus.DRAFT;

    private LocalDateTime scheduledAt;

    private LocalDateTime sentAt;

    private String createdBy;

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum CampaignStatus {
        DRAFT, SCHEDULED, SENT, FAILED
    }
}
