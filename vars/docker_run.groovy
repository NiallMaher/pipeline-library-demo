#!/usr/bin/groovy
package com.cleverbuilder

def bob() {
  "docker run --rm " +
         '--env APP_PATH="`pwd`" ' +
         '--env RELEASE=true ' +
         "-v \"`pwd`:`pwd`\" " +
         "-v /var/run/docker.sock:/var/run/docker.sock " +
         "armdocker.rnd.ericsson.se/proj-orchestration-so/bob:1.4.0-8" 
}

def gradle(operation){
sh "docker run -u root --rm " +
        "-v \"`pwd`:/home/gradle/project\" " +
        "-w \"/home/gradle/project/eso_workspace/projects/oss-eso-mt\" " +
        "armdocker.rnd.ericsson.se/proj-orchestration-so/so-gradle-image ${operation}"
}

def unified_installer(eaiVersion){
sh "docker run -u root --rm " +
        "-v \"`pwd`/outputs:/root/USER_INSTALL_DIR\" " +
        "-v \"`pwd`/eso_workspace/build/distributions:/root/CUSTOMER_GPARS\" " +
        "armdocker.rnd.ericsson.se/proj-orchestration-so/eai_unified_installer:${eaiVersion}"
}

def git_cmd(operation){
sh "docker run --rm -v ${env.WORKSPACE}:/git alpine/git ${operation}"
}

def helm_cmd(operation){
sh "docker run --rm " +
        "-v ${env.WORKSPACE}/.kube/config:/root/.kube/config " +
        "-v ${env.WORKSPACE}/helm-home:/root/.helm " +
        "-v ${env.WORKSPACE}:${env.WORKSPACE} linkyard/docker-helm:2.10.0 ${operation}"
}


def kube_cmd(operation){
sh "docker run --rm " +
        "-v ${env.WORKSPACE}/.kube/config:/config/.kube/config " +
        "-v ${env.WORKSPACE}:${env.WORKSPACE} " +
        "-e KUBECONFIG=/config/.kube/config lachlanevenson/k8s-kubectl:v1.11.2 ${operation}"
}
