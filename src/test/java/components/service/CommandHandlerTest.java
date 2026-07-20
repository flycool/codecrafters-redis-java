package components.service;

import config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(classes = AppConfig.class)
class CommandHandlerTest {

    @Autowired
    private CommandHandler commandHandler;

    @Test
    void info() {
        ArrayList<String> commands = new ArrayList<>();
        commands.add("info");
        commands.add("replication");

        String info = commandHandler.info(commands);
        assertEquals("$11\r\nrole:master\r\n", info);

    }
}