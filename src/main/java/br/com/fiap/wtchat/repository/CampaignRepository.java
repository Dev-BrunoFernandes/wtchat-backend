package br.com.fiap.wtchat.repository;

import br.com.fiap.wtchat.model.Campaign;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CampaignRepository extends MongoRepository<Campaign, String> {
    List<Campaign> findBySegmentId(String segmentId);
    List<Campaign> findByStatus(Campaign.CampaignStatus status);
    List<Campaign> findByCreatedByOrderByCreatedAtDesc(String createdBy);
}
