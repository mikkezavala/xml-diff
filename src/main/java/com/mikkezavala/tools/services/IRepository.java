package com.mikkezavala.tools.services;


import com.mikkezavala.tools.domain.Resource;

import java.util.concurrent.BlockingQueue;

public interface IRepository {

    String getFileByName(String file);

    void fillQueue(BlockingQueue<Resource> queue);
}
