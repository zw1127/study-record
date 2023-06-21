package cn.javastudy.springboot.webflux.functional;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import cn.javastudy.springboot.webflux.employee.Employee;
import cn.javastudy.springboot.webflux.employee.EmployeeRepository;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Configuration
public class EmployeeFunctionalConfig {

    @Bean
    EmployeeRepository employeeRepository() {
        return new EmployeeRepository();
    }

    @Bean
    RouterFunction<ServerResponse> getAllEmployeesRoute() {
        return route(GET("/employees"), this::allEmployees);
    }

    @Bean
    RouterFunction<ServerResponse> getEmployeeByIdRoute() {
        return route(GET("/employees/{id}"), this::employeeById);
    }

    @Bean
    RouterFunction<ServerResponse> updateEmployeeRoute() {
        return route(POST("/employees/update"), this::employeeUpdate);
    }

    @Bean
    RouterFunction<ServerResponse> composedRoutes() {
        return RouterFunctions.route()
            .GET("/employees", this::allEmployees)
            .GET("/employees/{id}", this::employeeById)
            .POST("/employees/update", this::employeeUpdate)
            .build();
    }

    private Mono<ServerResponse> allEmployees(ServerRequest request) {
        return ServerResponse.ok().body(employeeRepository().findAllEmployees(), Employee.class);
    }

    private Mono<ServerResponse> employeeById(ServerRequest request) {
        return ServerResponse.ok()
            .body(employeeRepository().findEmployeeById(request.pathVariable("id")), Employee.class);
    }

    private Mono<ServerResponse> employeeUpdate(ServerRequest request) {
        return request.bodyToMono(Employee.class)
            .doOnNext(employeeRepository()::updateEmployee)
            .then(ServerResponse.ok().build());
    }

    @Bean
    RouterFunction<ServerResponse> helloRoute() {
//        return RouterFunctions.route(RequestPredicates.GET("/hello"), this::helloWorld);
        return RouterFunctions.route().GET("/hello", this::helloWorld).build();
    }

    private Mono<ServerResponse> helloWorld(ServerRequest request) {
        String time = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'Z").format(ZonedDateTime.now());
        String helloworld = "Hello world!" + time;
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(helloworld);
    }
}
