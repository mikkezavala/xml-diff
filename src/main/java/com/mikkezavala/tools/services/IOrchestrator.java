package com.mikkezavala.tools.services;

public interface IOrchestrator {

    void digest();

    void startProducer(IRepository repository);

    void startConsumers(IRepository repository, int numberConsumers);

    void startDiffConsumers(int numberConsumers);
}
