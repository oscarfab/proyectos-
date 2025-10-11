pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'Java'
    }

    stages {
        stage('Checkout') {
            steps {
                git ' https://github.com/oscarfab/proyectos-.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'target/*.jar'
        }
    }
}