# Base POM for Services

## Use

This is meant to be added to the `pom` file for any service that is made for this project.  This is meant to be a baseline to ensure consistency and that primary items that cannot live in just the parent pom file are recorded and can be added to each module as needed.

If using Idea IDE, be wary of indent levels in auto generated pom files.  Can lead to confusion/issues.  Potentially will incorporate linting of these files at a future date.

## Project setup

- Set Version to match what is in `./pom.xml`
- `realtivePath` is needed with the sub directories - this should be the parent pom for this project

```xml
<parent>
    <groupId>com.tkforgeworks.cookconnect</groupId>
    <artifactId>CookConnect</artifactId>
    <version>0.0.1</version>
    <relativePath>../../pom.xml</relativePath>
</parent>
```

## Build set up

Primarily here is that `Jib` is included for container creation of each module.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
        </plugin>
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
        <plugin>
            <groupId>com.google.cloud.tools</groupId>
            <artifactId>jib-maven-plugin</artifactId>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>build</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```