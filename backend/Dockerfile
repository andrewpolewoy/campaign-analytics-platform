FROM payara/micro:6.2024.12-jdk17

COPY target/campaign-analytics.war /opt/payara/deployments/

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/opt/payara/payara-micro.jar", "--deploy", "/opt/payara/deployments/campaign-analytics.war", "--contextroot", "/"]