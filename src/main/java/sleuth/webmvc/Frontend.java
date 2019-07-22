package sleuth.webmvc;

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

import reactor.core.publisher.Mono;

@EnableAutoConfiguration
@CrossOrigin // So that javascript can be hosted elsewhere
public class Frontend {

  @Autowired WebClient webClient;

  String backendBaseUrl = System.getProperty("spring.example.backendBaseUrl", "http://localhost:9000");

  public Mono<ServerResponse> callBackend(ServerRequest request) {
    return webClient.get().uri(backendBaseUrl + "/api")
                    .retrieve().bodyToMono(String.class)
                    .flatMap(s -> ServerResponse.ok().body(BodyInserters.fromObject(s)));
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
