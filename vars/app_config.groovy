#!/usr/bin/env groovy
/*
def bob(String operation = 'name') {
  "docker run --rm " +
                '--env APP_PATH="`pwd`" ' +
                '--env RELEASE=${RELEASE} ' +
                "-v \"`pwd`:`pwd`\" " +
                "-v /var/run/docker.sock:/var/run/docker.sock " +
                "armdocker.rnd.ericsson.se/proj-orchestration-so/bob:1.4.0-8 ${operation}"
}
*/
def test(name) {
  echo "Hello ${name}"
}
