package br.com.fiap.wtchat.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "reminders")
public class Reminder {

    @Id
    private String id;

    private String userId;

    private String title;

    private String subtitle;

    /** ISO date string: yyyy-MM-dd */
    private String date;

    /** HH:mm */
    private String time;

    private String address;

    private String duration;

    private String note;

    private List<String> participants; // user IDs
    private String ownerName;
}
