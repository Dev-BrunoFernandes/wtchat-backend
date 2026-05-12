package br.com.fiap.wtchat.service;

import br.com.fiap.wtchat.dto.CustomerRequest;
import br.com.fiap.wtchat.model.Customer;
import br.com.fiap.wtchat.model.Message;
import br.com.fiap.wtchat.repository.CustomerRepository;
import br.com.fiap.wtchat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final MessageRepository messageRepository;
    private final AuditService auditService;

    public Customer create(CustomerRequest request, String operatorId) {
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setSegmentId(request.getSegmentId());
        customer.setTags(request.getTags() != null ? request.getTags() : new ArrayList<>());
        customer.setScore(request.getScore());
        customer.setStatus(request.getStatus() != null ? request.getStatus() : Customer.Status.LEAD);
        if (request.getNote() != null) {
            customer.getNotes().add(request.getNote());
        }
        customerRepository.save(customer);
        auditService.log(operatorId, null, "CREATE_CUSTOMER", "Customer", customer.getId(), "Cliente criado: " + customer.getName());
        return customer;
    }

    public List<Customer> findAll(String search, String status) {
        if (search != null && !search.isBlank()) {
            return customerRepository.findByNameContainingIgnoreCase(search);
        }
        if (status != null && !status.isBlank()) {
            return customerRepository.findByStatus(Customer.Status.valueOf(status.toUpperCase()));
        }
        return customerRepository.findAll();
    }

    public Customer findById(String id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }

    public Customer update(String id, CustomerRequest request, String operatorId) {
        Customer customer = findById(id);
        if (request.getName() != null) customer.setName(request.getName());
        if (request.getEmail() != null) customer.setEmail(request.getEmail());
        if (request.getPhone() != null) customer.setPhone(request.getPhone());
        if (request.getSegmentId() != null) customer.setSegmentId(request.getSegmentId());
        if (request.getTags() != null) customer.setTags(request.getTags());
        if (request.getScore() > 0) customer.setScore(request.getScore());
        if (request.getStatus() != null) customer.setStatus(request.getStatus());
        if (request.getNote() != null) {
            if (customer.getNotes() == null) customer.setNotes(new ArrayList<>());
            customer.getNotes().add(request.getNote());
        }
        customer.setUpdatedAt(LocalDateTime.now());
        auditService.log(operatorId, null, "UPDATE_CUSTOMER", "Customer", id, "Cliente atualizado: " + customer.getName());
        return customerRepository.save(customer);
    }

    public void delete(String id, String operatorId) {
        customerRepository.deleteById(id);
        auditService.log(operatorId, null, "DELETE_CUSTOMER", "Customer", id, "Cliente removido");
    }

    public Map<String, Object> getTimeline(String customerId) {
        Customer customer = findById(customerId);
        List<Message> messages = messageRepository.findByRecipientIdOrderByCreatedAtDesc(customerId);
        return Map.of(
                "customer", customer,
                "recentMessages", messages.stream().limit(10).toList(),
                "totalMessages", messages.size()
        );
    }
}
