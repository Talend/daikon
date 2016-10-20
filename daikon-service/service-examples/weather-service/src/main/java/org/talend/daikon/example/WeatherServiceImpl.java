package org.talend.daikon.example;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.http.HttpStatus;
import org.talend.daikon.annotation.ServiceImplementation;
import org.talend.daikon.http.HttpResponseContext;
import org.talend.services.WeatherService;

/**
 * Implementation of the {@link WeatherService}.
 */
@ServiceImplementation
public class WeatherServiceImpl implements WeatherService {

    private final Map<String, Weather> cityToWeather = new HashMap<>();

    @PostConstruct
    public void init() {
        cityToWeather.put("PARIS", Weather.CLOUDY);
        cityToWeather.put("SAN FRANCISCO", Weather.FOGGY);
        cityToWeather.put("PHILADELPHIA", Weather.SUNNY);
    }

    @Override
    public Weather getWeather(String city) {
        if (!cityToWeather.containsKey(city.toUpperCase())) {
            HttpResponseContext.status(HttpStatus.NOT_FOUND);
            return null;
        }
        return cityToWeather.get(city.toUpperCase());
    }

    @Override
    public void updateWeather(String city, Weather weather) {
        if (!cityToWeather.containsKey(city.toUpperCase())) {
            HttpResponseContext.status(HttpStatus.NOT_FOUND);
            return;
        }
        cityToWeather.put(city.toUpperCase(), weather);
    }
}
