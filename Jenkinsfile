pipeline {
   environment{
        registryProject='registry.gitlab.com/afrikpay1/gulfcam-backend'
        IMAGE="${registryProject}"
        dockerImage = ""
   }

   agent any

   stages {
      stage('Clone') {
         steps {
            checkout scm
            sh "mkdir -p reports"
         }
      }

      stage('Tests'){
          steps{
              script{
                  catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE'){
                    sh "touch reports/report_tests.html || exit && ./gradlew clean test"   
                  }
              }
          }

          post {
            always {
                // publish html
                publishHTML target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: false,
                        keepAll: true,
                        reportDir: 'reports',
                        reportFiles: 'report_tests.html',
                        reportName: 'Tests Report'
                    ]
            }
         }
      }

      stage('Build'){
         steps{
            //move to gradle production
            sh 'rm -f build.gradle && mv build_production.gradle build.gradle'

            //remove application.properties and use application.yml if using application.yml
            sh 'rm -f src/main/resources/application.properties'
            sh 'mv src/main/resources/application_production.properties src/main/resources/application.properties'

            sh 'chmod -Rf 777 .'

            sh "./gradlew bootJar"
         }

         post {
            always {
               //Archive inside artifact
               archiveArtifacts 'build/libs/*.jar'
            }
         }
      }

      stage('SonarQube analysis'){
         environment {
               SCANNER_HOME = tool 'sonarqube'
         }

         steps{
            catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE'){
               withSonarQubeEnv(installationName: 'sonar server', credentialsId: 'jenkins-user-on-sonarqube'){
                  sh '''./gradlew sonarqube \
                              -Dsonar.login=$LoginSonar \
                              -Dsonar.password=$PasswordSonar \
                              -Dsonar.projectKey=$ProjectKey \
                              -Dsonar.projectName=$ProjetName \
                              -Dsonar.projectVersion=${BUILD_NUMBER}-${GIT_COMMIT_SHORT} \
                              -Dorg.gradle.warning.mode=all \
                              --warning-mode=all'''
               }
            }
         }
      }

      stage('Build docker image'){
         steps{
            script {                            
               docker.withTool('docker'){
                  dockerImage = docker.build("$IMAGE", '.')
               }
            }
         }
      }

      stage('Push in registry'){
         steps{
            script{

              docker.withRegistry('https://registry.gitlab.com', 'gitlab-credential') {
                  dockerImage.push("$BUILD_NUMBER")
                  dockerImage.push 'latest'
               }
            }
         }
      }

      stage('Deploy on dev server'){
         steps{
            sshagent(credentials : ['connect-ssh-dev-server']) {
               sh 'ssh -o StrictHostKeyChecking=no -t $USERNAME@$SSH_HOST  "docker pull ${registryProject}:${BUILD_NUMBER} && docker ps -q --filter name=$APPLICATION_NAME | grep -q . && docker stop $APPLICATION_NAME || true && docker rm $APPLICATION_NAME || true && docker run -p 9009:8080 -d --restart=always --name $APPLICATION_NAME --network=local-network ${registryProject}:${BUILD_NUMBER}"'
            }
         }
      }
   }
}


