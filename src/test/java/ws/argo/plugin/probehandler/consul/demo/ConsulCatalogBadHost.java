package ws.argo.plugin.probehandler.consul.demo;

import com.orbitz.consul.CatalogClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.catalog.CatalogService;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jmsimpson on 11/2/15.
 */
public class ConsulCatalogBadHost {


    public static void main(String[] args) {


        Consul consul = null;
//        try {
            consul = Consul.newClient("this-is-a-bad-host.amazonaws.com", 80);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }


        CatalogClient catalogClient = consul.catalogClient();

        ConsulResponse<Map<String,List<String>>> response;
        response = catalogClient.getServices();

        Set<String> serviceNames = response.getResponse().keySet();

        for (String serviceName : serviceNames) {
            ConsulResponse<List<CatalogService>> serviceResponse = catalogClient.getService(serviceName);
            List<CatalogService> catalogServices = serviceResponse.getResponse();

            for (CatalogService svc: catalogServices) {
                System.out.print("Service [ " + svc.getServiceName() + " ]");


            }

        }


//        for (String serviceName : serviceNames) {
//            List<String> services = response.getResponse().get(serviceName);
//            System.out.print("Service: name [ " + serviceName + " ] [ ");
//            for (String service : services) {
//                System.out.print(service + " ");
//            }
//            System.out.println("]");
//        }

    }

}
