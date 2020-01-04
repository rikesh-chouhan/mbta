package mbta.restclient;

import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import mbta.Constants;


public class Request {

    HttpClient httpClient;
    String apiKey;
    Logger logger;

    public Request(String key) {
        apiKey = key;
        httpClient = HttpClientBuilder.create().build();
        logger = LoggerFactory.getLogger(this.getClass());
    }

    public void getRoutes() {
        try {
            String filter = URLEncoder.encode(Constants.FILTER_TYPE, Constants.UTF_8);
            String types = URLEncoder.encode("0,1", Constants.UTF_8);
            String uri = Constants.ROUTES_PATH + "?" + filter + "=" + types;
            logger.info("uri: {}", uri);
            HttpUriRequest httpUriRequest = buildRequest(uri);
            HttpResponse response = httpClient.execute(httpUriRequest);
            if (response.getStatusLine().getStatusCode() >= 400) {
                logError(response);
            } else {
                String json = getDecompressedEntity(response);
                JSONArray array = JsonPath.read(json, "$..long_name");
                array.forEach( element -> logger.info(element.toString()));
            }
        } catch (Exception e) {
            logger.error("There was an error fetching routes ", e);
        }
    }

    public void getRouteByName(String routeName) {
        HttpUriRequest httpUriRequest = buildRequest(Constants.ROUTE_BY_NAME + routeName);
        try {
            HttpResponse response = httpClient.execute(httpUriRequest);
            if (response.getStatusLine().getStatusCode() >= 400) {
                logError(response);
            } else {
                logger.info("Successful request status: {} for route: {}",
                        response.getStatusLine().toString(), routeName);
                String json = getDecompressedEntity(response);
                for (String field: Constants.PER_ROUTE_FIELDS) {
                    JSONArray array = JsonPath.read(json, field);
                    String data = array.toJSONString();
                    logger.info(data);
                }
            }
        } catch (Exception e) {
            logger.error("There was an error fetching route: " + routeName, e);
        }
    }

    String getDecompressedEntity(HttpResponse response) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder sb = new StringBuilder();
        br.lines().forEach(aLine -> {
            sb.append(aLine).append("\n");
        });
        return sb.toString();
    }

    protected void logError(HttpResponse response) {
        logger.error("There was an error: " + response.getStatusLine().toString());
        logger.error(response.getEntity().toString());
    }

    protected HttpUriRequest buildRequest(String uri) {
        return RequestBuilder.get()
            .addHeader(Constants.API_KEY_HEADER, apiKey)
            .addHeader("Accept", "application/json")
            .setUri(uri)
            .build();
    }
}
