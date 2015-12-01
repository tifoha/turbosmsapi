package org.turbosms.api;

/**
 * Created by yuriy_gorbylov on 01.12.2015.
 */
public interface TurboSmsApi {

    Double balance();

    String sendSMS(String sender, String destination, String text, String wappush);

    String getMessageStatus(String messageId);
}
