[![Build Status](https://travis-ci.org/aposin/LicenseScout.svg?branch=master)](https://travis-ci.org/aposin/LicenseScout)
[![codecov](https://codecov.io/gh/aposin/LicenseScout/branch/master/graph/badge.svg)](https://codecov.io/gh/aposin/LicenseScout)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/68b073442bd540f4a0a0ca1b33e5181b)](https://www.codacy.com/app/aposin-bot/LicenseScout?utm_source=github.com&utm_medium=referral&utm_content=aposin/LicenseScout&utm_campaign=Badge_Grade)
![GitHub top language](https://img.shields.io/github/languages/top/aposin/LicenseScout.svg)
[![CLA assistant](https://cla-assistant.io/readme/badge/aposin/LicenseScout)](https://cla-assistant.io/aposin/LicenseScout)
[![GitHub](https://img.shields.io/github/license/aposin/LicenseScout.svg)](https://github.com/aposin/LicenseScout/blob/master/LICENSE)

# LicenseScout

LicenseScout is a Maven Plug-in to identify third-party artifacts (libraries) and their licenses, in Java as well as JavaScript projects.
The goal is to get an overview over the used licenses, and the artifacts for which no license could be detected.

## Getting started

For detail information, see the [full documentation](org.aposin.licensescout.core/doc/documentation.adoc).

### Prerequisites

* Maven 3 installation

### Usage

Declare the Plugin and executions in the `pom.xml`:

```xml
<plugins>
    <plugin>
        <groupId>org.aposin.licensescout</groupId>
        <artifactId>licensescout-maven-plugin</artifactId>
        <version>1.1.4</version>
        <executions>
            <execution>
                <id>find-licenses</id>
                <phase>verify</phase>
                <goals>
                    <goal>scanJava</goal>
                </goals>
                <configuration>
                    <scanDirectory>${project.build.directory}/products/my.product/win32/win32/x86/plugins/</scanDirectory>
                    <outputDirectory>${licensescout.outputDirectory}</outputDirectory>
                    <outputs>
                        <output>
                            <type>TXT</type>
                            <filename>${licensescout.outputFilename.txt}</filename>
                            <url>${licensereport.url.txt}</url>
                        </output>
                    </outputs>
                    <licensesFilename>${licensescout-configuration.dir}/licenses.xml</licensesFilename>
                    <providersFilename>${licensescout-configuration.dir}/providers.xml</providersFilename>
                    <noticesFilename>${licensescout-configuration.dir}/notices.xml</noticesFilename>
                    <checkedArchivesFilename>${licensescout-configuration.dir}/checkedarchives.csv</checkedArchivesFilename>
                    <licenseUrlMappingsFilename>${licensescout-configuration.dir}/urlmappings.csv</licenseUrlMappingsFilename>
                    <licenseNameMappingsFilename>${licensescout-configuration.dir}/namemappings.csv</licenseNameMappingsFilename>
                    <globalFiltersFilename>${licensescout-configuration.dir}/globalfilters.csv</globalFiltersFilename>
                    <filteredVendorNamesFilename>${licensescout-configuration.dir}/filteredvendornames.csv</filteredVendorNamesFilename>
            </configuration>
            </execution>
        </executions>

    </plugin>
</plugin>
```

:information_source: For further information about how to configure the Plugin, please see the [full documentation](org.aposin.licensescout.core/doc/documentation.adoc).

As an example output, the [NOTICE.txt](NOTICE.txt) in this repository is the result of LiceseScout running on itself.

### Building

To build and run LicenseScout on itself in this repository:

```bash
cd org.aposin.licensescout.quickstart
mvn clean install
```

The reports are written to the directory `org.aposin.licensescout.licensereport/target`.

## Contributing

Please read [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) for details on our code of conduct, and [CONTRIBUTING.md](CONTRIBUTING.md) for the process for submitting pull requests to us.

## Authors

The authors list is maintained in the [CONTRIBUTORS.txt](CONTRIBUTORS.txt) file.
See also the [Contributors](https://github.com/aposin/LicenseScout/graphs/contributors) list at GitHub.

## License

This project is under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.  
