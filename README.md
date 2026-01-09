# Caesium ✨
Caesium is a fast, efficient Java bytecode obfuscator originally created by [sim0n](https://github.com/sim0n).
This fork is maintained by **9e_Docteur (ninedocteur / Loris P.)**, with a CLI designed for automation (Gradle/CI) and real-world private project usage.

![Image of Caesium UI](https://i.imgur.com/drrn9ib.png)

## Summary 📌
- [Why this fork](#why-this-fork)
- [Credits](#credits)
- [Currently available mutators](#currently-available-mutators)
- [Notes](#notes)
- [Usage](#usage)
- [CLI (Gradle)](#cli-gradle)
- [Maven coordinates](#maven-coordinates)
- [Community](#community)
- [Changelog](#changelog)
- [Special thanks to](#special-thanks-to)

## Why this fork 🚀
I built this version for my personal and private projects (closed-source), to have a reliable, easy-to-integrate, fully scriptable obfuscation pipeline.
It's released under the Creative Commons Attribution-NonCommercial 4.0 (CC BY-NC 4.0) license: you can modify and share it for non-commercial use, with credit to both sim0n and 9e-Docteur.

## Credits 🙌
- Original author: [sim0n](https://github.com/sim0n)
- Fork & CLI integration: 9e_Docteur (ninedocteur / Loris P.)

### Currently available mutators 🧩
* Class Folder (Turns classes into folders)
* Control Flow
* Crasher (Will crash almost every GUI based RE tool)
* Local Variable
* Line Number
* Number
* Polymorph
* Reference (invokedynamics)
* String
* Trim (Currently only trims math functions)

## Notes 📝
You have to add every dependency your jar relies on.
Caesium is very optimised and the performance loss shouldn't be more than 5-10% (unless you're using reference mutation)

## Usage ▶️
### GUI 🖥️
- Run the jar with no arguments to open the GUI.
- Select mutators in the mutators tab.
- Hit mutate. Done!

### CLI 🧰
- Run the jar with arguments to use the CLI (no GUI will open).
- The CLI is intended for automation (Gradle/CI) and headless use.
- Example:
```
java -jar caesium-cli-1.1.jar --input app.jar --output app-obf.jar --string --control-flow
```

## CLI (Gradle) 🛠️
You can call the CLI entry point `be.ninedocteur.caesium.cli.CaesiumCli` from a Gradle `JavaExec` task.

Example (Gradle Groovy DSL):
```
tasks.register("caesiumObfuscate", JavaExec) {
    classpath = files("path/to/caesium-cli-1.1.jar")
    mainClass.set("be.ninedocteur.caesium.cli.CaesiumCli")
    args "--input", "$buildDir/libs/app.jar",
         "--output", "$buildDir/libs/app-obf.jar",
         "--string", "--control-flow", "--number", "--line-number", "remove",
         "--local-variables", "rename",
         "--dictionary", "numbers"
}
```

CLI options (partial list):
- `--input` / `--output`
- `--string` and `--string-exclude` (repeatable)
- `--control-flow`, `--number`, `--polymorph`, `--reference`
- `--class-folder`, `--trim`, `--shuffle`
- `--crasher`, `--bad-annotation`, `--image-crash`
- `--line-number` (`remove` or `scramble`)
- `--local-variables` (`remove` or `rename`)
- `--library` (repeatable)
- `--dictionary` (`abc`, `ABC`, `III`, `numbers`, `wack`)
- `--overwrite`

## Maven coordinates 📦
```
<dependency>
  <groupId>be.ninedocteur</groupId>
  <artifactId>caesium-cli</artifactId>
  <version>1.1</version>
</dependency>
```

## Community 🌐
If you want to join the discord for Caesium to talk, ask questions or anything then feel free to join [the discord](https://discord.gg/kxC2FYMfNZ)

## Changelog 📅
### 1.1.2 (2026-01-09)
- Core logging now uses the CLI color logger (no Log4j console output).
- Fixed a crash when parsing fat/uber jars by skipping invalid class entries instead of throwing.

### 1.1.1 (2026-01-09)
- Added the CLI colored logger (INFO, SUCCESS, WARN, ERROR).

### 1.1 (2026-01-09)
- Added a full CLI entry point for automation (Gradle/CI), with options for mutators, dictionary, dependencies, and outputs.
- CLI now auto-routes when arguments are passed; GUI remains default with no args.
- Updated Maven coordinates to `be.ninedocteur:caesium-cli:1.1`.
- Added GitHub Actions workflows for Javadoc publication and Maven package publishing.
- Fixed Javadoc generation errors and improved documentation coverage.
- Updated README with new usage, CLI notes, and Maven dependency snippet.
- Added a simple compilation test script (`test-compile.sh`).

## Special thanks to ❤️
![yourkit logo](https://www.yourkit.com/images/yklogo.png)

[YourKit](https://www.yourkit.com/) is the creator of <a href="https://www.yourkit.com/java/profiler/">YourKit Java Profiler</a>, <a href="https://www.yourkit.com/.net/profiler/">YourKit .NET Profiler</a>, and <a href="https://www.yourkit.com/youmonitor/">YourKit YouMonitor</a>. They support open source projects with their fully featured application profilers. It's used to ensure that this project will be as fast as possible.
