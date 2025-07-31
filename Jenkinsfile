pipeline{
    agent any;
    stages{
        stage("code"){
            steps{
                git url:"https://github.com/Swayamnakshane/java_quotes_app.git", branch:"master"
            }
        }
        stage("build"){
            steps{
                sh "docker build -t jenkinsapp ."
            }
        }
        stage("deploy"){
            steps{
                sh"docker run -d --name jenkinsa -p 8000:8000 jenkinsapp:latest"
            }
        }
    }
}
