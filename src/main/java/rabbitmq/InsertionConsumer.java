package rabbitmq;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class InsertionConsumer {
    private final Logger log = Logger.getLogger("InsertionConsumer");

    private final WhitelistDAO whitelistDAO;

    public InsertionConsumer(WhitelistDAO whitelistDAO) {
        this.whitelistDAO = whitelistDAO;
    }

    public void handleMessage(final HashMap<String, Object> message) {
        final String client = (String) message.get("client");
        final String regex = (String) message.get("regex");

        WhitelistObject whitelistObject = new WhitelistObject();
        whitelistObject.setClient(client);
        whitelistObject.setRegex(regex);
        whitelistDAO.insert(whitelistObject);

        log.log(Level.INFO, "Processed insertion message.");
    }
}