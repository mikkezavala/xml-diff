package com.mikkezavala.tools.services;

import com.mikkezavala.tools.domain.Job;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlunit.builder.DiffBuilder;

import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;
import org.xmlunit.diff.ElementSelectors;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class DiffService extends Thread {

    private BlockingQueue<Job> jobs;

    private static final Logger LOGGER = LoggerFactory.getLogger(DiffService.class);

    DiffService(BlockingQueue<Job> jobs, int threadNum) {
        super("Diff-Service-" + threadNum);
        this.jobs = jobs;
    }

    @Override
    public void run() {

        int filesDiff = 0;
        LOGGER.info("Running Diff Jobs");

        try {
            while (true) {
                Job job = jobs.poll(2500, TimeUnit.MILLISECONDS);

                if (job != null) {
                    diffDocuments(job);

                    ++filesDiff;
                } else {
                    break;
                }
            }
        } catch (InterruptedException e) {
            LOGGER.info("Jobs Halted");
        }

        LOGGER.info("Thread {} Diff {} Files", Thread.currentThread().getName(), filesDiff);
    }

    private void diffDocuments(Job job) {
        String resourceA = job.getResourceA();
        String resourceB = job.getResourceB();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {

            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream ioTest = IOUtils.toInputStream(resourceB, "UTF-8");
            InputStream ioControl = IOUtils.toInputStream(resourceA, "UTF-8");
            ComparatorService comparator = new ComparatorService(builder.parse(ioTest), builder.parse(ioControl));

            Diff myDiff = DiffBuilder.compare(resourceA).withTest(resourceB)
                    .ignoreComments()
                    .ignoreWhitespace()
                    .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName)).build();

            if (myDiff.hasDifferences()) {

                File diffFile = new File("./diff-" + job.getFileBase());
                try (BufferedWriter out = new BufferedWriter(new FileWriter(diffFile))) {
                    for (Difference difference : myDiff.getDifferences()) {

                        out.write("\n################\n");
                        writeResult(out, comparator.getNodeList(difference.getComparison()));
                        out.write(difference.toString());
                        out.write("\n################\n");
                    }

                } catch (IOException e) {
                    LOGGER.error(" -- Failed Saving Diff File {} --", diffFile);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed", e);
        }
    }

    private void writeResult(BufferedWriter out, NodeList nodes) throws IOException {

        if (Objects.nonNull(nodes) && nodes.getLength() > 0) {

            Element node = (Element) nodes.item(0);
            Node explanationId = node.getElementsByTagName("ID").item(0);
            Node skippedId = node.getElementsByTagName("SkippedToIDForExplanationData").item(0);

            out.write("ID: " + explanationId.getTextContent() + "\n");
            out.write("Skipped ID: " + skippedId.getTextContent() + "\n");

        }
    }

}
