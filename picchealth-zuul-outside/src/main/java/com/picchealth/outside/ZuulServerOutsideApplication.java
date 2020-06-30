package com.picchealth.outside;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
@EnableApolloConfig
public class ZuulServerOutsideApplication {

    public static void main(String[] args) {
        SpringApplication.run ( ZuulServerOutsideApplication.class, args);
    }

}