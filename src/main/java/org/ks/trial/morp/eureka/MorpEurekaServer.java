package org.ks.trial.morp.eureka;

import org.ks.trial.morp.eureka.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.ks.trial.morp.eureka.constants.Constants.HEROKU_SWAGGER_UI_URL;
import static org.ks.trial.morp.eureka.constants.Constants.SWAGGER_UI_URL;

@SpringBootApplication
@EnableEurekaServer
@EnableFeignClients
public class MorpEurekaServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MorpEurekaServer.class);

    public static void main(String[] args) {
        SpringApplication.run(MorpEurekaServer.class, args);
    }

    @Bean
    ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        return new ProtobufHttpMessageConverter();
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.ks.trial.morp"))
                .build();
    }

    @Autowired
    private Environment environment;

    @EventListener(ApplicationReadyEvent.class)
    public void firedUpAllCylinders() {
        String host = Constants.LOCALHOST;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            LOGGER.error("Failed to obtain ip address. ", e);
        }
        String port = environment.getProperty(Constants.LOCAL_SPRING_PORT);
        String herokuHost = environment.getProperty("spring.application.heroku");
        LOGGER.info("Eureka service is up now! Expected Heroku Swagger running on: {}, exact url: {}",
                String.format(HEROKU_SWAGGER_UI_URL, herokuHost, port),
                String.format(SWAGGER_UI_URL, host, port));
    }
}
