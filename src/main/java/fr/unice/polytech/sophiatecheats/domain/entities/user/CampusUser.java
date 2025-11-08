package fr.unice.polytech.sophiatecheats.domain.entities.user;

import java.math.BigDecimal;

public class CampusUser extends User {

    private CampusUser(Builder builder) {
        super(builder.email, builder.name);
        this.setStudentCredit(builder.balance);
    }

    public static Builder builder() {
        return new Builder();
    }

    public void setBalance(double balance) {
        this.setStudentCredit(BigDecimal.valueOf(balance));
    }

    public BigDecimal getBalance() {
        return this.getStudentCredit();
    }

    public static class Builder {
        private String email;
        private String name;
        private BigDecimal balance = BigDecimal.ZERO;

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder balance(double balance) {
            this.balance = BigDecimal.valueOf(balance);
            return this;
        }

        public CampusUser build() {
            return new CampusUser(this);
        }
    }
}
