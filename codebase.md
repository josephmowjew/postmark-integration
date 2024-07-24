# Stop-DevEnv.ps1

```ps1
# Stop Spring Boot application
Write-Host "Stopping Spring Boot application..."
Stop-Process -Name "java" -ErrorAction SilentlyContinue

# Stop Kafka
Write-Host "Stopping Kafka..."
docker-compose down
```

# Start-DevEnv.ps1

```ps1
# Start Kafka
Write-Host "Starting Kafka..."
docker-compose up -d

# Wait for Kafka to be ready
Write-Host "Waiting for Kafka to be ready..."
Start-Sleep -Seconds 10

# Create Kafka topics
Write-Host "Creating Kafka topics..."
docker-compose exec kafka kafka-topics --create --topic new_client_alert --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
docker-compose exec kafka kafka-topics --create --topic crm_update --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
docker-compose exec kafka kafka-topics --create --topic triage_request --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1

# Start Spring Boot application using Maven Wrapper
Write-Host "Starting Spring Boot application..."
.\mvnw spring-boot:run
```

# README.md

```md
# Email Microservice

## Overview
This email microservice is designed to handle client communications and system notifications for LyvePulse. It uses an event-driven architecture with Apache Kafka for message processing and Postmark for email delivery.

## Features
- Automated email address generation for new clients
- Processing of client-related events via Kafka topics
- Sending of welcome emails and other notifications using Postmark
- Integration with GuerrillaMail for temporary email address generation (development only)

## Architecture
- Java 17
- Spring Boot 3.2.2
- Apache Kafka for event streaming
- Postmark API for email delivery
- Docker for local Kafka setup

## Prerequisites
- Java Development Kit (JDK) 17
- Docker and Docker Compose
- Maven (or use the included Maven Wrapper)
- PowerShell (for running scripts on Windows)

## Setup

### 1. Clone the repository
\`\`\`
git clone https://github.com/josephmowjew/postmark-integration.git
cd email-microservice
\`\`\`

### 2. Configure environment variables
Create a `.env` file in the project root and add the following:
\`\`\`
POSTMARK_API_TOKEN=your_postmark_api_token
POSTMARK_FROM_EMAIL=your_sender_email@example.com
\`\`\`

### 3. Start the development environment
Run the following PowerShell script:
\`\`\`
.\Start-DevEnv.ps1
\`\`\`
This script will:
- Start Kafka using Docker Compose
- Create necessary Kafka topics
- Run the Spring Boot application

## Usage

### Kafka Topics
- `new_client_alert`: Notifies of new client registrations
- `crm_update`: Handles CRM update events
- `triage_request`: Manages triage requests for incoming emails

### API Endpoints
- POST /api/webhook/email-event: Handles email events from Postmark
- POST /api/email/send-2fa: Sends two-factor authentication emails
- POST /api/email/send-welcome: Sends welcome emails to new clients

## Development Workflow

1. Make changes to the codebase
2. Run tests: `.\mvnw test`
3. Start the development environment: `.\Start-DevEnv.ps1`
4. Stop the development environment: `.\Stop-DevEnv.ps1`

## Testing
- Unit tests: `.\mvnw test`
- Integration tests: Ensure Docker is running, then `.\mvnw verify`

## Important Notes
- The current implementation uses GuerrillaMail for temporary email address generation. This is not suitable for production use.
- TODO: Replace GuerrillaMail with a production-ready email service before deployment.

## Troubleshooting
- If Kafka fails to start, ensure Docker is running and ports 29092 and 22181 are available.
- For Postmark-related issues, verify your API token and sender email in the `.env` file.

## Contributing
Please read CONTRIBUTING.md for details on our code of conduct and the process for submitting pull requests.

## License
This project is licensed under the MIT License - see the LICENSE.md file for details.
```

# pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.2.2</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.qubedcare</groupId>
	<artifactId>postmark-integration</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>postmark-integration</name>
	<description>Email microservice for LyvePulse</description>
	<properties>
		<java.version>17</java.version>
		<testcontainers.version>1.17.6</testcontainers.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.kafka</groupId>
			<artifactId>spring-kafka</artifactId>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springdoc</groupId>
			<artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
			<version>2.3.0</version>
		</dependency>
		<dependency>
            <groupId>com.postmarkapp</groupId>
            <artifactId>postmark</artifactId>
            <version>1.10.0</version>
        </dependency>
		
		
		<!-- Test dependencies -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.kafka</groupId>
			<artifactId>spring-kafka-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${testcontainers.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>kafka</artifactId>
            <version>${testcontainers.version}</version>
            <scope>test</scope>
        </dependency>
		<dependency>
			<groupId>org.awaitility</groupId>
			<artifactId>awaitility</artifactId>
			<version>4.2.0</version>
			<scope>test</scope>
		</dependency>
		<dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
					<excludes>
						<exclude>
							<groupId>org.projectlombok</groupId>
							<artifactId>lombok</artifactId>
						</exclude>
					</excludes>
				</configuration>
			</plugin>
		</plugins>
	</build>

</project>
```

# mvnw.cmd

```cmd
<# : batch portion
@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.2
@REM
@REM Optional ENV vars
@REM   MVNW_REPOURL - repo url base for downloading maven distribution
@REM   MVNW_USERNAME/MVNW_PASSWORD - user and password for downloading maven
@REM   MVNW_VERBOSE - true: enable verbose log; others: silence the output
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_PSMODULEP_SAVE=%PSModulePath%
@SET PSModulePath=
@FOR /F "usebackq tokens=1* delims==" %%A IN (`powershell -noprofile "& {$scriptDir='%~dp0'; $script='%__MVNW_ARG0_NAME__%'; icm -ScriptBlock ([Scriptblock]::Create((Get-Content -Raw '%~f0'))) -NoNewScope}"`) DO @(
  IF "%%A"=="MVN_CMD" (set __MVNW_CMD__=%%B) ELSE IF "%%B"=="" (echo %%A) ELSE (echo %%A=%%B)
)
@SET PSModulePath=%__MVNW_PSMODULEP_SAVE%
@SET __MVNW_PSMODULEP_SAVE=
@SET __MVNW_ARG0_NAME__=
@SET MVNW_USERNAME=
@SET MVNW_PASSWORD=
@IF NOT "%__MVNW_CMD__%"=="" (%__MVNW_CMD__% %*)
@echo Cannot start maven from wrapper >&2 && exit /b 1
@GOTO :EOF
: end batch / begin powershell #>

$ErrorActionPreference = "Stop"
if ($env:MVNW_VERBOSE -eq "true") {
  $VerbosePreference = "Continue"
}

# calculate distributionUrl, requires .mvn/wrapper/maven-wrapper.properties
$distributionUrl = (Get-Content -Raw "$scriptDir/.mvn/wrapper/maven-wrapper.properties" | ConvertFrom-StringData).distributionUrl
if (!$distributionUrl) {
  Write-Error "cannot read distributionUrl property in $scriptDir/.mvn/wrapper/maven-wrapper.properties"
}

switch -wildcard -casesensitive ( $($distributionUrl -replace '^.*/','') ) {
  "maven-mvnd-*" {
    $USE_MVND = $true
    $distributionUrl = $distributionUrl -replace '-bin\.[^.]*$',"-windows-amd64.zip"
    $MVN_CMD = "mvnd.cmd"
    break
  }
  default {
    $USE_MVND = $false
    $MVN_CMD = $script -replace '^mvnw','mvn'
    break
  }
}

# apply MVNW_REPOURL and calculate MAVEN_HOME
# maven home pattern: ~/.m2/wrapper/dists/{apache-maven-<version>,maven-mvnd-<version>-<platform>}/<hash>
if ($env:MVNW_REPOURL) {
  $MVNW_REPO_PATTERN = if ($USE_MVND) { "/org/apache/maven/" } else { "/maven/mvnd/" }
  $distributionUrl = "$env:MVNW_REPOURL$MVNW_REPO_PATTERN$($distributionUrl -replace '^.*'+$MVNW_REPO_PATTERN,'')"
}
$distributionUrlName = $distributionUrl -replace '^.*/',''
$distributionUrlNameMain = $distributionUrlName -replace '\.[^.]*$','' -replace '-bin$',''
$MAVEN_HOME_PARENT = "$HOME/.m2/wrapper/dists/$distributionUrlNameMain"
if ($env:MAVEN_USER_HOME) {
  $MAVEN_HOME_PARENT = "$env:MAVEN_USER_HOME/wrapper/dists/$distributionUrlNameMain"
}
$MAVEN_HOME_NAME = ([System.Security.Cryptography.MD5]::Create().ComputeHash([byte[]][char[]]$distributionUrl) | ForEach-Object {$_.ToString("x2")}) -join ''
$MAVEN_HOME = "$MAVEN_HOME_PARENT/$MAVEN_HOME_NAME"

if (Test-Path -Path "$MAVEN_HOME" -PathType Container) {
  Write-Verbose "found existing MAVEN_HOME at $MAVEN_HOME"
  Write-Output "MVN_CMD=$MAVEN_HOME/bin/$MVN_CMD"
  exit $?
}

if (! $distributionUrlNameMain -or ($distributionUrlName -eq $distributionUrlNameMain)) {
  Write-Error "distributionUrl is not valid, must end with *-bin.zip, but found $distributionUrl"
}

# prepare tmp dir
$TMP_DOWNLOAD_DIR_HOLDER = New-TemporaryFile
$TMP_DOWNLOAD_DIR = New-Item -Itemtype Directory -Path "$TMP_DOWNLOAD_DIR_HOLDER.dir"
$TMP_DOWNLOAD_DIR_HOLDER.Delete() | Out-Null
trap {
  if ($TMP_DOWNLOAD_DIR.Exists) {
    try { Remove-Item $TMP_DOWNLOAD_DIR -Recurse -Force | Out-Null }
    catch { Write-Warning "Cannot remove $TMP_DOWNLOAD_DIR" }
  }
}

New-Item -Itemtype Directory -Path "$MAVEN_HOME_PARENT" -Force | Out-Null

# Download and Install Apache Maven
Write-Verbose "Couldn't find MAVEN_HOME, downloading and installing it ..."
Write-Verbose "Downloading from: $distributionUrl"
Write-Verbose "Downloading to: $TMP_DOWNLOAD_DIR/$distributionUrlName"

$webclient = New-Object System.Net.WebClient
if ($env:MVNW_USERNAME -and $env:MVNW_PASSWORD) {
  $webclient.Credentials = New-Object System.Net.NetworkCredential($env:MVNW_USERNAME, $env:MVNW_PASSWORD)
}
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
$webclient.DownloadFile($distributionUrl, "$TMP_DOWNLOAD_DIR/$distributionUrlName") | Out-Null

# If specified, validate the SHA-256 sum of the Maven distribution zip file
$distributionSha256Sum = (Get-Content -Raw "$scriptDir/.mvn/wrapper/maven-wrapper.properties" | ConvertFrom-StringData).distributionSha256Sum
if ($distributionSha256Sum) {
  if ($USE_MVND) {
    Write-Error "Checksum validation is not supported for maven-mvnd. `nPlease disable validation by removing 'distributionSha256Sum' from your maven-wrapper.properties."
  }
  Import-Module $PSHOME\Modules\Microsoft.PowerShell.Utility -Function Get-FileHash
  if ((Get-FileHash "$TMP_DOWNLOAD_DIR/$distributionUrlName" -Algorithm SHA256).Hash.ToLower() -ne $distributionSha256Sum) {
    Write-Error "Error: Failed to validate Maven distribution SHA-256, your Maven distribution might be compromised. If you updated your Maven version, you need to update the specified distributionSha256Sum property."
  }
}

# unzip and move
Expand-Archive "$TMP_DOWNLOAD_DIR/$distributionUrlName" -DestinationPath "$TMP_DOWNLOAD_DIR" | Out-Null
Rename-Item -Path "$TMP_DOWNLOAD_DIR/$distributionUrlNameMain" -NewName $MAVEN_HOME_NAME | Out-Null
try {
  Move-Item -Path "$TMP_DOWNLOAD_DIR/$MAVEN_HOME_NAME" -Destination $MAVEN_HOME_PARENT | Out-Null
} catch {
  if (! (Test-Path -Path "$MAVEN_HOME" -PathType Container)) {
    Write-Error "fail to move MAVEN_HOME"
  }
} finally {
  try { Remove-Item $TMP_DOWNLOAD_DIR -Recurse -Force | Out-Null }
  catch { Write-Warning "Cannot remove $TMP_DOWNLOAD_DIR" }
}

Write-Output "MVN_CMD=$MAVEN_HOME/bin/$MVN_CMD"

```

# mvnw

```
#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.3.2
#
# Optional ENV vars
# -----------------
#   JAVA_HOME - location of a JDK home dir, required when download maven via java source
#   MVNW_REPOURL - repo url base for downloading maven distribution
#   MVNW_USERNAME/MVNW_PASSWORD - user and password for downloading maven
#   MVNW_VERBOSE - true: enable verbose log; debug: trace the mvnw script; others: silence the output
# ----------------------------------------------------------------------------

set -euf
[ "${MVNW_VERBOSE-}" != debug ] || set -x

# OS specific support.
native_path() { printf %s\\n "$1"; }
case "$(uname)" in
CYGWIN* | MINGW*)
  [ -z "${JAVA_HOME-}" ] || JAVA_HOME="$(cygpath --unix "$JAVA_HOME")"
  native_path() { cygpath --path --windows "$1"; }
  ;;
