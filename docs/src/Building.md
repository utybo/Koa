# Building Koa

Koa is built using [Gradle](https://gradle.org/). You will need a recent Java Development
Kit (JDK for short) on your system but you do not need to install Gradle for most use cases.

You can grab a JDK over at [Adoptium](https://adoptium.net).

## Using Gradle

Most tasks will require you to use Gradle. You can run Gradle tasks by using the wrapper scripts (`gradlew` for
macOS/Linux or `gradlew.bat` for Windows).

```sh
$ ./gradlew
```

## Building Koa

Koa can be built using the `build` task, like so:

```sh
$ ./gradlew build
```

This will run all the tests, code analysis tools and produce JAR files that you can then ingest. 

?> We recommend using [our pre-built JAR files](GettingStarted.md#adding-koa) instead of compiling Koa yourself.

Koa is organized in [modules](GettingStarted.md#modules). Each module provides either specific functionality (like Ktor features).

## Building the documentation

The documentation website uses custom Gradle tasks. There are two scenarios:

* Building the website
* Serving the website for previewing it while editing.

For the first case, you can just run:

```sh
$ ./gradlew buildDocs
```

For the second case, you will need two separate terminals.

```sh
# In the first terminal
$ ./gradlew -t buildDocs
# In the second terminal
$ ./gradlew serveDocs
```
