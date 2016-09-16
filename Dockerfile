FROM clojure:alpine

RUN apk add --update git && \
    rm -rf /var/cache/apk

VOLUME ["/etc/iplant/de"]

WORKDIR /usr/src/app

COPY project.clj /usr/src/app/
RUN lein deps

COPY conf/main/logback.xml /usr/src/app/
COPY . /usr/src/app

RUN lein uberjar && \
    cp target/notification-agent-standalone.jar .

RUN ln -s "/usr/bin/java" "/bin/notification-agent"

ENTRYPOINT ["notification-agent", "-Dlogback.configurationFile=/etc/iplant/de/logging/notificationagent-logging.xml", "-cp", ".:notification-agent-standalone.jar", "notification_agent.core"]
CMD ["--help"]

ARG git_commit=unknown
ARG version=unknown

LABEL org.cyverse.git-ref="$git_commit"
LABEL org.cyverse.version="$version"
