package dao;

import model.Weather;

import java.time.LocalDate;
import java.util.List;

public interface IWeatherDAO {



    void create(Weather weather);

    Weather update(Weather weather);

    Weather getById(int id);

    List<Weather> getAll();


}
