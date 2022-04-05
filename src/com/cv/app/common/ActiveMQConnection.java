/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.common;

import java.io.IOException;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.TransportListener;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class ActiveMQConnection {
    
    private static final Logger log = Logger.getLogger(ActiveMQConnection.class.getName());
    private static transient ConnectionFactory factory;
    private transient Connection connection;
    private transient Session session;
    private transient MessageProducer producer;
    private boolean status = false;
    
    private class AmqTransportListener implements TransportListener {

        public AmqTransportListener(){
            super();
        }
        
        @Override
        public void onCommand(Object arg0) {
            //System.out.println("TransportListener onCommand");
            log.info("TransportListener onCommand");
        }
        
        @Override
        public void onException(IOException error){
            log.error("TransportListener onException");
            log.error(error);
            status = false;
        }
        
        @Override
        public void transportInterupted() {
            log.error("TransportListener transportInterupted");
            status = false;
            /*try {
                failProducer.disconnect();
            } catch (JMSException e) {
                e.printStackTrace();
            }*/
        }

        @Override
        public void transportResumed() {
            log.error("TransportListener transportResumed");
            /*if (!failProducer.isConnected()) {
                try {
                    failProducer.init();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }*/
        }
    }
    
    public ActiveMQConnection(String url){
        try {
            factory = new ActiveMQConnectionFactory(url);
            connection = factory.createConnection();
            connection.start();
            ((org.apache.activemq.ActiveMQConnection)connection).addTransportListener(new AmqTransportListener());
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(null);
            status = true;
            log.info("AMQ-Initilization is successful.");
        } catch (JMSException ex) {
            log.error("initialiseAMQ : " + ex);
            status = false;
        }
    }
    
    public void sendMessage(String topic, Message message) throws JMSException {
        Destination destination = session.createQueue(topic);
        producer.send(destination, message);
    }

    public MapMessage getMapMessageTemplate(){
        MapMessage message = null;
        
        try{
            message = session.createMapMessage();
        }catch(JMSException ex){
            log.error("getMapMessageTemplate : " + ex);
        }
        
        return message;
    }

    public boolean isStatus() {
        return status;
    }
}
