/*
 * Copyright 2015 Jeff Simpson.
 *
 * This file is part of the Argo Hashicorp Consul Probe Handler plugin.
 *
 * Hasicorp and Consul (c) Hashicorp, Inc.
 *
 * Argo MQTT Transport plugin is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package ws.argo.plugin.probehandler.consul;

import com.orbitz.consul.CatalogClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.ConsulException;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.catalog.CatalogService;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import ws.argo.plugin.probehandler.ProbeHandlerConfigException;
import ws.argo.plugin.probehandler.ProbeHandlerPlugin;
import ws.argo.wireline.probe.ProbeWrapper;
import ws.argo.wireline.response.ResponseWrapper;
import ws.argo.wireline.response.ServiceWrapper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Integer.parseInt;

/**
 * Created by jmsimpson on 10/29/15.
 */
public class ConsulProbeHandlerPlugin implements ProbeHandlerPlugin {

    private static final Logger LOGGER = Logger.getLogger(ConsulProbeHandlerPlugin.class.getName());
    private String _consulURL;
    private Integer _consulPort;

    private Consul _consul;
    private CatalogClient _catalogClient;

    @Override
    public ResponseWrapper handleProbeEvent(ProbeWrapper probeWrapper) {
        LOGGER.fine("Consul ProbeHandler Plugin handling probe: " + probeWrapper.asXML());

        ResponseWrapper response = new ResponseWrapper(probeWrapper.getProbeId());


        if (probeWrapper.isNaked()) {
            LOGGER.fine("Query all detected - no service contract IDs in probe");

            ArrayList<ServiceWrapper> serviceList = getAllServices();

            for (ServiceWrapper entry : serviceList) {
                // If the set of contract IDs is empty, get all of them
                response.addResponse(entry);
            }

        } else {
//            for (String serviceContractID : probe.getServiceContractIDs()) {
//                LOGGER.fine("Looking to detect " + serviceContractID + " in entry list.");
//                for (ServiceWrapper entry : serviceList) {
//                    if (entry.getServiceContractID().equals(serviceContractID)) {
//                        // Boom Baby - we got one!!!
//                        response.addResponse(entry);
//                    }
//                }
//            }
//            for (String serviceInstanceID : probe.getServiceInstanceIDs()) {
//                LOGGER.fine("Looking to detect " + serviceInstanceID + " in entry list.");
//                for (ServiceWrapper entry : serviceList) {
//                    if (entry.getId().equals(serviceInstanceID)) {
//                        // Boom Baby - we got one!!!
//                        response.addResponse(entry);
//                    }
//                }
//            }
        }



        return response;
    }

    private ArrayList<ServiceWrapper> getAllServices() {

        ArrayList<ServiceWrapper> serviceList = new ArrayList<ServiceWrapper>();
        ConsulResponse<Map<String,List<String>>> response;
        response = _catalogClient.getServices();

        Set<String> serviceTypes = response.getResponse().keySet();


        for (String serviceType : serviceTypes) {
            ConsulResponse<List<CatalogService>> serviceResponse = _catalogClient.getService(serviceType);
            List<CatalogService> catalogServices = serviceResponse.getResponse();

            for (CatalogService svc: catalogServices) {
                ServiceWrapper serviceBean = new ServiceWrapper(svc.getServiceId());
                serviceBean.setServiceContractID(serviceType);

                StringBuffer nameBuf = new StringBuffer();
                StringBuffer tagsJson = new StringBuffer();
                nameBuf.append(serviceType).append(" ");
                tagsJson.append("[ ");

                List<String> tags = svc.getServiceTags();

                for (String tag : tags) {
                    nameBuf.append(tag).append(" ");
                    tagsJson.append("\"").append(tag).append("\"");
                }

                tagsJson.append(" ]");

                serviceBean.setServiceName(nameBuf.toString().trim());
                serviceBean.setConsumability(ServiceWrapper.MACHINE_CONSUMABLE);

                serviceBean.addAccessPoint("access", svc.getAddress(), String.valueOf(svc.getServicePort()), "", "tagsJson", tagsJson.toString());

                serviceList.add(serviceBean);
            }

        }

        return serviceList;

    }

    @Override
    public void initializeWithPropertiesFilename(String xmlConfigFilename) throws ProbeHandlerConfigException {
        Properties properties;
        try {
            properties = processPropertiesFile(xmlConfigFilename);
            initializeConsulClient(properties);
        } catch (ProbeHandlerConfigException e) {
            throw new ProbeHandlerConfigException("Error reading config [" + xmlConfigFilename + "]", e);
        } catch (IOException e) {
            throw new ProbeHandlerConfigException("Error initializing Consul client", e);
        }

    }

    private void initializeConsulClient(Properties properties) throws ProbeHandlerConfigException {

        int port = Integer.parseInt(properties.getProperty("port"));
        String host = properties.getProperty("host");

        try {
            _consul = Consul.newClient(host, port);
        } catch (ConsulException e) {
            throw new ProbeHandlerConfigException("Unable to connect to Consul at host [" + host + "].");
        }

        _catalogClient = _consul.catalogClient();

    }

    @Override
    public String pluginName() {
        return "Consul Probe Handler";
    }

    /**
     * Digs through the xml file to get the particular configuration items necessary to
     * run this responder transport.
     *
     * @param xmlConfigFilename the name of the xml configuration file
     * @return an XMLConfiguration object
     * @throws ProbeHandlerConfigException if something goes awry
     */
    private Properties processPropertiesFile(String xmlConfigFilename) throws ProbeHandlerConfigException, IOException {

        Properties props = new Properties();
        XMLConfiguration config;

        try {
            config = new XMLConfiguration(xmlConfigFilename);
        } catch (ConfigurationException e) {
            throw new ProbeHandlerConfigException(e.getLocalizedMessage(), e);
        }

        String host = config.getString("host");
        String port = config.getString("port", "80");
        props.put("host", host);
        props.put("port", port);


        //  Put TLS configuration here

        if (config.getString("username") != null)
            props.put("username", config.getString("username"));
        if (config.getString("password") != null)
            props.put("password", config.getString("password"));

        return props;
    }

}
