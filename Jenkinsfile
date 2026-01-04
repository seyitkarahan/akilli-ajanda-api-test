pipeline {
    agent any

    triggers {
            githubPush()
    }

    environment {
        CHROME_HEADLESS = 'true'
    }

    stages {

        stage('1- Checkout Source Code') {
            steps {
                echo 'Pulling source code from GitHub...'
                checkout scm
            }
        }

        stage('2- Build Application') {
            steps {
                echo 'Building the application...'
                sh 'chmod +x ./gradlew'
                sh './gradlew clean build -x test'
            }
        }

        stage('3- Unit Tests') {
            steps {
                echo 'Running Unit Tests...'
                sh './gradlew test --tests "*ServiceTest"'
            }
        }


        stage('5- Integration Tests') {
            steps {
                echo 'Running Integration Tests...'
                sh './gradlew test --tests "*IntegrationTest"'
            }
        }

        stage('6.1- System Test: Authentication') {
            steps {
                echo 'Running Authentication Selenium Tests...'
                sh './gradlew test --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.SeleniumIntegrationTest"'
            }
        }

        stage('6.2- System Test: Core Features') {
            steps {
                echo 'Running Core Feature Selenium Tests...'
                sh '''
                ./gradlew test \
                --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.CategoryPageTest" \
                --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.NotePageTest" \
                --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.TaskPageTest"
                '''
            }
        }

        stage('6.3- System Test: Media & Events') {
            steps {
                echo 'Running Media and Event Selenium Tests...'
                sh '''
                ./gradlew test \
                --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.ImageFilePageTest" \
                --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.EventPageTest"
                '''
            }
        }
    }

    post {
        always {
            echo 'Archiving test reports...'
            junit 'build/test-results/test/*.xml'
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Please review logs.'
        }
    }
}

