#LANDING
web: java -Dhttp.agent=default_java_agent $JAVA_OPTS -jar Landing/target/dependency/webapp-runner.jar --port $PORT --enable-compression --compressable-mime-types text/html,text/xml,text/plain,text/css,text/javascript,text/json,image/png,image/jpeg,font/woff,font/woff2,application/x-javascript,application/javascript,application/json $TOMCAT_AUTH Landing/target/*.war
#ACQUISITION
web: java -Dhttp.agent=default_java_agent $JAVA_OPTS -jar Acquisition/target/dependency/webapp-runner.jar --port $PORT --enable-compression --compressable-mime-types text/html,text/xml,text/plain,text/css,text/javascript,text/json,image/png,image/jpeg,font/woff,font/woff2,application/x-javascript,application/javascript,application/json $TOMCAT_AUTH Acquisition/target/*.war
#COMPANYEXT
web: java -Dhttp.agent=default_java_agent $JAVA_OPTS -jar CompanyExt/target/dependency/webapp-runner.jar --port $PORT --path /company --enable-compression --compressable-mime-types text/html,text/xml,text/plain,text/css,text/javascript,text/json,image/png,image/jpeg,font/woff,font/woff2,application/x-javascript,application/javascript,application/json $TOMCAT_AUTH CompanyExt/target/*.war
#ENTITYEXT
web: java -Dhttp.agent=default_java_agent $JAVA_OPTS -jar EntityExt/target/dependency/webapp-runner.jar --port $PORT --path /funcionarios --enable-compression --compressable-mime-types text/html,text/xml,text/plain,text/css,text/javascript,text/json,image/png,image/jpeg,font/woff,font/woff2,application/x-javascript,application/javascript,application/json $TOMCAT_AUTH EntityExt/target/*.war
#BACKOFFICE
web: java $JAVA_OPTS -jar Backoffice/target/dependency/webapp-runner.jar --port $PORT --context-xml Backoffice/target/dependency/apache-context.xml --enable-compression --compressable-mime-types text/html,text/xml,text/plain,text/css,text/javascript,text/json,image/png,image/jpeg,font/woff,font/woff2,application/x-javascript,application/javascript,application/json Backoffice/target/*.war
#JOB
worker:    sh Job/target/bin/worker
schedule:  sh Schedule/target/bin/worker
