package ws.argo.plugin.probehandler.consul.test;

import org.junit.Test;
import ws.argo.plugin.probehandler.ProbeHandlerConfigException;
import ws.argo.plugin.probehandler.ProbeHandlerPlugin;
import ws.argo.plugin.probehandler.consul.ConsulProbeHandlerPlugin;
import ws.argo.probe.Probe;
import ws.argo.probe.UnsupportedPayloadType;
import ws.argo.wireline.probe.ProbeWrapper;
import ws.argo.wireline.response.ResponseWrapper;

import java.net.MalformedURLException;
import java.util.UUID;

/**
 * Created by jmsimpson on 11/12/15.
 */
public class ProbeTest {

    @Test
    public void testNakedProbe() throws UnsupportedPayloadType, MalformedURLException, ProbeHandlerConfigException {

        ProbeWrapper probe = new ProbeWrapper(UUID.randomUUID().toString());
        probe.addRespondToURL("blank", "http://localhost:8080/probeResponse");

        ProbeHandlerPlugin consulPlugin = new ConsulProbeHandlerPlugin();

        consulPlugin.initializeWithPropertiesFilename("goodConsulConfig.xml");

        ResponseWrapper responseWrapper = consulPlugin.handleProbeEvent(probe);

        System.out.println("Number of services [" + responseWrapper.numberOfServices() + "]");


    }



}
