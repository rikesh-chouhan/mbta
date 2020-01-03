package mbta;

public class Constants {

    public static final String ROUTES_PATH = "https://api-v3.mbta.com/routes";
    public static final String SUBWAY_ROUTES = ROUTES_PATH + "?filter=1";
    public static final String ROUTE_BY_NAME = ROUTES_PATH + "/";
    public static final String API_KEY_NAME = "api-key";
    public static final String API_KEY_HEADER = "x-api-key";
    public static final String API_FILE = "app.properties";
    public static final String[] PER_ROUTE_FIELDS = {
            "$.data.attributes.direction_names",
            "$.data.attributes.direction_destinations"
    };
}
