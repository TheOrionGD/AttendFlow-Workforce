# PowerShell script to run the Attendance Management System

# Prerequisites:
# - Install XAMPP (for MySQL) at D:\XAMPP
# - Install Apache Tomcat at D:\XAMPP\tomcat
# - Ensure Java JDK and Maven are installed

# Step 1: Start MySQL (via XAMPP)
Write-Host "Starting MySQL..."
Start-Process "D:\XAMPP\xampp-control.exe"
# Wait for user to start MySQL manually
Read-Host "Press Enter after starting MySQL in XAMPP Control Panel"

# Set up database
Write-Host "Setting up database..."
Get-Content scripts/init_db.sql | & "D:\XAMPP\mysql\bin\mysql.exe" -u root -p
Write-Host "Database setup complete."

# Step 2: Set up Maven
Write-Host "Setting up Maven..."
# Assuming Maven is installed manually at C:\maven or in PATH
Write-Host "Maven ready."

# Build the project
Write-Host "Building the project..."
mvn clean compile
mvn package
Write-Host "Build complete."

# Step 3: Deploy to Tomcat
Write-Host "Deploying to Tomcat..."
$tomcatPath = "D:\XAMPP\tomcat"
$env:CATALINA_HOME = $tomcatPath
Copy-Item "target\attendance-management-system-1.0.0.war" "$tomcatPath\webapps\"
Write-Host "WAR file deployed. Start Tomcat manually from XAMPP Control Panel."
Write-Host "Access at http://localhost:8080/attendance-management-system-1.0.0/"