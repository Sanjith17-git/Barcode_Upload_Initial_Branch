# Use an official OpenJDK 21 image
FROM eclipse-temurin:21-jdk
 
# Set working directory inside container
WORKDIR /app
 
# Copy WAR file from local build context to container
#To Change the war and path
COPY {ApiName.war} /app/{APIName.war}
 
# Expose the port your application listens on
EXPOSE {port}
 
# Run the WAR file
CMD ["java", "-jar", "Barcode_Generator.war"]