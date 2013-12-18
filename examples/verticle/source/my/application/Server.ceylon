import io.vertx.ceylon.platform { ... }
import io.vertx.ceylon { Vertx, Registration }
import io.vertx.ceylon.http { HttpServer, HttpServerRequest }
import ceylon.promises { Promise }
import io.vertx.ceylon.eventbus { Message }

shared class Server(Vertx vertx, Container container) extends Verticle(vertx, container) {
    
    shared actual void start() {
        print("Starting verticle");

        void handle(HttpServerRequest req) {
            req.response.
                    contentType("text/html").
                    end("<html><body>
                         <h1>Hello from Vert.x with Ceylon!</h1>
                         
                         <h2>Method</h2>
                         <p>``req.method``</p>
                         <h2>Path</h2>               
                         <p>``req.path``</p>
                         <h2>Headers</h2>
                         <p>``req.headers``</p>
                         <h2>Parameters</h2>
                         <p>``req.parameters``</p>
                         <h2>Query parameter</h2>
                         <p>``req.queryParameters``</p>
                         <h2>Form parameters</h2>
                         <p>``req.formParameters else {}``</p>
                                                                                                           
                         <form action='/post' method='POST'>
                         <input type='text' name='foo'>
                         <input type='submit'>
                         </form>
                                                                                                                                               
                         </body></html>").close();

            vertx.eventBus.send("foo", "Request ``req.path`` from ``req.remoteAddress.address``");
        }
        
        // Bind http server
        value server = vertx.createHttpServer();
        Promise<HttpServer> http = server.requestHandler(handle).listen(8080);
        http.then_((HttpServer arg) => print("Http server bound on 8080"));
        
        // Register event bus for logging messages
        Registration registration = vertx.eventBus.registerHandler("foo", (Message<String> msg) => print(msg.body));
        registration.completed.then_((Null arg) => print("Event handler registered"));

    }
    
    shared actual void stop() {
        print("Stopping verticle");
    }
}
