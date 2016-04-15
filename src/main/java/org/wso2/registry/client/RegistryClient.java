package org.wso2.registry.client;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.pagination.PaginationContext;
import org.wso2.carbon.registry.ws.client.registry.WSRegistryServiceClient;
import org.wso2.registry.client.util.ConfigHolder;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

public class RegistryClient {

    private static Registry wsRegistryServiceClient;
    private static List<String> escapingPaths;

    private void init() throws AxisFault, RegistryException {

        System.setProperty("javax.net.ssl.trustStore", ConfigHolder.getInstance().getProperty("TRUST.STORE.LOCATION"));
        System.setProperty("javax.net.ssl.trustStorePassword", ConfigHolder.getInstance().getProperty("TRUST.STORE" +
                                                                                                      ".PASSWORD"));
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
        System.setProperty("carbon.repo.write.mode", "true");

        ConfigurationContext configContext = ConfigurationContextFactory.createConfigurationContextFromFileSystem
                (ConfigHolder.getInstance().getProperty("AXIS2.REPO"), ConfigHolder.getInstance().getProperty("AXIS2" +
                                                                                                              ".CONF"));
        wsRegistryServiceClient = new WSRegistryServiceClient(ConfigHolder.getInstance().getProperty("GREG.URL"),
                                                              ConfigHolder.getInstance().getProperty("GREG.USERNAME")
                , ConfigHolder.getInstance().getProperty("GREG.PASSWORD"), configContext);

        if(StringUtils.isNotBlank(ConfigHolder.getInstance().getProperty("ESCAPE.PATHS"))) {
            escapingPaths = Arrays.asList(ConfigHolder.getInstance().getProperty("ESCAPE.PATHS").split(","));
        }
    }

    public static void main(String[] args) {

        RegistryClient registryClient = new RegistryClient();
        try {
            registryClient.init();
        } catch (AxisFault axisFault) {
            System.out.println("Error occurred while creating axis2 context.");
            axisFault.printStackTrace();
            System.exit(-1);
        } catch (RegistryException e) {
            System.out.println("Error occurred while connecting to the G-REG server.");
            e.printStackTrace();
            System.exit(-1);
        }

        try {
            registryClient.copyContent(ConfigHolder.getInstance().getProperty("FROM.PATH"),
                                       ConfigHolder.getInstance().getProperty("FROM.PATH"),
                                       ConfigHolder.getInstance().getProperty("TO.PATH"));
        } catch (RegistryException e) {
            System.out.println("Error occurred while copying content.");
            e.printStackTrace();
            System.exit(-1);
        } finally {
            PaginationContext.destroy();
            try {
                ((WSRegistryServiceClient) wsRegistryServiceClient).logut();
            } catch (RemoteException | LogoutAuthenticationExceptionException e) {
                System.out.println("Error occurred while logging out from the G-REG server.");
                e.printStackTrace();
                System.exit(-1);
            }
        }

        System.exit(0);
    }

    private void copyContent(String currentPath, String fromPath, String toPath) throws RegistryException {

        System.out.println("Checking - " + currentPath);
        if(escapingPaths != null && escapingPaths.contains(currentPath)) {
            System.out.println("Escaping - " + currentPath);
            return;
        }
        Object object = wsRegistryServiceClient.get(currentPath).getContent();
        if (object instanceof String[]) {
            for (String path : (String[]) object) {
                copyContent(path, fromPath, toPath);
            }
        } else if (object instanceof byte[] || object == null) {
            System.out.println("Copying - " + currentPath);
            wsRegistryServiceClient.copy(currentPath, currentPath.replace(fromPath, toPath));
        } else {
            System.out.println("Unknown content in the path - " + currentPath);
        }
    }

}
