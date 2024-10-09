# ♕ BYU CS 240 Chess
Phase 2 SequenceDiagram: https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5y49K+KDvvorxLAC5wFrKaooOUCAHjysL7oeqLorE2IJoYLphm6ZIUgatLlqOJpEuGFoctyvIGoKwowEBoakW6bKYR6uqZLG-rTiGrrSpGHLRjAHHxnKWHJpBJbITy2a5pg-4gtBJRXAMRGjAuk59NOs7NuOrZ9N+V7yYU2Q9jA-aDr0ykjqpOnVlOQZafONltsuq7eH4gReCg6B7gevjMMe6SZJgxkXkU1DXtIACiu6RfUkXNC0D6qE+3SaY26Dtr+ZxApcaVzrJ4lZSxMDwfYflIb5vqoRiGEiRqOEsnhYAcQ5sItelaAkUyTF8aUkTDBANCCUGwkwZ2cnlMhfnSQgeaFZ2P5XPp4WGV2ORgH2A5DkunCueugSQrau7QjAADio6sgFp7BeezAKdep2xQl9ijql9kdZlbITTAeUZXJzEiXB0JIdC1XoY6zoEg1pIwOSYDnaMAbvXOXVmhGhSWpRHCssAyqwxdEOJmJOVpsdsQzXNJMLQZVxTC9oyqOMlT9PTKAAJLSEzACMvYAMwACxPCemQGhWExfDoCCgA2oujisXyswAcqO4t7DAjTLccSZGbdm3mT0dMXUzFQs6OHPc3zgtTML+oqfcfQS1LIAy3b8tPErKsO2rGtmLtnhuRu2A+FA2DcPAbGGAjhhXUFIV3ZeK3lDeDTPa9wTI+gQ4e6MmsdsTqalL9z69NnKAQVTCngjAnp6rCcAR2DWKE1hUOMY1sMUlHsKl6jZG9Vj+OjEKYSlwx3W8ZX8owIqyrN-VbfahHXesxzvc9Rj-E2oPKCjU6RN-oV5T116mQUwVFf3WmBt9CvnPlDzAswLnq1x3rQ6G6M5v35bT++yu-v7QCJYFAyoIDJBgAAKQgDyM6o5AiS2ljddaANE6VCqJSO8LRWZvTrB1IcodgDAKgHACA8EoCzFvs-L6h8i74KlkQkhZCKFm2kOXVMKC96wRgAAK2gWgWEUCpIoDRDVOe2EF7ug7vDUc3cWHvnoZQRh0A168Q3lyHktpWbDxgBzMeaNqYwQVEqFUmF57j1JMAG0cBcbAlhAQhhpDlF6L7mogSEA-QdnsR2SA29nHr3ZKUPwWhMjb13s6fOwJyiCLQGff62swqKVLFQ+Jr9TJbV6Dtf+a53IBC8IQrsXpYDAGwKHQg8REgpECmeZBl8k5RRinFBKxhPoRMuOfdhk8uEgG4HgWEtUxrxO+ogQpsT5rxMWkklppxUlmW2n-TAQA


This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
