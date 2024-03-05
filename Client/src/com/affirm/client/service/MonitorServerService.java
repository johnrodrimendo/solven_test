package com.affirm.client.service;

import com.affirm.heroku.model.ServerStatus;

import java.util.List;
import java.util.Map;

public interface MonitorServerService {

    List<ServerStatus.Grouped> getStatusChartData(String dateFrom) throws Exception;

    Map<String, Double> getAccumulatedUptimeData( List<ServerStatus.Grouped> data, Integer divider);
}
