package model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor

@Entity
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String city;
    private float temperature;
    private int humidity;
    private float downpour;

    @Column(name = "weather_type")
    private String weatherType;
    private String wind;

    @Temporal(TemporalType.DATE)
    private LocalDate date;
    @PrePersist
    private void onPrePersist() {
        date = LocalDate.now();
    }

    public Weather(String city, float temperature, int humidity, float downpour, String weatherType, String wind) {
        this.city = city;
        this.temperature = temperature;
        this.humidity = humidity;
        this.downpour = downpour;
        this.weatherType = weatherType;
        this.wind = wind;
    }
}