esac

# set JAVACMD and JAVACCMD
set_java_home() {
  # For Cygwin and MinGW, ensure paths are in Unix format before anything is touched
  if [ -n "${JAVA_HOME-}" ]; then
    if [ -x "$JAVA_HOME/jre/sh/java" ]; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
      JAVACCMD="$JAVA_HOME/jre/sh/javac"
    else
      JAVACMD="$JAVA_HOME/bin/java"
      JAVACCMD="$JAVA_HOME/bin/javac"

      if [ ! -x "$JAVACMD" ] || [ ! -x "$JAVACCMD" ]; then
        echo "The JAVA_HOME environment variable is not defined correctly, so mvnw cannot run." >&2
        echo "JAVA_HOME is set to \"$JAVA_HOME\", but \"\$JAVA_HOME/bin/java\" or \"\$JAVA_HOME/bin/javac\" does not exist." >&2
        return 1
      fi
    fi
  else
    JAVACMD="$(
      'set' +e
      'unset' -f command 2>/dev/null
      'command' -v java
    )" || :
    JAVACCMD="$(
      'set' +e
      'unset' -f command 2>/dev/null
      'command' -v javac
    )" || :

    if [ ! -x "${JAVACMD-}" ] || [ ! -x "${JAVACCMD-}" ]; then
      echo "The java/javac command does not exist in PATH nor is JAVA_HOME set, so mvnw cannot run." >&2
      return 1
    fi
  fi
}

# hash string like Java String::hashCode
hash_string() {
  str="${1:-}" h=0
  while [ -n "$str" ]; do
    char="${str%"${str#?}"}"
    h=$(((h * 31 + $(LC_CTYPE=C printf %d "'$char")) % 4294967296))
    str="${str#?}"
  done
  printf %x\\n $h
}

verbose() { :; }
[ "${MVNW_VERBOSE-}" != true ] || verbose() { printf %s\\n "${1-}"; }

die() {
  printf %s\\n "$1" >&2
  exit 1
}

trim() {
  # MWRAPPER-139:
  #   Trims trailing and leading whitespace, carriage returns, tabs, and linefeeds.
  #   Needed for removing poorly interpreted newline sequences when running in more
  #   exotic environments such as mingw bash on Windows.
  printf "%s" "${1}" | tr -d '[:space:]'
}

# parse distributionUrl and optional distributionSha256Sum, requires .mvn/wrapper/maven-wrapper.properties
while IFS="=" read -r key value; do
  case "${key-}" in
  distributionUrl) distributionUrl=$(trim "${value-}") ;;
  distributionSha256Sum) distributionSha256Sum=$(trim "${value-}") ;;
  esac
done <"${0%/*}/.mvn/wrapper/maven-wrapper.properties"
[ -n "${distributionUrl-}" ] || die "cannot read distributionUrl property in ${0%/*}/.mvn/wrapper/maven-wrapper.properties"

case "${distributionUrl##*/}" in
maven-mvnd-*bin.*)
  MVN_CMD=mvnd.sh _MVNW_REPO_PATTERN=/maven/mvnd/
  case "${PROCESSOR_ARCHITECTURE-}${PROCESSOR_ARCHITEW6432-}:$(uname -a)" in
  *AMD64:CYGWIN* | *AMD64:MINGW*) distributionPlatform=windows-amd64 ;;
  :Darwin*x86_64) distributionPlatform=darwin-amd64 ;;
  :Darwin*arm64) distributionPlatform=darwin-aarch64 ;;
  :Linux*x86_64*) distributionPlatform=linux-amd64 ;;
  *)
    echo "Cannot detect native platform for mvnd on $(uname)-$(uname -m), use pure java version" >&2
    distributionPlatform=linux-amd64
    ;;
  esac
  distributionUrl="${distributionUrl%-bin.*}-$distributionPlatform.zip"
  ;;
maven-mvnd-*) MVN_CMD=mvnd.sh _MVNW_REPO_PATTERN=/maven/mvnd/ ;;
*) MVN_CMD="mvn${0##*/mvnw}" _MVNW_REPO_PATTERN=/org/apache/maven/ ;;
esac

# apply MVNW_REPOURL and calculate MAVEN_HOME
# maven home pattern: ~/.m2/wrapper/dists/{apache-maven-<version>,maven-mvnd-<version>-<platform>}/<hash>
[ -z "${MVNW_REPOURL-}" ] || distributionUrl="$MVNW_REPOURL$_MVNW_REPO_PATTERN${distributionUrl#*"$_MVNW_REPO_PATTERN"}"
distributionUrlName="${distributionUrl##*/}"
distributionUrlNameMain="${distributionUrlName%.*}"
distributionUrlNameMain="${distributionUrlNameMain%-bin}"
MAVEN_USER_HOME="${MAVEN_USER_HOME:-${HOME}/.m2}"
MAVEN_HOME="${MAVEN_USER_HOME}/wrapper/dists/${distributionUrlNameMain-}/$(hash_string "$distributionUrl")"

