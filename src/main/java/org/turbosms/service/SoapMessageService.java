package org.turbosms.service;

import javax.xml.soap.SOAPMessage;

/**
 * Created by yuriy_gorbylov on 01.12.2015.
 */
public interface SoapMessageService {

    SOAPMessage call(String methodName, Parameter... parameters);

}
