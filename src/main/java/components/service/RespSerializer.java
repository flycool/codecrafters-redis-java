package components.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class RespSerializer {

    public List<ArrayList<String>> deserialize(String commandStr) {
        log.debug(commandStr);
        List<ArrayList<String>> res = new ArrayList<>();

        try {
            String[] splitCommands = commandStr.split("\\*");
            ArrayList<String> commands = new ArrayList<>();
            for (String c : splitCommands) {
                if (c.isEmpty()) continue;
                commands.add(c);
            }
            log.debug(commands.toString());

            for (int i = 0; i < commands.size(); i++) {
                String[] cc = commands.get(i).split("\\$");

                if (cc.length < 2) continue;

                ArrayList<String> commandList = new ArrayList<>();
                for (int j = 1; j < cc.length; j++) {
                    String[] scc = cc[j].split("\r\n");
                    commandList.add(scc[1]);
                }
                res.add(commandList);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return res;
    }

    public String encodingRespString(String s) {
        int len = s.length();
        String resp = "$";
        resp += len;
        resp += "\r\n" + s + "\r\n";
        return resp;
    }

}
