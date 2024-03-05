package com.affirm.jobs.model;

import com.affirm.common.service.ErrorService;
import com.affirm.jobs.service.impl.QueryBotService;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

import java.util.Date;
import java.util.List;

/**
 * Created by john on 19/06/17.
 */
public class WorkerSqsRunnable implements Runnable {

    public static final int STATUS_WAITING = 1;
    public static final int STATUS_WORKING = 2;

    private QueryBotService queryBotService;
    private AmazonSQSClient amazonSQSClient;
    private ErrorService errorService;
    private String sqsUrl;
    private boolean runThreads = true;
    private Integer currentQueryBotId;
    private int status = STATUS_WAITING;
    private String threadName;
    private Date startWorkingDate;

    public WorkerSqsRunnable(QueryBotService queryBotService, AmazonSQSClient amazonSQSClient, ErrorService errorService, String sqsUrl) {
        this.queryBotService = queryBotService;
        this.amazonSQSClient = amazonSQSClient;
        this.errorService = errorService;
        this.sqsUrl = sqsUrl;
    }

    @Override
    public void run() {
        threadName = Thread.currentThread().getName();
        System.out.println(" >>>>>> Thread " + threadName + " initialized!");
        while (runThreads) {
            try {
                // Get only one message from the Queue
                status = STATUS_WAITING;
                List<Message> msgs = amazonSQSClient.receiveMessage(new ReceiveMessageRequest(sqsUrl).withMaxNumberOfMessages(1)).getMessages();
                if (msgs.size() > 0) {
                    Message message = msgs.get(0);
                    System.out.println("Thread " + threadName + " received message: " + message.getBody());
                    amazonSQSClient.deleteMessage(new DeleteMessageRequest(sqsUrl, message.getReceiptHandle()));

                    startQueryBot(Integer.parseInt(message.getBody()));
                    queryBotService.runBot(currentQueryBotId);
                    finishQueryBot();
                } else {
                    Thread.sleep(100);
                }
            } catch (Throwable ex) {
                errorService.onError(ex);
                finishQueryBot();
            }
        }
        System.out.println(" <<<<< Thread " + threadName + " finish!");
    }

    private void startQueryBot(int queryBotId) {
        currentQueryBotId = queryBotId;
        startWorkingDate = new Date();
        status = STATUS_WORKING;
        System.out.println("Thread " + threadName + " started query " + currentQueryBotId);
    }

    private void finishQueryBot() {
        System.out.println("Thread " + threadName + " finished query " + currentQueryBotId + " after " + getWorkingTimeInMs());
        currentQueryBotId = null;
        startWorkingDate = null;
    }

    public String getWorkingTimeInMs() {
        if (startWorkingDate == null)
            return "";
        return new Date().getTime() - startWorkingDate.getTime() + "ms";
    }

    public void stop() {
        runThreads = false;
    }

    public Integer getCurrentQueryBotId() {
        return currentQueryBotId;
    }

    public int getStatus() {
        return status;
    }

    public String getThreadName() {
        return threadName;
    }

}
