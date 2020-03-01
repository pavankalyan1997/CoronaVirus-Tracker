package io.coronaVirus.coronavirustracker.controller;

import io.coronaVirus.coronavirustracker.models.LocationStats;
import io.coronaVirus.coronavirustracker.services.CoronaDataServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    CoronaDataServices coronaDataServices;

    @GetMapping("/")
    public String home(Model model){
        List<LocationStats> allStats=coronaDataServices.getAllStats();
        int totalReportedCases=allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int totalNewCases=allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
        int totalDeaths=allStats.stream().mapToInt(stat->stat.getDeathCases()).sum();
        int recoveredCases=allStats.stream().mapToInt(stat->stat.getRecoveredCases()).sum();
        model.addAttribute("locationStats",allStats);
        model.addAttribute("totalReportedCases",totalReportedCases);
        model.addAttribute("totalNewCases",totalNewCases);
        model.addAttribute("totalDeaths",totalDeaths);
        model.addAttribute("recoveredCases",recoveredCases);


        return "home";
    }
}
