pipeline {
    agent any

    tools {
        // Jenkins'te "Global Tool Configuration" altında JDK 21'in kurulu ve adının 'jdk-21' olduğundan emin olun.
        // Eğer farklı bir isim verdiyseniz burayı güncelleyin.
        jdk 'jdk-21'
    }

    environment {
        // Testlerin CI ortamında kararlı çalışması için bazı ayarlar
        CHROME_HEADLESS = 'true'
    }

    stages {
        stage('Build & Setup') {
            steps {
                echo 'Building the application...'
                // Testleri çalıştırmadan sadece projeyi derle
                sh './gradlew clean build -x test'
            }
        }

        stage('Test Scenario 1: Authentication') {
            steps {
                echo 'Running Login and Registration tests...'
                // Sadece SeleniumIntegrationTest sınıfını çalıştır
                sh './gradlew test --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.SeleniumIntegrationTest"'
            }
        }

        stage('Test Scenario 2: Core Features') {
            steps {
                echo 'Running Category, Note, and Task tests...'
                // Birden fazla test sınıfını aynı anda çalıştır
                sh './gradlew test --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.CategoryPageTest" --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.NotePageTest" --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.TaskPageTest"'
            }
        }

        stage('Test Scenario 3: Media & Events') {
            steps {
                echo 'Running Image and Event tests...'
                sh './gradlew test --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.ImageFilePageTest" --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.EventPageTest"'
            }
        }
    }

    post {
        always {
            echo 'Archiving test results...'
            // Test sonuçlarını Jenkins arayüzünde göster
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
