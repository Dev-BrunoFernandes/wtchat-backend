package br.com.fiap.wtchat.service;

import br.com.fiap.wtchat.dto.CampaignRequest;
import br.com.fiap.wtchat.model.Campaign;
import br.com.fiap.wtchat.model.Customer;
import br.com.fiap.wtchat.model.Message;
import br.com.fiap.wtchat.repository.CampaignRepository;
import br.com.fiap.wtchat.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final CustomerRepository customerRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final AuditService auditService;

    public Campaign create(CampaignRequest request, String operatorId) {
        Campaign campaign = new Campaign();
        campaign.setTitle(request.getTitle());
        campaign.setBody(request.getBody());
        campaign.setUrl(request.getUrl());
        campaign.setSegmentId(request.getSegmentId());
        campaign.setActions(request.getActions());
        campaign.setActionUrls(request.getActionUrls());
        campaign.setCreatedBy(operatorId);
        if (request.getScheduledAt() != null) {
            campaign.setScheduledAt(request.getScheduledAt());
            campaign.setStatus(Campaign.CampaignStatus.SCHEDULED);
        }
        return campaignRepository.save(campaign);
    }

    public Campaign send(String campaignId, String operatorId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campanha não encontrada"));

        List<Customer> customers = campaign.getSegmentId() != null
                ? customerRepository.findBySegmentId(campaign.getSegmentId())
                : customerRepository.findAll();

        // Envia via WebSocket para todos os clientes do segmento
        for (Customer customer : customers) {
            if (customer.getUserId() != null) {
                Message msg = new Message();
                msg.setSenderId("SYSTEM");
                msg.setRecipientId(customer.getUserId());
                msg.setContent(campaign.getBody());
                msg.setType(Message.MessageType.CAMPAIGN);
                msg.setActions(campaign.getActions());
                msg.setStatus(Message.MessageStatus.SENT);

                messagingTemplate.convertAndSendToUser(
                        customer.getUserId(),
                        "/queue/campaigns",
                        msg
                );
            }
        }

        campaign.setStatus(Campaign.CampaignStatus.SENT);
        campaign.setSentAt(LocalDateTime.now());

        auditService.log(operatorId, null, "SEND_CAMPAIGN", "Campaign", campaignId,
                "Campanha enviada para " + customers.size() + " clientes do segmento: " + campaign.getSegmentId());

        return campaignRepository.save(campaign);
    }

    public List<Campaign> findAll() {
        return campaignRepository.findAll();
    }

    public Campaign findById(String id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campanha não encontrada"));
    }
}
