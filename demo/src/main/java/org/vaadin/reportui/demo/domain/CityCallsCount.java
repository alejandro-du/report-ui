package org.vaadin.reportui.demo.domain;

import java.util.Objects;

public class CityCallsCount {

    City city;

    long calls;

    public CityCallsCount() {
    }

    public CityCallsCount(City city, long calls) {
        this.city = city;
        this.calls = calls;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CityCallsCount that = (CityCallsCount) o;
        return Objects.equals(city, that.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city);
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public long getCalls() {
        return calls;
    }

    public void setCalls(long calls) {
        this.calls = calls;
    }

}
