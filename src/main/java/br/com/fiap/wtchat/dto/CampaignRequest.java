package br.com.fiap.wtchat.dto;

import br.com.fiap.wtchat.model.Message;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class CampaignRequest {
    private String title;
    private String body;
    private String url;
    private String segmentId;
    private List<Message.MessageAction> actions;
    private Map<String, String> actionUrls;
    private LocalDateTime scheduledAt;
}
