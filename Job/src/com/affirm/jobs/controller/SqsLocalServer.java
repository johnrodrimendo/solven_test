package com.affirm.jobs.controller;

import com.affirm.system.configuration.Configuration;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;
import org.elasticmq.NodeAddress;
import org.elasticmq.rest.sqs.SQSRestServerBuilder;

import java.util.List;
/* Intructions
 * This is only for local development
 * Queues url have to be setup as enviroment variables
 * in intellij WorkerInitializer configuration
 *  AWS_SQS_WEBSCRAPPER_QUEUE_URL http://localhost:9324/queue/webscrapper-local
 *  AWS_SQS_SCHEDULE_QUEUE_URL http://localhost:9324/queue/schedule-local
 *  Worker must be launched before the deployment of client and Backoffice
 */

public class SqsLocalServer {

    public void build() {
        System.out.println("Iniciando cola local");

//        Map<String, SqsQueue> queues = new ConcurrentHashMap<>();

//        EN CASO DE SQS LOCAL. SOLO ES NECESARIO EL NOMBRE DEL QUEUE. NO LA URL COMPLETA
        String[] queueUrls = {
                Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER),
                Configuration.queueUrl(Configuration.SqsQueue.SCHEDULE),
                Configuration.queueUrl(Configuration.SqsQueue.EVALUATION_INITIALIZER),
                Configuration.queueUrl(Configuration.SqsQueue.EVALUATION_PROCESSOR),
                Configuration.queueUrl(Configuration.SqsQueue.REPORT),
                Configuration.queueUrl(Configuration.SqsQueue.FRAUD_ALERTS),
                Configuration.queueUrl(Configuration.SqsQueue.APPROVE_LOAN_APPLICATION),
                Configuration.queueUrl(Configuration.SqsQueue.DEFAULT)
        };

//        CREAR LOCAL SERVER
        SQSRestServerBuilder.withPort(Configuration.SQS_QUEUE_LOCAL_PORT)
                .withServerAddress(new NodeAddress("http", "localhost", Configuration.SQS_QUEUE_LOCAL_PORT, "")).start();

        System.out.println("Lanzando colas");
        // Use standard ElasticMQ credentials ("x", "x")
        AmazonSQSClient amazonSQSClient = new AmazonSQSClient();
        for (String queueUrl : queueUrls) {
            // ElasticMQ is running on the same machine as integration test
            CreateQueueRequest cqr = new CreateQueueRequest();
            cqr.setQueueName(queueUrl.substring(queueUrl.lastIndexOf("/") + 1));
            cqr.addAttributesEntry("defaultVisibilityTimeout", "30");
            cqr.addAttributesEntry("delay", "5");
            cqr.addAttributesEntry("receiveMessageWait", "0");
            amazonSQSClient.setEndpoint(Configuration.SQS_ELASTICMQ_URL);
            // Create queue
            amazonSQSClient.createQueue(cqr);
            // Queue URL in ElasticMQ is http://host:port/queue/{queue_name}
//            queues.put(cqr.getQueueName(), new SqsQueue(amazonSQSClient, queueUrl));
        }

        System.out.println("Colas activas: " + amazonSQSClient.listQueues().toString());
    }
}

class SqsQueue {

    private final AmazonSQSClient client;
    private final String queueUrl;

    SqsQueue(AmazonSQSClient client, String queueUrl) {
        this.client = client;
        this.queueUrl = queueUrl;
    }

    public void send(Message toSend) {
        SendMessageRequest sendMessageRequest = new SendMessageRequest(queueUrl, toSend.getBody());
        sendMessageRequest.setMessageAttributes(toSend.getMessageAttributes());
        client.sendMessage(sendMessageRequest);
    }

    public List read(int maxMessages) {
        ReceiveMessageRequest request = new ReceiveMessageRequest(queueUrl);
        request.setMaxNumberOfMessages(maxMessages);
        ReceiveMessageResult receiveMessage = client.receiveMessage(request);
        return receiveMessage.getMessages();
    }

    public void purge() {
        client.purgeQueue(new PurgeQueueRequest(queueUrl));
    }
}