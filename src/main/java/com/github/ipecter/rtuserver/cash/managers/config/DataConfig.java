package com.github.ipecter.rtuserver.cash.managers.config;

import lombok.Data;

@Data
public class DataConfig {

    private int savePeriod = 20;
    private boolean databaseUse = false;
    private String databaseIp = "localhost";
    private String databasePort = "27017";
    private String databaseUsername = "root";
    private String databasePassword = "passwordispassword";
    private String databaseName = "cash_data";

}
