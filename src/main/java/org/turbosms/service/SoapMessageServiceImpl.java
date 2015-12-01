package org.turbosms.service;

import org.turbosms.utils.SoapUtils;
import org.turbosms.exception.TurboSmsException;

import javax.xml.soap.*;

/**
 * Created by yuriy_gorbylov on 01.12.2015.
 */
public class SoapMessageServiceImpl implements SoapMessageService {

    private String login;
    private String password;
    private String namespace;
    private String url;

    public SoapMessageServiceImpl(String login, String password) {
        this.login = login;
        this.password = password;
        this.namespace = "http://turbosms.in.ua/api/Turbo";
        this.url = "http://turbosms.in.ua/api/soap.html";
    }

    public SOAPMessage call(String methodName, Parameter... parameters) {
        try {
            SOAPMessage soapMessage = initSOAPMessage();
            SOAPBody body = soapMessage.getSOAPBody();
            SOAPElement parent = body.addChildElement(methodName, "ns1");
            for (Parameter parameter : parameters) {
                parent.addChildElement(parameter.getName(), "ns1").addTextNode(parameter.getValue());
            }
            soapMessage.getMimeHeaders().addHeader("Cookie", getSessionId());
            soapMessage.saveChanges();
            return SoapUtils.call(soapMessage, url);
        } catch (SOAPException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getSessionId() throws SOAPException {
        SOAPMessage soapMessage = initSOAPMessage();
        SOAPBody soapBody = soapMessage.getSOAPBody();
        soapBody.addChildElement("Auth", "ns1")
                .addChildElement("login", "ns1").addTextNode(login)
                .getParentElement()
                .addChildElement("password", "ns1").addTextNode(password);
        soapMessage.saveChanges();

        SOAPMessage response = SoapUtils.call(soapMessage, url);

        if (response == null) {
            throw new TurboSmsException("Bad response.");
        }
        String[] headers = response.getMimeHeaders().getHeader("Set-Cookie");
        if (headers != null && headers.length != 0) {
            return headers[0];
        } else {
            throw new TurboSmsException("There is no session id.");
        }

    }

    private SOAPMessage initSOAPMessage() throws SOAPException {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("ns1", namespace);
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", url);
        soapMessage.saveChanges();
        return soapMessage;
    }

    public String getDefaultNamespace() {
        return namespace;
    }

    public void setDefaultNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getDefaultUrl() {
        return url;
    }

    public void setDefaultUrl(String url) {
        this.url = url;
    }
}
