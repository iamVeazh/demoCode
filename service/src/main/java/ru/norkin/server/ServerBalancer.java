package ru.norkin.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.norkin.tx.TxExecutor;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerBalancer {

    private final TxExecutor txExecutor;
    private static final Logger log = LoggerFactory.getLogger(ServerBalancer.class);
    private HashMap<Server, Long> serverMap = new HashMap<>();
    private static final int TIMEOUT = 1000;

    public ServerBalancer(TxExecutor txExecutor) {
        this.txExecutor = txExecutor;
    }

    public HashMap<Server, Long> getServersMap() {
        try {
            return txExecutor.callInNewTx(em -> {
                List<Server> servers = em.createQuery("select s from Server s").getResultList();

                for (Server server : servers) {
                    serverMap.put(server, (Long) em.createQuery("select count(u) from User u where u.server.id = ?1").setParameter(1, server.getId()).getSingleResult());
                }

                return serverMap;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Server getMinimalLoadedServerFromServerMap(HashMap<Server, Long> serverMap) {

        if(serverMap.isEmpty()) {
            log.error("THERE ARE NO SERVERS TO ADD");
            return null;
        }

        log.info("get minimal loaded server data for user");

        Server server = serverMap.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new RuntimeException("Map is empty"));

        if (ping(server.getIp())) {
            log.info("Server " + server.getIp() + " is ready for use");
            return server;
        }
        log.error("ALERT " + server.getIp() + " IS DOWN");
        serverMap.remove(server);
        return getMinimalLoadedServerFromServerMap(serverMap);
    }

    private static boolean ping(String addr) {
        try {
            InetAddress address = InetAddress.getByName(addr);
            return address.isReachable(TIMEOUT);
        } catch (IOException exc){
            return false;
        }
    }

}
