package br.com.fiap.wtchat.repository;

import br.com.fiap.wtchat.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends MongoRepository<Customer, String> {
    Optional<Customer> findByUserId(String userId);
    List<Customer> findBySegmentId(String segmentId);
    List<Customer> findByTagsContaining(String tag);
    List<Customer> findByStatus(Customer.Status status);
    List<Customer> findByNameContainingIgnoreCase(String name);
}
