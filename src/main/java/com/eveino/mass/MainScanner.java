package com.eveino.mass;

import com.eveino.entity.BaseConfig;
import com.eveino.entity.BaseScanner;
import com.eveino.util.ScannerUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Mass测试1
 *
 * @author 贰拾壹
 * @create 2018-08-25 23:38
 */
public class MainScanner extends BaseScanner {
    //mass模式和nmap模式下都使用baseConfig
    //mass2Nmap模式下，mass使用baseConfig，nmap使用singleNmapConfig
    private BaseConfig baseConfig, singleNmapConfig;

    private MainScanner() {
    }

    public MainScanner(BaseConfig baseConfig) {
        this.baseConfig = baseConfig;
    }

    public MainScanner(BaseConfig baseConfig, BaseConfig singleNmapConfig) {
        this.baseConfig = baseConfig;
        this.singleNmapConfig = singleNmapConfig;
    }

    public Map<String, Set<String>> scan() throws IOException, InterruptedException {
        //线程数量
        ExecutorService totalThreadPool = Executors.newFixedThreadPool(Integer.parseInt(baseConfig.getThreadNumber()));
        BlockingQueue<StringBuilder> rawResultQueue = new LinkedBlockingQueue<>();
        boolean flag = true;
        //目标IP存入Queue，一行一个IP
        BlockingQueue<String> rawIpQueue = ScannerUtil.target2Queue(baseConfig.getTargetIP());
        //ExcludeIP存入Queue
        BlockingQueue<String> excludeIpQueue = ScannerUtil.target2Queue(baseConfig.getExcludeIP());
        //去除rawIpQueue中的excludeIpQueue
        rawIpQueue.removeAll(excludeIpQueue);
        //将rawIpQueue分组
        BlockingQueue<String> sliceIpQueue = ScannerUtil.ipQueue2SliceQueue(rawIpQueue,baseConfig.getSliceSize());
        String iPStringList;
        while (flag) {
            //从Queue中取IP进行处理
            //此时只有一个ipQueue，消费者不断从队列中取数据，不会有阻塞，所以使用poll
            iPStringList = sliceIpQueue.take();
            if (!iPStringList.equals("ip2SliceQueueFinished") && !iPStringList.equals("[]")) {
                //去掉,[]
                String targetIps = iPStringList.replaceAll(",|\\[|\\]", "");
                //一次扫描任务中，单个IP需要扫描次数，singleIPScanTime
                for (int i = 0; i < Integer.parseInt(baseConfig.getSingleIPScanTime()); i++) {
                    totalThreadPool.execute(new Thread(new ScannerThread(baseConfig.getWorkType(), targetIps, rawResultQueue, baseConfig.getBasePath(), baseConfig.getPort(), baseConfig.getOptions(), baseConfig.getRate())));
                }
            } else {
                flag = false;
            }
        }
        //等待所有进程执行完毕，结果保存到resultQueue中后，往下执行
        totalThreadPool.shutdown();
        totalThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        //for debug
        //System.out.println(rawResultQueue);

        switch (baseConfig.getWorkType()) {
            case "mass":
                //处理massscan扫描结果，将扫描结果转成Map，Map<String,Set<String>>
                // 格式：IP:PORTS，192.168.31.1 [80,8192]
                return ScannerUtil.massResult2Map(rawResultQueue);
            case "nmap":
                //处理nmap扫描结果，将扫描结果转成Map，Map<String,Set<String>>
                //格式IP;Status，192.168.31.1 [[53, open, domain, dnsmasq 2.71], [80, open, http, nginx]]
                return ScannerUtil.nmapResult2Map(rawResultQueue);
            case "mass2Nmap":
                //mass2Nmap
                Map<String, Set<String>> massResultMap;
                massResultMap = ScannerUtil.massResult2Map(rawResultQueue);
                BlockingQueue<String> massIpQueue = new LinkedBlockingQueue<>();
                for (Map.Entry<String, Set<String>> entry : massResultMap.entrySet()) {
                    //StringUtils将Set中的端口转换成str
                    String ip = entry.getKey();
                    String ports = StringUtils.join(entry.getValue(), ",");
                    massIpQueue.put(ip + " -p" + ports);
                }
                massIpQueue.put("ipAndPort2QueueFinished");
                ExecutorService nampThreadPool = Executors.newFixedThreadPool(Integer.parseInt(singleNmapConfig.getThreadNumber()));
                BlockingQueue<StringBuilder> mass2NmapResultQueue = new LinkedBlockingQueue<>();
                boolean flag2 = true;
                String ipAndPort;
                while (flag2) {
                    ipAndPort = massIpQueue.take();
                    if (!ipAndPort.equals("ipAndPort2QueueFinished")) {
                        for (int i = 0; i < Integer.parseInt(singleNmapConfig.getSingleIPScanTime()); i++) {
                            nampThreadPool.execute(new Thread(new ScannerThread(singleNmapConfig.getWorkType(), ipAndPort, mass2NmapResultQueue, singleNmapConfig.getBasePath(), "", singleNmapConfig.getOptions(), null)));
                        }
                    } else {
                        flag2 = false;
                    }
                }
                //等待所有进程执行完毕，结果保存到resultQueue中后，往下执行
                nampThreadPool.shutdown();
                nampThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
                return ScannerUtil.nmapResult2Map(mass2NmapResultQueue);
        }
        return null;
    }

}
