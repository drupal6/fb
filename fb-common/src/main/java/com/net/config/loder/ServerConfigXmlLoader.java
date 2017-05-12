package com.net.config.loder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.net.config.ServerConfig;


/**
 * 加载Server配置xml
 */
public class ServerConfigXmlLoader {
	
	private Logger logger = LoggerFactory.getLogger(ServerConfigXmlLoader.class);

	public ServerConfig load(String file) {
		InputStream in = null;
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			in = new FileInputStream(file);
			Document doc = builder.parse(in);
			NodeList list = doc.getElementsByTagName("server");

			ServerConfig config = new ServerConfig();
			if (list.getLength() > 0) {
				Node node = list.item(0);
				NodeList childs = node.getChildNodes();

				for (int j = 0; j < childs.getLength(); j++) {
					if ("server-name".equals(childs.item(j).getNodeName()))
						config.setName(childs.item(j).getTextContent());
					else if ("server-id".equals(childs.item(j).getNodeName()))
						config.setId(Integer.parseInt(childs.item(j)
								.getTextContent()));
					else if ("server-port".equals(childs.item(j).getNodeName())) {
						config.setPort(Integer.parseInt(childs.item(j)
								.getTextContent()));
					} else if("boss-num".equals(childs.item(j).getNodeName())) {
						config.setBossNum(Integer.parseInt(childs.item(j)
								.getTextContent()));
					} else if("worker-num".equals(childs.item(j).getNodeName())) {
						config.setWorkerNum(Integer.parseInt(childs.item(j)
								.getTextContent()));
					}
				}
			}

			logger.info("load server config !");
			return config;
		} catch (Exception e) {
			this.logger.error(e.getMessage(), e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}
}
