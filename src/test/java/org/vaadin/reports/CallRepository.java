package org.vaadin.reports;

import java.util.List;

/**
 * @author Alejandro Duarte
 */
public class CallRepository {

    public static List<Call> findAll() {
        return JPAService.runInTransaction(em ->
                em.createQuery("select c from Call c order by c.city, c.status").getResultList()
        );
    }

    public static Call save(Call call) {
        return JPAService.runInTransaction(em -> em.merge(call));
    }

    public static List<CityCallsCount> getCountPerCity() {
        return JPAService.runInTransaction(em -> em.createNativeQuery(
                "SELECT city, COUNT(id) calls FROM Call GROUP BY city ORDER BY city",
                "CityCallsCountMapping").getResultList());
    }

}
