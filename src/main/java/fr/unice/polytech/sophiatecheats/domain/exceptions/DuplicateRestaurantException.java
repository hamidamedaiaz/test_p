package fr.unice.polytech.sophiatecheats.domain.exceptions;

public class DuplicateRestaurantException extends RuntimeException {
    public DuplicateRestaurantException(String name, String address) {
        super("Un restaurant nommé '" + name + "' à l'adresse '" + address + "' existe déjà");
    }
}
