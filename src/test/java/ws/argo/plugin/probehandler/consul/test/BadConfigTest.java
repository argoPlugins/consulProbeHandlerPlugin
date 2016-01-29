package ws.argo.plugin.probehandler.consul.test;

import org.junit.Test;
import ws.argo.plugin.probehandler.ProbeHandlerConfigException;
import ws.argo.plugin.probehandler.ProbeHandlerPlugin;
import ws.argo.plugin.probehandler.consul.ConsulProbeHandlerPlugin;

/**
 * Created by jmsimpson on 11/9/15.
 */
public class BadConfigTest {

    @Test (expected = ProbeHandlerConfigException.class)
    public void testBadConfig() throws ProbeHandlerConfigException {

        ProbeHandlerPlugin consulPlugin = new ConsulProbeHandlerPlugin();

        consulPlugin.initializeWithPropertiesFilename("badConsulConfig.xml");

    }

}
