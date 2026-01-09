# Caesium
Caesium is a fast, efficient Java bytecode obfuscator originally created by [sim0n](https://github.com/sim0n).
This fork is maintained by **9e_Docteur (ninedocteur / Loris P.)**, with a CLI designed for automation (Gradle/CI) and real-world private project usage.

![Image of Caesium UI](https://i.imgur.com/drrn9ib.png)

## Why this fork
I built this version for my personal and private projects (closed-source), to have a reliable, easy-to-integrate, fully scriptable obfuscation pipeline.
It's released to the public under the MIT license so anyone can use it freely and adapt it to their needs.

## Credits
- Original author: [sim0n](https://github.com/sim0n)
- Fork & CLI integration: 9e_Docteur (ninedocteur / Loris P.)

### Currently available mutators
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

## Notes
You have to add every dependency your jar relies on.
Caesium is very optimised and the performance loss shouldn't be more than 5-10% (unless you're using reference mutation)

## Usage
- Run the jar.
- Select mutators in the mutators tab.
- Hit mutate. Done!

## CLI (Gradle)
You can call the CLI entry point `be.ninedocteur.caesium.cli.CaesiumCli` from a Gradle `JavaExec` task.

Example (Gradle Groovy DSL):
```
tasks.register("caesiumObfuscate", JavaExec) {
    classpath = files("path/to/caesium-1.0.9.jar")
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

## Community 
If you want to join the discord for Caesium to talk, ask questions or anything then feel free to join [the discord](https://discord.gg/kxC2FYMfNZ)

## Special thanks to
![yourkit logo](https://www.yourkit.com/images/yklogo.png)

[YourKit](https://www.yourkit.com/) is the creator of <a href="https://www.yourkit.com/java/profiler/">YourKit Java Profiler</a>, <a href="https://www.yourkit.com/.net/profiler/">YourKit .NET Profiler</a>, and <a href="https://www.yourkit.com/youmonitor/">YourKit YouMonitor</a>. They support open source projects with their fully featured application profilers. It's used to ensure that this project will be as fast as possible.
