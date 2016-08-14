package com.botcrator.support;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import static org.w3c.dom.Node.ELEMENT_NODE;


public class SettingsWriter {
    final static Logger logger = Logger.getLogger(SettingsWriter.class.getSimpleName());
    private final static File file = new File("settings.xml");

    public static void writeUser(String username, String tokenString) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(file);

        Node settings = document.getFirstChild();

        //Get users node
        NodeList settingsChildNodes = settings.getChildNodes();
        Node users = null;
        for (int i = 0; i < settingsChildNodes.getLength(); i++) {
            Node item = settingsChildNodes.item(i);
            if (item.getNodeType() != ELEMENT_NODE) continue;
            if (item.getNodeName().equals("users")) {
                users = item;
                break;
            }
        }

        //Get users
        assert users != null;
        Element user = document.createElement("user");

        Element name = document.createElement("name");
        name.setTextContent(username);
        user.appendChild(name);

        Element token = document.createElement("token");
        token.setTextContent(tokenString);
        user.appendChild(token);

        users.appendChild(user);

        Transformer tr = TransformerFactory.newInstance().newTransformer();
        tr.setOutputProperty(OutputKeys.METHOD, "xml");
        tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tr.setOutputProperty(OutputKeys.INDENT, "yes");
        tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        // send DOM to file
        FileOutputStream outputStream = new FileOutputStream(file);
        tr.transform(new DOMSource(document),
                new StreamResult(outputStream));
        outputStream.flush();
        logger.info("A new user has been added to " + file);
    }

}
