# Quick Build Guide for Student LMS

## Prerequisites Check
✅ Android Studio installed (Arctic Fox or newer)  
✅ Java JDK 8+ installed  
✅ Internet connection (for Gradle dependencies)

## Step-by-Step Build Instructions

### Step 1: Open the Project
1. Launch **Android Studio**
2. Click **File** → **Open**
3. Navigate to: `d:\AI\StudentLMS`
4. Click **OK**
5. Wait for "Gradle sync" to complete (bottom status bar)
   - First sync may take 5-10 minutes to download dependencies

### Step 2: Resolve Any Gradle Sync Issues

If you see errors during Gradle sync:

**"SDK not found"**
- Go to **File** → **Project Structure** → **SDK Location**
- Set Android SDK path (usually `C:\Users\<YourName>\AppData\Local\Android\Sdk`)

**"Gradle version mismatch"**
- Click "Fix Gradle wrapper" in the error message
- Or download Gradle manually from gradle.org

**"Dependency download failed"**
- Check internet connection
- Try **File** → **Invalidate Caches / Restart**

### Step 3: Build the Project

1. Click **Build** → **Make Project** (or press `Ctrl+F9`)
2. Wait for build to complete (watch bottom status bar)
3. If successful, you'll see "BUILD SUCCESSFUL" in the Build output

### Step 4: Generate APK

**For Debug APK (Testing):**
1. Click **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
2. Wait for "APK(s) generated successfully" notification
3. Click "locate" to find the APK at:
   ```
   d:\AI\StudentLMS\app\build\outputs\apk\debug\app-debug.apk
   ```

**For Release APK (Production):**
1. First, create a signing key:
   - **Build** → **Generate Signed Bundle / APK**
   - Select **APK** → Click **Next**
   - Click **Create new...** to create keystore
2. Follow wizard to sign the APK

### Step 5: Install on Device

**Option A: Using Android Studio**
1. Connect Android phone via USB
2. Enable "USB Debugging" on phone (Developer Options)
3. Click green **Run** button (▶) in Android Studio
4. Select your device from the list

**Option B: Using ADB Command**
```powershell
adb install d:\AI\StudentLMS\app\build\outputs\apk\debug\app-debug.apk
```

**Option C: Manual Install**
1. Copy `app-debug.apk` to your phone
2. Open file manager on phone
3. Tap the APK file
4. Allow "Install from unknown sources" if prompted
5. Tap **Install**

## Common Build Errors & Fixes

### Error: "Manifest merger failed"
**Fix:** Check `AndroidManifest.xml` for duplicate entries

### Error: "Duplicate class found"
**Fix:** Run `.\gradlew clean` then rebuild

### Error: "Resource not found"
**Fix:** 
- Sync project with Gradle files
- **Build** → **Clean Project**
- **Build** → **Rebuild Project**

### Error: "AAPT2 error"
**Fix:** Enable legacy AAPT in `gradle.properties`:
```
android.enableAapt2=false
```

### Error: "Firebase" related errors
**Fix:** 
- The placeholder `google-services.json` is for demo only
- Either replace with your real Firebase config
- OR temporarily comment out Firebase dependencies in `app/build.gradle`

## Testing Without Firebase

If you don't have Firebase set up yet:

1. Open `app/build.gradle`
2. Comment out these lines (add `//` at the start):
```gradle
// id 'com.google.gms.google-services'  // Line 3

// implementation platform('com.google.firebase:firebase-bom:32.7.0')
// implementation 'com.google.firebase:firebase-database'
// implementation 'com.google.firebase:firebase-messaging'
// implementation 'com.google.firebase:firebase-auth'
```

3. Remove or comment out in `StudentLMSApplication.java`:
   - Firebase-related code in `onCreate()`

4. Rebuild the project

## Verify Installation

After installing the APK on your device:

1. Open the app
2. You should see:
   - ✅ Bottom navigation with 5 icons
   - ✅ Dashboard with greeting
   - ✅ "Coming Soon" placeholders for other tabs
3. Sample LMS assignments may appear (from placeholder data)

## Next Steps After Successful Build

1. **Add Real Firebase Config** - Replace `google-services.json`
2. **Integrate LMS APIs** - Add credentials to connectors
3. **Test LMS Sync** - Connect your Google Classroom/Moodle/Canvas
4. **Implement Study Plan** - Build calendar UI
5. **Add Resources** - Implement file picker and viewer

## Need Help?

Check the detailed documentation:
- [README.md](file:///d:/AI/StudentLMS/README.md) - Complete documentation
- [walkthrough.md](file:///C:/Users/sumit/.gemini/antigravity/brain/a9c51b49-f20a-40af-a84a-6c816e6dc336/walkthrough.md) - Project walkthrough

## Quick Commands Reference

```powershell
# Clean build
cd d:\AI\StudentLMS
.\gradlew clean

# Build debug APK (after Gradle wrapper exists)
.\gradlew assembleDebug

# Build release APK
.\gradlew assembleRelease

# Install on connected device
adb install app\build\outputs\apk\debug\app-debug.apk

# Uninstall from device
adb uninstall com.studentlms

# View device logs
adb logcat | Select-String "StudentLMS"
```

---

**Status**: ✅ Code is clean and ready to build!  
**Last Updated**: Fixed syntax error in StudyPlanFragment.java

Happy Building! 🚀
