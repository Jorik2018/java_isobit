package org.isobit;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;

public class SOAPClient {

	private ExecutorService executorService = Executors.newCachedThreadPool();
    private Client client;
    
    public Client getClient() {
    	if(client==null)
        client = ClientBuilder.newBuilder()
                .executorService(executorService)
                .build();
        return client;
    }
	
	public  Document getDocument(String target, String action, String body) throws Exception {
		String xml = (String) getClient().target(target).request().header("SOAPAction", action)
					.header("Content-Type", "text/xml")
					.post(Entity.entity(body, "text/xml; charset=UTF-8"), String.class);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		return db.parse(new ByteArrayInputStream(xml.getBytes()));
	}

	public Object transform(NodeList nodeList) {
		if(nodeList.getLength()==1&&nodeList.item(0).getNodeType()==Node.TEXT_NODE) {
			return nodeList.item(0).getTextContent();
		}
		List<Map<Object, Object>> dataArr = new ArrayList();
		Map<Object, Object> dataObject = new HashMap<>();
		
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node tempNode = nodeList.item(count);
			if (tempNode.getNodeType() == 1) {
				//System.out.println(tempNode.getNodeName()+" "+tempNode.hasChildNodes());	
				if (tempNode.hasChildNodes() && tempNode.getChildNodes().getLength() > 0) {
					Object child = transform(tempNode.getChildNodes());
					if (dataObject.containsKey(tempNode.getNodeName())) {
						Object oo = dataObject.get(tempNode.getNodeName());
						if (oo instanceof List) {
							((List) oo).add((child instanceof List) ? ((List) child).get(0) : child);
						} else {
							List<Object> l = new ArrayList();
							l.add(oo);
							dataObject.put(tempNode.getNodeName(), l);
							l.add((child instanceof List) ? ((List) child).get(0) : child);
						}
					} else {
						dataObject.put(tempNode.getNodeName(), child);
					}
				} else {
					dataObject.put(tempNode.getNodeName(), tempNode.getTextContent());
				}
			}
		}
		
		dataArr.add(dataObject);
		return (dataArr.size() > 1) ? dataArr : dataObject;
	}
}
