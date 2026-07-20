package components.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class Store {
    public ConcurrentHashMap<String, Value> map;

    public Store() {
        map = new ConcurrentHashMap<>();
    }

    public Set<String> getKeys() {
        return map.keySet();
    }

    // expiry = -1 : no expiry
    public String set(String key, String v, long expiryMills) {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expire = LocalDateTime.MAX;
            if (expiryMills != -1) {
                expire = now.plus(expiryMills, ChronoUnit.MILLIS);
            }
            Value value = new Value(v, now, expire);
            map.put(key, value);
            return "+OK\r\n";
        } catch (Exception e) {
            log.error(e.getMessage());
            return "$-1\r\n";
        }
    }

    public String get(String key) {
        try {
            Value value = map.get(key);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expire = value.expiry;
            if(expire.isBefore(now)) {
                map.remove(key);
                return "$-1\r\n";
            }
            return value.value;
        } catch (Exception e) {
            log.error(e.getMessage());
            return "$-1\r\n";
        }
    }
}
