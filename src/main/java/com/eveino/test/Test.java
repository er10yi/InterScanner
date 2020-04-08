package com.eveino.test;

import com.eveino.entity.BaseConfig;
import com.eveino.mass.MainScanner;

import java.io.*;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 测试类
 *
 * @author 贰拾壹
 * @create 2018-08-26 0:09
 */
public class Test {

    private static String massWorkType, massThreadNumber, massSingleIPScanTime, massBasePath, massPort, massOptions, massRate, massTargetIP, massExcludeIP, massSliceSize;
    private static String nmapWorkType, nmapThreadNumber, nmapSingleIPScanTime, nmapBasePath, nmapPort, nmapOptions, nmapRate, nmapTargetIP, nmapExcludeIP, nmapSliceSize;

    static {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("config/config.properties"));
            //mass
            massWorkType = properties.getProperty("massWorkType");
            massThreadNumber = properties.getProperty("massThreadNumber");
            massSingleIPScanTime = properties.getProperty("massSingleIPScanTime");
            massBasePath = properties.getProperty("massBasePath");
            massPort = properties.getProperty("massPort");
            massOptions = properties.getProperty("massOptions");
            massRate = properties.getProperty("massRate");
            massTargetIP = properties.getProperty("massTargetIP");
            massExcludeIP = properties.getProperty("massExcludeIP");
            massSliceSize = properties.getProperty("massSliceSize");
            //nmap
            nmapWorkType = properties.getProperty("nmapWorkType");
            nmapThreadNumber = properties.getProperty("nmapThreadNumber");
            nmapSingleIPScanTime = properties.getProperty("nmapSingleIPScanTime");
            nmapBasePath = properties.getProperty("nmapBasePath");
            nmapPort = properties.getProperty("nmapPort");
            nmapOptions = properties.getProperty("nmapOptions");
            nmapRate = null;
            nmapTargetIP = properties.getProperty("nmapTargetIP");
            nmapExcludeIP = properties.getProperty("nmapExcludeIP");
            nmapSliceSize = properties.getProperty("nmapSliceSize");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        Long time1 = System.currentTimeMillis();
        Map<String, Set<String>> massResultMap, nmapResultMap, mass2NmapResultMap;
        try {
            if (massWorkType.equals("mass")) {
                BaseConfig massConfig = new BaseConfig(massWorkType, massThreadNumber, massSingleIPScanTime, massBasePath, massPort, massOptions, massRate, massTargetIP, massExcludeIP, massSliceSize);
                MainScanner massScanner = new MainScanner(massConfig);
                massResultMap = massScanner.scan();

                for (Map.Entry<String, Set<String>> entry : massResultMap.entrySet()) {
                    //StringUtils将Set中的端口转换成str
                    //String ports = StringUtils.join(entry.getValue(), ",");
                    //System.out.println(entry.getKey() + " " + ports);
                    System.out.println(entry.getKey() + entry.getValue());
                }
            }
            if (nmapWorkType.equals("nmap")) {
                BaseConfig nmapConfig = new BaseConfig(nmapWorkType, nmapThreadNumber, nmapSingleIPScanTime, nmapBasePath, nmapPort, nmapOptions, nmapRate, nmapTargetIP, nmapExcludeIP, nmapSliceSize);
                MainScanner nmapScanner = new MainScanner(nmapConfig);
                nmapResultMap = nmapScanner.scan();
                for (Map.Entry<String, Set<String>> entry : nmapResultMap.entrySet()) {
                    //StringUtils将Set中的端口转换成str
                    //String ports = StringUtils.join(entry.getValue(), ",");
                    //System.out.println(entry.getKey() + " " + ports);
                    System.out.println(entry.getKey() + entry.getValue());
                }
            }
            if (massWorkType.equals("mass2Nmap")) {

                BaseConfig massConfig = new BaseConfig(massWorkType, massThreadNumber, massSingleIPScanTime, massBasePath, massPort, massOptions, massRate, massTargetIP, massExcludeIP, massSliceSize);
                //BaseConfig nmapConfig = new BaseConfig(nmapWorkType, nmapThreadTimeOut, nmapThreadNumber, nmapSingleIPScanTime, nmapBasePath, null, nmapOptions, null, null);
                BaseConfig nmapConfig = new BaseConfig(nmapWorkType, nmapThreadNumber, nmapSingleIPScanTime, nmapBasePath, nmapPort, nmapOptions, nmapRate, nmapTargetIP, nmapExcludeIP, nmapSliceSize);
                MainScanner mass2NmapScanner = new MainScanner(massConfig, nmapConfig);
                mass2NmapResultMap = mass2NmapScanner.scan();
                for (Map.Entry<String, Set<String>> entry : mass2NmapResultMap.entrySet()) {
                    //StringUtils将Set中的端口转换成str
                    //String ports = StringUtils.join(entry.getValue(), ",");
                    //System.out.println(entry.getKey() + " " + ports);
                    System.out.println(entry.getKey() + entry.getValue());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Long time2 = System.currentTimeMillis();
        System.out.println("time:" + Long.toString(time2 - time1));

        //mass+nmap
        //nmap：使用T4 模式
        //nmap 1-65535(时间最久)
        //三种运行模式，可以同时运行，可分开运行
    }


}
