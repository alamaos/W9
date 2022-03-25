package com.example.w9;

import android.os.StrictMode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class TheatherHandling {
    private ArrayList<Theather> theathers;
    ArrayList<String> movieLocation = new ArrayList<>();
    ArrayList<Integer> movieID = new ArrayList<>();

    private static TheatherHandling theatherHandling = new TheatherHandling();

    private TheatherHandling(){
        theathers = new ArrayList<>();

        DocumentBuilder builder = null;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String url = "https://www.finnkino.fi/xml/TheatreAreas/";
            Document document = builder.parse(url);
            document.getDocumentElement().normalize();
            System.out.println("Root element: "+ document.getDocumentElement().getNodeName());
            NodeList nodeList = document.getDocumentElement().getElementsByTagName("TheatreArea");

            for (int i = 0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                System.out.println("Element is "+node.getNodeName());
                if (node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;
                    String location = element.getElementsByTagName("Name").item(0).getTextContent();
                    int ID = Integer.valueOf(element.getElementsByTagName("ID").item(0).getTextContent());
                    movieLocation.add(location);
                    movieID.add(ID);
                    theathers.add(new Theather(location,ID));
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public static TheatherHandling getInstance(){
        return theatherHandling;
    }

    public List<Theather> getList() {
        return theathers;
    }
    public List<String> getNames(){return movieLocation;}

    public  int findID(String name) {
        for (Theather x : theathers) {
            if (x.getTown() == name) {
                return x.getID();
            }
        }
        return 1029;
    }
}
