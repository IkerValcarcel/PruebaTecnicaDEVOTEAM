import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        /*
        Con parametro de entrada
        String fileDir = args[1];
        */
        String fileDir = "data/data.xml";
        NodeList dataXML = readXML(fileDir);
        String outDir = "data/output.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(outDir));
        for (int i = 0; i < dataXML.getLength(); i++) {
            Node node = dataXML.item(i);
            writeFile(writer, node);
        }

    }

    private static NodeList readXML(String fileDir) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(fileDir);
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement();
        return root.getElementsByTagName("wd:Organization");
    }
    private static void writeFile(BufferedWriter writer, Node node) throws IOException {
        if (node.getNodeType() != Node.ELEMENT_NODE)
            return;
        Element organizationElement = (Element) node;
        NodeList organizationChildNodes = organizationElement.getElementsByTagName("*");

        for (int i = 0; i < organizationChildNodes.getLength(); i++) {
            Node organizationNode = organizationChildNodes.item(i);
            Element element = (Element) organizationNode;
            String tagName = element.getNodeName();
            String value = element.getTextContent().trim();
            switch (tagName) {
                case "wd:ID":
                    String type = element.getAttribute("wd:type");
                    String elementToWrite = "WID".equals(type) || "Organization_Reference_ID".equals(type) ?
                            type + ":" + value + ";" : "";
                    writer.write(elementToWrite);
                    break;
                case "wd:Name":
                    writer.write("Name: " + value);
                    break;
                case "Organization_Type_Reference":
                    String organizationSubtype = element.getElementsByTagName("wd:ID").item(0).getTextContent().trim();
                    writer.write("Organization_Subtype_ID:" + organizationSubtype);
                    break;
                case "wd:External_IDs_Data":
                    String externalID = element.getElementsByTagName("wd:ID").item(0).getTextContent().trim();
                    writer.write("External_ID:" + externalID);
                    break;
                case "wd:Inactive":
                    String isInactive = value;
                    writer.write("IsInactive:" + isInactive);
                    break;
                case "wd:Hierarchy_Data":
                    String topLevelOrganization = element.getElementsByTagName("wd:ID").item(0).getTextContent().trim();
                    String superiorOrganization = element.getElementsByTagName("wd:ID").item(1).getTextContent().trim();
                    writer.write("Top-Level_Organization_Reference_ID:" + topLevelOrganization);
                    writer.write("Superior-Organization_Organization_Reference_ID:" + superiorOrganization);
                    break;
            }

        }
        writer.newLine();
        writer.flush();
    }
}