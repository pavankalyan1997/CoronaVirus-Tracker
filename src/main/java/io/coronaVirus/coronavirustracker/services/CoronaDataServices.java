package io.coronaVirus.coronavirustracker.services;

import io.coronaVirus.coronavirustracker.models.DeathStat;
import io.coronaVirus.coronavirustracker.models.LocationStats;
import io.coronaVirus.coronavirustracker.models.RecoveredStat;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoronaDataServices {
    private static String VIRUS_DATA_URL="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";
    private static String DEATH_DATA_URL="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Deaths.csv";
    private static String RECOVERED_DATA_URL="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Recovered.csv";

    private List<LocationStats> allStats=new ArrayList<>();
    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {
        List<LocationStats> newStats=new ArrayList<>();
        HttpClient client=HttpClient.newHttpClient();

        // virus data reported
        HttpRequest virusDatarequest=HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();
        HttpResponse<String> virusDataResponse=client.send(virusDatarequest, HttpResponse.BodyHandlers.ofString());

        // death data
        HttpRequest deathDataRequest=HttpRequest.newBuilder()
                .uri(URI.create(DEATH_DATA_URL))
                .build();
        HttpResponse<String> deathDataResponse=client.send(deathDataRequest, HttpResponse.BodyHandlers.ofString());

        // recovered data
        HttpRequest recoveredDataRequest=HttpRequest.newBuilder()
                .uri(URI.create(RECOVERED_DATA_URL))
                .build();
        HttpResponse<String> recoveredDataResponse=client.send(recoveredDataRequest, HttpResponse.BodyHandlers.ofString());


        System.out.println(deathDataResponse.body());
        StringReader virusDataBodyReader=new StringReader(virusDataResponse.body());
        StringReader deathDataBodyReader=new StringReader(deathDataResponse.body());
        StringReader recoveredDataBodyReader=new StringReader(recoveredDataResponse.body());

        Iterable<CSVRecord> virusDataRecords = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(virusDataBodyReader);
        Iterable<CSVRecord> deathDataRecords = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(deathDataBodyReader);
        Iterable<CSVRecord> recoveredDataRecords = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(recoveredDataBodyReader);

        List<DeathStat> deathStatList=new ArrayList<>();
        for(CSVRecord record:deathDataRecords){
            DeathStat deathStat=new DeathStat();
            deathStat.setState(record.get("Province/State"));
            deathStat.setDeathCases(Integer.parseInt(record.get(record.size()-1)));
            deathStatList.add(deathStat);
        }

        List<RecoveredStat> recoveredStatList=new ArrayList<>();
        for(CSVRecord record:recoveredDataRecords){
            RecoveredStat recoveredStat=new RecoveredStat();
            recoveredStat.setState(record.get("Province/State"));
            recoveredStat.setRecoveredCases(Integer.parseInt(record.get(record.size()-1)));
            recoveredStatList.add(recoveredStat);
        }


        for (CSVRecord record : virusDataRecords) {
            LocationStats locationStats=new LocationStats();
            String state=record.get("Province/State");
            String country=record.get("Country/Region");
            if(state==null || state.equals("")){
                locationStats.setState("-");
            }
            else {
                locationStats.setState(state);
            }
            locationStats.setCountry(country);

            int latestCases=Integer.parseInt(record.get(record.size()-1));
            int prevDayCases=Integer.parseInt(record.get(record.size()-2));

            locationStats.setLatestTotalCases(latestCases);
            locationStats.setDiffFromPrevDay(latestCases-prevDayCases);
            //System.out.println(locationStats);
            deathStatList.forEach(deathStat -> {
                if(deathStat.getState().equals(state)){
                    locationStats.setDeathCases(deathStat.getDeathCases());
                }
            });

            recoveredStatList.forEach(recoveredStat -> {
                if(recoveredStat.getState().equals(state)){
                    locationStats.setRecoveredCases(recoveredStat.getRecoveredCases());
                }
            });
            newStats.add(locationStats);
        }






        this.allStats=newStats;

    }

    public List<LocationStats> getAllStats() {
        return allStats;
    }

}
