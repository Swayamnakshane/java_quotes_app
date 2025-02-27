@Library('shared') _
pipeline{
    agent {label 'devil'}
    stages{
        stage("code"){
            steps {
                script{
                    clone ("https://github.com/Swayamnakshane/java_quotes_app.git","master")
                }
                
            }
        }
        stage("test"){
            steps {
                script{
                    trivy ()
                }
            }
        }
        stage("build"){
            steps {
                script{
                    build ("demoapp")
                }
            }
        }
        stage("push to docker"){
            steps {
                script{
                    dockerpush ("dockerhubcred","demoapp")
                }
            }
        }
        stage("deploy"){
            steps{
                script{
                    dockerrun ()
                }
            }
        }
    }
}
