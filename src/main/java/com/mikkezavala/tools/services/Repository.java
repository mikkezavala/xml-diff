package com.mikkezavala.tools.services;

import com.mikkezavala.tools.domain.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;


public class Repository implements IRepository {

    private String location;

    private static final Logger LOGGER = LoggerFactory.getLogger(Repository.class);

    public Repository(String location) {
        this.location = location;
    }

    @Override
    public void fillQueue(BlockingQueue<Resource> queue) {
        Path directory = Paths.get(location);
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(directory, "*.xml")) {
            for (Path path : paths) {
                String name = path.getFileName().toString();
                String resourceId = name.substring(0, name.indexOf("-"));
                queue.put(new Resource(resourceId, name, path.toString()));
            }
        } catch (IOException e) {
            LOGGER.error("Failure reading file");
        } catch (InterruptedException e) {
            LOGGER.error("Queue files was interrupted");
        }
    }

    @Override
    public String getFileByName(String fileName) {
        String filePath = null;
        Path path = Paths.get(location + "/" + fileName);

        if (Files.exists(path) && Files.isReadable(path) && Files.isRegularFile(path)) {
            filePath = path.toString();
        }

        return filePath;
    }
}
