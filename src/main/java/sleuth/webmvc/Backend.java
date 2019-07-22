package sleuth.webmvc;

import java.util.Date;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@EnableAutoConfiguration
@RestController
public class Backend {

  public Mono<ServerResponse> printDate(ServerRequest request) {
    String dateString = new Date().toString();
    return ServerResponse.ok().body(BodyInserters.fromObject(dateString));
  }

  @Bean
  public RouterFunction<ServerResponse> route() {
    return RouterFunctions.route(RequestPredicates.GET("/api"), this::printDate);
  }


  public static void main(String[] args) {
    SpringApplication.run(Backend.class,
                          "--spring.application.name=backend",
                          "--server.port=9000"
    );
  }
}
