package com.eveino.test;

import com.eveino.entity.BaseConfig;
import com.eveino.mass.MainScanner;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 导出jar测试
 *
 * @author 贰拾壹
 * @create 2018-08-28 11:15
 */

public class TestJar {

    private static String massWorkType, massThreadNumber, massSingleIPScanTime, massBasePath, massPort, massOptions, massRate, massTargetIP, massExcludeIP, massSliceSize;
    private static String nmapWorkType, nmapThreadNumber, nmapSingleIPScanTime, nmapBasePath, nmapPort, nmapOptions, nmapRate, nmapTargetIP, nmapExcludeIP, nmapSliceSize;

    //static {
    //    Properties properties = new Properties();
    //    try {
    //
    //        properties.load(new FileReader("config/config.properties"));
    //        //mass
    //        massWorkType = properties.getProperty("massWorkType");
    //        massThreadTimeOut = properties.getProperty("massThreadTimeOut");
    //        massThreadNumber = properties.getProperty("massThreadNumber");
    //        massSingleIPScanTime = properties.getProperty("massSingleIPScanTime");
    //        massBasePath = properties.getProperty("massBasePath");
    //        massPort = properties.getProperty("massPort");
    //        massOptions = properties.getProperty("massOptions");
    //        massRate = properties.getProperty("massRate");
    //        massTargetPath = properties.getProperty("massTargetPath");
    //        //nmap
    //        nmapWorkType = properties.getProperty("nmapWorkType");
    //        nmapThreadTimeOut = properties.getProperty("nmapThreadTimeOut");
    //        nmapThreadNumber = properties.getProperty("nmapThreadNumber");
    //        nmapSingleIPScanTime = properties.getProperty("nmapSingleIPScanTime");
    //        nmapBasePath = properties.getProperty("nmapBasePath");
    //        nmapPort = properties.getProperty("nmapPort");
    //        nmapOptions = properties.getProperty("nmapOptions");
    //        nmapRate = null;
    //        nmapTargetPath = properties.getProperty("nmapTargetPath");
    //    } catch (IOException e) {
    //        e.printStackTrace();
    //    }
    //
    //}

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("usage:");
            System.out.println("java -cp InterScanner.jar com.eveino.test.Test mass config.properties");
            System.out.println("or");
            System.out.println("java -cp InterScanner.jar com.eveino.test.Test nmap config.properties");
            System.out.println("or");
            System.out.println("java -cp InterScanner.jar com.eveino.test.Test mass2Nmap config.properties");
            System.exit(0);
        }
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(args[1]));
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
        Long time1 = System.currentTimeMillis();
        Map<String, Set<String>> massResultMap, nmapResultMap, mass2NmapResultMap;
        String model = args[0];
        switch (model) {
            case "mass":
                try {
                    BaseConfig massConfig = new BaseConfig(massWorkType, massThreadNumber, massSingleIPScanTime, massBasePath, massPort, massOptions, massRate, massTargetIP, massExcludeIP, massSliceSize);
                    MainScanner massScanner = new MainScanner(massConfig);
                    massResultMap = massScanner.scan();
                    for (Map.Entry<String, Set<String>> entry : massResultMap.entrySet()) {
                        //StringUtils将Set中的端口转换成str
                        //String ports = StringUtils.join(entry.getValue(), ",");
                        //System.out.println(entry.getKey() + " " + ports);
                        System.out.println(entry.getKey() + entry.getValue());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "nmap":
                try {
                    BaseConfig nmapConfig = new BaseConfig(nmapWorkType, nmapThreadNumber, nmapSingleIPScanTime, nmapBasePath, nmapPort, nmapOptions, nmapRate, nmapTargetIP, nmapExcludeIP, nmapSliceSize);
                    MainScanner nmapScanner = new MainScanner(nmapConfig);
                    nmapResultMap = nmapScanner.scan();
                    for (Map.Entry<String, Set<String>> entry : nmapResultMap.entrySet()) {
                        //StringUtils将Set中的端口转换成str
                        //String ports = StringUtils.join(entry.getValue(), ",");
                        //System.out.println(entry.getKey() + " " + ports);
                        System.out.println(entry.getKey() + entry.getValue());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "mass2Nmap":
                try {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        Long time2 = System.currentTimeMillis();
        System.out.println("time:" + Long.toString(time2 - time1));
        //mass+nmap
        //nmap：使用T4 模式
        //nmap 1-65535(时间最久)
        //三种运行模式，可以同时运行，可分开运行
    }


}
