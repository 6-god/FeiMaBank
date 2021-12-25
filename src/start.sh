rm -rf ./*.class
javac -cp ./mysql-connector-java-8.0.26/mysql-connector-java-8.0.26.jar ./*.java -Xlint
java -cp ./mysql-connector-java-8.0.26/mysql-connector-java-8.0.26.jar:./ Main
