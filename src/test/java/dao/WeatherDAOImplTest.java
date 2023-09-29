package dao;

import config.HibernateConfig;
import jakarta.persistence.EntityManagerFactory;
import model.Weather;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WeatherDAOImplTest {

    private static EntityManagerFactory emf;
    private static IWeatherDAO weatherDAO;


    @BeforeEach
    public void setUp() {
        HibernateConfig.addAnnotatedClasses(Weather.class);
        emf = HibernateConfig.getEntityManagerFactoryConfig("weathertest");
        weatherDAO = WeatherDAOImpl.getInstance(emf);
    }


    @Test
    void create() {
        Weather weather = new Weather();
        weather.setCity("Lyngby");
        weather.setTemperature(10.0f);
        weather.setHumidity(10);
        weather.setDownpour(10.0f);
        weather.setWeatherType("Sunny");
        weather.setWind("10 m/s");
        weatherDAO.create(weather);
    }

    @Test
    void update() {
        Weather weather = new Weather();
        weather.setCity("Vanløse");
        weather.setTemperature(10.0f);
        weather.setHumidity(10);
        weather.setDownpour(10.0f);
        weather.setWeatherType("Sunny");
        weather.setWind("10 m/s");
        weatherDAO.update(weather);
        assertEquals("Vanløse", weather.getCity());


    }

    @Test
    void getById() {
        Weather weather = weatherDAO.getById(1);
        System.out.println(weather);
    }

    @Test
    void getAll() {
        List<Weather> weather = weatherDAO.getAll();
        Assertions.assertNotNull(weather);
        System.out.println(weather);

    }
}