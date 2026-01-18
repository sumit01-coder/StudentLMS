package com.studentlms.data.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.studentlms.data.dao.ChatMessageDao;
import com.studentlms.data.dao.ChatMessageDao_Impl;
import com.studentlms.data.dao.LMSAccountDao;
import com.studentlms.data.dao.LMSAccountDao_Impl;
import com.studentlms.data.dao.LMSAssignmentDao;
import com.studentlms.data.dao.LMSAssignmentDao_Impl;
import com.studentlms.data.dao.ReminderDao;
import com.studentlms.data.dao.ReminderDao_Impl;
import com.studentlms.data.dao.ResourceDao;
import com.studentlms.data.dao.ResourceDao_Impl;
import com.studentlms.data.dao.StudySessionDao;
import com.studentlms.data.dao.StudySessionDao_Impl;
import com.studentlms.data.dao.SubjectDao;
import com.studentlms.data.dao.SubjectDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile SubjectDao _subjectDao;

  private volatile LMSAssignmentDao _lMSAssignmentDao;

  private volatile LMSAccountDao _lMSAccountDao;

  private volatile ReminderDao _reminderDao;

  private volatile ResourceDao _resourceDao;

  private volatile StudySessionDao _studySessionDao;

  private volatile ChatMessageDao _chatMessageDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(3) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `subjects` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `color` TEXT, `priority` INTEGER NOT NULL, `totalHours` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `study_sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `subjectId` INTEGER NOT NULL, `startTime` INTEGER NOT NULL, `endTime` INTEGER NOT NULL, `notes` TEXT, `completed` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `lms_assignments` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `lmsType` TEXT, `lmsId` TEXT, `courseId` TEXT, `courseName` TEXT, `title` TEXT, `description` TEXT, `dueDate` INTEGER NOT NULL, `submissionUrl` TEXT, `isSubmitted` INTEGER NOT NULL, `syncedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `lms_accounts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `lmsType` TEXT, `email` TEXT, `accessToken` TEXT, `refreshToken` TEXT, `lastSyncTime` INTEGER NOT NULL, `isActive` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `reminders` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT, `description` TEXT, `dateTime` INTEGER NOT NULL, `type` TEXT, `isRecurring` INTEGER NOT NULL, `relatedId` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `resources` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT, `type` TEXT, `urlOrPath` TEXT, `subjectId` INTEGER NOT NULL, `addedDate` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `chat_messages` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `message` TEXT, `isUser` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `sessionId` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '0e0635cbfaa0071a51c21361655be64e')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `subjects`");
        db.execSQL("DROP TABLE IF EXISTS `study_sessions`");
        db.execSQL("DROP TABLE IF EXISTS `lms_assignments`");
        db.execSQL("DROP TABLE IF EXISTS `lms_accounts`");
        db.execSQL("DROP TABLE IF EXISTS `reminders`");
        db.execSQL("DROP TABLE IF EXISTS `resources`");
        db.execSQL("DROP TABLE IF EXISTS `chat_messages`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsSubjects = new HashMap<String, TableInfo.Column>(5);
        _columnsSubjects.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSubjects.put("name", new TableInfo.Column("name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSubjects.put("color", new TableInfo.Column("color", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSubjects.put("priority", new TableInfo.Column("priority", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSubjects.put("totalHours", new TableInfo.Column("totalHours", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSubjects = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSubjects = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSubjects = new TableInfo("subjects", _columnsSubjects, _foreignKeysSubjects, _indicesSubjects);
        final TableInfo _existingSubjects = TableInfo.read(db, "subjects");
        if (!_infoSubjects.equals(_existingSubjects)) {
          return new RoomOpenHelper.ValidationResult(false, "subjects(com.studentlms.data.models.Subject).\n"
                  + " Expected:\n" + _infoSubjects + "\n"
                  + " Found:\n" + _existingSubjects);
        }
        final HashMap<String, TableInfo.Column> _columnsStudySessions = new HashMap<String, TableInfo.Column>(6);
        _columnsStudySessions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStudySessions.put("subjectId", new TableInfo.Column("subjectId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStudySessions.put("startTime", new TableInfo.Column("startTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStudySessions.put("endTime", new TableInfo.Column("endTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStudySessions.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStudySessions.put("completed", new TableInfo.Column("completed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysStudySessions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesStudySessions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoStudySessions = new TableInfo("study_sessions", _columnsStudySessions, _foreignKeysStudySessions, _indicesStudySessions);
        final TableInfo _existingStudySessions = TableInfo.read(db, "study_sessions");
        if (!_infoStudySessions.equals(_existingStudySessions)) {
          return new RoomOpenHelper.ValidationResult(false, "study_sessions(com.studentlms.data.models.StudySession).\n"
                  + " Expected:\n" + _infoStudySessions + "\n"
                  + " Found:\n" + _existingStudySessions);
        }
        final HashMap<String, TableInfo.Column> _columnsLmsAssignments = new HashMap<String, TableInfo.Column>(11);
        _columnsLmsAssignments.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLmsAssignments.put("lmsType", new TableInfo.Column("lmsType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLmsAssignments.put("lmsId", new TableInfo.Column("lmsId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLmsAssignments.put("courseId", new TableInfo.Column("courseId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLmsAssignments.put("courseName", new TableInfo.Column("courseName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLmsAssignments.put("title", new TableInfo.Column("title", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLmsAssignments.put("description", new TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLmsAssignments.put("dueDate", new TableInfo.Column("dueDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLmsAssignments.put("submissionUrl", new TableInfo.Column("submissionUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLmsAssignments.put("isSubmitted", new TableInfo.Column("isSubmitted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLmsAssignments.put("syncedAt", new TableInfo.Column("syncedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLmsAssignments = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLmsAssignments = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLmsAssignments = new TableInfo("lms_assignments", _columnsLmsAssignments, _foreignKeysLmsAssignments, _indicesLmsAssignments);
        final TableInfo _existingLmsAssignments = TableInfo.read(db, "lms_assignments");
        if (!_infoLmsAssignments.equals(_existingLmsAssignments)) {
          return new RoomOpenHelper.ValidationResult(false, "lms_assignments(com.studentlms.data.models.LMSAssignment).\n"
                  + " Expected:\n" + _infoLmsAssignments + "\n"
                  + " Found:\n" + _existingLmsAssignments);
        }
        final HashMap<String, TableInfo.Column> _columnsLmsAccounts = new HashMap<String, TableInfo.Column>(7);
        _columnsLmsAccounts.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLmsAccounts.put("lmsType", new TableInfo.Column("lmsType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLmsAccounts.put("email", new TableInfo.Column("email", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLmsAccounts.put("accessToken", new TableInfo.Column("accessToken", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLmsAccounts.put("refreshToken", new TableInfo.Column("refreshToken", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLmsAccounts.put("lastSyncTime", new TableInfo.Column("lastSyncTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLmsAccounts.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLmsAccounts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLmsAccounts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLmsAccounts = new TableInfo("lms_accounts", _columnsLmsAccounts, _foreignKeysLmsAccounts, _indicesLmsAccounts);
        final TableInfo _existingLmsAccounts = TableInfo.read(db, "lms_accounts");
        if (!_infoLmsAccounts.equals(_existingLmsAccounts)) {
          return new RoomOpenHelper.ValidationResult(false, "lms_accounts(com.studentlms.data.models.LMSAccount).\n"
                  + " Expected:\n" + _infoLmsAccounts + "\n"
                  + " Found:\n" + _existingLmsAccounts);
        }
        final HashMap<String, TableInfo.Column> _columnsReminders = new HashMap<String, TableInfo.Column>(7);
        _columnsReminders.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReminders.put("title", new TableInfo.Column("title", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReminders.put("description", new TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReminders.put("dateTime", new TableInfo.Column("dateTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReminders.put("type", new TableInfo.Column("type", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReminders.put("isRecurring", new TableInfo.Column("isRecurring", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReminders.put("relatedId", new TableInfo.Column("relatedId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReminders = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesReminders = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoReminders = new TableInfo("reminders", _columnsReminders, _foreignKeysReminders, _indicesReminders);
        final TableInfo _existingReminders = TableInfo.read(db, "reminders");
        if (!_infoReminders.equals(_existingReminders)) {
          return new RoomOpenHelper.ValidationResult(false, "reminders(com.studentlms.data.models.Reminder).\n"
                  + " Expected:\n" + _infoReminders + "\n"
                  + " Found:\n" + _existingReminders);
        }
        final HashMap<String, TableInfo.Column> _columnsResources = new HashMap<String, TableInfo.Column>(6);
        _columnsResources.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsResources.put("title", new TableInfo.Column("title", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsResources.put("type", new TableInfo.Column("type", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsResources.put("urlOrPath", new TableInfo.Column("urlOrPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsResources.put("subjectId", new TableInfo.Column("subjectId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsResources.put("addedDate", new TableInfo.Column("addedDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysResources = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesResources = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoResources = new TableInfo("resources", _columnsResources, _foreignKeysResources, _indicesResources);
        final TableInfo _existingResources = TableInfo.read(db, "resources");
        if (!_infoResources.equals(_existingResources)) {
          return new RoomOpenHelper.ValidationResult(false, "resources(com.studentlms.data.models.Resource).\n"
                  + " Expected:\n" + _infoResources + "\n"
                  + " Found:\n" + _existingResources);
        }
        final HashMap<String, TableInfo.Column> _columnsChatMessages = new HashMap<String, TableInfo.Column>(5);
        _columnsChatMessages.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("message", new TableInfo.Column("message", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("isUser", new TableInfo.Column("isUser", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("sessionId", new TableInfo.Column("sessionId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysChatMessages = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesChatMessages = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoChatMessages = new TableInfo("chat_messages", _columnsChatMessages, _foreignKeysChatMessages, _indicesChatMessages);
        final TableInfo _existingChatMessages = TableInfo.read(db, "chat_messages");
        if (!_infoChatMessages.equals(_existingChatMessages)) {
          return new RoomOpenHelper.ValidationResult(false, "chat_messages(com.studentlms.data.models.ChatMessage).\n"
                  + " Expected:\n" + _infoChatMessages + "\n"
                  + " Found:\n" + _existingChatMessages);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "0e0635cbfaa0071a51c21361655be64e", "dd3265eea4323d17f9f97f68ae35ca04");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "subjects","study_sessions","lms_assignments","lms_accounts","reminders","resources","chat_messages");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `subjects`");
      _db.execSQL("DELETE FROM `study_sessions`");
      _db.execSQL("DELETE FROM `lms_assignments`");
      _db.execSQL("DELETE FROM `lms_accounts`");
      _db.execSQL("DELETE FROM `reminders`");
      _db.execSQL("DELETE FROM `resources`");
      _db.execSQL("DELETE FROM `chat_messages`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(SubjectDao.class, SubjectDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(LMSAssignmentDao.class, LMSAssignmentDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(LMSAccountDao.class, LMSAccountDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ReminderDao.class, ReminderDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ResourceDao.class, ResourceDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(StudySessionDao.class, StudySessionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ChatMessageDao.class, ChatMessageDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public SubjectDao subjectDao() {
    if (_subjectDao != null) {
      return _subjectDao;
    } else {
      synchronized(this) {
        if(_subjectDao == null) {
          _subjectDao = new SubjectDao_Impl(this);
        }
        return _subjectDao;
      }
    }
  }

  @Override
  public LMSAssignmentDao lmsAssignmentDao() {
    if (_lMSAssignmentDao != null) {
      return _lMSAssignmentDao;
    } else {
      synchronized(this) {
        if(_lMSAssignmentDao == null) {
          _lMSAssignmentDao = new LMSAssignmentDao_Impl(this);
        }
        return _lMSAssignmentDao;
      }
    }
  }

  @Override
  public LMSAccountDao lmsAccountDao() {
    if (_lMSAccountDao != null) {
      return _lMSAccountDao;
    } else {
      synchronized(this) {
        if(_lMSAccountDao == null) {
          _lMSAccountDao = new LMSAccountDao_Impl(this);
        }
        return _lMSAccountDao;
      }
    }
  }

  @Override
  public ReminderDao reminderDao() {
    if (_reminderDao != null) {
      return _reminderDao;
    } else {
      synchronized(this) {
        if(_reminderDao == null) {
          _reminderDao = new ReminderDao_Impl(this);
        }
        return _reminderDao;
      }
    }
  }

  @Override
  public ResourceDao resourceDao() {
    if (_resourceDao != null) {
      return _resourceDao;
    } else {
      synchronized(this) {
        if(_resourceDao == null) {
          _resourceDao = new ResourceDao_Impl(this);
        }
        return _resourceDao;
      }
    }
  }

  @Override
  public StudySessionDao studySessionDao() {
    if (_studySessionDao != null) {
      return _studySessionDao;
    } else {
      synchronized(this) {
        if(_studySessionDao == null) {
          _studySessionDao = new StudySessionDao_Impl(this);
        }
        return _studySessionDao;
      }
    }
  }

  @Override
  public ChatMessageDao chatMessageDao() {
    if (_chatMessageDao != null) {
      return _chatMessageDao;
    } else {
      synchronized(this) {
        if(_chatMessageDao == null) {
          _chatMessageDao = new ChatMessageDao_Impl(this);
        }
        return _chatMessageDao;
      }
    }
  }
}
