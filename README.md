# Code Companion

## Concept
The Goal of the Project is to provide realtime coding assistance on a second screen to help beginners with their coding tasks.  
The Project consists of a Plugin for the Jetbrains IntelliJ Platform reporting to the connecting Android App using WebRTC.
This App shows the current tasks, compiler errors and coding statistics.  


## Requirements

* Android Phone running Android 6.0 (Marshmallow) or later
* IntelliJ IDEA Version 2020.3.2 or later

### Installation
To use the Project, the Android App and the Connection Plugin need to be installed. 
* [Android App Google Play Store (coming soon)]()
* [Android App APK Download (coming soon)]()
* [IntelliJ Plugin Download (Plugin Marketplace) (coming soon)]()
* [IntelliJ Plugin Download (.zip File) (coming soon)]()



### Connection
![](Screenshots/ConnectionView.png)  
![](Screenshots/Plugin.png)  

When installed, the Plugin shows a QR-Code containing connection information. This code has to be scanned inside the Android App to start the connection process.
When the connection has been established correctly, the CodeCompanion Logo and App Status indicator change to green. (/better sentence)

### Tasks

![](Screenshots/TaskView.png)  
The task view shows the individual tasks of the coding assignment. The tasks can be individually sorted and checked.


#### Provide Task information
The "task.json" file is located inside the root of the project folder, and holds the project name, tasks and project due date.  
The given tasks show up in the task view of the app, and update in real-time.
The due date value is used to send a notification 1 week, 3 days and 1 day before the deadline reminding the user to finish the task.

Example:
 
```
{
  "informations": {
    "name" : "Testaufgabe 1.0",
    "lecture" : "EIDI",
    "date" : "2021-01-15",
    "deadline" : "2021-03-15"
  },
  "tasks":[
    {
      "description": "Create a hello world programm, which prints your name on the command line"
    },
    {
      "description": "Use an if-loop to check whether given name is a valid name or not. Check for numbers and other invalid signs"
    },
    {
      "description": "With a for-loop you can iterate every element in an array. Iterate the given array 'names' and print out every name to the commandline"
    }
  ]
}
```

### Errors
![](Screenshots/ErrorView.png)  ![](Screenshots/ErrorDetailView.png)  
The error view contains all compiler errors and warnings of the file currently being worked on, and is updated in real time. When clicked, a more detailed explaination on the error is shown, also offering the possibility of looking the problem up on google using the phone or desktop.


### Statistics

![](Screenshots/StatisticsView.png)  
The statistics view shows the time spent coding, produced lines of code and produced errors and warning. If the App is connected, the values are project specific. If the app is disconnected, it shows overall statistics of all projects together.

## Team
Code Companion is developed by Fabian (@PLACEBOBRO), Lukas (@RealWhimsy), Mathias (@goetzmat) and Maximilian (@MaximilianSeewald) during the "Advanced Software Engineering" Course of Uni Regensburg.


## Template Project

A template project can be found [here](https://github.com/UniRegensburg/unsere-app-fur-die-universitat-regensburg-code-companion/tree/main/TemplateProject).