exec_maven() {
  unset MVNW_VERBOSE MVNW_USERNAME MVNW_PASSWORD MVNW_REPOURL || :
  exec "$MAVEN_HOME/bin/$MVN_CMD" "$@" || die "cannot exec $MAVEN_HOME/bin/$MVN_CMD"
}

if [ -d "$MAVEN_HOME" ]; then
  verbose "found existing MAVEN_HOME at $MAVEN_HOME"
  exec_maven "$@"
fi

case "${distributionUrl-}" in
*?-bin.zip | *?maven-mvnd-?*-?*.zip) ;;
*) die "distributionUrl is not valid, must match *-bin.zip or maven-mvnd-*.zip, but found '${distributionUrl-}'" ;;
esac

# prepare tmp dir
if TMP_DOWNLOAD_DIR="$(mktemp -d)" && [ -d "$TMP_DOWNLOAD_DIR" ]; then
  clean() { rm -rf -- "$TMP_DOWNLOAD_DIR"; }
  trap clean HUP INT TERM EXIT
else
  die "cannot create temp dir"
fi

mkdir -p -- "${MAVEN_HOME%/*}"

# Download and Install Apache Maven
verbose "Couldn't find MAVEN_HOME, downloading and installing it ..."
verbose "Downloading from: $distributionUrl"
verbose "Downloading to: $TMP_DOWNLOAD_DIR/$distributionUrlName"

# select .zip or .tar.gz
if ! command -v unzip >/dev/null; then
  distributionUrl="${distributionUrl%.zip}.tar.gz"
  distributionUrlName="${distributionUrl##*/}"
fi

# verbose opt
__MVNW_QUIET_WGET=--quiet __MVNW_QUIET_CURL=--silent __MVNW_QUIET_UNZIP=-q __MVNW_QUIET_TAR=''
[ "${MVNW_VERBOSE-}" != true ] || __MVNW_QUIET_WGET='' __MVNW_QUIET_CURL='' __MVNW_QUIET_UNZIP='' __MVNW_QUIET_TAR=v

# normalize http auth
case "${MVNW_PASSWORD:+has-password}" in
'') MVNW_USERNAME='' MVNW_PASSWORD='' ;;
has-password) [ -n "${MVNW_USERNAME-}" ] || MVNW_USERNAME='' MVNW_PASSWORD='' ;;
esac

if [ -z "${MVNW_USERNAME-}" ] && command -v wget >/dev/null; then
  verbose "Found wget ... using wget"
  wget ${__MVNW_QUIET_WGET:+"$__MVNW_QUIET_WGET"} "$distributionUrl" -O "$TMP_DOWNLOAD_DIR/$distributionUrlName" || die "wget: Failed to fetch $distributionUrl"
elif [ -z "${MVNW_USERNAME-}" ] && command -v curl >/dev/null; then
  verbose "Found curl ... using curl"
  curl ${__MVNW_QUIET_CURL:+"$__MVNW_QUIET_CURL"} -f -L -o "$TMP_DOWNLOAD_DIR/$distributionUrlName" "$distributionUrl" || die "curl: Failed to fetch $distributionUrl"
elif set_java_home; then
  verbose "Falling back to use Java to download"
  javaSource="$TMP_DOWNLOAD_DIR/Downloader.java"
  targetZip="$TMP_DOWNLOAD_DIR/$distributionUrlName"
  cat >"$javaSource" <<-END
	public class Downloader extends java.net.Authenticator
	{
	  protected java.net.PasswordAuthentication getPasswordAuthentication()
	  {
	    return new java.net.PasswordAuthentication( System.getenv( "MVNW_USERNAME" ), System.getenv( "MVNW_PASSWORD" ).toCharArray() );
	  }
	  public static void main( String[] args ) throws Exception
	  {
	    setDefault( new Downloader() );
	    java.nio.file.Files.copy( java.net.URI.create( args[0] ).toURL().openStream(), java.nio.file.Paths.get( args[1] ).toAbsolutePath().normalize() );
	  }
	}
	END
  # For Cygwin/MinGW, switch paths to Windows format before running javac and java
  verbose " - Compiling Downloader.java ..."
  "$(native_path "$JAVACCMD")" "$(native_path "$javaSource")" || die "Failed to compile Downloader.java"
  verbose " - Running Downloader.java ..."
  "$(native_path "$JAVACMD")" -cp "$(native_path "$TMP_DOWNLOAD_DIR")" Downloader "$distributionUrl" "$(native_path "$targetZip")"
fi

# If specified, validate the SHA-256 sum of the Maven distribution zip file
if [ -n "${distributionSha256Sum-}" ]; then
  distributionSha256Result=false
  if [ "$MVN_CMD" = mvnd.sh ]; then
    echo "Checksum validation is not supported for maven-mvnd." >&2
    echo "Please disable validation by removing 'distributionSha256Sum' from your maven-wrapper.properties." >&2
    exit 1
  elif command -v sha256sum >/dev/null; then
    if echo "$distributionSha256Sum  $TMP_DOWNLOAD_DIR/$distributionUrlName" | sha256sum -c >/dev/null 2>&1; then
      distributionSha256Result=true
    fi
  elif command -v shasum >/dev/null; then
    if echo "$distributionSha256Sum  $TMP_DOWNLOAD_DIR/$distributionUrlName" | shasum -a 256 -c >/dev/null 2>&1; then
      distributionSha256Result=true
    fi
  else
    echo "Checksum validation was requested but neither 'sha256sum' or 'shasum' are available." >&2
    echo "Please install either command, or disable validation by removing 'distributionSha256Sum' from your maven-wrapper.properties." >&2
    exit 1
  fi
  if [ $distributionSha256Result = false ]; then
    echo "Error: Failed to validate Maven distribution SHA-256, your Maven distribution might be compromised." >&2
    echo "If you updated your Maven version, you need to update the specified distributionSha256Sum property." >&2
    exit 1
  fi
fi

# unzip and move
if command -v unzip >/dev/null; then
  unzip ${__MVNW_QUIET_UNZIP:+"$__MVNW_QUIET_UNZIP"} "$TMP_DOWNLOAD_DIR/$distributionUrlName" -d "$TMP_DOWNLOAD_DIR" || die "failed to unzip"
else
  tar xzf${__MVNW_QUIET_TAR:+"$__MVNW_QUIET_TAR"} "$TMP_DOWNLOAD_DIR/$distributionUrlName" -C "$TMP_DOWNLOAD_DIR" || die "failed to untar"
fi
printf %s\\n "$distributionUrl" >"$TMP_DOWNLOAD_DIR/$distributionUrlNameMain/mvnw.url"
mv -- "$TMP_DOWNLOAD_DIR/$distributionUrlNameMain" "$MAVEN_HOME" || [ -d "$MAVEN_HOME" ] || die "fail to move MAVEN_HOME"

clean || :
exec_maven "$@"

```

# HELP.md

```md
# Read Me First
The following was discovered as part of building this project:

* The original package name 'com.qubedcare.postmark-integration' is invalid and this project uses 'com.qubedcare.postmark_integration' instead.

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.2.7/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.2.7/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.2.7/reference/htmlsingle/index.html#web)
* [Spring for Apache Kafka](https://docs.spring.io/spring-boot/docs/3.2.7/reference/htmlsingle/index.html#messaging.kafka)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/3.2.7/reference/htmlsingle/index.html#actuator)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.


```

# docker-compose.yml

```yml
version: '3'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - 22181:2181
  
  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    ports:
      - 29092:29092
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:29092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
```

# Create-Topics.ps1

```ps1
# Wait for Kafka to be ready
Start-Sleep -Seconds 10

# Create topics
docker-compose exec kafka kafka-topics --create --topic new_client_alert --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
docker-compose exec kafka kafka-topics --create --topic crm_update --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
docker-compose exec kafka kafka-topics --create --topic triage_request --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1

Write-Host "Kafka topics created."
```

# .gitignore

```
HELP.md
target/
!.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/

### STS ###
.apt_generated
.classpath
.factorypath
.project
.settings
.springBeans
.sts4-cache

### IntelliJ IDEA ###
.idea
*.iws
*.iml
*.ipr

### NetBeans ###
/nbproject/private/
/nbbuild/
/dist/
/nbdist/
/.nb-gradle/
build/
!**/src/main/**/build/
!**/src/test/**/build/

### VS Code ###
.vscode/
# Docker volumes
kafka-data/
zookeeper-data/
```

# .vscode\settings.json

```json
{
    "java.configuration.updateBuildConfiguration": "interactive"
}
```

# .mvn\wrapper\maven-wrapper.properties

```properties
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
wrapperVersion=3.3.2
distributionType=only-script
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.7/apache-maven-3.9.7-bin.zip

```

# src\test\resources\application-test.properties

```properties
# Kafka configuration for tests
spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}
kafka.topic.new-client-alert=new_client_alert
kafka.topic.crm-update=crm_update
spring.kafka.consumer.group-id=test-consumer-group
kafka.topic.triage-request=triage_request

