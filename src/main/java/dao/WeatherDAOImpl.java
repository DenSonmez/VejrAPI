package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import model.Weather;

import java.time.LocalDate;
import java.util.List;

public class WeatherDAOImpl implements IWeatherDAO {

    private static IWeatherDAO instance;
    private static EntityManagerFactory emf;

    public static IWeatherDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new WeatherDAOImpl();
        }
        return instance;
    }

    @Override
    public void create(Weather weather) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(weather);
            em.getTransaction().commit();
        }

    }

    @Override
    public Weather update(Weather weather) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.merge(weather);
            em.getTransaction().commit();
        }
        return weather;
    }

    @Override
    public Weather getById(int id) {
        Weather weather = null;
        try (var em = emf.createEntityManager()) {
            Query query = em.createQuery("SELECT w FROM Weather w WHERE w.id = :id");
            query.setParameter("id", id);
            return weather = (Weather) query.getSingleResult();
        }
    }


    @Override
    public List<Weather> getAll() {
        Weather[] weathers = null;
        try (var em = emf.createEntityManager()) {
            Query query = em.createQuery("SELECT w FROM Weather w");
            return query.getResultList();
        }
    }
}