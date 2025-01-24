pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo 'Building the project...'
                // Compile Java code using Maven
                bat 'mvn clean compile'
            }
        }
    }
}
