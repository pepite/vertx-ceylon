import ceylon.collection { ... }
import io.vertx.ceylon { Vertx }

"Run the module `mymodule`."
shared Object run() {
    
    print("HELLO FROM CEYLON ``HashMap()``");
    
    Vertx vertx = Vertx();
    print("Vertx ``vertx``");
    
    
    
    return vertx;
    
}