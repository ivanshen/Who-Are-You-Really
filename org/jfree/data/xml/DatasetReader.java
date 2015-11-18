package org.jfree.data.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.PieDataset;
import org.xml.sax.SAXException;

public class DatasetReader {
    public static PieDataset readPieDatasetFromXML(File file) throws IOException {
        return readPieDatasetFromXML(new FileInputStream(file));
    }

    public static PieDataset readPieDatasetFromXML(InputStream in) throws IOException {
        PieDataset result = null;
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            PieDatasetHandler handler = new PieDatasetHandler();
            parser.parse(in, handler);
            result = handler.getDataset();
        } catch (SAXException e) {
            System.out.println(e.getMessage());
        } catch (ParserConfigurationException e2) {
            System.out.println(e2.getMessage());
        }
        return result;
    }

    public static CategoryDataset readCategoryDatasetFromXML(File file) throws IOException {
        return readCategoryDatasetFromXML(new FileInputStream(file));
    }

    public static CategoryDataset readCategoryDatasetFromXML(InputStream in) throws IOException {
        CategoryDataset result = null;
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            CategoryDatasetHandler handler = new CategoryDatasetHandler();
            parser.parse(in, handler);
            result = handler.getDataset();
        } catch (SAXException e) {
            System.out.println(e.getMessage());
        } catch (ParserConfigurationException e2) {
            System.out.println(e2.getMessage());
        }
        return result;
    }
}
