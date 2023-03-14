FROM icr.io/appcafe/open-liberty:full-java8-openj9-ubi as server-setup
COPY /target/mym2.zip /config/
USER root
RUN apt-get update \
    && apt-get install -y --no-install-recommends unzip
RUN unzip /config/mym2.zip && \
    mv /wlp/usr/servers/mym2Server/* /config/ && \
    rm -rf /config/wlp && \
    rm -rf /config/mym2.zip
USER 1001

FROM icr.io/appcafe/open-liberty:full-java8-openj9-ubi
LABEL maintainer="Graham Charters" vendor="IBM" github="https://github.com/OpenLiberty"
COPY --from=server-setup /config/ /config/
EXPOSE 9080 9443
