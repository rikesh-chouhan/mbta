
package mbta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import mbta.restclient.TransitDataProvider;

/**
 * Main driver class for listing transit data
 */
public class App {

    private static final long SECOND = 1 * 1000;
    private Properties configs;
    private static Logger logger = LoggerFactory.getLogger(App.class);

    public App(Properties settings) {
        configs = settings;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            logger.info("usage 1: routes");
            logger.info("Lists Subway routes - type 0 and 1");
            logger.info("usage 2: routes stats");
            logger.info("Provides Subway routes with least and most number of stops");
        } else {
            String action = args[0];
            InputStream inputStream = getPropertiesFileStream();
            Properties properties = loadPropertiesFile(inputStream);
            if (properties == null) {
                logger.error("There was an error loading configs from: {} " +
                        "please make sure the file is in classpath", Constants.API_FILE);
                System.exit(-1);
            }
            App app = new App(properties);
            if (action.equalsIgnoreCase("routes")) {
                if (args.length > 1) {
                    if ("stats".equals(args[1].toLowerCase())) {
                        app.routeStats();
                    }
                } else {
                    app.runRoutes();
                }
            } else {
                System.out.println("unknown option: " + action);
            }
        }
    }

    /**
     * Load configs needed for running this app
     *
     * @param inputStream
     * @return
     */
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
        Map<String, String> routePaths = new TransitDataProvider(configs.getProperty(Constants.API_KEY_NAME)).getRoutes();
        routePaths.keySet().forEach(name -> logger.info("Subway name: {}", name));
    }

    public void routeStats() {
        Map<String, String> routePaths = new TransitDataProvider(configs.getProperty(Constants.API_KEY_NAME)).getRoutes();
        Map<String, List<String>> routeStops = new LinkedHashMap<>();
        String nameMostStops = "", nameLeastStops = "";
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for (Map.Entry<String, String> entry: routePaths.entrySet()) {
            logger.info("Getting stops for route: {}", entry.getKey());
            String split[] = entry.getValue().split("/");
            List<String> stops = stopsForRoute(split[2]);
            routeStops.put(entry.getValue(), stops);
            if (stops.size() < min) {
                min = stops.size();
                nameLeastStops = entry.getKey();
            }
            if (stops.size() > max) {
                max = stops.size();
                nameMostStops = entry.getKey();
            }
        }
        logger.info("Subway route with least stops: {} count: {}", nameLeastStops, min);
        logger.info("Subway route with most stops: {} count: {}", nameMostStops, max);
    }

    public void runSpecificRouteName(String name) {
        new TransitDataProvider(configs.getProperty(Constants.API_KEY_NAME)).routeByName(name);
    }

    public List<String> stopsForRoute(String routeId) {
        return new TransitDataProvider(configs.getProperty(Constants.API_KEY_NAME)).stopsForRoute(routeId);
    }

    private void addSleep() {
        logger.debug("Waiting for 1 second");
        try {
            Thread.sleep(SECOND);
            logger.debug("Done waiting");
        } catch (InterruptedException ie) {
            logger.error("Error while waiting a minute before making next request", ie);
        }
    }
}
