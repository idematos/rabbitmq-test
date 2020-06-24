package rabbitmq;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class WhitelistDAO {
    private final JdbcTemplate jdbcTemplate;

    public WhitelistDAO(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        try {
            jdbcTemplate.update("CREATE TABLE IF NOT EXISTS Whitelist (client VARCHAR(128), regex VARCHAR(128) NOT NULL)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insert(final WhitelistObject whiteListObject) {
        final String client = whiteListObject.getClient();
        final String regex = whiteListObject.getRegex();
        try {
            jdbcTemplate.update("INSERT INTO Whitelist VALUES (?, ?)", client, regex);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public Set<WhitelistObject> findApplicableWhitelist(final String client) {
        try {
            final SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT * FROM Whitelist WHERE client IS NULL OR client = ?", client);

            final Set<WhitelistObject> applicableWhiteList = new HashSet<>();
            while (rowSet.next()) {
                final WhitelistObject whitelistObject = new WhitelistObject();
                whitelistObject.setClient(rowSet.getString("client"));
                whitelistObject.setRegex(rowSet.getString("regex"));

                applicableWhiteList.add(whitelistObject);
            }
            return applicableWhiteList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
