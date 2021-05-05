# ![](https://github.com/k3b/CameraFolder/raw/master/app/src/main/res/drawable/camera_folder.png) Camera Folder: "Take a Photo from Camera" everywhere where you can open a photo file 

When "Camera Folder" is installed many Android-Apps that can open jpg files can also take a photo from camera.

Note that "Camera Folder" has no userinterface and no starticon of its own.

The app is super tiny: 0,16 MB

## Example usecase:

Inside the drawing app [Simple-Draw](https://github.com/SimpleMobileTools/Simple-Draw) 
"open" a photo from [Open Camera](https://f-droid.org/en/packages/net.sourceforge.opencamera) app.

![](https://github.com/k3b/CameraFolder/raw/master/fastlane/metadata/android/en-US/images/phoneScreenshots/1-workflow.png)

* (0) open the app "Simple-Draw"
* (1) Open Menu
* (2) Coose "Open file"
* The Android-System-File-Chooser opens.
* (4) Choose Hamburger Menu
* The Provider View opens
* (5) In the Provider View choose "Camera Folder"
* The "Choose Camera APP" opens
* (6) From the available Camera Apps choose "OpenCamera"
* (7) Take a Photo with the Camera App and choose "OK" 
* (8) back again in "Simple-Draw" process the photo.    

## Requirements:

* Android-4.4 (api 19) or later with camera hardware.
* At least one Camera app must be installed (i.e. [Open Camera](https://f-droid.org/en/packages/net.sourceforge.opencamera))
* Required Permissions:  
  * CAMERA needed to ask a camera app to take a photo
  * WRITE_EXTERNAL_STORAGE to save the photo to a file

## Technical details

* "Camera Folder" plugs into the Android-System-File-Chooser that is used by many Android apps.
* It Translates from ACTION_GET_CONTENT.to MediaStore.ACTION_IMAGE_CAPTURE
 
## Privacy

No adds, no usertracking, no internet connection, free open source, available on f-droid
