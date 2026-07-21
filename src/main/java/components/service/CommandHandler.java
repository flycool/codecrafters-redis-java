package components.service;

import components.repository.Store;
import components.server.RedisConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Slf4j
@Component
public class CommandHandler {

    @Autowired
    public RespSerializer respSerializer;

    @Autowired
    public Store store;

    @Autowired
    public RedisConfig redisConfig;

    public String ping(ArrayList<String> command) {
        return "+PONG\n";
    }

    public String echo(ArrayList<String> command) {
        return respSerializer.encodingRespString(command.get(1));
    }

    public String set(ArrayList<String> command) {
        try {
            long expiry = -1;
            int pxIndex = command.indexOf("px");
            if (pxIndex != -1) {
                expiry = Long.parseLong(command.get(pxIndex + 1));
            }

            String key = command.get(1);
            String value = command.get(2);
            System.out.println("key: " + key);
            System.out.println("value: " + value);

            return store.set(key, value, expiry);
        } catch (Exception e) {
            log.error(e.getMessage());
            return "$-1\r\n";
        }
    }

    public String get(ArrayList<String> command) {
        try {
            String key = command.get(1);
            System.out.println("v: " + key);
            String value = store.get(key);
            return respSerializer.encodingRespString(value);
        } catch (Exception e) {
            log.error(e.getMessage());
            return "$-1\r\n";
        }
    }

    public String info(ArrayList<String> command) {
        try {
            int replicationIndex = command.indexOf("replication");
            String res = "";
            if (replicationIndex != -1) {
                String role = "role:" + redisConfig.getRole();
                String masterReplId = "master_replid:" + redisConfig.getMasterReplId();
                String masterReplOffset = "master_repl_offset:" + redisConfig.getMasterReplOffset();

                String[] info = new String[]{role, masterReplId, masterReplOffset};

                String replicationData = String.join("\r\n", info);

                return respSerializer.encodingRespString(replicationData);
            }
            return res;
        } catch (Exception e) {
            log.error(e.getMessage());
            return "$-1\r\n";
        }
    }
}
