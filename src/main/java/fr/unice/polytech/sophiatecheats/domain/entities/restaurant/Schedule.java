package fr.unice.polytech.sophiatecheats.domain.entities.restaurant;

import fr.unice.polytech.sophiatecheats.domain.exceptions.RestaurantValidationException;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record Schedule(LocalTime openingTime, LocalTime closingTime) {

    public Schedule(LocalTime openingTime, LocalTime closingTime) {
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        validate();
    }

    public static Schedule defaultSchedule() {
        return new Schedule(LocalTime.of(9, 0), LocalTime.of(22, 0));
    }

    private void validate() {
        if (openingTime != null && closingTime != null && openingTime.isAfter(closingTime)) {
            throw new RestaurantValidationException("L'heure d'ouverture ne peut pas être après l'heure de fermeture");
        }
    }

    public boolean isOpenAt(LocalTime time) {
        if (time == null || openingTime == null || closingTime == null) {
            return false;
        }
        return !time.isBefore(openingTime) && !time.isAfter(closingTime);
    }

    public boolean isOpenAt(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        return isOpenAt(dateTime.toLocalTime());
    }

    @Override
    public String toString() {
        return String.format("Schedule{openingTime=%s, closingTime=%s}", openingTime, closingTime);
    }
}
