package org.vaadin.reports;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * @author Alejandro Duarte
 */
@Entity
@Data
public class Call {

    @Id
    @GeneratedValue
    private Long id;

    private String client;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private City city;

    private LocalDateTime startTime;

    private Integer duration;

    @Enumerated(EnumType.STRING)
    private Status status;

}
