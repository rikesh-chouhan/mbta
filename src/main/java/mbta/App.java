
package mbta;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import mbta.restclient.Constants;
import mbta.restclient.Request;

public class App {

    Properties configs;
    Request request;

    public App(Properties settings) {
        configs = settings;
        request = new Request(configs.getProperty(Constants.API_KEY_NAME));
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("usage: mbta.app path-to-properties-file routes");
        } else {
            String propertyFilePath = args[0];
            String action = args[1];
            Properties properties = loadPropertiesFile(propertyFilePath);
            App app = new App(properties);
            if (action.equalsIgnoreCase("routes")) {
                app.runAction(action);
            } else {
                app.runSpecificRouteName(action);
            }
        }
    }

    public static Properties loadPropertiesFile(String file) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException fne) {
            fne.printStackTrace();
            return null;
        }
        return properties;
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