# Disable actual email sending for tests
email.sending.enabled=false

# Use H2 in-memory database for tests (if applicable)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# Override any other properties from the main application.properties as needed for tests
postmark.api.token=test-token
postmark.from.email=test@example.com
postmark.webhook.secret=test-secret

# Logging for tests
logging.level.org.springframework=DEBUG
logging.level.com.qubedcare.postmark_integration=DEBUG
```

# src\main\resources\application.properties

```properties
spring.application.name=postmark-integration
server.port=5050

# Logging
logging.level.root=INFO
logging.level.com.qubedcare.postmark_integration=DEBUG

# Actuator endpoints
management.endpoints.web.exposure.include=health,info,metrics

# OpenAPI Documentation
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html

# Postmark API key (use environment variable in production)
postmark.api.key=${POSTMARK_API_KEY:your-cbc98390-13b2-4f09-9676-98001aa96c50}


# Email configuration
#email.from.address=noreply@lyvepulse.com
email.from.address=joseph.mojoo@sparcsystems.africa
email.domain=lyvepulse.com
email.welcome.subject=Welcome to Our Service
email.welcome.template=Dear %s,\n\nWelcome to our service! We're excited to have you on board.

# Kafka Configuration
spring.kafka.bootstrap-servers=localhost:29092
spring.kafka.consumer.group-id=email-microservice-group
spring.kafka.consumer.auto-offset-reset=earliest

# Kafka Topics
kafka.topic.new-client-alert=new_client_alert
kafka.topic.crm-update=crm_update
kafka.topic.triage-request=triage_request

# Postmark Configuration
postmark.api.token=${POSTMARK_API_TOKEN:cbc98390-13b2-4f09-9676-98001aa96c50}
postmark.from.email=${POSTMARK_FROM_EMAIL:joseph.mojoo@sparcsystems.africa}
postmark.webhook.secret=your-webhook-secret

# GuerrillaMail Configuration (if needed)
guerrillamail.api.url=https://api.guerrillamail.com/ajax.php?f=get_email_address

spring.profiles.active=dev


```

# src\test\java\com\qubedcare\postmark_integration\WebhookIntegrationTest.java

```java
package com.qubedcare.postmark_integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qubedcare.postmark_integration.dto.DeliveryEventDTO;
import com.qubedcare.postmark_integration.dto.TaskDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "postmark.webhook.secret=testSecret"
})
@ActiveProfiles("test")
class WebhookIntegrationTest {

    @TestConfiguration
    static class KafkaTestConfig {
        @Bean
        public ProducerFactory<String, TaskDTO> producerFactory(EmbeddedKafkaBroker embeddedKafkaBroker) {
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString());
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            return new DefaultKafkaProducerFactory<>(configProps);
        }

        @Bean
        public KafkaTemplate<String, TaskDTO> kafkaTemplate(ProducerFactory<String, TaskDTO> producerFactory) {
            return new KafkaTemplate<>(producerFactory);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Value("${postmark.webhook.secret}")
    private String webhookSecret;

    @Value("${kafka.topic.triage-request}")
    private String triageRequestTopic;

    private KafkaMessageListenerContainer<String, TaskDTO> container;
    private BlockingQueue<TaskDTO> records;

    @BeforeEach
    void setUp() {
        records = new LinkedBlockingQueue<>();
        container = createContainer();
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @AfterEach
    void tearDown() {
        container.stop();
    }

    @Test
    void testDeliveryWebhook() throws Exception {
        DeliveryEventDTO deliveryEvent = new DeliveryEventDTO();
        deliveryEvent.setMessageId("test-message-id");
        // Set other necessary fields

        String payload = objectMapper.writeValueAsString(deliveryEvent);
        String signature = computeSignature(payload, webhookSecret);

        mockMvc.perform(post("/webhook/delivery")
                .content(payload)
                .header("X-Postmark-Signature", signature)
                .contentType("application/json"))
                .andExpect(status().isOk());

        TaskDTO receivedTask = records.poll(10, TimeUnit.SECONDS);
        assertNotNull(receivedTask, "Task was not received in Kafka");
        // Add more assertions as needed
    }

    private KafkaMessageListenerContainer<String, TaskDTO> createContainer() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        DefaultKafkaConsumerFactory<String, TaskDTO> cf = new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(TaskDTO.class));

        ContainerProperties containerProperties = new ContainerProperties(triageRequestTopic);
        containerProperties.setMessageListener((MessageListener<String, TaskDTO>) record -> records.add(record.value()));

        return new KafkaMessageListenerContainer<>(cf, containerProperties);
    }

    private String computeSignature(String payload, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        return Base64.getEncoder().encodeToString(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
    }
}
```

# src\test\java\com\qubedcare\postmark_integration\PostmarkIntegrationApplicationTests.java

```java
package com.qubedcare.postmark_integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PostmarkIntegrationApplicationTests {

	@Test
	void contextLoads() {
	}

}

```

# src\test\java\com\qubedcare\postmark_integration\ComprehensiveEmailMicroserviceE2ETest.java

```java
package com.qubedcare.postmark_integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qubedcare.postmark_integration.kafka.NewClientAlertConsumer;
import com.qubedcare.postmark_integration.model.Client;
import com.qubedcare.postmark_integration.service.EmailService;
import com.qubedcare.postmark_integration.service.CrmService;
import com.qubedcare.postmark_integration.dto.DeliveryEventDTO;
import com.qubedcare.postmark_integration.dto.OpenEventDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@ActiveProfiles("test")
@DirtiesContext
public class ComprehensiveEmailMicroserviceE2ETest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private EmailService emailService;

    @SpyBean
    private CrmService crmService;
    

    @Autowired
    private NewClientAlertConsumer newClientAlertConsumer;

    @Value("${kafka.topic.new-client-alert}")
    private String newClientAlertTopic;

    private CountDownLatch latch;

    @BeforeEach
    void setUp() {
        latch = new CountDownLatch(1);
        newClientAlertConsumer.setLatch(latch);
    }

    @Test
    void testCompleteEmailFlow() throws Exception {
        // 1. Set up test data
        Client client = new Client("1", "John Doe", null);
        String clientJson = objectMapper.writeValueAsString(client);

        // 2. Mock EmailService behavior
        when(emailService.generateEmailAddress(any(String.class))).thenReturn("john.doe@example.com");
        doNothing().when(emailService).sendWelcomeEmail(any(Client.class));

        // 3. Send new client alert to Kafka
        kafkaTemplate.send(newClientAlertTopic, clientJson);
        System.out.println("Sent new client alert to Kafka");

        // 4. Wait for Kafka consumer to process the message
        boolean messageProcessed = latch.await(10, TimeUnit.SECONDS);
        System.out.println("Kafka consumer processed message: " + messageProcessed);

        // 5. Verify email generation and welcome email sending
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(emailService).generateEmailAddress("John Doe");
            verify(emailService).sendWelcomeEmail(any(Client.class));
        });

        // 6. Verify CRM update
        Client updatedClient = new Client("1", "John Doe", "john.doe@example.com");
        kafkaTemplate.send("crm-update", objectMapper.writeValueAsString(updatedClient));

        // await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
        //     verify(crmService).updateClientEmail("1", "john.doe@example.com");
        // });

        // 7. Simulate delivery event
        DeliveryEventDTO deliveryEvent = new DeliveryEventDTO();
        deliveryEvent.setRecipient("john.doe@example.com");
        deliveryEvent.setMessageId("test-message-id");
        restTemplate.postForEntity("/webhook/delivery", deliveryEvent, String.class);
        System.out.println("Simulated delivery event");

        // 8. Simulate open event
        OpenEventDTO openEvent = new OpenEventDTO();
        openEvent.setRecipient("john.doe@example.com");
        openEvent.setMessageId("test-message-id");
        restTemplate.postForEntity("/webhook/open", openEvent, String.class);
        System.out.println("Simulated open event");

        // // 9. Verify event processing
        // verify(crmService, timeout(10000)).updateEmailDeliveryStatus(any(DeliveryEventDTO.class));
        // verify(crmService, timeout(10000)).updateEmailOpenStatus(eq("1"), eq("test-message-id"));
    }
}
```

# src\main\java\com\qubedcare\postmark_integration\PostmarkIntegrationApplication.java

```java
package com.qubedcare.postmark_integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PostmarkIntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostmarkIntegrationApplication.class, args);
    }
}
```

# src\test\java\com\qubedcare\postmark_integration\kafka\NewClientAlertConsumerIntegrationTest.java

```java
package com.qubedcare.postmark_integration.kafka;

