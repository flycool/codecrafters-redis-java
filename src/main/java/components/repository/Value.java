package components.repository;

import java.time.LocalDateTime;

public class Value {
    public String value;
    public LocalDateTime created;
    public LocalDateTime expiry;

    public Value(String value, LocalDateTime created, LocalDateTime expiry) {
        this.value = value;
        this.created = created;
        this.expiry = expiry;
    }
}
