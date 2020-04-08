package com.eveino.util;

import org.apache.commons.net.util.SubnetUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 工具类
 *
 * @author 贰拾壹
 * @create 2018-08-25 23:25
 */
public class ScannerUtil {
    //private static final String massRegex = "([1-9][0-9]*)/\\w+\\son\\s(\\d+\\.\\d+\\.\\d+\\.\\d+)";
    //private static final Pattern massPattern = Pattern.compile(massRegex);
    //
    ////private static final String regex = "(?:^|\n)Host.*|(?:^|\n)Not shown.*|(?:^|\n)Some.*|(?:^|\n)PORT.*|(?:^|\n)[0-9]?\\sservices\\sunrecognized.*|/tcp";
    //private static final String nmapRegex = "for\\s(\\d+\\.\\d+\\.\\d+\\.\\d+)\n?(((?:^|\n)[0-9].*)+)";
    //private static final Pattern nmapPattern = Pattern.compile(nmapRegex);

    public static BlockingQueue<String> target2Queue(String path) throws IOException, InterruptedException {
        File targetFile = new File(path);
        BlockingQueue<String> resultQueue = new LinkedBlockingQueue<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(targetFile));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            line = line.trim();
            //CIDR表示的地址，将其转换成IP
            if (line.contains("/")) {
                SubnetUtils subnetUtils = new SubnetUtils(line);
                String[] IPs = subnetUtils.getInfo().getAllAddresses();
                for (String ip : IPs) {
                    resultQueue.put(ip);
                }
                continue;
            }
            resultQueue.put(line);
        }

        return resultQueue;
    }

    public static BlockingQueue<String> ipQueue2SliceQueue(BlockingQueue<String> rawIpQueue, String groupSize) throws InterruptedException {
        BlockingQueue<String> resultQueue = new LinkedBlockingQueue<>();
        int sliceSize = 255;
        if (!groupSize.equals("")) {
            sliceSize = Integer.parseInt(groupSize);
        }
        int groupNumber = rawIpQueue.size() / sliceSize;
        for (int i = 0; i <= groupNumber; i++) {
            List<String> list = new ArrayList<>();
            rawIpQueue.drainTo(list, sliceSize);
            resultQueue.put(list.toString());
        }
        //结束标志
        resultQueue.put("ip2SliceQueueFinished");
        return resultQueue;
    }

    public static Map<String, Set<String>> massResult2Map(BlockingQueue<StringBuilder> resultQueue) {
        String result = resultQueue.toString();

        //for debug
        //System.out.println(result);

        String mulRegex = "([1-9][0-9]*)/\\w+\\son\\s(\\d+\\.\\d+\\.\\d+\\.\\d+)";
        Pattern pattern = Pattern.compile(mulRegex);
        Matcher matcher = pattern.matcher(result);
        Map<String, Set<String>> massResultMap = new LinkedHashMap<>();
        List<String> resultList = new ArrayList<>();
        while (matcher.find()) {
            String ports = matcher.group(1);
            String ip = matcher.group(2);
            resultList.add(ip + "-" + ports);
            Set<String> set = new LinkedHashSet<>();
            set.add(ports);
        }
        //将resultList中的结果去重，存入massResultMap
        for (String line : resultList) {
            String ip = line.split("-")[0];
            String port = line.split("-")[1];
            if (massResultMap.containsKey(ip)) {
                Set<String> set = massResultMap.get(ip);
                set.add(port);
                massResultMap.put(ip, set);
            } else {
                Set<String> set = new LinkedHashSet<>();
                set.add(port);
                massResultMap.put(ip, set);
            }
        }
        return massResultMap;
    }

    public static StringBuilder scanResult2StringBuilder(String workType, String ip, String basePath, String port, String options, String rate) throws IOException, InterruptedException {
        String cmd;
        if (port.isEmpty()) {
            port = "1-65535";
        }
        cmd = basePath + " " + ip + " -p" + port + " " + options + " --rate=" + rate;
        if (workType.equals("nmap")) {
            //带-p，结果从mass过来的，不需要指定open
            if (ip.contains("-p")) {
                cmd = basePath + " " + ip + " " + options.replace("--open", "");
            } else if (port.equals("regular")) {
                cmd = basePath + " " + ip + " " + options;
            } else {
                cmd = basePath + " " + ip + " -p" + port + " " + options;
            }
        }
        //System.out.println(cmd);

        Process process = Runtime.getRuntime().exec(cmd);
        StringBuilder stringBuilder = new StringBuilder();
        //BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader brE = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        //启动一个新进程，用于清空ErrorStream，防止process阻塞
        //lambda替代匿名Runnable
        new Thread(() -> {
            try {
                while (brE.readLine() != null) ;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        //new Thread(new Runnable() {
        //    public void run() {
        //        BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        //        try {
        //            while (br.readLine() != null) ;
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }
        //    }
        //}).start();

        String line;
        while ((line = br.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        return stringBuilder;
    }

    public static Map<String, Set<String>> nmapResult2Map(BlockingQueue<StringBuilder> resultQueue) {
        String temp = resultQueue.toString();

        //for debug
        //System.out.println(temp);


        String regex = "(?:^|\n)Host.*|(?:^|\n)Not shown.*|(?:^|\n)Some.*|(?:^|\n)PORT.*|(?:^|\n)[0-9]?\\sservice.?\\sunrecognized.*|(?:^|\n)SF.*|/tcp";
        String result = temp.replaceAll(regex, "");
        //匹配IP及对应服务
        //String mulRegex = "for\\s(\\d+\\.\\d+\\.\\d+\\.\\d+)(((?:^|\n)[0-9].*)+)";
        String mulRegex = "for\\s(\\d+\\.\\d+\\.\\d+\\.\\d+)\n?(((?:^|\n)[0-9].*)+)";
        Pattern pattern = Pattern.compile(mulRegex);
        Matcher matcher = pattern.matcher(result);
        Map<String, Set<String>> nmapResultMap = new LinkedHashMap<>();

        while (matcher.find()) {
            //singleStatus[0]:PORT，singleStatus[1]:STATE，singleStatus[2]:SERVICE，singleStatus[3]:VERSION
            Set<String> resultSet = new LinkedHashSet<>();
            String ip = matcher.group(1);
            String status = matcher.group(2);
            //单行IP状态，PORT     STATE    SERVICE       VERSION
            //用于保存分割后的状态
            //将lineStatus分解成单个数组元素，并保存到singleStatus中
            String[] lineStatus = status.split("\n");
            for (String line : lineStatus) {
                String[] singleStatus = new String[4];
                //去掉第一个空元素
                if (line.length() == 0) {
                    continue;
                } else {
                    //按空格分割成数组
                    String[] statuArray = line.split("\\s+");
                    //对PORT、STATE、SERVICE赋值
                    System.arraycopy(statuArray, 0, singleStatus, 0, 3);
                    //对VERSION赋值，如果statuArray等于3，则VERSION为null
                    if (statuArray.length > 3) {
                        String[] version = Arrays.copyOfRange(statuArray, 3, statuArray.length);
                        StringBuilder stringBuilder = new StringBuilder();
                        //空格分割VERSION中的单词，并去掉末尾的空格
                        for (int i = 0; i < version.length; i++) {
                            if (i != version.length - 1) {
                                stringBuilder.append(version[i]).append(" ");
                            } else {
                                stringBuilder.append(version[i]);
                            }
                        }
                        singleStatus[3] = stringBuilder.toString();
                    } else {
                        singleStatus[3] = null;
                    }
                }
                String singleStatusString = Arrays.asList(singleStatus).toString();
                resultSet.add(singleStatusString);
            }
            //存入nmapResultMap，并去重
            //TODO 版本去重
            if (nmapResultMap.containsKey(ip)) {
                Set<String> set = nmapResultMap.get(ip);
                set.addAll(resultSet);
                nmapResultMap.put(ip, set);
            } else {
                nmapResultMap.put(ip, resultSet);
            }
        }

        return nmapResultMap;
    }
}