import com.qubedcare.postmark_integration.model.Client;
import com.qubedcare.postmark_integration.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NewClientAlertConsumerIntegrationTest {

    @Value("${kafka.topic.new-client-alert}")
    private String topic;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @MockBean
    private EmailService emailService;

    private Producer<String, String> producer;

    @BeforeAll
    void setUp() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        producer = new DefaultKafkaProducerFactory<>(configs, new StringSerializer(), new StringSerializer()).createProducer();
    }

    @Test
    void testNewClientAlertConsumer() throws Exception {
        Client client = new Client("1", "John Doe", null);
        ObjectMapper objectMapper = new ObjectMapper();
        String clientJson = objectMapper.writeValueAsString(client);

        producer.send(new ProducerRecord<>(topic, clientJson));
        producer.flush();

        verify(emailService, timeout(5000)).generateEmailAddress(any(String.class));
        verify(emailService, timeout(5000)).sendWelcomeEmail(any(Client.class));
    }

    @AfterAll
    void tearDown() {
        if (producer != null) {
            producer.close();
        }
    }
}
```

# src\test\java\com\qubedcare\postmark_integration\kafka\KafkaConsumerIntegrationTest.java

```java
package com.qubedcare.postmark_integration.kafka;

import com.qubedcare.postmark_integration.model.Client;
import com.qubedcare.postmark_integration.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class KafkaConsumerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailService emailService;

    @Value("${kafka.topic.new-client-alert}")
    private String topic;

    @Autowired
    private ConsumerFactory<String, String> consumerFactory;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<String, String> consumer;

    @BeforeEach
    void setUp() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
        consumer = consumerFactory.createConsumer("testGroup", "");
        consumer.subscribe(Collections.singleton(topic));
        // Clear any existing messages
        KafkaTestUtils.getRecords(consumer);
        
        // Reset mocks before each test
        reset(emailService);
    }

    @Test
    void testNewClientAlertConsumer() throws Exception {
        // Arrange
        Client client = new Client("1", "John Doe", null);
        String clientJson = objectMapper.writeValueAsString(client);
        
        // Set up mock behavior
        when(emailService.generateEmailAddress(client.getName())).thenReturn("john.doe@lyvepulse.com");

        // Act
        kafkaTemplate.send(topic, clientJson);

        // Assert
        ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, topic, Duration.ofSeconds(10));
        assertNotNull(singleRecord);
        assertEquals(clientJson, singleRecord.value());

        // Verify EmailService method calls with timeout
        verify(emailService, timeout(5000)).generateEmailAddress(client.getName());
        verify(emailService, timeout(5000)).sendWelcomeEmail(any(Client.class));
    }
    
}
```

# src\test\java\com\qubedcare\postmark_integration\kafka\KafkaConnectivityTest.java

```java
package com.qubedcare.postmark_integration.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" },
               topics = {"${kafka.topic.new-client-alert}", "${kafka.topic.crm-update}", "${kafka.topic.triage-request}"})
public class KafkaConnectivityTest {

    @Autowired
    private KafkaAdmin kafkaAdmin;

    @Value("${kafka.topic.new-client-alert}")
    private String newClientAlertTopic;

    @Value("${kafka.topic.crm-update}")
    private String crmUpdateTopic;

    @Value("${kafka.topic.triage-request}")
    private String triageRequestTopic;

    @Test
    public void testKafkaConnectivity() throws ExecutionException, InterruptedException {
        AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties());
        
        Set<String> topics = adminClient.listTopics().names().get();

        assertTrue(topics.contains(newClientAlertTopic), "New client alert topic should exist");
        assertTrue(topics.contains(crmUpdateTopic), "CRM update topic should exist");
        assertTrue(topics.contains(triageRequestTopic), "Triage request topic should exist");

        adminClient.close();
    }
}
```

# src\test\java\com\qubedcare\postmark_integration\kafka\CrmUpdateConsumerTest.java

```java
package com.qubedcare.postmark_integration.kafka;

public class CrmUpdateConsumerTest {
    
}

```

# src\test\java\com\qubedcare\postmark_integration\service\WebhookServiceTest.java

```java
package com.qubedcare.postmark_integration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qubedcare.postmark_integration.dto.DeliveryEventDTO;
import com.qubedcare.postmark_integration.dto.OpenEventDTO;
import com.qubedcare.postmark_integration.dto.BounceEventDTO;
import com.qubedcare.postmark_integration.dto.TaskDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class WebhookServiceTest {

    @Mock
    private KafkaTemplate<String, TaskDTO> kafkaTemplate;

    private WebhookService webhookService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        webhookService = new WebhookService(kafkaTemplate, objectMapper);
        ReflectionTestUtils.setField(webhookService, "webhookSecret", "testSecret");
        ReflectionTestUtils.setField(webhookService, "triageRequestTopic", "testTopic");
    }
    @Test
    void verifySignature_ValidSignature_ReturnsTrue() throws Exception {
        DeliveryEventDTO payload = new DeliveryEventDTO();
        payload.setMessageId("test-message-id");
        String jsonPayload = objectMapper.writeValueAsString(payload);
        String signature = computeSignature(jsonPayload, "testSecret");

        assertTrue(webhookService.verifySignature(payload, signature));
    }

    @Test
    void verifySignature_InvalidSignature_ReturnsFalse() throws Exception {
        DeliveryEventDTO payload = new DeliveryEventDTO();
        payload.setMessageId("test-message-id");
        String invalidSignature = "invalidSignature";

        assertFalse(webhookService.verifySignature(payload, invalidSignature));
    }

    @Test
    void processDeliveryEvent_Success() {
        DeliveryEventDTO deliveryEvent = new DeliveryEventDTO();
        deliveryEvent.setMessageId("test-message-id");
        deliveryEvent.setRecipient("test@example.com");

        when(kafkaTemplate.send(eq("testTopic"), any(TaskDTO.class)))
            .thenReturn(CompletableFuture.completedFuture(null));

        assertDoesNotThrow(() -> webhookService.processDeliveryEvent(deliveryEvent));

        verify(kafkaTemplate).send(eq("testTopic"), any(TaskDTO.class));
    }

    @Test
    void processOpenEvent_Success() {
        OpenEventDTO openEvent = new OpenEventDTO();
        openEvent.setMessageId("test-message-id");
        openEvent.setRecipient("test@example.com");
        OpenEventDTO.ClientInfo clientInfo = new OpenEventDTO.ClientInfo();
        clientInfo.setName("Test Client");
        openEvent.setClient(clientInfo);

        when(kafkaTemplate.send(eq("testTopic"), any(TaskDTO.class)))
            .thenReturn(CompletableFuture.completedFuture(null));

        assertDoesNotThrow(() -> webhookService.processOpenEvent(openEvent));

        verify(kafkaTemplate).send(eq("testTopic"), any(TaskDTO.class));
    }

    @Test
    void processBounceEvent_Success() {
        BounceEventDTO bounceEvent = new BounceEventDTO();
        bounceEvent.setMessageId("test-message-id");
        bounceEvent.setEmail("test@example.com");

        when(kafkaTemplate.send(eq("testTopic"), any(TaskDTO.class)))
            .thenReturn(CompletableFuture.completedFuture(null));

        assertDoesNotThrow(() -> webhookService.processBounceEvent(bounceEvent));

        verify(kafkaTemplate).send(eq("testTopic"), any(TaskDTO.class));
    }

    @Test
    void processEvent_KafkaFailure() {
        DeliveryEventDTO deliveryEvent = new DeliveryEventDTO();
        deliveryEvent.setMessageId("test-message-id");
        deliveryEvent.setRecipient("test@example.com");

        CompletableFuture<SendResult<String, TaskDTO>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Kafka error"));

        when(kafkaTemplate.send(any(), any())).thenReturn(failedFuture);

        assertThrows(RuntimeException.class, () -> webhookService.processDeliveryEvent(deliveryEvent));
    }


    private String computeSignature(String payload, String secret) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}
```

# src\test\java\com\qubedcare\postmark_integration\service\GuerrillaMailServiceTest.java

```java
package com.qubedcare.postmark_integration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
@ActiveProfiles("test")
class GuerrillaMailServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private GuerrillaMailService guerrillaMailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        guerrillaMailService = new GuerrillaMailService(restTemplate);
    }

    @Test
    void getEmailAddress_Success() throws IOException {
        String mockResponse = "{\"email_addr\":\"test123@guerrillamail.com\"}";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

        String email = guerrillaMailService.getEmailAddress();

        assertEquals("test123@guerrillamail.com", email);
    }

    @Test
    void getEmailAddress_Failure() {
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenThrow(new RuntimeException("API Error"));

        assertThrows(IOException.class, () -> guerrillaMailService.getEmailAddress());
    }
}
```

# src\test\java\com\qubedcare\postmark_integration\service\EmailServiceTest.java

```java
package com.qubedcare.postmark_integration.service;

import com.qubedcare.postmark_integration.model.Client;
import com.qubedcare.postmark_integration.exception.EmailGenerationException;
import com.qubedcare.postmark_integration.exception.SendingFailureException;
import com.postmarkapp.postmark.client.ApiClient;
import com.postmarkapp.postmark.client.data.model.message.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("test")
class EmailServiceTest {

    @Mock
    private ApiClient postmarkClient;

