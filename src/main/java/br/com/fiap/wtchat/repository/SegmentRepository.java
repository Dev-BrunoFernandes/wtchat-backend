package br.com.fiap.wtchat.repository;

import br.com.fiap.wtchat.model.Segment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SegmentRepository extends MongoRepository<Segment, String> {
}
