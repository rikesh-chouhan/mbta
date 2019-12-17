package mbta.restclient;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

public class Request {

    HttpClient httpClient;
    String apiKey;

    public Request(String key) {
        apiKey = key;
        httpClient = HttpClientBuilder.create().build();
    }

    public void getRoutes() {
        HttpUriRequest httpUriRequest = RequestBuilder.get()
                .addHeader(Constants.API_KEY_HEADER, apiKey)
                .setUri(Constants.SUBWAY_ROUTES)
                .build();
        try {
            HttpResponse response = httpClient.execute(httpUriRequest);
            if (response.getStatusLine().getStatusCode() >= 400) {
                System.out.println("There was an error: " + response.getStatusLine().toString());
                System.out.println(response.getEntity());
            } else {
                System.out.println("Successful request: " + response.getStatusLine().toString());
                System.out.println(response.getEntity().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getRouteByName(String routeName) {
        HttpUriRequest httpUriRequest = RequestBuilder.get()
                .addHeader(Constants.API_KEY_HEADER, apiKey)
                .setUri(Constants.ROUTE_BY_NAME + "/" + routeName)
                .build();
        try {
            HttpResponse response = httpClient.execute(httpUriRequest);
            if (response.getStatusLine().getStatusCode() >= 400) {
                System.out.println("There was an error: " + response.getStatusLine().toString());
                System.out.println(response.getEntity());
            } else {
                System.out.println("Successful request: " + response.getStatusLine().toString());
                System.out.println(response.getEntity().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
