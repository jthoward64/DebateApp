﻿# DebateApp

It's an app ... for debate. OK, more specifically it is a combination digital flow creator and timer for PF, LD, and policy debate

## Getting Started

### Prerequisites

**These requirements only apply if you want to build from source or use the jar file**

```
* JDK 11 (including JavaFX)
* Maven
```

### Installing
Sorry about any security prompts, code signing certificates are expensive

MacOS
1. Download the [macOS installer](https://github.com/tajetaje/DebateApp/releases/latest)
2. Open it
3. Drag DebateApp into your applications folder

**If you see any warnings about the app being unverified, see [this](https://support.apple.com/guide/mac-help/open-a-mac-app-from-an-unidentified-developer-mh40616/mac) help page**

Windows
1. Download the [Windows installer](https://github.com/tajetaje/DebateApp/releases/latest)
2. Just run the installer, it will create a Start Menu entry that will run the program

Ubuntu
1. Download the [.DEB installer](https://github.com/tajetaje/DebateApp/releases/latest)
2. Run or extract it

Other Linux distros
1. Download the [.tar.gz archive](https://github.com/tajetaje/DebateApp/releases/latest) marked for linux
2. Extract the archive
3. Move the folder to your preferred install location
4. (optional) [Create a desktop shortcut](https://www.maketecheasier.com/create-desktop-file-linux/)


Extractable archives are also provided for each OS for manual installation

## Using the app
#### Choose an event
1. Click "Event" in the menu
2. Click next event until you have reached your preferred event

To always open the app to a certain event, change "Default Event" in settings

### Save your flow
1. Either press ctrl+s or click save in the File menu
2. Select a file to save and click OK

### Switching layouts
Select your preferred layout under View 

### Disabling the toolbars on the editors
If you find the editor toolbars distracting, or unnecessary, they can be disabled in settings

### Make DebateApp always on top
In the view menu, select "always on top"

## Known issues
 * In a Windows multi-monitor setup with UI scaling on, menus may open in odd locations
 * Aggressive anti-viruses (e.g. Avast, or macOS Gatekeeper) usually throw a warning about unsigned code
    * Solution: after the first scan most AVs will ignore DebateApp, if they don't simply mark the DebateApp exe as excluded

## Built With

* [IntelliJ IDEA](https://www.jetbrains.com/idea/) - The IDE and build tool used
* [Maven](https://maven.apache.org/) - Dependency Management
* [JavaPackager](https://github.com/fvarrui/JavaPackager) - Used to generate native packages

## Building from source

1. Clone the repository
2. Run `mvn clean compile`
3. Execute `main.java.DebateAppMain`

## Versioning

I use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/tajetaje/DebateApp/tags). 

## Author

* **TajeTaje** - [My Github](https://github.com/tajetaje)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

## Acknowledgments

* [ControlsFX](https://github.com/controlsfx/controlsfx) just has some really useful pre-coded controls that saved a lot of time in this project
* [JMetro](https://www.pixelduke.com/java-javafx-theme-jmetro/) provides native look and feel to the windows version
* [Freepik](https://www.flaticon.com/authors/freepik) created some icons that were used in the app
