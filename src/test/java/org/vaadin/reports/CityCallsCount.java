package org.vaadin.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SqlResultSetMapping(name = "CityCallsCountMapping", classes = {
        @ConstructorResult(targetClass = CityCallsCount.class,
                columns = {@ColumnResult(name = "city", type = String.class), @ColumnResult(name = "calls", type = Integer.class)})
})
public class CityCallsCount {

    @Id
    String city;

    Integer calls;

}
