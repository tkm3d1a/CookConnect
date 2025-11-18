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
            log.debug("===== EUREKA PEER STATUS =====");
            log.debug("Number of peers: {}",peers.size());

            for (PeerEurekaNode peer : peers) {
                log.debug("Peer: {}",peer.getServiceUrl());

                try {
                    Field targetHostField = peer.getClass().getDeclaredField("targetHost");
                    targetHostField.setAccessible(true);
                    String targetHost = (String) targetHostField.get(peer);
                    log.debug("\tTarget host: {}",targetHost);

                    Field batchingField = peer.getClass().getDeclaredField("batchingDispatcher");
                    batchingField.setAccessible(true);
                    Object batchingDispatcher = batchingField.get(peer);
                    log.debug("\tBatching dispatcher: {}",batchingDispatcher != null ? "Active" : "Inactive");
                } catch (Exception e) {
                    log.debug("Unable to inspect internal state: {}",e.getMessage());
                }
            }
            log.debug("==============================");
        }
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 30000)
    @ConditionalOnBooleanProperty(
            name = "tkforgeworks.diagnostics.peer.registration.debugging",
            havingValue = true,
            matchIfMissing = false
    )
    public void checkEurekaRegistrations() {
        log.debug("===== EUREKA INSTANCE CHECK [Port: {}] =====", serverPort);

        if (serverContext == null || serverContext.getRegistry() == null) {
            log.debug("Registry not available");
            return;
        }

        try {
            Application eurekaApp = serverContext.getRegistry().getApplication("EUREKA");
            if (eurekaApp == null) {
                log.warn("WARNING: No EUREKA application registered!");
                log.warn("This is why replicas show as unavailable!");
            } else {
                log.debug("EUREKA application found with {} instances",eurekaApp.getInstances().size());
                for (InstanceInfo instance : eurekaApp.getInstances()) {
                    log.debug("\tInstance: {}",instance.getInstanceId());
                    log.debug("\t\t- Host: {}:{}", instance.getHostName(),instance.getPort());
                    log.debug("\t\t- Status: {}",instance.getStatus());
                    log.debug("\t\t- Home URL: {}",instance.getHomePageUrl());

                    // Check if this matches a peer URL
                    String instanceUrl = "http://" + instance.getHostName() + ":" + instance.getPort() + "/eureka/";
                    boolean matchesPeer = false;
                    for (PeerEurekaNode peer : serverContext.getPeerEurekaNodes().getPeerEurekaNodes()) {
                        String peerUrl = peer.getServiceUrl();
                        if (peerUrl.equals(instanceUrl)) {
                            matchesPeer = true;
                            log.debug("\t\t- MATCHES PEER URL: {}",peerUrl);
                            break;
                        }
                    }
                    if (!matchesPeer) {
                        log.debug("\t\t- WARNING: Does not match any peer URL!");
                    }
                }
            }

        } catch (Exception e) {
            log.debug("Error checking peer registrations: {}",e.getMessage());
        }

        log.debug("==================================================\n");
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 45000)
    @ConditionalOnBooleanProperty(
            name = "tkforgeworks.diagnostics.registration.debugging",
            havingValue = true,
            matchIfMissing = false
    )
    public void checkAllRegisteredApplications() {
        log.debug("===== EUREKA REGISTRATION CHECK [Eureka Port: {}] =====", serverPort);

        if (serverContext == null || serverContext.getRegistry() == null) {
            log.warn("Registry not available");
            return;
        }

        try {
            log.debug("All registered applications:");
            for (Application app : serverContext.getRegistry().getSortedApplications()) {
                log.debug("\t- {} ({} instances)",app.getName(),app.getInstances().size());
            }

        } catch (Exception e) {
            log.error("Error checking registrations: {}",e.getMessage());
        }

        log.debug("==================================================\n");
    }
}
