# MYSL - Object Detection Model & Android App

This repository contains instructions for training and exporting an object detection model, as well as setting up an Android app that uses the trained model.

---

## Step 1: Train and Export Object Detection Model

1. **Clone the repository:**

   ```bash
   git clone https://github.com/KoukiFTW/MYSL

## Step 2: Object Detection Android App Setup

1. **Prepare the Assets**

   - Navigate to the `android_app` folder:
   
     ```
     android_app/
     ```

   - Place your `.tflite` model and `.txt` label file inside the `assets` folder. The folder location is:
   
     ```
     android_app/android_app/app/src/main/assets/
     ```

2. **Modify the Constants.kt File**

   - Open the `Constants.kt` file located at:
   
     ```
     android_app/android_app/app/src/main/java/com/surendramaran/yolov8tflite
     ```

   - Update the paths for your model and label files inside the `Constants.kt` file to match the location of your `.tflite` model and `.txt` label file in the `assets` folder.

3. **Set Up Android Studio**

   - Download and install [Android Studio](https://developer.android.com/studio).

   - Once installed, open Android Studio from your applications menu.

   - On the welcome screen of Android Studio, select **"Open an existing Android Studio project"**.

   - Navigate to the root folder of the cloned repository and select it.

4. **Build and Run the App**

   - In Android Studio, click on the **Build** button to compile the project.

   - After the build completes successfully, click on **Run** to deploy the app to your Android device or emulator.
