package components.server;

import org.springframework.stereotype.Component;

@Component
public class RedisConfig {
    public int port;
    public String role;

    public RedisConfig() {}

    public RedisConfig(int port) {
        this.port = port;
        this.role = "master";
    }

    public RedisConfig(int port, String role) {
        this.port = port;
        this.role = role;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
