package org.vaadin.reports;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Random;
import java.util.function.Function;

/**
 * @author Alejandro Duarte
 */
public class JPAService {

    private static EntityManagerFactory factory;

    public static void init() {
        if (factory == null) {
            factory = Persistence.createEntityManagerFactory("test-pu");
            createTestData();
        }
    }

    private static void createTestData() {
        String[] firstNames = {"John", "Peter", "Alice", "Joshua", "Mike", "Olivia", "Nina", "Alex", "Rita", "Dan", "Umberto", "Henrik", "Marian", "Edgar", "Juan"};
        String[] lastNames = {"Smith", "Gordon", "Simpson", "Brown", "Clavel", "Simons", "Verne", "Scott", "Allison", "Gates", "Rowling", "Barks", "Ross", "Schneider", "Duarte"};

        Random r = new Random(0);

        for (int i = 0; i < 100; i++) {
            Person person = new Person();
            person.setName(firstNames[r.nextInt(firstNames.length)] + " " + lastNames[r.nextInt(lastNames.length)]);
            person.setPhoneNumber("+358 02 555 " + r.nextInt(10) + r.nextInt(10) + r.nextInt(10) + r.nextInt(10));
            person.setCity(City.values()[r.nextInt(City.values().length)]);

            PersonRepository.save(person);
        }
    }

    public static void close() {
        factory.close();
    }

    public static EntityManagerFactory getFactory() {
        return factory;
    }

    public static <T> T runInTransaction(Function<EntityManager, T> function) {
        EntityManager entityManager = null;

        try {
            entityManager = JPAService.getFactory().createEntityManager();
            entityManager.getTransaction().begin();

            T result = function.apply(entityManager);

            entityManager.getTransaction().commit();
            return result;

        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

}
