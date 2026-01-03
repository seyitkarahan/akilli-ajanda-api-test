pipeline {
    agent any

    environment {
        CHROME_HEADLESS = 'true'
    }

    stages {
        stage('Setup Environment') {
            steps {
                script {
                    echo "Checking for Google Chrome..."
                    // Check if Chrome is installed
                    def chromeLocation = sh(script: 'which google-chrome || which google-chrome-stable || echo "not_found"', returnStdout: true).trim()

                    if (chromeLocation == 'not_found') {
                        echo "Google Chrome not found. Attempting to install..."
                        // Try to install Chrome (this might fail if not root, but it's worth a try or at least logging the need)
                        try {
                            sh '''
                                if [ "$(id -u)" -eq 0 ]; then
                                    apt-get update && apt-get install -y wget gnupg ca-certificates
                                    wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /usr/share/keyrings/google-chrome-keyring.gpg
                                    echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-chrome-keyring.gpg] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list
                                    apt-get update && apt-get install -y google-chrome-stable
                                else
                                    echo "----------------------------------------------------------------"
                                    echo "WARNING: Chrome is missing and current user is not root."
                                    echo "Please install 'Google Chrome' on the Jenkins server manually,"
                                    echo "OR install the 'Docker Pipeline' and 'Docker' plugins in Jenkins"
                                    echo "to allow running tests inside a container."
                                    echo "----------------------------------------------------------------"
                                fi
                            '''
                        } catch (Exception e) {
                            echo "Failed to install Chrome: ${e.message}"
                        }
                    } else {
                        echo "Google Chrome found at ${chromeLocation}"
                        sh "${chromeLocation} --version"
                    }
                }
            }
        }

        stage('Build & Setup') {
            steps {
                echo 'Building the application...'
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
