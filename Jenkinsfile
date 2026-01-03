pipeline {
    agent {
        docker {
            // JDK 21, Chrome ve ChromeDriver içeren hazır bir imaj kullanıyoruz
            image 'sormuras/maven-google-chrome:jdk-21'
            // Gradle cache'ini korumak için (isteğe bağlı, performansı artırır)
            args '-v $HOME/.gradle:/root/.gradle'
        }
    }

    environment {
        CHROME_HEADLESS = 'true'
        // Gradle'ın Docker içinde root olarak çalışmasına izin ver
        GRADLE_USER_HOME = '/root/.gradle'
    }

    stages {
        stage('Build & Setup') {
            steps {
                echo 'Building the application...'
                // Gradle wrapper'a çalıştırma izni ver
                sh 'chmod +x ./gradlew'
                sh './gradlew clean build -x test'
            }
        }

        stage('Test Scenario 1: Authentication') {
            steps {
                echo 'Running Login and Registration tests...'
                sh './gradlew test -Dspring.profiles.active=test --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.SeleniumIntegrationTest"'
            }
        }

        stage('Test Scenario 2: Core Features') {
            steps {
                echo 'Running Category, Note, and Task tests...'
                sh './gradlew test -Dspring.profiles.active=test --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.CategoryPageTest" --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.NotePageTest" --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.TaskPageTest"'
            }
        }

        stage('Test Scenario 3: Media & Events') {
            steps {
                echo 'Running Image and Event tests...'
                sh './gradlew test -Dspring.profiles.active=test --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.ImageFilePageTest" --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.EventPageTest"'
            }
        }
    }

    post {
        always {
            echo 'Archiving test results...'
            junit 'build/test-results/test/*.xml'
        }
        failure {
            echo 'Pipeline failed! Please check the logs.'
        }
        success {
            echo 'All scenarios passed successfully!'
        }
    }
}
