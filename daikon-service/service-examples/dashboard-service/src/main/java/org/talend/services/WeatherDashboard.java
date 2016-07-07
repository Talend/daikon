package org.talend.services;

import java.util.List;

public class WeatherDashboard {

    private List<WeatherService.Weather> weathers;

    public WeatherDashboard() {
    }

    public WeatherDashboard(List<WeatherService.Weather> weathers) {
        this.weathers = weathers;
    }

    public List<WeatherService.Weather> getWeathers() {
        return weathers;
    }

    public void setWeathers(List<WeatherService.Weather> weathers) {
        this.weathers = weathers;
    }
}
