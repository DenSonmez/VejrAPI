package utils;

import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class WeatherApiReaderTest {

    @Test
    void testConnectionToApiEndpoint() throws IOException, URISyntaxException, InterruptedException {
        var apiReader = WeatherApiReader.getInstance();
        String url = "https://vejr.eu/api.php";
        String location = "København";
        String locationParameter = "location="+location;
        String degreesParameter = "degree=C";
        URI uri = apiReader.appendUri(url, locationParameter);
        uri = apiReader.appendUri(uri.toString(), degreesParameter);
        // https://vejr.eu/api.php?Location=København&degree=C
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("User-Agent", "Insomnia/2023.5.5")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        // 200 betyder at alt var okay fra api
        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Status
        assertEquals(200, statusCode);
    }

    @Test
    // Vi testet om vores uri er den vi får tilbage fra appendUri metoden
    public void testAppendUri() throws UnsupportedEncodingException, URISyntaxException {
        WeatherApiReader apiReader = WeatherApiReader.getInstance();
        String url = "https://vejr.eu/api.php";
        String location = "København";
        String locationParameter = "location="+location;
        String degreesParameter = "degree=C";

        URI uri = apiReader.appendUri(url, locationParameter);
        uri = apiReader.appendUri(uri.toString(), degreesParameter);

        // forventet resultat
        String expectedUri = "https://vejr.eu/api.php?location=København&degree=C";

        assertEquals(expectedUri, uri.toString());
    }


}