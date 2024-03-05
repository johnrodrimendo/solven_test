#!/bin/sh

java -Dhttp.agent=default_java_agent -jar EntityExt/target/dependency/webapp-runner.jar --path /funcionarios --enable-compression  EntityExt/target/*.war