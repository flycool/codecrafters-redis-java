package components.repository;

import components.service.RespSerializer;
import config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(classes = AppConfig.class)
class StoreTest {
    @Autowired
    private Store store;
    @Autowired
    private RespSerializer respSerializer;

    @BeforeEach
    public void setUp() {
        store.map.clear();
    }

    @Test
    public void testSetAndGetKey() {
        String key = "key";
        String value = "value";

        String setResult = store.set(key, value, -1);
        String getResult = store.get(key);

        assertEquals("+OK\r\n", setResult);
        assertEquals(value, getResult);
    }

}