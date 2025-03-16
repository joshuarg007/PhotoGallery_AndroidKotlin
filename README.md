# Photo Gallery App  

## Overview  
This is a simple photo gallery application that loads and displays images from both local storage and online sources. The app dynamically fetches up to three images from the device and three from the internet, organizing them with labeled sections. If no local images are available, the app displays default images from the drawable folder.  

## Features  
- Requests storage permissions to access local images  
- Loads up to three images from device storage  
- Displays three images from online sources  
- Uses default images when no local images are found  
- Organizes images into labeled sections for clarity  
- Frames images with a border for visual separation  

## File Structure  
- **MainActivity.kt** – Handles image loading, permissions, and UI updates  
- **activity_main.xml** – Defines the main user interface with a ScrollView and LinearLayout  
- **image_border.xml** – Adds borders around images for styling  
- **res/drawable/** – Stores default images used when no local images are available  
- **AndroidManifest.xml** – Declares necessary permissions for accessing storage and online images  

## How It Works  
1. The app requests storage permission on startup  
2. If permission is granted, it retrieves up to three images from local storage  
3. If no local images are found, default images are displayed  
4. The app fetches three images from online sources and displays them  
5. Each image is wrapped in a FrameLayout with a border for styling  
6. Images are displayed in a scrollable layout with labeled sections  

## Setup Instructions  
1. Clone or download the project  
2. Open the project in Android Studio  
3. Add default images to `res/drawable/` if needed  
4. Replace the online image URLs in `MainActivity.kt` with preferred links  
5. Run the application on an Android device or emulator  

This project provides a simple yet structured way to display local and online images within an Android application.