package com.mikkezavala.tools.services;

import com.mikkezavala.tools.domain.Resource;

import java.util.concurrent.BlockingQueue;

public class Producer extends Thread {

    private IRepository provider;
    private BlockingQueue<Resource> queue;


    Producer(BlockingQueue<Resource> queue, IRepository provider) {
        super("Producer");
        this.queue = queue;
        this.provider = provider;
    }

    @Override
    public void run() {
        provider.fillQueue(queue);
    }
}
