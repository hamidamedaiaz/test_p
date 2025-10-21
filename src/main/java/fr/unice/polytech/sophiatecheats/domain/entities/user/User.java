package fr.unice.polytech.sophiatecheats.domain.entities.user;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Représente un utilisateur du système SophiaTechEats.
 * Sert d'identifiant pour les commandes et gère le crédit étudiant.
 *
 * Note: Cette classe ne gère pas l'authentification ou l'inscription,
 * elle sert uniquement d'identifiant métier.
 */
@Setter
@Getter
public class User {

    private final UUID id;
    private final String name;
    private final String email;
    private BigDecimal studentCredit;

    public User(String email, String name) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.name = name;
        this.studentCredit = BigDecimal.ZERO;
    }

    public User(UUID id, String email, String name, BigDecimal studentCredit) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.studentCredit = studentCredit != null ? studentCredit : BigDecimal.ZERO;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public BigDecimal getStudentCredit() {
        return studentCredit;
    }

    // Gestion du crédit étudiant
    public void addCredit(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.studentCredit = this.studentCredit.add(amount);
        }
    }

    public boolean hasEnoughCredit(BigDecimal amount) {
        return amount != null && this.studentCredit.compareTo(amount) >= 0;
    }

    public void deductCredit(BigDecimal amount) {
        if (amount != null && hasEnoughCredit(amount)) {
            this.studentCredit = this.studentCredit.subtract(amount);
        } else {
            throw new IllegalArgumentException("Insufficient credit");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", studentCredit=" + studentCredit +
                '}';
    }
}
