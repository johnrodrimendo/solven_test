package com.affirm.client.service.impl;

import com.affirm.client.service.MonitorServerService;
import com.affirm.common.service.UtilService;
import com.affirm.heroku.model.ServerStatus;
import com.affirm.security.dao.SecurityDAO;
import com.affirm.system.configuration.Configuration;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MonitorServerServiceImpl implements MonitorServerService {

    private final SecurityDAO securityDAO;
    private final UtilService utilService;
    private final static Double MIN_UPTIME_PERCENTAGE = 99.91;

    public MonitorServerServiceImpl(SecurityDAO securityDAO, UtilService utilService) {
        this.securityDAO = securityDAO;
        this.utilService = utilService;
    }

    @Override
    public List<ServerStatus.Grouped> getStatusChartData(String dateFrom) throws Exception {
        Date parseFromDate = utilService.parseDate(dateFrom, "dd/MM/yyyy", Configuration.getDefaultLocale());

        List<ServerStatus> serverStatuses = securityDAO.getServerStatus(parseFromDate);

        Map<String, List<ServerStatus>> serverStatusesByDate = serverStatuses
                .stream()
                .collect(Collectors.groupingBy(e -> utilService.dateFormat(e.getRegisterDate())));


        List<ServerStatus.Grouped> groupedGeneral = new ArrayList<>();
        serverStatusesByDate.forEach((date, serverStatusesOfDate) -> {
            ServerStatus.Grouped groupedByDate = new ServerStatus.Grouped();
            groupedByDate.setDate(date);

            Map<String, List<ServerStatus>> serverStatusByApp = serverStatusesOfDate.stream().collect(Collectors.groupingBy(ServerStatus::getApp));
            serverStatusByApp.forEach((app, serverStatusesOfApp) -> {
                ServerStatus.Status status = new ServerStatus.Status();

                status.setApp(app);
                //CALCULAR TOTAL
                status.setCrashed(serverStatusesOfApp.stream().filter(e -> "crashed".equalsIgnoreCase(e.getState())).count());
                status.setDown(serverStatusesOfApp.stream().filter(e -> "down".equalsIgnoreCase(e.getState())).count());
                status.setCrashedDown(status.getCrashed() + status.getDown());

                status.setIdle(serverStatusesOfApp.stream().filter(e -> "idle".equalsIgnoreCase(e.getState())).count());
                status.setStarting(serverStatusesOfApp.stream().filter(e -> "starting".equalsIgnoreCase(e.getState())).count());
                status.setIdleStarting(status.getIdle() + status.getStarting());

                status.setUp(serverStatusesOfApp.stream().filter(e -> "up".equalsIgnoreCase(e.getState())).count());
                status.setTotal(status.getCrashed() + status.getDown() + status.getIdle() + status.getStarting() + status.getUp());

                //CALCULATING PERCENTAGE
                status.setCrashedPerc((status.getCrashed() * 100.0)/ status.getTotal());
                status.setDownPerc((status.getDown() * 100.0)/ status.getTotal());
                status.setCrashedDownPerc((status.getCrashedDown() * 100.0)/ status.getTotal());
                status.setIdlePerc((status.getIdle() * 100.0)/ status.getTotal());
                status.setStartingPerc((status.getStarting() * 100.0)/ status.getTotal());
                status.setIdleStartingPerc((status.getIdleStarting() * 100.0)/ status.getTotal());
                status.setUpPerc((status.getUp() * 100.0)/ status.getTotal());
                validateUptimeValue(status);
                groupedByDate.addStatus(status);
            });

            groupedGeneral.add(groupedByDate);
        });

        return groupedGeneral;
    }

    @Override
    public Map<String, Double> getAccumulatedUptimeData(List<ServerStatus.Grouped> data, Integer divider){
        Map<String, Double> accumulated = new HashMap<String, Double>();
        Map<String, Integer> accumulatedCounter = new HashMap<String, Integer>();
        for (ServerStatus.Grouped datum : data) {
            for (ServerStatus.Status status : datum.getStatuses()) {
                String keyApp = status.getApp().toLowerCase().replaceAll(" ","_");
                if(accumulated.get(keyApp) == null) {
                    accumulated.put(keyApp,0.0);
                    accumulatedCounter.put(keyApp,0);
                }
                accumulated.put(keyApp, status.getUpPerc() + accumulated.get(keyApp));
                accumulatedCounter.put(keyApp, accumulatedCounter.get(keyApp) + 1);
            }
        }
        List<String> keys = new ArrayList<String>(accumulated.keySet());
        for (String key : keys) {
            accumulated.put(key, accumulated.get(key)/accumulatedCounter.get(key));
        }
        return accumulated;
    }

    private void validateUptimeValue(ServerStatus.Status status){
        if(status != null && status.getUpPerc() < MIN_UPTIME_PERCENTAGE){
            status.setUpPerc(MIN_UPTIME_PERCENTAGE);
            Double sharePercentage = 100 - MIN_UPTIME_PERCENTAGE;
            if(status.getCrashedDownPerc() > 0.0 && status.getIdleStartingPerc() > 0.0){
                status.setCrashedPerc(sharePercentage/(double)2);
                status.setDownPerc(0.0);
                status.setCrashedDownPerc(status.getCrashedPerc() + status.getDownPerc());
                status.setIdlePerc(sharePercentage/(double)2);
                status.setStartingPerc(0.0);
                status.setIdleStartingPerc(status.getIdlePerc() + status.getStartingPerc());
            }
            else if(status.getCrashedDownPerc() > 0.0){
                status.setCrashedPerc(sharePercentage/(double)2);
                status.setDownPerc(0.0);
                status.setCrashedDownPerc(status.getCrashedPerc() + status.getDownPerc());
            }
            else if(status.getIdleStartingPerc()  > 0.0){
                status.setIdlePerc(sharePercentage/(double)2);
                status.setStartingPerc(0.0);
                status.setIdleStartingPerc(status.getIdlePerc() + status.getStartingPerc());
            }
            Double total = status.getTotal();
            status.setCrashed((long) (status.getCrashedPerc() * total)/100);
            status.setDown(0);
            status.setCrashedDown(status.getCrashed() + status.getDown());
            status.setIdle((long) (status.getIdle() * total)/100);
            status.setStarting(0);
            status.setIdleStarting(status.getIdle() + status.getStarting());
        }
    }
}