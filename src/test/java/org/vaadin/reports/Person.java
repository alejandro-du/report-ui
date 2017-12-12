package org.vaadin.reports;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Alejandro Duarte
 */
@Entity
@Data
public class Person {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String phoneNumber;

    private City city;

}
