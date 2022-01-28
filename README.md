# Chat System
A local peer-to-peer chat system application.

![An screenshot of the main screen](https://i.ibb.co/zXQ2h6N/main-screenshot.png)

## How to run the application?
### From the github project
* Download the project
* Download & install the Java JDK, version 11
* Configure the JAVA_HOME system variable to match that Java version
* In the root directory of the project,
  * on **Linux**: type `./gradlew run`
  * on **Windows**: type `.\gradlew run`
  * on **Mac OS**: type `./gradlew run` (application not tested on Mac OS)
### From the release zip file
>💡 **Note**: Java needs to be configured as described above. Only tested on Linux.
* Download the `.zip` file from the release section of the project on Github
* Unzip the file
* Open the terminal in the root of the `ChatSystem-1.0` directory
  * on **Linux**: type `bin/ChatSystem`
  * on **Windows**: type `bin/ChatSystem.bat`
  * on **Mac OS**: type `bin/ChatSystem` (application not tested on Mac OS)
## How to run the tests?
In the root directory of the project,
  * on **Linux**: type `./gradlew test`
  * on **Windows**: type `.\gradlew test`
  * on **Mac OS**: type `./gradlew test` (application not tested on Mac OS)
## Currently available features
* 🔌 Select the network you want to discuss on
* 👤 Select a username and have its availability checked by connected users
* 💬 Receive and send text messages to and from the connected users
* ⏲️ Get the time when the message was sent (and the full date if the message is not from the current day)
* 🚫 Know when a user is disconnected from the network
* 🖋️ Change your username at any time
* 🛑 Disconnect from the network
