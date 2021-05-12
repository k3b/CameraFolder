# ![](https://github.com/k3b/CameraFolder/raw/master/app/src/main/res/drawable/camera_folder.png) Camera Folder: "Take a Photo from Camera" everywhere where you can open a photo file 

When "Camera Folder" is installed, any Android app that can open jpg files can also take a photo from camera.

Note that "Camera Folder" has no user interface and no start icon of its own.

The app is super tiny: 0.16 MB

## Example usecase:

From the drawing app [Simple Draw](https://github.com/SimpleMobileTools/Simple-Draw) we will "open" a photo from the [Open Camera](https://f-droid.org/en/packages/net.sourceforge.opencamera) app.

![](hhttps://github.com/cosify/CameraFolder/tree/master/fastlane/metadata/android/en-US/images/phoneScreenshots/Workflow-2.png)

* (0) Open the app "Simple Draw"
* (1) Open the overflow Menu
* (2) Choose "Open file". The Android System file picker opens.
* (3) Click the Hamburger menu
* (4) The Provider View opens
* (5) In the Provider View choose "Camera Folder"
* (6) The Camera picker opens.From the available Camera apps choose "Open Camera" 
* (Please note that in Android 11 and above the camera picker has been removed and therefore the default camera app will automatically open)
* (7) Take a photo with "Open Camera" and choose "OK" 
* (8) You will be back again in "Simple Draw" with the photo you have just taken ready for processing.    

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


-----

## Donations: 

If you like this app please consider to donating to https://f-droid.org/donate .

Since android-developping is a hobby (and an education tool) i donot want any 
money for my apps so donation should go to projects i benefit from.

