package com.affirm.equifax;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stbn on 31/10/16.
 */
public class HeaderHandlerResolver implements HandlerResolver {

    public List<Handler> getHandlerChain(PortInfo portInfo) {
        List<Handler> handlerChain = new ArrayList<Handler>();

        HeaderHandler hh = new HeaderHandler();

        handlerChain.add(hh);

        return handlerChain;
    }
}
