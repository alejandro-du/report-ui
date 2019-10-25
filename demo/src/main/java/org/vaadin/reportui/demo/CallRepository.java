package org.vaadin.reportui.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Alejandro Duarte
 */
@Repository
public interface CallRepository extends JpaRepository<Call, Long> {

    @Query("select new org.vaadin.reportui.demo.CityCallsCount(city, count(id)) from Call GROUP BY city ORDER BY city")
    List<CityCallsCount> getCountPerCity();

}
