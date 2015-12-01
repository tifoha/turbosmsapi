package org.turbosms;

import org.turbosms.api.TurboSmsApiImpl;

/**
 * Created by yuriy_gorbylov on 01.12.2015.
 */
public class BaseUsage {

    public static void main(String[] args) {
        TurboSmsApiImpl turboSmsApi = new TurboSmsApiImpl("1bobik1", "202190");

        //getting balance
        Double balance = turboSmsApi.balance();
        System.out.println(balance);

        //getting message status
        String messageStatus = turboSmsApi.getMessageStatus("messageId");
        System.out.println(messageStatus);

        //sending sms
        String sendSMS = turboSmsApi.sendSMS("sender", "destination", "text", "wappush");
        System.out.println(sendSMS);

        //setting namespace
        turboSmsApi.setDefaultNamespace("namespace");
        //setting url
        turboSmsApi.setDefaultUrl("url");

        //adding, removing and getting success sms status, which will be checked in response
        turboSmsApi.addSuccessSMSSendStatus("success sms status");
        System.out.println(turboSmsApi.getSuccessSMSSendStatuses());
        turboSmsApi.removeSuccessSMSSendStatus("success sms status");
        System.out.println(turboSmsApi.getSuccessSMSSendStatuses());
    }
}
