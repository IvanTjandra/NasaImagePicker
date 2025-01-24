pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo 'Building the project...'
                // Compile Java code using Maven
                sh 'mvn clean compile'
            }
        }
        stage('Test') {
            steps {
                echo 'Running tests...'
                // Run unit tests using Maven
                sh 'mvn test'
            }
        }
    }
}
