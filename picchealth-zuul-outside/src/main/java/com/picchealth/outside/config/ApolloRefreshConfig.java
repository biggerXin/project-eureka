package com.picchealth.outside.config;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author by zhangbr
 * @date 2020/5/9.
 */
@Slf4j
@Component
public class ApolloRefreshConfig implements ApplicationContextAware {

    /**
     * spring控制器
     */
    private ApplicationContext applicationContext;

    @Value("${apollo.bootstrap.namespaces}")
    private String[] namespaces;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext){
        this.applicationContext = applicationContext;
    }

    /**
     * 启动时增加监听
     */
    @PostConstruct
    public void addRefreshListener() {
        for (String namespace : namespaces) {
            Config config = ConfigService.getConfig(namespace);
            //对namespace增加监听方法
            config.addChangeListener(changeEvent -> {
                for (String key : changeEvent.changedKeys()) {
                    log.info("**************刷新Apollo配置:{}**************", changeEvent.getChange(key));
                }
                //将变动的配置刷新到应用中
                this.applicationContext.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));
            });
        }
    }
}