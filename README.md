# Student LMS - AI-Powered Learning Management System

An intelligent Android application that helps students manage their learning with AI-powered study plans, LMS integration, reminders, and real-time resource access.

## Features

### 📊 AI-Powered Dashboard
- Personalized study recommendations based on learning patterns
- Real-time progress tracking
- Study streak counter
- Weekly performance analytics

### 📚 External LMS Integration
- **Google Classroom** - Sync assignments and deadlines
- **Moodle** - Course modules and quizzes
- **Canvas** - Assignment groups and grades
- Countdown timers for upcoming deadlines
- Color-coded urgency indicators
- Automatic background sync every 4 hours

### 📅 Smart Study Planning
- AI-generated weekly study plans
- Calendar view with study sessions
- Progress tracking per subject
- Flexible editing and rescheduling

### 📖 Resource Library
- Support for PDFs, videos, web links, and notes
- Smart categorization by subject
- Search and filter functionality
- Offline access

### ⏰ Intelligent Reminders
- Study session alerts
- Assignment deadline notifications
- Custom reminders
- Smart notification timing

### 🎨 Modern UI/UX
- Material Design 3
- Dynamic color theming
- Smooth animations
- Responsive layouts

## Prerequisites

- Android Studio Arctic Fox or later
- JDK 8 or higher
- Android SDK (API 24+)
- Gradle 8.2+

## Setup Instructions

### 1. Clone or Extract the Project
```bash
cd d:\AI\StudentLMS
```

### 2. Configure Firebase (Required for real-time sync)

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or use existing one
3. Add an Android app with package name: `com.studentlms`
4. Download `google-services.json`
5. Replace the placeholder file at `app/google-services.json`

### 3. Configure LMS Integration (Optional)

#### Google Classroom
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create OAuth 2.0 credentials
3. Enable Google Classroom API
4. Add credentials to the app

#### Moodle
1. Enable Web Services on your Moodle instance
2. Generate a security token
3. Update `MoodleConnector.java` with your Moodle URL and token

#### Canvas
1. Generate API token from Canvas settings
2. Update `CanvasConnector.java` with your Canvas instance URL

### 4. Build the Project

#### Using Android Studio:
1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Click **Build > Make Project**
4. Click **Build > Build Bundle(s) / APK(s) > Build APK(s)**

#### Using Command Line:
```bash
# Debug build
gradlew assembleDebug

# Release build (requires signing)
gradlew assembleRelease
```

### 5. Install on Device
```bash
# Install debug APK
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Project Structure

```
StudentLMS/
├── app/
│   ├── src/main/
│   │   ├── java/com/studentlms/
│   │   │   ├── MainActivity.java
│   │   │   ├── StudentLMSApplication.java
│   │   │   ├── ui/              # UI Fragments
│   │   │   ├── data/            # Data models & database
│   │   │   ├── services/        # Background services & LMS connectors
│   │   │   └── receivers/       # Broadcast receivers
│   │   ├── res/                 # Resources (layouts, drawables, strings)
│   │   └── AndroidManifest.xml
│   ├── build.gradle
│   └── google-services.json
├── build.gradle
└── settings.gradle
```

## Key Components

### Data Layer
- **Room Database**: Local data persistence
- **DAOs**: Data access objects for database operations
- **Models**: Subject, StudySession, LMSAssignment, Reminder, Resource

### Services
- **LMSSyncWorker**: Background sync using WorkManager
- **NotificationService**: FCM push notifications
- **LMS Connectors**: Google Classroom, Moodle, Canvas integrators

### UI Layer
- **MainActivity**: Bottom navigation host
- **DashboardFragment**: AI insights and LMS assignments
- **StudyPlanFragment**: Calendar and study planning
- **ResourcesFragment**: Learning materials library
- **RemindersFragment**: Notification management
- **ProfileFragment**: Settings and LMS connections

## Technologies Used

- **Language**: Java
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room (SQLite)
- **Background Tasks**: WorkManager
- **Networking**: Retrofit, OkHttp
- **UI**: Material Design 3, RecyclerView, ViewPager2
- **Notifications**: Firebase Cloud Messaging
- **Charts**: MPAndroidChart
- **Image Loading**: Glide
- **AI/ML**: ML Kit

## Permissions Required

- `INTERNET` - Network communication
- `ACCESS_NETWORK_STATE` - Check network status
- `POST_NOTIFICATIONS` - Show notifications (Android 13+)
- `SCHEDULE_EXACT_ALARM` - Precise reminder timing
- `WAKE_LOCK` - Background sync
- `RECEIVE_BOOT_COMPLETED` - Restart services after reboot

## Troubleshooting

### Build Errors
- Ensure JDK 8+ is installed
- Run `gradlew clean` and rebuild
- Check internet connection for dependency downloads

### Firebase Issues
- Verify `google-services.json` is valid
- Check package name matches Firebase console
- Enable required Firebase services

### LMS Sync Not Working
- Check network connectivity
- Verify API credentials are correct
- Review Logcat for error messages

## Future Enhancements

- [ ] Offline AI recommendations
- [ ] Study group collaboration
- [ ] Voice-assisted study sessions
- [ ] Integration with more LMS platforms
- [ ] Gamification and achievements
- [ ] Export study reports

## License

This project is created for educational purposes.

## Support

For issues or questions, please check the implementation plan and code documentation.

---

**Built with ❤️ for Student Success**
