
package mbta;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import mbta.restclient.Request;

public class App {

    Properties configs;
    Request request;

    public App(Properties settings) {
        configs = settings;
        request = new Request(configs.getProperty(Constants.API_KEY_NAME));
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("usage: routes [route_type]");
        } else {
            String action = args[0];
            InputStream inputStream = getPropertiesFileStream();
            Properties properties = loadPropertiesFile(inputStream);
            App app = new App(properties);
            if (action.equalsIgnoreCase("routes")) {
                if (args.length > 1) {
                    String specificRoute = args[1];
                    app.runSpecificRouteName(specificRoute);
                } else {
                    app.runAction(action);
                }
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

    public void runAction(String action) {
        if (action.equals("routes")) {
            request.getRoutes();
        }
    }

    public void runSpecificRouteName(String name) {
        request.getRouteByName(name);
    }

}
