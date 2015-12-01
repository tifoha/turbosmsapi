package org.turbosms.api;

import org.turbosms.service.Parameter;
import org.turbosms.service.SoapMessageService;
import org.turbosms.service.SoapMessageServiceImpl;
import org.turbosms.exception.TurboSmsException;

import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by yuriy_gorbylov on 01.12.2015.
 */
public class TurboSmsApiImpl implements TurboSmsApi {

    private final Set<String> validMessageStatusResponses = new HashSet<String>(){{
        add("Отправлено");
        add("В очереди");
        add("Сообщение передано в мобильную сеть");
        add("Сообщение доставлено получателю");
        add("Истек срок сообщения");
        add("Удалено оператором");
        add("Не доставлено");
        add("Сообщение доставлено на сервер");
        add("Неизвестный статус");
        add("Не достаточно кредитов на счете");
        add("Удалено пользователем");
        add("Ошибка, сообщение не отправлено");
    }};
    private final Set<String> successSendSMSStatusResponses = new HashSet<String>(){{
        add("Сообщения успешно отправлены");
    }};
    private final SoapMessageServiceImpl soapMessageService;

    public TurboSmsApiImpl(String login, String password) {
        this.soapMessageService = new SoapMessageServiceImpl(login, password);
    }

    public Double balance() {
        try {
            SOAPMessage response = soapMessageService.call("GetCreditBalance");
            String responseValue = getResponseValue(response.getSOAPBody().getChildElements());
            if (!responseValue.matches("-?\\d+(\\.\\d+)?")) { // is response value numeric
                throw new TurboSmsException(responseValue);
            }
            return Double.valueOf(responseValue);
        } catch (SOAPException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String sendSMS(String sender, String destination, String text, String wappush) {
        try {
            SOAPMessage response = soapMessageService.call(
                    "SendSMS",
                    new Parameter("sender", sender),
                    new Parameter("destination", destination),
                    new Parameter("text", text),
                    new Parameter("wappush", wappush)
            );
            String responseValue = getResponseValue(response.getSOAPBody().getChildElements());
            if (!successSendSMSStatusResponses.contains(responseValue) &&
                !responseValue.contains("-")){ //response as message ids
                throw new TurboSmsException("SMS has not been sent. Reason: " + responseValue);
            }
            return responseValue;
        } catch (SOAPException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getMessageStatus(String messageId) {
        try {
            SOAPMessage response = soapMessageService.call(
                    "GetMessageStatus",
                    new Parameter("MessageId", messageId)
            );
            String responseValue = getResponseValue(response.getSOAPBody().getChildElements());
            if (!validMessageStatusResponses.contains(responseValue)){
                throw new TurboSmsException("Bad request. Reason: " + responseValue);
            }
            return responseValue;
        } catch (SOAPException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getResponseValue(Iterator childElements) throws SOAPException {
        SOAPBodyElement bodyElement = (SOAPBodyElement) childElements.next();
        return bodyElement.getValue() == null ?
                getResponseValue(bodyElement.getChildElements()) :
                bodyElement.getValue();
    }

    public void addSuccessSMSSendStatus(String status){
        this.successSendSMSStatusResponses.add(status);
    }

    public void removeSuccessSMSSendStatus(String status){
        if (successSendSMSStatusResponses.contains(status)){
            this.successSendSMSStatusResponses.remove(status);
        }
    }

    public Set<String> getSuccessSMSSendStatuses(){
        return this.successSendSMSStatusResponses;
    }

    public String getDefaultNamespace() {
        return this.soapMessageService.getDefaultNamespace();
    }

    public void setDefaultNamespace(String namespace) {
        this.soapMessageService.setDefaultNamespace(namespace);
    }

    public String getDefaultUrl() {
        return this.soapMessageService.getDefaultUrl();
    }

    public void setDefaultUrl(String url) {
        soapMessageService.setDefaultUrl(url);
    }
}
