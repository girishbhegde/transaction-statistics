package com.n26.test.transaction.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ghegde on 5/6/17.
 */
public class ApplicationServer extends AbstractVerticle{
    private static Logger LOGGER = LoggerFactory.getLogger(ApplicationServer.class);
    private static int port = 8080;

    @Override
    public void start() throws Exception {
        LOGGER.debug("Application Starting");
        //deploy the verticles
        vertx.deployVerticle(StatisticsVerticle.class.getCanonicalName());
        vertx.deployVerticle(TransactionVerticle.class.getCanonicalName());
        vertx.deployVerticle(QueueRotationVerticle.class.getCanonicalName());
        LOGGER.debug("Verticles deployed");

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        //add router for transaction
        router.post("/transactions").handler(routingContext -> {
            JsonObject input = routingContext.getBodyAsJson();
            vertx.eventBus().send(TransactionVerticle.TRANSACTION_VERTICLE, Json.encode(input), res -> {
                Message<Object> result = res.result();
                if (res.failed()) {
                    routingContext.response().setStatusCode(500);
                } else if (result.body().equals("NO_CONTENT")) {
                    routingContext.response().setStatusCode(204);
                } else {
                    routingContext.response().setStatusCode(201);
                }
                routingContext.response().end();
            });
        });

        //add router for statistics
        router.get("/statistics").handler(routingContext -> {
            vertx.eventBus().send(StatisticsVerticle.STATISTICS_VERTICLE, "", res->{
                Message<Object> result = res.result();
                if (res.failed() || result == null) {
                    routingContext.response().setStatusCode(500);
                }else{
                    routingContext.response().setStatusCode(200);
                    routingContext.response().end(result.body().toString());
                }
            });
        });

        //start http server
        HttpServer server  = vertx.createHttpServer();
        server.requestHandler(router::accept);
        server.listen(port);
        LOGGER.info("Application Started");
    }

    public static void main(String[] args) {
        //read port
        if(args.length==1){
            try {
                port = Integer.parseInt(args[0]);
            }catch(NumberFormatException e){
                System.out.println("please provide a numeric port number");
            }
        }

        //start vertx and deploy first verticle
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(ApplicationServer.class.getCanonicalName());
    }

}
