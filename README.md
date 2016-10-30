# Repositable

Minimalistic Maven Repository Manager.

## Introduction

Repositable is a extremally minimal, but fully-functional maven repository manager. It stores artifacts as files and 
generates metadata from thier listing. It is ment to be simple alternative to 
[Artifactory](https://www.jfrog.com/artifactory/) or [Nexus](http://www.sonatype.org/nexus/) or 
[Archiva](https://archiva.apache.org/), that has no web UI, but consumes much less memory. Its targeted at
individuals that want to code and just publish artifacts.

Repositable is written in Java as a single-jar application with embedded HTTP server.

Supported features:
* Multiple named repositories,
* Authentication: anybody can download artifacts, only named users can upload,
* Virtual repositories: merging multiple repository to be visible as one,
* Repository filters: allowing only specified group of artifacts to be uploaded to repository,

Repositable is by design configured by single XML configuration.

## How to install

Download `jar-with-dependencies` jar from releases section, prepare a configuration file and just 
run jar:

```shell
java -jar repositable-x.x.x-jar-with-dependencies.jar <configuration>
```

## Configuration

Configuration is self-explanatory:

```xml
<server>
    <users>
        <user id="first">
            <username>my-username-first</username>
            <password>plaintextpassword</password>
        </user>
        <user id="second">
            <username>second-username</username>
            <password>otherplaintextpassword</password>
        </user>
    </users>
    <port>8000</port>
    <repository name="snapshots">
        <location>/opt/storege/snapshots</location>
        <filters>
            <group>com.yourgroup</group>
            <snapshots />
        </filters>
        <uploaders>
            <user ref="first" />
            <user ref="second" />
        </uploaders>
    </repository>
    <repository name="releases">
        <location>/opt/storege/releases</location>
        <filters>
            <group>org.yourgroup</group>
            <releases />
        </filters>
        <uploaders>
            <user ref="second" />
        </uploaders>
    </repository>
    <virtual name="all">
        <sources>
            <repository ref="libs-snapshot-local" />
            <repository ref="libs-release-local" />
        </sources>
    </virtual>
</server>
```