    @Mock
    private GuerrillaMailService guerrillaMailService;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emailService = new EmailService(postmarkClient, guerrillaMailService);
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@example.com");
        ReflectionTestUtils.setField(emailService, "welcomeEmailSubject", "Welcome to Our Service");
        ReflectionTestUtils.setField(emailService, "welcomeEmailTemplate", "Dear %s,\n\nWelcome to our service!");
        ReflectionTestUtils.setField(emailService, "emailDomain", "lyvepulse.com");
    }

    @Test
    void generateEmailAddress_Success() throws IOException, EmailGenerationException {
        when(guerrillaMailService.getEmailAddress()).thenReturn("random123@guerrillamail.com");
        String generatedEmail = emailService.generateEmailAddress("John Doe");
        assertEquals("random123@guerrillamail.com", generatedEmail);
        verify(guerrillaMailService, times(1)).getEmailAddress();
    }

    @Test
    void generateEmailAddress_Failure() throws IOException {
        when(guerrillaMailService.getEmailAddress()).thenThrow(new IOException("API Error"));
        assertThrows(EmailGenerationException.class, () -> emailService.generateEmailAddress("John Doe"));
    }

    @Test
    void sendWelcomeEmail_Success() throws Exception {
        Client client = new Client("1", "John Doe", "john@example.com");
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
       
        when(postmarkClient.deliverMessage(any(Message.class))).thenReturn(null);
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail(client));
        verify(postmarkClient).deliverMessage(messageCaptor.capture());
        Message capturedMessage = messageCaptor.getValue();
        assertEquals("test@example.com", capturedMessage.getFrom());
        assertEquals("john@example.com", capturedMessage.getTo());
        assertEquals("Welcome to Our Service", capturedMessage.getSubject());
        assertTrue(capturedMessage.getHtmlBody().contains("Dear John Doe"));
    }

    @Test
    void sendWelcomeEmail_Failure() throws Exception {
        Client client = new Client("1", "John Doe", "john@example.com");
        when(postmarkClient.deliverMessage(any(Message.class))).thenThrow(new RuntimeException("Sending failed"));
        assertThrows(SendingFailureException.class, () -> emailService.sendWelcomeEmail(client));
    }
}
```

# src\test\java\com\qubedcare\postmark_integration\controller\WebhookControllerTest.java

```java
package com.qubedcare.postmark_integration.controller;

import com.qubedcare.postmark_integration.dto.DeliveryEventDTO;
import com.qubedcare.postmark_integration.dto.OpenEventDTO;
import com.qubedcare.postmark_integration.dto.BounceEventDTO;
import com.qubedcare.postmark_integration.service.WebhookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class WebhookControllerTest {

    @Mock
    private WebhookService webhookService;

    private WebhookController webhookController;

    private static final String SUCCESS_MESSAGE = "Event processed successfully";
    private static final String INVALID_SIGNATURE_MESSAGE = "Invalid signature";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webhookController = new WebhookController(webhookService);
    }

    @Test
    void handleDeliveryEvent_ValidSignature_ReturnsOk() {
        when(webhookService.verifySignature(any(DeliveryEventDTO.class), anyString())).thenReturn(true);
        ResponseEntity<String> response = webhookController.handleDeliveryEvent(new DeliveryEventDTO(), "validSignature");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SUCCESS_MESSAGE, response.getBody());
        verify(webhookService).processDeliveryEvent(any(DeliveryEventDTO.class));
    }

    @Test
    void handleDeliveryEvent_InvalidSignature_ReturnsBadRequest() {
        when(webhookService.verifySignature(any(DeliveryEventDTO.class), anyString())).thenReturn(false);
        ResponseEntity<String> response = webhookController.handleDeliveryEvent(new DeliveryEventDTO(), "invalidSignature");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(INVALID_SIGNATURE_MESSAGE, response.getBody());
        verify(webhookService, never()).processDeliveryEvent(any(DeliveryEventDTO.class));
    }

    @Test
    void handleOpenEvent_ValidSignature_ReturnsOk() {
        when(webhookService.verifySignature(any(OpenEventDTO.class), anyString())).thenReturn(true);
        ResponseEntity<String> response = webhookController.handleOpenEvent(new OpenEventDTO(), "validSignature");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SUCCESS_MESSAGE, response.getBody());
        verify(webhookService).processOpenEvent(any(OpenEventDTO.class));
    }

    @Test
    void handleOpenEvent_InvalidSignature_ReturnsBadRequest() {
        when(webhookService.verifySignature(any(OpenEventDTO.class), anyString())).thenReturn(false);
        ResponseEntity<String> response = webhookController.handleOpenEvent(new OpenEventDTO(), "invalidSignature");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(INVALID_SIGNATURE_MESSAGE, response.getBody());
        verify(webhookService, never()).processOpenEvent(any(OpenEventDTO.class));
    }

    @Test
    void handleBounceEvent_ValidSignature_ReturnsOk() {
        when(webhookService.verifySignature(any(BounceEventDTO.class), anyString())).thenReturn(true);
        ResponseEntity<String> response = webhookController.handleBounceEvent(new BounceEventDTO(), "validSignature");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SUCCESS_MESSAGE, response.getBody());
        verify(webhookService).processBounceEvent(any(BounceEventDTO.class));
    }

    @Test
    void handleBounceEvent_InvalidSignature_ReturnsBadRequest() {
        when(webhookService.verifySignature(any(BounceEventDTO.class), anyString())).thenReturn(false);
        ResponseEntity<String> response = webhookController.handleBounceEvent(new BounceEventDTO(), "invalidSignature");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(INVALID_SIGNATURE_MESSAGE, response.getBody());
        verify(webhookService, never()).processBounceEvent(any(BounceEventDTO.class));
    }
}

```

# src\test\java\com\qubedcare\postmark_integration\config\PostmarkConfigTest.java

```java
package com.qubedcare.postmark_integration.config;

import com.postmarkapp.postmark.client.ApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class PostmarkConfigTest {

    @Autowired
    private ApiClient postmarkClient;

    @Test
    public void testPostmarkClientInitialization() {
        assertNotNull(postmarkClient, "Postmark client should be initialized");
    }
}
```

# src\test\java\com\qubedcare\postmark_integration\model\EmailEventTest.java

```java
package com.qubedcare.postmark_integration.model;

   import org.junit.jupiter.api.Test;
   import java.util.Date;
   import static org.junit.jupiter.api.Assertions.*;

   public class EmailEventTest {

       @Test
       public void testEmailEventCreation() {
           Date now = new Date();
           EmailEvent event = new EmailEvent(EmailEventType.SENT, now, "Email sent to john@example.com");
           assertEquals(EmailEventType.SENT, event.getEventType());
           assertEquals(now, event.getTimestamp());
           assertEquals("Email sent to john@example.com", event.getEmailDetails());
       }
   }
```

# src\test\java\com\qubedcare\postmark_integration\model\ClientTest.java

```java
package com.qubedcare.postmark_integration.model;

   import org.junit.jupiter.api.Test;
   import static org.junit.jupiter.api.Assertions.*;

   public class ClientTest {

       @Test
       public void testClientCreation() {
           Client client = new Client("1", "John Doe", "john.doe@example.com");
           assertEquals("1", client.getId());
           assertEquals("John Doe", client.getName());
           assertEquals("john.doe@example.com", client.getEmailAddress());
       }

       @Test
       public void testClientEquality() {
           Client client1 = new Client("1", "John Doe", "john.doe@example.com");
           Client client2 = new Client("1", "John Doe", "john.doe@example.com");
           Client client3 = new Client("2", "Jane Doe", "jane.doe@example.com");

           assertEquals(client1, client2);
           assertNotEquals(client1, client3);
       }
   }
```

# src\main\java\com\qubedcare\postmark_integration\service\WebhookService.java

```java
package com.qubedcare.postmark_integration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qubedcare.postmark_integration.dto.DeliveryEventDTO;
import com.qubedcare.postmark_integration.dto.OpenEventDTO;
import com.qubedcare.postmark_integration.dto.BounceEventDTO;
import com.qubedcare.postmark_integration.dto.TaskDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class WebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);

    @Value("${postmark.webhook.secret}")
    private String webhookSecret;

    @Value("${kafka.topic.triage-request}")
    private String triageRequestTopic;

    private final KafkaTemplate<String, TaskDTO> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public WebhookService(KafkaTemplate<String, TaskDTO> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    

    public boolean verifySignature(Object payload, String signature) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            String computedSignature = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(jsonPayload.getBytes(StandardCharsets.UTF_8)));
            return computedSignature.equals(signature);
        } catch (Exception e) {
            logger.error("Error verifying webhook signature", e);
            return false;
        }
    }

    public void processDeliveryEvent(DeliveryEventDTO payload) {
        logger.info("Processing delivery event: {}", payload);
        TaskDTO task = createTaskFromDeliveryEvent(payload);
        sendTaskToKafka(task);
    }

    public void processOpenEvent(OpenEventDTO payload) {
        logger.info("Processing open event: {}", payload);
        TaskDTO task = createTaskFromOpenEvent(payload);
        sendTaskToKafka(task);
    }

    public void processBounceEvent(BounceEventDTO payload) {
        logger.info("Processing bounce event: {}", payload);
        TaskDTO task = createTaskFromBounceEvent(payload);
        sendTaskToKafka(task);
    }

    private TaskDTO createTaskFromDeliveryEvent(DeliveryEventDTO payload) {
        TaskDTO task = new TaskDTO();
        task.setType("Delivery");
        task.setEmailAddress(payload.getRecipient());
        task.setDetails("Email delivered at " + payload.getDeliveredAt());
        return task;
    }

    private TaskDTO createTaskFromOpenEvent(OpenEventDTO payload) {
        TaskDTO task = new TaskDTO();
        task.setType("Open");
        task.setEmailAddress(payload.getRecipient());
        String clientName = payload.getClient() != null ? payload.getClient().getName() : "Unknown";
        task.setDetails("Email opened at " + payload.getReceivedAt() + " using " + clientName);
        return task;
    }

    private TaskDTO createTaskFromBounceEvent(BounceEventDTO payload) {
        TaskDTO task = new TaskDTO();
        task.setType("Bounce");
        task.setEmailAddress(payload.getEmail());
        task.setDetails("Email bounced at " + payload.getBouncedAt() + ". Reason: " + payload.getDescription());
        return task;
    }

    private void sendTaskToKafka(TaskDTO task) {
        try {
            kafkaTemplate.send(triageRequestTopic, task).get(); // This will block and throw an exception if the send fails
            logger.info("Task sent to Kafka topic: {}", triageRequestTopic);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed to send task to Kafka topic: {}", triageRequestTopic, e);
            throw new RuntimeException("Failed to send task to Kafka", e);
        }
    }
}
```

# src\main\java\com\qubedcare\postmark_integration\service\IEmailService.java

```java
package com.qubedcare.postmark_integration.service;

