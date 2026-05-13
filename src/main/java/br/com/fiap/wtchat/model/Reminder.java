package br.com.fiap.wtchat.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
}
