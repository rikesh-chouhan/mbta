package mbta;

public class Constants {

    public static final String PROVIDER_PATH = "https://api-v3.mbta.com";
    public static final String ROUTES_PATH = PROVIDER_PATH + "/routes";
    public static final String FILTER_TYPE = "filter[type]";
    public static final String ROUTE_BY_NAME = ROUTES_PATH + "/";
    public static final String API_KEY_NAME = "api-key";
    public static final String API_KEY_HEADER = "x-api-key";
    public static final String API_FILE = "app.properties";
    public static final String[] PER_ROUTE_FIELDS = {
            "$.data.attributes.direction_names",
            "$.data.attributes.direction_destinations"
    };
    public static final String UTF_8 = "UTF-8";
    public static final String STOPS_BY_ROUTE = "https://api-v3.mbta.com/stops?filter[route]";
}