import com.qubedcare.postmark_integration.model.Client;
import com.qubedcare.postmark_integration.exception.EmailGenerationException;
import com.qubedcare.postmark_integration.exception.SendingFailureException;

public interface IEmailService {
    String generateEmailAddress(String clientName) throws EmailGenerationException;
    void sendWelcomeEmail(Client client) throws SendingFailureException;
}
```

# src\main\java\com\qubedcare\postmark_integration\service\ICrmService.java

```java
package com.qubedcare.postmark_integration.service;

import com.qubedcare.postmark_integration.dto.DeliveryEventDTO;


public interface ICrmService {
    void updateClientEmail(String clientId, String emailAddress);
    void updateEmailOpenStatus(String clientId, String messageId);
    void updateEmailDeliveryStatus(DeliveryEventDTO deliveryEventDTO);
}
```

# src\main\java\com\qubedcare\postmark_integration\service\GuerrillaMailService.java

```java
package com.qubedcare.postmark_integration.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class GuerrillaMailService {

    private static final Logger logger = LoggerFactory.getLogger(GuerrillaMailService.class);
    private static final String GUERRILLA_MAIL_API = "https://api.guerrillamail.com/ajax.php?f=get_email_address";

    private final RestTemplate restTemplate;

    public GuerrillaMailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getEmailAddress() throws IOException {
        try {
            String response = restTemplate.getForObject(GUERRILLA_MAIL_API, String.class);
            // Parse the response to extract the email address
            // This is a simplified version and might need adjustment based on the actual API response
            String emailAddress = response.split("email_addr\":\"")[1].split("\"")[0];
            logger.info("Generated temporary email address: {}", emailAddress);
            return emailAddress;
        } catch (Exception e) {
            logger.error("Failed to generate temporary email address", e);
            throw new IOException("Failed to generate temporary email address", e);
        }
    }
}
```

# src\main\java\com\qubedcare\postmark_integration\service\EmailService.java

```java
package com.qubedcare.postmark_integration.service;

import com.qubedcare.postmark_integration.model.Client;
import com.qubedcare.postmark_integration.exception.EmailGenerationException;
import com.qubedcare.postmark_integration.exception.SendingFailureException;
import com.postmarkapp.postmark.client.ApiClient;
import com.postmarkapp.postmark.client.data.model.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import java.io.IOException;

@Service
public class EmailService implements IEmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final ApiClient postmarkClient;
    private final GuerrillaMailService guerrillaMailService;

    @Value("${email.from.address}")
    private String fromEmail;

    @Value("${email.welcome.subject}")
    private String welcomeEmailSubject;

    @Value("${email.welcome.template}")
    private String welcomeEmailTemplate;

    @Value("${email.domain}")
    private String emailDomain;

    public EmailService(ApiClient postmarkClient, GuerrillaMailService guerrillaMailService) {
        this.postmarkClient = postmarkClient;
        this.guerrillaMailService = guerrillaMailService;
    }

    @Override
    public String generateEmailAddress(String clientName) throws EmailGenerationException {
        try {
            // Use GuerrillaMail to generate a temporary email address
            String email = guerrillaMailService.getEmailAddress();
            logger.info("Generated email address for client {}: {}", clientName, email);
            return email;
        } catch (IOException e) {
            logger.error("Failed to generate email address for client: {}", clientName, e);
            throw new EmailGenerationException("Failed to generate email address", e);
        }

        // Future implementation (keep as comment for now):
        /*
        try {
            // Check if the input is already an email address
            if (clientName.contains("@")) {
                String[] parts = clientName.split("@");
                clientName = parts[0];
            }

            // Generate email based on client's name and domain
            String emailPrefix = clientName.toLowerCase()
                    .replaceAll("[^a-z0-9]", ".")
                    .replaceAll("\\.+", ".") // Replace multiple dots with a single dot
                    .replaceAll("^\\.|\\.$", ""); // Remove leading and trailing dots

            String email = emailPrefix + "@" + emailDomain;
           
            logger.info("Generated email address for client {}: {}", clientName, email);
            return email;
        } catch (Exception e) {
            logger.error("Failed to generate email address for client: {}", clientName, e);
            throw new EmailGenerationException("Failed to generate email address", e);
        }
        */
    }

    @Override
    @Retryable(value = SendingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendWelcomeEmail(Client client) throws SendingFailureException {
        String emailContent = String.format(welcomeEmailTemplate, client.getName());
        Message message = new Message(fromEmail, client.getEmailAddress(), welcomeEmailSubject, emailContent);
        try {
            postmarkClient.deliverMessage(message);
            logger.info("Welcome email sent to client: {}", client.getEmailAddress());
        } catch (Exception e) {
            logger.error("Failed to send welcome email to client: {}", client.getEmailAddress(), e);
            throw new SendingFailureException("Failed to send welcome email", e);
        }
    }
}
```

# src\main\java\com\qubedcare\postmark_integration\service\CrmService.java

```java
package com.qubedcare.postmark_integration.service;

import org.springframework.stereotype.Service;
import com.qubedcare.postmark_integration.dto.DeliveryEventDTO;

@Service
public class CrmService implements ICrmService {

    @Override
    public void updateClientEmail(String clientId, String emailAddress) {
        // Dummy implementation
        System.out.println("Updating client email...");
        System.out.printf("Client ID: %s, Email Address: %s%n", clientId, emailAddress);
    }

    @Override
    public void updateEmailOpenStatus(String clientId, String messageId) {
        // Dummy implementation
        System.out.println("Updating email open status...");
        System.out.printf("Client ID: %s, Message ID: %s%n", clientId, messageId);
    }

    @Override
    public void updateEmailDeliveryStatus(DeliveryEventDTO deliveryEventDTO) {
        // Dummy implementation
        System.out.println("Updating email delivery status...");
        System.out.println("Delivery Event: " + deliveryEventDTO);
    }
}

```

# src\main\java\com\qubedcare\postmark_integration\model\EmailEvent.java

```java
package com.qubedcare.postmark_integration.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailEvent {
    private EmailEventType eventType;
    private Date timestamp;
    private String emailDetails;
}

enum EmailEventType {
    SENT, DELIVERED, OPENED, CLICKED, BOUNCED
}
```

# src\main\java\com\qubedcare\postmark_integration\model\Client.java

```java
package com.qubedcare.postmark_integration.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    private String id;
    private String name;
    private String emailAddress;
}
```

# src\main\java\com\qubedcare\postmark_integration\kafka\NewClientAlertConsumer.java

```java
package com.qubedcare.postmark_integration.kafka;

import com.qubedcare.postmark_integration.model.Client;
import com.qubedcare.postmark_integration.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class NewClientAlertConsumer {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;
    private CountDownLatch latch = new CountDownLatch(1);

    public NewClientAlertConsumer(EmailService emailService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topic.new-client-alert}")
    public void consume(String message) throws Exception {
        System.out.println("Consumed message: " + message);
        Client client = objectMapper.readValue(message, Client.class);
        String emailAddress = emailService.generateEmailAddress(client.getName());
        client.setEmailAddress(emailAddress);
        emailService.sendWelcomeEmail(client);
        latch.countDown();
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }
}

```

# src\main\java\com\qubedcare\postmark_integration\kafka\CrmUpdateConsumer.java

```java
package com.qubedcare.postmark_integration.kafka;

