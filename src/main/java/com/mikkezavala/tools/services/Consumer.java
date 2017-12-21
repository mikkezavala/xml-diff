package com.mikkezavala.tools.services;

import com.mikkezavala.tools.domain.Job;
import com.mikkezavala.tools.domain.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Consumer extends Thread {

    private BlockingQueue<Job> jobs;

    private BlockingQueue<Resource> queue;

    private IRepository against;

    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

    Consumer(BlockingQueue<Resource> queue, IRepository against, BlockingQueue<Job> jobs, int workerNum) {
        super("Consumer-" + workerNum);
        this.queue = queue;
        this.against = against;
        this.jobs = jobs;
    }

    @Override
    public void run() {

        int jobRunByThis = 0;
        String threadName = Thread.currentThread().getName();
        LOGGER.info("Retrieving files for diff queue");

        try {
            while (true) {
                Resource value = queue.poll(1500, TimeUnit.MILLISECONDS);
                if (value != null) {
                    createJob(value);
                    jobRunByThis++;
                } else {
                    break;
                }
            }
        } catch (InterruptedException e) {
            LOGGER.error("Execution Interrupted for {}", threadName);
        }

        LOGGER.info("Thread {} enqueued {} jobs", threadName, jobRunByThis);
    }

    private void createJob(Resource resource) throws InterruptedException {
        String pathFileB = against.getFileByName(resource.getFileName());

        if (pathFileB != null) {
            try {
                String fileA = new String(Files.readAllBytes(Paths.get(resource.getLocation())));
                String fileB = new String(Files.readAllBytes(Paths.get(pathFileB)));
                jobs.put(new Job(resource.getFileName(), fileA, fileB));

            } catch (IOException e) {
                LOGGER.error("Failed reading file job for {}", resource.getFileName(), e);
            }
        }
    }
}
