package org.talend.daikon.example;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.talend.daikon.annotation.ServiceImplementation;
import org.talend.daikon.client.ClientService;
import org.talend.services.DashboardService;
import org.talend.services.WeatherDashboard;
import org.talend.services.WeatherService;

@ServiceImplementation
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    ClientService clientService;

    @Override
    public WeatherDashboard getDashboard() {
        final WeatherService weatherService = clientService.of(WeatherService.class);
        final List<WeatherService.Weather> weathers = Arrays.asList(weatherService.getWeather("PARIS"), //
                weatherService.getWeather("SAN FRANCISCO"), //
                weatherService.getWeather("PHILADELPHIA"));
        return new WeatherDashboard(weathers);
    }
}
