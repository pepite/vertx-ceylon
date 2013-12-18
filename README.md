# Vertx Ceylon module

A Vert.x module for running Ceylon verticles in a Vert.x server.

# Ceylon Vertx module

Vertx applications can be written in Ceylon using the Ceylon Vertx [module](https://modules.ceylon-lang.org/modules/io.vertx.ceylon).
Such applications can run natively (with `ceylon run`) or can be deployed in Vertx using this Vertx module.

## Writing a Verticle in Ceylon

    import io.vertx.ceylon { ... }
    import io.vertx.ceylon.platform { ... }

    shared class Server(Vertx vertx, Container container) satisfies Verticle(vertx, container) {

      shared actual void start() {
        print("Verticle starting")
      }

      shared actual void stop() {
        print("Verticle stopping");
      }
    }

Your verticle should be named `Server` and be present in your module root (along with `module.ceylon` file).

## Running

This module allows to run Ceylon Vertx application modules in Vertx. Assuming you created a module `my.module/1.0.0` for
your application (with `ceylon compile` or Eclipse) you can run your module from Vertx using:

    vertx run my.application/1.0.0.module

## Installation

Requires:
- valid CEYLON_HOME pointing to ceylon-1.0.0
- Vertx 2.0.2

Checkout this repository and install it:

    git clone https://github.com/vietj/vertx-ceylon
    cd vertx-ceylon
    mvn install

Edit the `$VERTX_HOME/conf/lang.properties` to register the Ceylon language `vietj~lang-ceylon~1.0-SNAPSHOT:org.vertx.ceylon.platform.impl.CeylonVerticleFactory`
with the `.module` extension (per the [language support documentation](http://vertx.io/language_support.html), two lines should be added:

    # Language run-times
    ...
    ceylon=vietj~lang-ceylon~0.1.0-SNAPSHOT:org.vertx.ceylon.platform.impl.CeylonVerticleFactory

    # Mapping of file extension to language runtime
    ...
    .module=ceylon

## Example

- checkout this project
- change working dir to `examples/verticle`
- compile `ceylon compile my.application`
- run `vertx run my.application/1.0.0.module`
