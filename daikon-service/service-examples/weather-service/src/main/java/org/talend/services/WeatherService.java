package org.talend.services;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.talend.daikon.annotation.Service;

/**
 * <h3>Yet another weather service</h3>
 * <p>
 *     You can use any of HTML styles: <i>italic</i>, <b>bold</b>...
 * </p>
 * <p>
 *     You may also include pictures
 *     <p align="center"><img src="https://media.giphy.com/media/10T5HYx3OfeaKQ/giphy.gif"/></p>
 * </p>
 */
@Service(name = "WeatherService")
public interface WeatherService {

    /**
     * Defines all possible weather conditions.
     */
    enum Weather {
        /**
         * Sunny, don't forget sun protection.
         */
        SUNNY,
        /**
         * Cloudy, dull but safe for you skin.
         */
        CLOUDY,
        /**
         * Foggy, quite same as {@link #CLOUDY} but bring a rain coat.
         */
        FOGGY
    }

    /**
     * Use this operation to get how is weather like in given <code>city</code>. If no weather is known for
     * city.
     *
     * @param city A city name.
     * @return One of the available weather.
     *
     * @HTTP 404 If city is not known.
     * @returnWrapped org.talend.daikon.example.WeatherService.Weather
     */
    @RequestMapping(path = "weather/{city}", method = RequestMethod.GET)
    Weather getWeather(@PathVariable("city") String city);

    /**
     * Updates the weather for given <code>city</code>.
     * @param city A city name.
     * @param weather One of the available weather.
     *
     * @HTTP 404 City isn't known.
     */
    @RequestMapping(path = "weather/{city}", method = RequestMethod.PUT)
    void updateWeather(@PathVariable("city") String city, @RequestBody Weather weather);
}
