package sleuth.webmvc;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.WebFilter;

import brave.propagation.ExtraFieldPropagation;
import reactor.core.publisher.Mono;

@EnableAutoConfiguration
@CrossOrigin // So that javascript can be hosted elsewhere
public class Frontend {

  private static final Logger log = LoggerFactory.getLogger(Frontend.class);

  @Autowired WebClient webClient;

  String backendBaseUrl = System.getProperty("spring.example.backendBaseUrl", "http://localhost:9000");

  public Mono<ServerResponse> callBackend(ServerRequest request) {
    log.info("In Handler MDC : {}", MDC.getCopyOfContextMap());
    return webClient.get().uri(backendBaseUrl + "/api")
                    .retrieve().bodyToMono(String.class)
                    .flatMap(s -> ServerResponse.ok().body(BodyInserters.fromObject(s)));
  }

  @Bean WebFilter addFieldFilter() {
    return (exchange, chain) -> {
      log.info("In Filter.PRE MDC : {}", MDC.getCopyOfContextMap());

      if (MDC.get("requestId") != null) {
        ExtraFieldPropagation.set("requestId", MDC.get("requestId"));
      } else {
        ExtraFieldPropagation.set("requestId", UUID.randomUUID().toString());
      }

      return chain.filter(exchange);
    };
  }


  @Bean WebClient webClient() {
    return WebClient.builder().build();
  }

  @Bean
  public RouterFunction<ServerResponse> route() {
    return RouterFunctions.route(RequestPredicates.GET("/"), this::callBackend);
  }


  public static void main(String[] args) {
    SpringApplication.run(Frontend.class,
                          "--spring.application.name=frontend",
                          "--server.port=8081"
    );
  }
}
