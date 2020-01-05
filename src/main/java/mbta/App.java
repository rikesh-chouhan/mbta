
package mbta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import mbta.restclient.Request;

public class App {

    private static final long SECOND = 1 * 1000;
    Properties configs;
    Request request;
    Logger logger;

    public App(Properties settings) {
        configs = settings;
        request = new Request(configs.getProperty(Constants.API_KEY_NAME));
        logger = LoggerFactory.getLogger(this.getClass());
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("usage: routes");
        } else {
            String action = args[0];
            InputStream inputStream = getPropertiesFileStream();
            Properties properties = loadPropertiesFile(inputStream);
            App app = new App(properties);
            if (action.equalsIgnoreCase("routes")) {
                app.runRoutes();
            } else {
                System.out.println("unknown option: " + action);
            }
        }
    }

    public static Properties loadPropertiesFile(InputStream inputStream) {
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException fne) {
            fne.printStackTrace();
            return null;
        }
        return properties;
    }

    public static InputStream getPropertiesFileStream() {
        return ClassLoader.getSystemResourceAsStream(Constants.API_FILE);
    }

    public void runRoutes() {
        Map<String, String> routePaths = request.getRoutes();
        for (Map.Entry<String, String> entry: routePaths.entrySet()) {
            logger.info("Processing route: {}", entry.getKey());
            Request req = new Request(configs.getProperty(Constants.API_KEY_NAME));
            req.routeByPath(entry.getValue());
            logger.debug("Waiting for 1 second");
            try {
                Thread.sleep(SECOND);
                logger.debug("Done waiting");
            } catch (InterruptedException ie) {
                logger.error("Error while waiting a minute before making next request", ie);
            }
        }
    }

    public void runSpecificRouteName(String name) {
        request.routeByName(name);
    }

}
