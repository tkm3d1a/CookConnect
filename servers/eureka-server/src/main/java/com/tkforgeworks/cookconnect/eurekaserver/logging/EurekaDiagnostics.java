package com.tkforgeworks.cookconnect.eurekaserver.logging;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.eureka.EurekaServerContext;
import com.netflix.eureka.cluster.PeerEurekaNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class EurekaDiagnostics {
    private final EurekaServerContext serverContext;

    @Value("${server.port}")
    private int serverPort;

    @Scheduled(fixedDelay = 60000, initialDelay = 15000)
    @ConditionalOnBooleanProperty(
            name = "tkforgeworks.diagnostics.peer.debugging",
            havingValue = true,
            matchIfMissing = false
    )
    public void logPeerStatus() {
        if (serverContext != null) {
            List<PeerEurekaNode> peers = serverContext.getPeerEurekaNodes().getPeerEurekaNodes();
            log.info("===== EUREKA PEER STATUS =====");
            log.info("Number of peers: {}",peers.size());

            for (PeerEurekaNode peer : peers) {
                log.info("Peer: {}",peer.getServiceUrl());

                try {
                    Field targetHostField = peer.getClass().getDeclaredField("targetHost");
                    targetHostField.setAccessible(true);
                    String targetHost = (String) targetHostField.get(peer);
                    log.info("\tTarget host: {}",targetHost);

                    Field batchingField = peer.getClass().getDeclaredField("batchingDispatcher");
                    batchingField.setAccessible(true);
                    Object batchingDispatcher = batchingField.get(peer);
                    log.info("\tBatching dispatcher: {}",batchingDispatcher != null ? "Active" : "Inactive");
                } catch (Exception e) {
                    log.info("Unable to inspect internal state: {}",e.getMessage());
                }
            }
            log.info("==============================");
        }
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 30000)
    @ConditionalOnBooleanProperty(
            name = "tkforgeworks.diagnostics.peer.registration.debugging",
            havingValue = true,
            matchIfMissing = false
    )
    public void checkEurekaRegistrations() {
        log.info("===== EUREKA INSTANCE CHECK [Port: {}] =====", serverPort);

        if (serverContext == null || serverContext.getRegistry() == null) {
            log.info("Registry not available");
            return;
        }

        try {
            Application eurekaApp = serverContext.getRegistry().getApplication("EUREKA");
            if (eurekaApp == null) {
                log.warn("WARNING: No EUREKA application registered!");
                log.warn("This is why replicas show as unavailable!");
            } else {
                log.info("EUREKA application found with {} instances",eurekaApp.getInstances().size());
                for (InstanceInfo instance : eurekaApp.getInstances()) {
                    log.info("\tInstance: {}",instance.getInstanceId());
                    log.info("\t\t- Host: {}:{}", instance.getHostName(),instance.getPort());
                    log.info("\t\t- Status: {}",instance.getStatus());
                    log.info("\t\t- Home URL: {}",instance.getHomePageUrl());

                    // Check if this matches a peer URL
                    String instanceUrl = "http://" + instance.getHostName() + ":" + instance.getPort() + "/eureka/";
                    boolean matchesPeer = false;
                    for (PeerEurekaNode peer : serverContext.getPeerEurekaNodes().getPeerEurekaNodes()) {
                        String peerUrl = peer.getServiceUrl();
                        if (peerUrl.equals(instanceUrl)) {
                            matchesPeer = true;
                            log.info("\t\t- MATCHES PEER URL: {}",peerUrl);
                            break;
                        }
                    }
                    if (!matchesPeer) {
                        log.info("\t\t- WARNING: Does not match any peer URL!");
                    }
                }
            }

        } catch (Exception e) {
            log.info("Error checking peer registrations: {}",e.getMessage());
        }

        log.info("==================================================\n");
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 45000)
    @ConditionalOnBooleanProperty(
            name = "tkforgeworks.diagnostics.registration.debugging",
            havingValue = true,
            matchIfMissing = false
    )
    public void checkAllRegisteredApplications() {
        log.info("===== EUREKA REGISTRATION CHECK [Eureka Port: {}] =====", serverPort);

        if (serverContext == null || serverContext.getRegistry() == null) {
            log.info("Registry not available");
            return;
        }

        try {
            log.info("All registered applications:");
            for (Application app : serverContext.getRegistry().getSortedApplications()) {
                log.info("\t- {} ({} instances)",app.getName(),app.getInstances().size());
            }

        } catch (Exception e) {
            log.info("Error checking registrations: {}",e.getMessage());
        }

        log.info("==================================================\n");
    }
}
