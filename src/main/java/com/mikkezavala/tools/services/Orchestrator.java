package com.mikkezavala.tools.services;

import com.mikkezavala.tools.domain.Job;
import com.mikkezavala.tools.domain.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Orchestrator implements IOrchestrator {

    private List<Thread> threads = new ArrayList<>();

    private BlockingQueue<Job> jobs = new LinkedBlockingQueue<>();

    private BlockingQueue<Resource> queue = new LinkedBlockingQueue<>();

    private IRepository repositoryA;

    private IRepository repositoryB;

    private final static int MAX_THREADS = 2;//;

    private static final Logger LOGGER = LoggerFactory.getLogger(Orchestrator.class);

    public Orchestrator(IRepository repositoryA, IRepository repositoryB) {
        this.repositoryA = repositoryA;
        this.repositoryB = repositoryB;
    }

    @Override
    public void digest() {

        long start = System.currentTimeMillis();

        startProducer(repositoryA);

        startConsumers(repositoryB, MAX_THREADS);

        startDiffConsumers(MAX_THREADS);

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                LOGGER.error("Job Interrupted");
            }
        }

        long stop = System.currentTimeMillis();

        LOGGER.info("Diff Took {}s on {} threads", (stop - start) / 1000, MAX_THREADS);
    }

    @Override
    public void startProducer(IRepository repository) {
        Producer producer = new Producer(queue, repository);
        producer.start();
    }

    @Override
    public void startConsumers(IRepository repository, int numberConsumers) {
        for (int i = 0; i < numberConsumers; i++) {
            Consumer consumer = new Consumer(queue, repository, jobs, i);
            consumer.start();
        }
    }

    @Override
    public void startDiffConsumers(int numberConsumers) {
        for (int i = 0; i < numberConsumers; i++) {
            DiffService service = new DiffService(jobs, i);
            threads.add(service);
            service.start();
        }
    }
}
