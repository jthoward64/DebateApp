# DebateApp

It's an app ... for debate. OK, more specifically it is a combination digital flow creator and timer for PF, LD, and policy debate

## Getting Started

### Prerequisites

**These requirements only apply if you want to build from source or use the jar file**

```
* JDK 8 (including JavaFX)
* Some way to fetch the maven libraries (or a local copy)
```

### Installing

MacOS

1. Download the [.app bundle](https://github.com/tajetaje/DebateApp/releases/latest)
2. ove it into the applications folder

Windows

1. Download the [Windows installer](https://github.com/tajetaje/DebateApp/releases/latest)
2. Just run the installer, it will create a Start Menu entry that will run the program

Linux (Sorry, no packages yet)

1. Download the [.tar.gz](https://github.com/tajetaje/DebateApp/releases/latest) archive

##Using the app
####Choose an event
1. Click "Settings" in the menu
2. Hover over "Event"
3. Select your event
4. (Optional) Reopen the menu and select set as default to automatically use this event on startup

Ctrl + e will also allow you to cycle between events

###Save your flow
1. Either press ctrl+s or click save in the flow menu
2. Enter the name for you file(s)
3. Choose a directory to save your flow, defaults to the DebateApp folder in your personal user directory
4. Select whether to save your flow as a pair of png images, a csv file, or both

###Saving app settings
Settings save automatically to your user folder when closing the app

###Switching flow between pro and con
Either
 * Press Ctrl+Space
 
or

 * CLick pro or con under the "Flow" menu

### Make DebateApp always on top
In the view menu, select always on top

##Known issues
 * In a Windows multi-monitor setup with UI scaling on, menus may open in odd locations

## Built With

* [InteliJ IDEA](https://www.jetbrains.com/idea/) - The IDE and build tool used
* [Maven](https://maven.apache.org/) - Dependency Management
* [packr](https://github.com/libgdx/packr) - Used to generate bundles for macOS and linux

## Versioning

I use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/tajetaje/DebateApp/tags). 

## Author

* **TajeTaje** - [My Github](https://github.com/tajetaje)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

## Acknowledgments

* [OpenCSV](http://opencsv.sourceforge.net/) was used to save the flow as a csv file
* [ControlsFX](https://github.com/controlsfx/controlsfx) just has some really useful pre-coded controls that saved a lot of time in this project
* [JMetro](https://www.pixelduke.com/java-javafx-theme-jmetro/) provides native look and feel to the windows version
