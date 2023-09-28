package utils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import model.Weather;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static javax.management.Query.attr;

public class Scraper {

    public static List<Weather> fetchWeatherData() throws IOException, InterruptedException {
        String url = "https://vejr.tv2.dk/";

        List<Weather> WeatherList = new ArrayList<>();
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                        "(KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36")
                .get();
        Thread.sleep(1000); // 1 second

        // vi starter med at finde den helt store container, som er en fællesklasse for alt vejrdataen.
        // Vi søger efter id'et, da det er bedst at søge efter id da der kun et et id.
        Element todayBiggestContainer = doc.select("table.tc_datatable__main").first();
        Element daysBiggestContainer = doc.select("table.tc_datatable__main").last();

        //Herefter finder vi den mindre kasse som hver data ligger i:
        Elements mediumContainer = todayBiggestContainer.getElementsByTag("tbody").first().children();
        Elements mediumContainer2 = daysBiggestContainer.getElementsByTag("tbody").first().children();
        float sumTemp = 0;
        float sumDownPour = 0;
        //Today

        for(Element weatherContainerToday : mediumContainer) {


            //time during the day
            String time = weatherContainerToday.select("tc_weather__forecast__list__time").text();

            //Temperature
            String tempDuringDayTodayString = weatherContainerToday.child(2).text();


            // Downpour
            String downPourTodayString = weatherContainerToday.select("tc_weather__forecast__list__precipitation").text();

            // replace ° with nothing
            tempDuringDayTodayString = tempDuringDayTodayString.replace("°","");

            // Vi laver vores Strings om til en float
            float tempDuringDayToday = Float.parseFloat(tempDuringDayTodayString);
            float downPourToday = 0;
            try{
                if(downPourTodayString != null ||!downPourTodayString.equals("")){
                    downPourToday = Float.parseFloat(downPourTodayString);
                }
            } catch (NumberFormatException e) {
                //System.out.println("Error: parsing string:" + downPourTodayString + ". Message: " + e.getMessage());
            }

            // Så regner vi vores gennemsnit ud fra time
            sumTemp = sumTemp + tempDuringDayToday;
            sumDownPour = sumDownPour + downPourToday;

        }
        float averageTemp = sumTemp / mediumContainer.size();
        float averageDownPour = sumDownPour / mediumContainer.size();

        Weather today = new Weather("København", averageTemp, 1, averageDownPour, "Sunny", "5 m/s");
        WeatherList.add(today);

        //Days forward
            // Dato
        mediumContainer2.forEach(element -> {
            //Dato
            String date = element.select("tc_weather__forecast__list__time").text();
            // Temperature
            String tempDay = element.child(2).text().replace("°","");
            // String tempNight = weatherContainerAllDays.select("tc_weather__forecast__list__temperature_night").text();

            // Downpour
            String downPour = element.select("tc_weather__forecast__list__precipitation").text();

            // Vi laver vores Strings om til en float
            float tempDayFloat = Float.parseFloat(tempDay);
            //  float tempNightFloat = Float.parseFloat(tempNight);
            float downPourFloat = 0;
            try{
                if(downPour != null && !downPour.equals("")){
                    downPourFloat = Float.parseFloat(downPour);
                }
            } catch (NumberFormatException _e) {
                //System.out.println("Error: parsing string:" + downPour + ". Message: " + _e.getMessage());
            }

            Weather forward = new Weather("København", tempDayFloat, 1, downPourFloat, "Sunny", "5 m/s");
            WeatherList.add(forward);

        });
        return WeatherList;
    }
}
