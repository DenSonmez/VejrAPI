package utils;

import model.Weather;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;


public class Scraper {
    public static List<Weather> fetchWeatherData() throws IOException, InterruptedException {
        String url = "https://vejr.tv2.dk/";

        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                        "(KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36")
                .get();
        Thread.sleep(1000); // 1 second

        // // Finder HTML-elementer, der indeholder vejrdata for i dag og flere dage frem.
        Element todayBiggestContainer = doc.select("table.tc_datatable__main").first();
        Element daysBiggestContainer = doc.select("table.tc_datatable__main").last();
        // Henter alle rækker (tbody) i tabellerne, der indeholder vejrdata.

        Elements mediumContainer = todayBiggestContainer.getElementsByTag("tbody").first().children();
        Elements mediumContainer2 = daysBiggestContainer.getElementsByTag("tbody").first().children();

        // Opretter lister til at gemme vejrdata for i dag og flere dage frem.
        List<Weather> weatherListToday = mediumContainer.stream()
                .map(Scraper::parseWeatherElement)
                .toList();

        List<Weather> weatherListDaysForward = mediumContainer2.stream()
                .map(Scraper::parseWeatherElement)
                .toList();

        // Beregn gennemsnit for temperaturen i dag. Det gør vi fordi, at der er flere temperaturer for i dag.
        OptionalDouble averageTempToday = weatherListToday.stream()
                .mapToDouble(Weather::getTemperature)
                .average();

        // Beregn gennemsnit for nedbør i dag
        OptionalDouble averageDownPourToday = weatherListToday.stream()
                .mapToDouble(Weather::getDownpour)
                .average();

        // Opret Weather objekt for i dag med gennemsnitstemperatur og gennemsnitlig nedbør. det gør vi fordi, at der er flere temperaturer for i dag.
        Weather today = new Weather("København", (float) averageTempToday.orElse(0), 1,
                (float) averageDownPourToday.orElse(0), "Sunny", "5 m/s");

        // Tilføj i dag til listen
        weatherListToday.add(0, today);

        // Tilføj dagens vejrdata til listen
        weatherListToday.addAll(weatherListDaysForward);

        return weatherListToday;
    }

    // En hjælpefunktion, der analyserer HTML-elementer for vejrdata og returnerer et Weather-objekt.
    private static Weather parseWeatherElement(Element element) {
        // Henter tidspunktet for vejrdata, temperatur og nedbør fra HTML-elementet.
        String time = element.select("tc_weather__forecast__list__time").text();
        //her henter vi temperaturen fra elementet og fjerner ° fra strengen
        String tempDuringDayTodayString = element.child(2).text().replace("°", "");
        //her henter vi nedbøren fra elementet
        String downPourTodayString = element.select("tc_weather__forecast__list__precipitation").text();

        // Konverterer temperatur og nedbør fra streng til flydende tal.
        float tempDuringDayToday = Float.parseFloat(tempDuringDayTodayString);
        float downPourToday = 0;

        // Håndterer eventuelle fejl i konverteringen af nedbørstal.
        try {
            if (!downPourTodayString.isEmpty()) {
                downPourToday = Float.parseFloat(downPourTodayString);
            }
        } catch (NumberFormatException e) {
            // Håndterer fejl i parsingen her.
        }

        // Returnerer et Weather-objekt med de analyserede vejrdata.
        return new Weather("København", tempDuringDayToday, 1, downPourToday, "Sunny", "5 m/s");
    }
}
    /*  public static List<Weather> fetchWeatherData() throws IOException, InterruptedException {
        String url = "https://vejr.tv2.dk/";

        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                        "(KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36")
                .get();
        Thread.sleep(1000); // 1 second

        Element todayBiggestContainer = doc.select("table.tc_datatable__main").first();
        Element daysBiggestContainer = doc.select("table.tc_datatable__main").last();

        Elements mediumContainer = todayBiggestContainer.getElementsByTag("tbody").first().children();
        Elements mediumContainer2 = daysBiggestContainer.getElementsByTag("tbody").first().children();

        List<Weather> weatherListToday = new ArrayList<>();
        for (Element element : mediumContainer) {
            Weather weather = parseWeatherElement(element);
            weatherListToday.add(weather);
        }

        List<Weather> weatherListDaysForward = new ArrayList<>();
        for (Element element : mediumContainer2) {
            Weather weather = parseWeatherElement(element);
            weatherListDaysForward.add(weather);
        }

        // Beregn gennemsnit for temperaturen i dag
        double sumTempToday = 0;
        for (Weather weather : weatherListToday) {
            sumTempToday += weather.getTemperature();
        }
        double averageTempToday = sumTempToday / weatherListToday.size();

        // Beregn gennemsnit for nedbør i dag
        double sumDownPourToday = 0;
        for (Weather weather : weatherListToday) {
            sumDownPourToday += weather.getDownpour();
        }
        double averageDownPourToday = sumDownPourToday / weatherListToday.size();

        // Opret Weather objekt for i dag med gennemsnitstemperatur og gennemsnitlig nedbør
        Weather today = new Weather("København", (float) averageTempToday, 1,
                (float) averageDownPourToday, "Sunny", "5 m/s");

        // Tilføj i dag til listen
        weatherListToday.add(0, today);

        // Tilføj dagens vejrdata til listen
        weatherListToday.addAll(weatherListDaysForward);

        return weatherListToday;
    }

    private static Weather parseWeatherElement(Element element) {
        String time = element.select("tc_weather__forecast__list__time").text();
        String tempDuringDayTodayString = element.child(2).text().replace("°", "");
        String downPourTodayString = element.select("tc_weather__forecast__list__precipitation").text();

        float tempDuringDayToday = Float.parseFloat(tempDuringDayTodayString);
        float downPourToday = 0;

        try {
            if (!downPourTodayString.isEmpty()) {
                downPourToday = Float.parseFloat(downPourTodayString);
            }
        } catch (NumberFormatException e) {
            // Handle parsing error
        }

        return new Weather("København", tempDuringDayToday, 1, downPourToday, "Sunny", "5 m/s");
    }
}
*/