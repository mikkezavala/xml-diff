package com.mikkezavala.tools.services;

import com.mikkezavala.tools.exceptions.ComparatorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.Comparison.Detail;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.Objects;

public class ComparatorService {

    private Document documentA;

    private Document documentB;

    private static final String XPATH_ANCESTOR = "/ancestor::NodeExplanation";
    private static final XPath XPATH = XPathFactory.newInstance().newXPath();

    private static final Logger LOGGER = LoggerFactory.getLogger(ComparatorService.class);

    ComparatorService(Document documentA, Document documentB) {
        this.documentA = documentA;
        this.documentB = documentB;
    }

    public NodeList getNodeList(Comparison comparison) throws ComparatorException {

        NodeList nodes = lookup(comparison.getControlDetails());
        if (Objects.nonNull(nodes) && nodes.getLength() > 0) {
            return nodes;
        }

        return lookup(comparison.getTestDetails());
    }

    private NodeList lookup(Detail detail) throws ComparatorException {
        String xpathString = detail.getXPath();
        if (StringUtils.isNotEmpty(xpathString)) {
            NodeList nodes = (NodeList) extract(xpathString, documentA);
            if (nodes.getLength() > 0) {
                return nodes;
            }

            return (NodeList) extract(xpathString, documentB);
        }

        return null;
    }

    private Object extract(String xpath, Document doc) throws ComparatorException {

        String compositeXPath = xpath + XPATH_ANCESTOR;

        try {
            XPathExpression expr = XPATH.compile(compositeXPath);
            return expr.evaluate(doc, XPathConstants.NODESET);

        } catch (XPathExpressionException e) {
            LOGGER.debug(compositeXPath);
            throw new ComparatorException("Failure evaluating " + compositeXPath, e);
        }

    }
}
