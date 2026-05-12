package br.com.fiap.wtchat.dto;

import br.com.fiap.wtchat.model.Customer;
import lombok.Data;

import java.util.List;

@Data
public class CustomerRequest {
    private String name;
    private String email;
    private String phone;
    private String segmentId;
    private List<String> tags;
    private int score;
    private Customer.Status status = Customer.Status.LEAD;
    private String note;
}
