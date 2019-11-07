[![Build Status](https://travis-ci.org/aposin/LicenseScout.svg?branch=master)](https://travis-ci.org/aposin/LicenseScout)
[![codecov](https://codecov.io/gh/aposin/LicenseScout/branch/master/graph/badge.svg)](https://codecov.io/gh/aposin/LicenseScout)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/68b073442bd540f4a0a0ca1b33e5181b)](https://www.codacy.com/app/aposin-bot/LicenseScout?utm_source=github.com&utm_medium=referral&utm_content=aposin/LicenseScout&utm_campaign=Badge_Grade)
[![Known Vulnerabilities](https://snyk.io/test/github/aposin/LicenseScout/badge.svg?targetFile=org.aposin.licensescout.core%2Fpom.xml)](https://snyk.io/test/github/aposin/LicenseScout?targetFile=org.aposin.licensescout.core%2Fpom.xml)
![GitHub top language](https://img.shields.io/github/languages/top/aposin/LicenseScout.svg)
[![CLA assistant](https://cla-assistant.io/readme/badge/aposin/LicenseScout)](https://cla-assistant.io/aposin/LicenseScout)
[![GitHub](https://img.shields.io/github/license/aposin/LicenseScout.svg)](https://github.com/aposin/LicenseScout/blob/master/LICENSE)

---

<p align="center">
<img src="org.aposin.licensescout.documentation/images/png/LS_logo_with_text_green.png" width="350">
</p>

---

LicenseScout is a Maven Plug-in to identify third-party artifacts (libraries) and their licenses, in Java as well as JavaScript projects.
The goal is to get an overview over the used licenses, and the artifacts for which no license could be detected. A proper output document is generated listing the used licenses and the corresponding license texts. In a configuration file each license identified is marked as ACCEPTED or NOT_ACCEPTED. This decision depends on the your project's license and may require legal support.


## Getting started

For detail information, see the [full documentation](org.aposin.licensescout.documentation/doc/documentation.adoc).

### Prerequisites

* Maven 3 installation


### Run the quickstart example

To build and run LicenseScout on itself in this repository:

```bash
cd org.aposin.licensescout.quickstart
mvn clean install
```

This does:
* Compiles the LicenseScout Maven plug-in
* Creates a LicenseScout configuration bundle
* Runs the LicenseScout on its own dependencies and creates license reports for it

The reports are written to the directory `org.aposin.licensescout.licensereport/target`.

:information_source: For further information about how to configure the Plugin, please see the [full documentation](org.aposin.licensescout.core/doc/documentation.adoc).

As an example output, the [NOTICE.txt](NOTICE.txt) in this repository is the result of LiceseScout running on itself.

### Next steps

#### Run LicenseScout on your own project

Have a look at the `pom.xml` in `org.aposin.licensescout.licensereport`. It contains a typical configuration of how the LicenseScout is applied to a project.

You may want to start by re-using this project and just alter the parameter `scanDirectory` (Note: the value may also be an absolute path that points anywhere in the file system).

Alternatively, you can copy out the plugin configurations and take them over to your own project's POM.

#### Create a project specific configuration

The quickstart example you just executed uses a minimal configuration
that is just sufficient to run the LicenseScout on its own dependencies.

For you own project, you will need to maintain a configuration that is specific to your project. Take the project `org.aposin.licensescout.configuration.sample` as a starting point by creating a copy of it. If you cange the name of the project (recommended!) make sure you also adapt the group and artifact IDs that are used as configuration in the licensescout run (in `org.aposin.licensescout.licensereport/pom.xml`):

```xml
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-dependency-plugin</artifactId>
	<executions>
		<execution>
			...
			<configuration>
				<artifactItems>
					<artifactItem>
						<groupId>your.new.groupId</groupId>
						<artifactId>your.new.artifactId</artifactId>
						<version>${org.aposin.licensescout.configuration.version}</version>
						<classifier>configuration</classifier>
						...
					</artifactItem>
				</artifactItems>
				...
			</configuration>
		</execution>
	</executions>
</plugin>
```

Then, start adding the configurations you need:
* License definitions
* License notices
* Vendor names (for filtering out own artifacts)
* License name and URL mappings
* Exceptions for certain artifacts (`checkedarchives.csv`)
* Provider definitions

See also the full documentation: it describes the format of the configuration files and how their information is processed.

You may also want to create a customized version of the templates used for the license reports.

## Contributing

Please read [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) for details on our code of conduct, and [CONTRIBUTING.md](CONTRIBUTING.md) for the process for submitting pull requests to us.

## Authors

The authors list is maintained in the [CONTRIBUTORS.txt](CONTRIBUTORS.txt) file.
See also the [Contributors](https://github.com/aposin/LicenseScout/graphs/contributors) list at GitHub.

## License

This project is under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.  
