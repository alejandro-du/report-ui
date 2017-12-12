package org.vaadin.reports;

import java.util.List;

/**
 * @author Alejandro Duarte
 */
public class PersonRepository {

    public static List<Person> findAll() {
        return JPAService.runInTransaction(em ->
                em.createQuery("select p from Person p").getResultList()
        );
    }

    public static Person save(Person person) {
        return JPAService.runInTransaction(em -> em.merge(person));
    }

}