import com.qubedcare.postmark_integration.model.Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CrmUpdateConsumer {

    private static final Logger logger = LoggerFactory.getLogger(CrmUpdateConsumer.class);
    private final ObjectMapper objectMapper;

    public CrmUpdateConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topic.crm-update}")
    public void consume(String message) {
        try {
            Client updatedClient = objectMapper.readValue(message, Client.class);
            logger.info("Received CRM update: {}", updatedClient);
            
            // TODO: Implement logic to update local client information if necessary
            
            logger.info("Processed CRM update for client: {}", updatedClient.getId());
        } catch (Exception e) {
            logger.error("Error processing CRM update: {}", message, e);
            // TODO: Implement retry mechanism
        }
    }
}
```

# src\main\java\com\qubedcare\postmark_integration\exception\SendingFailureException.java

```java
package com.qubedcare.postmark_integration.exception;

public class SendingFailureException extends Exception {
    public SendingFailureException(String message) {
        super(message);
    }

    public SendingFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

# src\main\java\com\qubedcare\postmark_integration\exception\EmailGenerationException.java

```java
package com.qubedcare.postmark_integration.exception;

public class EmailGenerationException extends Exception {
    public EmailGenerationException(String message) {
        super(message);
    }

    public EmailGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

# src\main\java\com\qubedcare\postmark_integration\controller\WebhookController.java

```java
package com.qubedcare.postmark_integration.controller;

import com.qubedcare.postmark_integration.dto.DeliveryEventDTO;
import com.qubedcare.postmark_integration.dto.OpenEventDTO;
import com.qubedcare.postmark_integration.dto.BounceEventDTO;
import com.qubedcare.postmark_integration.service.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
@RestController
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);
    private static final String SUCCESS_MESSAGE = "Event processed successfully";
    private static final String INVALID_SIGNATURE_MESSAGE = "Invalid signature";
    private static final String PROCESSING_ERROR_MESSAGE = "Error processing event";

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/webhook/delivery")
    public ResponseEntity<String> handleDeliveryEvent(
            @Valid @RequestBody DeliveryEventDTO payload,
            @RequestHeader("X-Postmark-Signature") String signature) {
        
        return processEvent(payload, signature, "Delivery");
    }

    @PostMapping("/webhook/open")
    public ResponseEntity<String> handleOpenEvent(
            @Valid @RequestBody OpenEventDTO payload,
            @RequestHeader("X-Postmark-Signature") String signature) {
        
        return processEvent(payload, signature, "Open");
    }

    @PostMapping("/webhook/bounce")
    public ResponseEntity<String> handleBounceEvent(
            @Valid @RequestBody BounceEventDTO payload,
            @RequestHeader("X-Postmark-Signature") String signature) {
        
        return processEvent(payload, signature, "Bounce");
    }

    private ResponseEntity<String> processEvent(Object payload, String signature, String eventType) {
        try {
            if (webhookService.verifySignature(payload, signature)) {
                switch (eventType) {
                    case "Delivery":
                        webhookService.processDeliveryEvent((DeliveryEventDTO) payload);
                        break;
                    case "Open":
                        webhookService.processOpenEvent((OpenEventDTO) payload);
                        break;
                    case "Bounce":
                        webhookService.processBounceEvent((BounceEventDTO) payload);
                        break;
                }
                logger.info("{} event processed successfully for payload: {}", eventType, payload);
                return ResponseEntity.ok(SUCCESS_MESSAGE);
            } else {
                logger.warn("Invalid signature for {} event payload: {}", eventType, payload);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_SIGNATURE_MESSAGE);
            }
        } catch (Exception e) {
            logger.error("Error processing {} event for payload: {}", eventType, payload, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(PROCESSING_ERROR_MESSAGE);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        logger.error("Unhandled exception occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(PROCESSING_ERROR_MESSAGE);
    }
}

```

# src\main\java\com\qubedcare\postmark_integration\dto\TaskDTO.java

```java
package com.qubedcare.postmark_integration.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDTO {
    private String type;
    private String emailAddress;
    private String details;
    // Add other necessary fields
}
```

# src\main\java\com\qubedcare\postmark_integration\dto\OpenEventDTO.java

```java
package com.qubedcare.postmark_integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
public class OpenEventDTO {
    @JsonProperty("RecordType")
    private String recordType;

    @JsonProperty("MessageStream")
    private String messageStream;

    @JsonProperty("FirstOpen")
    private boolean firstOpen;

    @JsonProperty("Client")
    private ClientInfo client;

    @JsonProperty("OS")
    private OSInfo os;

    @JsonProperty("Platform")
    private String platform;

    @JsonProperty("UserAgent")
    private String userAgent;

    @JsonProperty("Geo")
    private GeoInfo geo;

    @JsonProperty("MessageID")
    private String messageId;

    @JsonProperty("Metadata")
    private Map<String, String> metadata;

    @JsonProperty("ReceivedAt")
    private Instant receivedAt;

    @JsonProperty("Tag")
    private String tag;

    @JsonProperty("Recipient")
    private String recipient;

    @Data
    @NoArgsConstructor
    public static class ClientInfo {
        @JsonProperty("Name")
        private String name;

        @JsonProperty("Company")
        private String company;

        @JsonProperty("Family")
        private String family;
    }

    @Data
    @NoArgsConstructor
    public static class OSInfo {
        @JsonProperty("Name")
        private String name;

        @JsonProperty("Company")
        private String company;

        @JsonProperty("Family")
        private String family;
    }

    @Data
    @NoArgsConstructor
    public static class GeoInfo {
        @JsonProperty("CountryISOCode")
        private String countryIsoCode;

        @JsonProperty("Country")
        private String country;

        @JsonProperty("RegionISOCode")
        private String regionIsoCode;

        @JsonProperty("Region")
        private String region;

        @JsonProperty("City")
        private String city;

        @JsonProperty("Zip")
        private String zip;

        @JsonProperty("Coords")
        private String coords;

        @JsonProperty("IP")
        private String ip;
    }
}

```

# src\main\java\com\qubedcare\postmark_integration\dto\EmailEventDTO.java

```java
package com.qubedcare.postmark_integration.dto;

public class EmailEventDTO {
    private String recipient;
    private String eventType;

    // Getters and setters
    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
```

# src\main\java\com\qubedcare\postmark_integration\dto\DeliveryEventDTO.java

```java
package com.qubedcare.postmark_integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Map;

public class DeliveryEventDTO {
    @JsonProperty("MessageID")
    private String messageId;

    @JsonProperty("Recipient")
    private String recipient;

    @JsonProperty("DeliveredAt")
    private Instant deliveredAt;

    @JsonProperty("Details")
    private String details;

    @JsonProperty("Tag")
    private String tag;

    @JsonProperty("ServerID")
    private Integer serverId;

    @JsonProperty("Metadata")
    private Map<String, String> metadata;

    @JsonProperty("RecordType")
    private String recordType;

    @JsonProperty("MessageStream")
    private String messageStream;

    // Getters and setters
    // ... (implement getters and setters for all fields)
    public String getMessageId() {
        return messageId;
    }
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    public String getRecipient() {
        return recipient;
    }
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    public Instant getDeliveredAt() {
        return deliveredAt;
    }
    public void setDeliveredAt(Instant deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
    public String getDetails() {
        return details;
    }
    public void setDetails(String details) {
        this.details = details;
    }
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    public Integer getServerId() {
        return serverId;
    }
    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }
    public Map<String, String> getMetadata() {
        return metadata;
    }
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
    public String getRecordType() {
        return recordType;
    }
    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }
    public String getMessageStream() {
        return messageStream;
    }
}
```

# src\main\java\com\qubedcare\postmark_integration\dto\BounceEventDTO.java

```java
package com.qubedcare.postmark_integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.Instant;
import java.util.Map;

@Data
public class BounceEventDTO {
    @JsonProperty("RecordType")
    private String recordType;

    @JsonProperty("MessageStream")
    private String messageStream;

    @JsonProperty("ID")
    private Long id;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("TypeCode")
    private Integer typeCode;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Tag")
    private String tag;

    @JsonProperty("MessageID")
    private String messageId;

    @JsonProperty("Metadata")
    private Map<String, String> metadata;

    @JsonProperty("ServerID")
    private Integer serverId;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("Details")
    private String details;

    @JsonProperty("Email")
    private String email;

    @JsonProperty("From")
    private String from;

    @JsonProperty("BouncedAt")
    private Instant bouncedAt;

    @JsonProperty("DumpAvailable")
    private boolean dumpAvailable;

    @JsonProperty("Inactive")
    private boolean inactive;

    @JsonProperty("CanActivate")
    private boolean canActivate;

    @JsonProperty("Subject")
    private String subject;

    @JsonProperty("Content")
    private String content;
}
```

# src\main\java\com\qubedcare\postmark_integration\config\RestTemplateConfig.java

```java
package com.qubedcare.postmark_integration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

# src\main\java\com\qubedcare\postmark_integration\config\PostmarkConfig.java

```java
package com.qubedcare.postmark_integration.config;

import com.postmarkapp.postmark.Postmark;
import com.postmarkapp.postmark.client.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PostmarkConfig {

    @Value("${postmark.api.token}")
    private String postmarkApiToken;

    @Bean
    public ApiClient postmarkClient() {
        return Postmark.getApiClient(postmarkApiToken);
    }
}
```

