package rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ValidationConsumer {
    private final Logger log = Logger.getLogger("ValidationConsumer");

    private final WhitelistDAO whitelistDAO;
    private final RabbitTemplate rabbitTemplate;

    public ValidationConsumer(WhitelistDAO whitelistDAO, RabbitTemplate rabbitTemplate) {
        this.whitelistDAO = whitelistDAO;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void handleMessage(final HashMap<String, Object> message) {
        final String client = (String) message.get("client");
        final String url = (String) message.get("url");
        final int correlationId = (int) message.get("correlationId");

        final Set<WhitelistObject> applicableWhitelist = whitelistDAO.findApplicableWhitelist(client);

        String responseRegex = null;
        final HashMap<String, Object> response = new HashMap<>();
        for (final WhitelistObject applicableWhitelistObject : applicableWhitelist) {
            final String whitelistObjectRegex = applicableWhitelistObject.getRegex();
            final Pattern pattern = Pattern.compile(whitelistObjectRegex);
            final Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                responseRegex = whitelistObjectRegex;
                break;
            }
        }
        response.put("match", responseRegex != null);
        response.put("regex", responseRegex);
        response.put("correlationId", correlationId);

        rabbitTemplate.convertAndSend(response);
        log.log(Level.INFO, "Processed validation message. Response: " + response);
    }
}