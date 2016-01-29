package ws.argo.plugin.probehandler.consul.demo;

import com.orbitz.consul.AgentClient;
import com.orbitz.consul.CatalogClient;
import com.orbitz.consul.Consul;

/**
 * Created by jmsimpson on 11/2/15.
 */
public class ConsulDeregisterServiceDemo {


    public static void main(String[] args) {

        Consul consul = Consul.newClient("ec2-54-85-63-205.compute-1.amazonaws.com", 80);
        AgentClient agentClient = consul.agentClient();


        String serviceName = "MyService2";
        String serviceId = "8";


        agentClient.register(8080, 30000L, serviceName, serviceId); // registers with a TTL of 3 seconds

        // check in with Consul, serviceId required only.  client will prepend "service:" for service level checks.
//        try {
//            agentClient.pass(serviceId);
//        } catch (NotRegisteredException e) {
//            e.printStackTrace();
//        }


    }


}
