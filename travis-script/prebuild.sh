#!/bin/bash
set -ex
git clone --depth=1 --branch=ee9-experimental https://github.com/jimma/jboss-jakarta-jaxrs-api_spec.git $TRAVIS_HOME/jboss-jakarta-jaxrs-api_spec
pushd $TRAVIS_HOME/jboss-jakarta-jaxrs-api_spec/jaxrs-api
mvn clean install -DskipTests=true
popd

git clone --depth=1 --branch=main https://github.com/jimma/optimus.git $TRAVIS_HOME/optimus
pushd $TRAVIS_HOME/optimus
mvn clean install -DskipTests=true
popd

git clone --depth=1 --branch=2.0.SP1 https://github.com/jimma/microprofile-rest-client.git $TRAVIS_HOME/microprofile-rest-client
pushd $TRAVIS_HOME/microprofile-rest-client
mvn clean install -DskipTests=true
popd

git clone --depth=1 --branch=master https://github.com/wildfly/wildfly.git $TRAVIS_HOME/wildfly
pushd $TRAVIS_HOME/wildfly
mvn clean install -DskipTests=true
popd
