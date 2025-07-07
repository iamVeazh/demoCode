package ru.norkin.server;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import monitor_v1.MonitorAPIGrpc;
import monitor_v1.Norkin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.norkin.tx.TxExecutor;
import java.util.HashMap;


public class ServerResourceImpl extends MonitorAPIGrpc.MonitorAPIImplBase {

    private static final Logger log = LoggerFactory.getLogger(ServerResourceImpl.class);
    private final TxExecutor txExecutor;

    public ServerResourceImpl(TxExecutor txExecutor) {
        this.txExecutor = txExecutor;
    }


    @Override
    public void getMinimalLoadedServerData(Norkin.Empty request, StreamObserver<Norkin.ServerDataResponse> responseObserver)  {
        try {

            ServerBalancer serverBalancer = new ServerBalancer(txExecutor);

            HashMap<Server, Long> serverMap = serverBalancer.getServersMap();
            Server server = serverBalancer.getMinimalLoadedServerFromServerMap(serverMap);

            if (server == null) {
                responseObserver.onError(Status.CANCELLED
                        .withDescription("THERE ARE NO FREE SERVERS")
                        .asRuntimeException());
                return;
            }

            Norkin.CookieContainer cookieContainer = Norkin.CookieContainer.newBuilder()
                    .setKey("10")
                    .setValue("10")
                    .build();

            Norkin.ServerDataResponse response = Norkin.ServerDataResponse.newBuilder()
                    .setIp(server.getIp())
                    .setPortUI(server.getPortUI())
                    .setPathUI(server.getPathUI())
                    .setUsername(server.getUsername())
                    .setPassword(server.getPassword())
                    .build();

            responseObserver.onNext(response);

            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("get minimal loaded server data error", e);
            responseObserver.onError(e);
        }
    }
}
