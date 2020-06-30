FROM harbortest.picchealth.com/hb-jczx-tyfwzt/java:1.8
VOLUME /tmp
ADD hfms-eureka-server-1.0.0.jar /hfms-eureka-server.jar
RUN bash -c 'touch /hfms-eureka-server.jar'
EXPOSE 9091
ENTRYPOINT ["java","-Dsun.net.inetaddr.ttl=60","-Dsun.net.inetaddr.negative.ttl=60","-jar","/hfms-eureka-server.jar"]