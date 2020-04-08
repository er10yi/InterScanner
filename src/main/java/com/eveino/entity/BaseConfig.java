package com.eveino.entity;

import java.io.Serializable;

/**
 * 基本配置类
 *
 * @author 贰拾壹
 * @create 2018-08-29 9:13
 */

public class BaseConfig implements Serializable {
    private static final long serialVersionUID = 3925893795645912949L;
    private String workType, threadNumber, singleIPScanTime, basePath, port, options, rate, targetIP,excludeIP, sliceSize;

    public BaseConfig() {
    }

    public BaseConfig(String workType, String threadNumber, String singleIPScanTime, String basePath, String port, String options, String rate, String targetIP, String excludeIP, String sliceSize) {
        this.workType = workType;
        this.threadNumber = threadNumber;
        this.singleIPScanTime = singleIPScanTime;
        this.basePath = basePath;
        this.port = port;
        this.options = options;
        this.rate = rate;
        this.targetIP = targetIP;
        this.excludeIP = excludeIP;
        this.sliceSize = sliceSize;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getThreadNumber() {
        return threadNumber;
    }

    public void setThreadNumber(String threadNumber) {
        this.threadNumber = threadNumber;
    }

    public String getSingleIPScanTime() {
        return singleIPScanTime;
    }

    public void setSingleIPScanTime(String singleIPScanTime) {
        this.singleIPScanTime = singleIPScanTime;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getTargetIP() {
        return targetIP;
    }

    public void setTargetIP(String targetIP) {
        this.targetIP = targetIP;
    }

    public String getExcludeIP() {
        return excludeIP;
    }

    public void setExcludeIP(String excludeIP) {
        this.excludeIP = excludeIP;
    }

    public String getSliceSize() {
        return sliceSize;
    }

    public void setSliceSize(String sliceSize) {
        this.sliceSize = sliceSize;
    }
}
