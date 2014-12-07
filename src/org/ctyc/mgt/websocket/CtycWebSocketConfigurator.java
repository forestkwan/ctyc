package org.ctyc.mgt.websocket;

import javax.websocket.server.ServerEndpointConfig.Configurator;

public class CtycWebSocketConfigurator extends Configurator {
 
    private static CtycWebSocket ctycWebSocket = new CtycWebSocket();
 
    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        return (T)ctycWebSocket;
    }
}