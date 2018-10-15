![Our cool banner](./assets/RocketGit.jpg)

---
[![Build Status](https://travis-ci.org/a-rmz/rocketgit.svg?branch=master)](https://travis-ci.org/a-rmz/rocketgit)

### How to get it running?

**Pre-requisites:**
* Java ≥ 8
* JavaFX Support
* [SceneBuilder](https://gluonhq.com/products/scene-builder/) (Recommended)
* IDE (Eclipse or IntelliJ Idea recommended)
* [Maven](https://maven.apache.org/)

#### Running with an IDE

1. Clone the code:
    * Via HTTPS:
     ```bash
     $ git clone https://github.com/a-rmz/rocketgit.git
     ```
    * Via SSH:
     ```bash
     $ git clone git@github.com:a-rmz/rocketgit.git
     ```
2. Import the project to your IDE:
    * [Eclipse](https://help.eclipse.org/neon/index.jsp?topic=%2Forg.eclipse.platform.doc.user%2Ftasks%2Ftasks-importproject.htm)
    * [IntelliJ Idea](https://www.jetbrains.com/help/idea/import-project-or-module-wizard.html)
3. Create a build task to run the Main class
4. Run the project

#### Running with Maven

 1. Clone the code:
     * Via HTTPS:
      ```bash
      $ git clone https://github.com/a-rmz/rocketgit.git
      ```
     * Via SSH:
      ```bash
      $ git clone git@github.com:a-rmz/rocketgit.git
      ```
2. Install the dependencies
    ```sh
    $ mvn install
    ``` 
3. Build the project:
    ```sh
    $ mvn clean package
    ```
4. Run the project:
    ```sh
    $ java -jar target/rocketgit-1.0-SNAPSHOT-jar-with-dependencies.jar
    ```