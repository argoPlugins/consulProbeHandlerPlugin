package ws.argo.plugin.probehandler.consul.demo;

import com.orbitz.consul.CatalogClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.catalog.CatalogService;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jmsimpson on 11/2/15.
 */
public class ConsulCatalogServiceQuery {


    public static void main(String[] args) {


        Consul consul = Consul.newClient("ec2-54-85-63-205.compute-1.amazonaws.com", 80);



        CatalogClient catalogClient = consul.catalogClient();

        ConsulResponse<Map<String,List<String>>> response;
        response = catalogClient.getServices();

        Map<String,List<String>> services = response.getResponse();

        Set<String> serviceTypes = response.getResponse().keySet();

        for (String serviceName : serviceTypes) {
            System.out.println("Service Type [ " + serviceName + " ]");

        }

       for (String serviceName : serviceTypes) {
            ConsulResponse<List<CatalogService>> serviceResponse = catalogClient.getService(serviceName);
            List<CatalogService> catalogServices = serviceResponse.getResponse();

            for (CatalogService svc: catalogServices) {

                System.out.println("Service [ " + svc.getServiceName() + " ]");


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
