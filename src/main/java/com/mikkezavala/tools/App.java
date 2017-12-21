package com.mikkezavala.tools;

import com.mikkezavala.tools.exceptions.ConfigException;
import com.mikkezavala.tools.services.IOrchestrator;
import com.mikkezavala.tools.services.Orchestrator;
import com.mikkezavala.tools.services.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class App {

    private static final String SETTINGS_FILE = "config.properties";

    public static void main(String[] args) {

        Properties props = getFromConfig();
        Repository repositoryA = buildRepositoryFromProps("repository_a", props);
        Repository repositoryB = buildRepositoryFromProps("repository_b", props);

        IOrchestrator orchestrator = new Orchestrator(repositoryA, repositoryB);
        orchestrator.digest();
    }

    private static Properties getFromConfig() {

        Properties props = new Properties();
        try {
            InputStream inputStream = App.class.getClassLoader().getResourceAsStream(SETTINGS_FILE);
            if (inputStream == null) {
                throw new ConfigException();
            }
            props.load(inputStream);

        } catch (IOException e) {
            throw new ConfigException();
        } catch (ConfigException e) {
            System.out.println("**** No config file available, fallback to interactive mode. ****");

            return null;
        }

        return props;
    }

    private static Repository buildRepositoryFromProps(String key, Properties props) {
        String repositoryLocation = props.getProperty(key + "_location");
        return new Repository(repositoryLocation);
    }

}
