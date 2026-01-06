pipeline {
    agent any

    triggers {
        githubPush()
    }


    environment {
        CHROME_HEADLESS = 'true'
        PATH = "/usr/local/bin:/opt/homebrew/bin:/Applications/Docker.app/Contents/Resources/bin:${env.PATH}"
    }

    options {
        timestamps()
        timeout(time: 40, unit: 'MINUTES')
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
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    sh './gradlew test --tests "com.seyitkarahan.akilli_ajanda_api.service.*"'
                }
            }
            post {
                always {
                    junit 'build/test-results/test/*.xml'
                }
            }
        }

        stage('4- Integration Tests') {
            steps {
                echo 'Running Integration Tests...'
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    sh './gradlew test --tests "com.seyitkarahan.akilli_ajanda_api.integration.*"'
                }
            }
            post {
                always {
                    junit 'build/test-results/test/*.xml'
                }
            }
        }

        stage('5- Start System with Docker') {
            steps {
                echo 'Starting system using Docker Compose...'
                sh '''
                    docker compose down -v || true
                    docker compose up -d --build

                    echo "Waiting for backend to be ready..."
                    for i in {1..60}; do
                        # Use curl with retry and timeout for a more reliable health check
                        if curl --retry 5 --retry-delay 2 --connect-timeout 5 -sf http://localhost:8081/login; then
                            echo "Backend is ready!"
                            break
                        fi
                        echo "Backend not ready yet. Retrying..."
                        sleep 5
                    done
                '''
            }
        }

        stage('6.1- Selenium System Test: Authentication') {
            steps {
                echo 'Running Authentication Selenium Tests...'
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    sh './gradlew test --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.SeleniumIntegrationTest"'
                }
            }
            post {
                always {
                    junit 'build/test-results/test/*.xml'
                }
            }
        }

        stage('6.2- Selenium System Test: Category Features') {
            steps {
                echo 'Running Category Selenium Tests...'
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    sh './gradlew test --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.CategoryPageTest"'
                }
            }
            post {
                always {
                    junit 'build/test-results/test/*.xml'
                }
            }
        }

        stage('6.3- Selenium System Test: Note Features') {
            steps {
                echo 'Running Note Selenium Tests...'
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    sh './gradlew test --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.NotePageTest"'
                }
            }
            post {
                always {
                    junit 'build/test-results/test/*.xml'
                }
            }
        }

        stage('6.4- Selenium System Test: Task Features') {
            steps {
                echo 'Running Task Selenium Tests...'
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    sh './gradlew test --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.TaskPageTest"'
                }
            }
            post {
                always {
                    junit 'build/test-results/test/*.xml'
                }
            }
        }

        stage('6.5- Selenium System Test: Event Features') {
            steps {
                echo 'Running Event Selenium Tests...'
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    sh './gradlew test --tests "com.seyitkarahan.akilli_ajanda_api.seleniumTest.EventPageTest"'
                }
            }
            post {
                always {
                    junit 'build/test-results/test/*.xml'
                }
            }
        }

        stage('7- Coverage Report') {
            steps {
                echo 'Generating JaCoCo Coverage Report...'
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    sh './gradlew jacocoTestReport || echo "JaCoCo report generation failed"'
                }
            }
            post {
                always {
                    echo 'HTML Publisher plugin is not installed. Skipping HTML report publishing.'
                }
            }
        }
    }

    post {
        always {
            echo 'Stopping Docker services...'
            sh 'docker compose down || true'
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline completed with some failures (tests may have failed).'
        }
    }
}