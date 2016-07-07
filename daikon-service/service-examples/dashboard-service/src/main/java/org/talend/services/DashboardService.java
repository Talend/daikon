package org.talend.services;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.talend.daikon.annotation.Service;

@Service(name = "DashboardService")
public interface DashboardService {

    @RequestMapping(path = "dashboard/", method = RequestMethod.GET)
    WeatherDashboard getDashboard();

}
