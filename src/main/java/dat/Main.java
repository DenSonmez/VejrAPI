package dat;

import com.google.gson.JsonObject;
import config.HibernateConfig;
import dao.IWeatherDAO;
import dao.WeatherDAOImpl;
import model.Weather;
import utils.Scraper;
import utils.WeatherApiReader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        HibernateConfig.addAnnotatedClasses(Weather.class);
        var emf = HibernateConfig.getEntityManagerFactoryConfig("weather");
        IWeatherDAO weatherDAO = WeatherDAOImpl.getInstance(emf);
        var apiReader = WeatherApiReader.getInstance();


        ExecutorService es = Executors.newFixedThreadPool(2);
        try {

            Future<List<Weather>> weatherFuture = es.submit(Scraper::fetchWeatherData);
            Future<JsonObject> jsonObjectFuture = es.submit(() -> apiReader.getWeatherData("København"));

            List<Weather> weatherList = weatherFuture.get();
            JsonObject enrichedData = jsonObjectFuture.get();


            Weather todayWeather = weatherList.get(0);
            var jsonElement = enrichedData.get("CurrentData").getAsJsonObject();

            todayWeather.setHumidity(Integer.parseInt(jsonElement.get("humidity").getAsString()));
            todayWeather.setWeatherType(jsonElement.get("skyText").getAsString());
            todayWeather.setWind(jsonElement.get("windText").getAsString().replace("\\", ""));

            weatherList.set(0, todayWeather);
            weatherDAO.create(weatherList.get(0));

        } catch (InterruptedException e) {
            System.out.println("A thread was interrupted and an exception. Exiting the program.\n" + e);
        } catch (ExecutionException e) {
            System.out.println("An exception was thrown, while attempting to receive result of an thread. Exiting the program.\n" + e);
        }
    }
}