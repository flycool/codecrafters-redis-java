import components.server.RedisConfig;
import components.server.MasterTcpServer;
import components.server.SlaveTcpServer;
import config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Main {
    public static void main(String[] args) {
        // You can use print statements as follows for debugging, they'll be visible
        // when running tests.
        System.out.println("Logs from your program will appear here!");

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        MasterTcpServer master = context.getBean(MasterTcpServer.class);
        SlaveTcpServer slave = context.getBean(SlaveTcpServer.class);
        RedisConfig redisConfig = context.getBean(RedisConfig.class);

        int port = 6379;
        redisConfig.setPort(port);
        redisConfig.setRole("master");
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--port":
                    port = Integer.parseInt(args[++i]);
                    redisConfig.setPort(port);
                    break;
                case "--replicaof":
                    redisConfig.setRole("slave");
                    String masterHost = args[i].split(" ")[0];
                    int masterPort = Integer.parseInt(args[i].split(" ")[1]);
                    redisConfig.setMasterHost(masterHost);
                    redisConfig.setMasterPort(masterPort);
                    break;

            }
        }

        if (redisConfig.getRole().equals("slave")) {
            slave.startServer();
        } else {
            master.startServer();
        }
    }
}
