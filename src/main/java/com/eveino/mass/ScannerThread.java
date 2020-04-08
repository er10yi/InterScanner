package com.eveino.mass;

import com.eveino.util.ScannerUtil;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * 多线程
 *
 * @author 贰拾壹
 * @create 2018-08-25 23:52
 */
public class ScannerThread implements Runnable {
    private String ip, basePath, port, options, rate, workType;
    private BlockingQueue<StringBuilder> rawResultQueue;

    public ScannerThread(String workType, String ip, BlockingQueue<StringBuilder> rawResultQueue, String basePath, String port, String options, String rate) {
        this.ip = ip;
        this.rawResultQueue = rawResultQueue;
        this.basePath = basePath;
        this.port = port;
        this.options = options;
        this.rate = rate;
        this.workType = workType;
    }

    @Override
    public void run() {
        try {
            StringBuilder scanResult = ScannerUtil.scanResult2StringBuilder(workType, ip, basePath, port, options, rate);
            if (scanResult.length() > 0) {
                rawResultQueue.offer(scanResult);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
