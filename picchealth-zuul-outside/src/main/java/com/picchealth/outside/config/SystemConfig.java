package com.picchealth.outside.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author:lijx
 * @ClassName:
 * @version:
 * @remark:
 */
@Configuration
public class SystemConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");//允许向该服务器提交请求的URI，*表示全部允许
        corsConfiguration.setAllowCredentials(true);// 允许cookies跨域
        corsConfiguration.addAllowedHeader("*");// 允许访问的头信息,*表示全部
        corsConfiguration.addAllowedMethod("*");//允许提交请求的方法，*表示全部允许，也可以单独设置GET、PUT等
        //corsConfiguration.setMaxAge(18000L);// 预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再预检了

        //获取签名放开跨域
        //urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**/picchealthappapi/api/api-server/picchealth/vx/getEpidemicSignature/epidemicSignature", corsConfiguration);

        CorsFilter corsFilter = new CorsFilter(urlBasedCorsConfigurationSource);
        return corsFilter;
    }

}
