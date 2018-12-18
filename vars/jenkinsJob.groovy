def call() {
    // Execute build pipeline job
    build_pipeline()
}

def build_pipeline() {

        parameters {
            string(name: 'SETTINGS_CONFIG_FILE_NAME', defaultValue: 'maven.settings.eso')
            string(name: 'BOB_VERSION', defaultValue: '1.4.0-8')
            string(name: 'SLAVE', defaultValue: 'so_slave')
        }

        environment {
            bob = "docker run --rm " +
                    '--env APP_PATH="`pwd`" ' +
                    '--env RELEASE=${RELEASE} ' +
                    "-v \"`pwd`:`pwd`\" " +
                    "-v /var/run/docker.sock:/var/run/docker.sock " +
                    "armdocker.rnd.ericsson.se/proj-orchestration-so/bob:${BOB_VERSION}"
        }

        stages {
            stage('Inject Settings.xml File') {
                steps {
                    configFileProvider([configFile(fileId: "${env.SETTINGS_CONFIG_FILE_NAME}", targetLocation: "${env.WORKSPACE}")]) {
                    }
                }
            }
            stage('Clean') {
                steps {
                    sh "${bob} clean"
                    sh 'git clean -xdff --exclude=.m2 --exclude=.sonar --exclude=settings.xml'
                }
            }
            stage('Lint') {
                steps {
                    sh "${bob} lint"
                }
            }
            stage('Build package and execute tests') {
                steps {
                    sh "${bob} build"
                }
            }
            stage('Build image and chart') {
                steps {
                    sh "${bob} image"
                }
            }
            stage('SonarQube full analysis') {
                when {
                    expression { params.RELEASE == "true" }
                }
                steps {
                    sh "${bob} sonar"
                }
            }
            stage('Push image and chart') {
                when {
                    expression { params.RELEASE == "true" }
                }
                steps {
                    sh "${bob} push"
                }
            }
            stage('Generate input for ESO staging') {
                when {
                    expression { params.RELEASE == "true" }
                }
                steps {
                    sh "${bob} generate-output-parameters"
                    archiveArtifacts 'artifact.properties'
                }
            }
        }

        post {
            always {
                archive "**/target/surefire-reports/*"
                junit '**/target/surefire-reports/*.xml'
                step([$class: 'JacocoPublisher'])
            }
        }
    }

