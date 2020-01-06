
package mbta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

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
                    } else if ("common".equals(args[1].toLowerCase())) {
                        app.findCommonStopsBetweenRoutes();
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

    /**
     * Fetch the route name (display name) to route path map.
     * eg:
     * "Red Line" -> "/routes/Red"
     *
     * @return
     */
    private Map<String, String> fetchRouteMap() {
        return new TransitDataProvider(configs.getProperty(Constants.API_KEY_NAME)).getRoutes();
    }

    public void runRoutes() {
        fetchRouteMap().keySet().forEach(name -> logger.info("Subway name: {}", name));
    }

    public void routeStats() {
        Map<String, List<String>> routeStops = routeStopsMap();
        String nameMostStops = "", nameLeastStops = "";
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for (Map.Entry<String, List<String>> entry: routeStops.entrySet()) {
            List<String> stops = entry.getValue();
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

    private String provideRouteIdFromPath(String path) {
        int index = path.lastIndexOf("/");
        return index > (-1) ? path.substring(index + 1) : path;
    }

    public void runSpecificRouteName(String name) {
        new TransitDataProvider(configs.getProperty(Constants.API_KEY_NAME)).routeByName(name);
    }

    public List<String> stopsForRoute(String routeId) {
        return new TransitDataProvider(configs.getProperty(Constants.API_KEY_NAME)).stopsForRoute(routeId);
    }

    /**
     * Create an index of stops to all routes it is on.
     * Use the existing route -> stops map to build the reverse map.
     */
    private void findCommonStopsBetweenRoutes() {
        Map<String, List<String>> routeStops = routeStopsMap();
        Map<String, Set<String>> sharedRoutes = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry: routeStops.entrySet()) {
            Set<String> routeNames = routeStops.keySet();
            routeNames.forEach( name -> {
                if (!name.equalsIgnoreCase(entry.getKey())) {
                    List<String> stopList = new ArrayList<>(entry.getValue());
                    List<String> commonSingle = stopList.stream()
                            .filter(routeStops.get(name)::contains)
                            .collect(Collectors.toList());
                    if (commonSingle.size() > 0) {
                        commonSingle.stream().forEach(stop -> {
                            Set<String> routes = null;
                            if (sharedRoutes.containsKey(stop)) {
                                routes = sharedRoutes.get(stop);
                            } else {
                                routes = new LinkedHashSet<>();
                            }
                            routes.add(entry.getKey());
                            routes.add(name);
                            sharedRoutes.put(stop, routes);
                        });
                    }
                }
            });
        }
        for (Map.Entry<String, Set<String>> entry: sharedRoutes.entrySet()) {
            logger.info("Stop: {} is common to these routes: {}", entry.getKey(), entry.getValue());
        }
    }

    /**
     * Fetch the route to stops map.
     * @return
     */
    private Map<String, List<String>> routeStopsMap() {
        Map<String, String> routePaths = fetchRouteMap();
        Map<String, List<String>> routeStops = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry: routePaths.entrySet()) {
            logger.debug("Getting stops for route: {}", entry.getKey());
            String routeId = provideRouteIdFromPath(entry.getValue());
            List<String> stops = stopsForRoute(routeId);
            routeStops.put(entry.getKey(), stops);
        }
        return routeStops;
    }
}
