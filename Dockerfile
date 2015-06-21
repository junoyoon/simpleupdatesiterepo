FROM maven
MAINTAINER Merkushev Kirill (github:lanwen)

ENV JUSEPPE_BASE_DIR        /juseppe
ENV JUSEPPE_PLUGINS_DIR     ${JUSEPPE_BASE_DIR}/plugins
ENV JUSEPPE_SAVE_TO_DIR     ${JUSEPPE_BASE_DIR}/json
ENV JUSEPPE_JSON_NAME       update-center.json
ENV JUSEPPE_BASE_URI        http://localhost:8080

RUN mkdir ${JUSEPPE_BASE_DIR} \
    && mkdir ${JUSEPPE_PLUGINS_DIR} \
    && mkdir ${JUSEPPE_SAVE_TO_DIR}
    
ADD . ${JUSEPPE_BASE_DIR}
WORKDIR ${JUSEPPE_BASE_DIR}

RUN ["mvn", "package"]

EXPOSE 8080

CMD java -jar -Dupdate.center.plugins.dir=${JUSEPPE_PLUGINS_DIR} -Dupdate.center.saveto.dir=${JUSEPPE_SAVE_TO_DIR} -Dupdate.center.json.name=${JUSEPPE_JSON_NAME} -Dupdate.center.baseurl=${JUSEPPE_BASE_URI} target/juseppe.jar