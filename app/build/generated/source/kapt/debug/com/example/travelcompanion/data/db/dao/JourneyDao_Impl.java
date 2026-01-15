package com.example.travelcompanion.data.db.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.travelcompanion.data.db.entities.JourneyEntity;
import com.example.travelcompanion.data.db.entities.NoteEntity;
import com.example.travelcompanion.data.db.entities.PhotoEntity;
import com.example.travelcompanion.data.db.entities.PointEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class JourneyDao_Impl implements JourneyDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<JourneyEntity> __insertionAdapterOfJourneyEntity;

  private final EntityInsertionAdapter<PointEntity> __insertionAdapterOfPointEntity;

  private final EntityInsertionAdapter<NoteEntity> __insertionAdapterOfNoteEntity;

  private final EntityInsertionAdapter<PhotoEntity> __insertionAdapterOfPhotoEntity;

  public JourneyDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfJourneyEntity = new EntityInsertionAdapter<JourneyEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `journeys` (`id`,`tripId`,`startTime`,`endTime`,`distance`,`time`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final JourneyEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getTripId());
        statement.bindLong(3, entity.getStartTime());
        if (entity.getEndTime() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getEndTime());
        }
        statement.bindDouble(5, entity.getDistance());
        statement.bindLong(6, entity.getTime());
      }
    };
    this.__insertionAdapterOfPointEntity = new EntityInsertionAdapter<PointEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `points` (`id`,`journeyId`,`latitude`,`longitude`,`timestamp`,`speed`,`accuracy`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PointEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getJourneyId());
        statement.bindDouble(3, entity.getLatitude());
        statement.bindDouble(4, entity.getLongitude());
        statement.bindLong(5, entity.getTimestamp());
        statement.bindDouble(6, entity.getSpeed());
        statement.bindDouble(7, entity.getAccuracy());
      }
    };
    this.__insertionAdapterOfNoteEntity = new EntityInsertionAdapter<NoteEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `notes` (`id`,`tripId`,`journeyId`,`pointId`,`content`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final NoteEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getTripId());
        if (entity.getJourneyId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getJourneyId());
        }
        if (entity.getPointId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getPointId());
        }
        if (entity.getContent() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getContent());
        }
        statement.bindLong(6, entity.getTimestamp());
      }
    };
    this.__insertionAdapterOfPhotoEntity = new EntityInsertionAdapter<PhotoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `photos` (`id`,`tripId`,`journeyId`,`pointId`,`filePath`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PhotoEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getTripId());
        if (entity.getJourneyId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getJourneyId());
        }
        if (entity.getPointId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getPointId());
        }
        if (entity.getFilePath() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getFilePath());
        }
        statement.bindLong(6, entity.getTimestamp());
      }
    };
  }

  @Override
  public Object insertJourney(final JourneyEntity journey,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfJourneyEntity.insertAndReturnId(journey);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertPoint(final PointEntity point, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPointEntity.insert(point);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertNote(final NoteEntity note, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfNoteEntity.insert(note);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertPhoto(final PhotoEntity photo, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPhotoEntity.insert(photo);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<JourneyEntity>> getJourneysByTrip(final long tripId) {
    final String _sql = "SELECT * FROM journeys WHERE tripId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, tripId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"journeys"}, new Callable<List<JourneyEntity>>() {
      @Override
      @NonNull
      public List<JourneyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTripId = CursorUtil.getColumnIndexOrThrow(_cursor, "tripId");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final int _cursorIndexOfDistance = CursorUtil.getColumnIndexOrThrow(_cursor, "distance");
          final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
          final List<JourneyEntity> _result = new ArrayList<JourneyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final JourneyEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTripId;
            _tmpTripId = _cursor.getLong(_cursorIndexOfTripId);
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final Long _tmpEndTime;
            if (_cursor.isNull(_cursorIndexOfEndTime)) {
              _tmpEndTime = null;
            } else {
              _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            }
            final double _tmpDistance;
            _tmpDistance = _cursor.getDouble(_cursorIndexOfDistance);
            final long _tmpTime;
            _tmpTime = _cursor.getLong(_cursorIndexOfTime);
            _item = new JourneyEntity(_tmpId,_tmpTripId,_tmpStartTime,_tmpEndTime,_tmpDistance,_tmpTime);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<PointEntity>> getPointsByJourney(final long journeyId) {
    final String _sql = "SELECT * FROM points WHERE journeyId = ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, journeyId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"points"}, new Callable<List<PointEntity>>() {
      @Override
      @NonNull
      public List<PointEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfJourneyId = CursorUtil.getColumnIndexOrThrow(_cursor, "journeyId");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSpeed = CursorUtil.getColumnIndexOrThrow(_cursor, "speed");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final List<PointEntity> _result = new ArrayList<PointEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PointEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpJourneyId;
            _tmpJourneyId = _cursor.getLong(_cursorIndexOfJourneyId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final float _tmpSpeed;
            _tmpSpeed = _cursor.getFloat(_cursorIndexOfSpeed);
            final float _tmpAccuracy;
            _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            _item = new PointEntity(_tmpId,_tmpJourneyId,_tmpLatitude,_tmpLongitude,_tmpTimestamp,_tmpSpeed,_tmpAccuracy);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<NoteEntity>> getNotesByTrip(final long tripId) {
    final String _sql = "SELECT * FROM notes WHERE tripId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, tripId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"notes"}, new Callable<List<NoteEntity>>() {
      @Override
      @NonNull
      public List<NoteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTripId = CursorUtil.getColumnIndexOrThrow(_cursor, "tripId");
          final int _cursorIndexOfJourneyId = CursorUtil.getColumnIndexOrThrow(_cursor, "journeyId");
          final int _cursorIndexOfPointId = CursorUtil.getColumnIndexOrThrow(_cursor, "pointId");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<NoteEntity> _result = new ArrayList<NoteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NoteEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTripId;
            _tmpTripId = _cursor.getLong(_cursorIndexOfTripId);
            final Long _tmpJourneyId;
            if (_cursor.isNull(_cursorIndexOfJourneyId)) {
              _tmpJourneyId = null;
            } else {
              _tmpJourneyId = _cursor.getLong(_cursorIndexOfJourneyId);
            }
            final Long _tmpPointId;
            if (_cursor.isNull(_cursorIndexOfPointId)) {
              _tmpPointId = null;
            } else {
              _tmpPointId = _cursor.getLong(_cursorIndexOfPointId);
            }
            final String _tmpContent;
            if (_cursor.isNull(_cursorIndexOfContent)) {
              _tmpContent = null;
            } else {
              _tmpContent = _cursor.getString(_cursorIndexOfContent);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new NoteEntity(_tmpId,_tmpTripId,_tmpJourneyId,_tmpPointId,_tmpContent,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<PhotoEntity>> getPhotosByTrip(final long tripId) {
    final String _sql = "SELECT * FROM photos WHERE tripId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, tripId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"photos"}, new Callable<List<PhotoEntity>>() {
      @Override
      @NonNull
      public List<PhotoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTripId = CursorUtil.getColumnIndexOrThrow(_cursor, "tripId");
          final int _cursorIndexOfJourneyId = CursorUtil.getColumnIndexOrThrow(_cursor, "journeyId");
          final int _cursorIndexOfPointId = CursorUtil.getColumnIndexOrThrow(_cursor, "pointId");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<PhotoEntity> _result = new ArrayList<PhotoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PhotoEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTripId;
            _tmpTripId = _cursor.getLong(_cursorIndexOfTripId);
            final Long _tmpJourneyId;
            if (_cursor.isNull(_cursorIndexOfJourneyId)) {
              _tmpJourneyId = null;
            } else {
              _tmpJourneyId = _cursor.getLong(_cursorIndexOfJourneyId);
            }
            final Long _tmpPointId;
            if (_cursor.isNull(_cursorIndexOfPointId)) {
              _tmpPointId = null;
            } else {
              _tmpPointId = _cursor.getLong(_cursorIndexOfPointId);
            }
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new PhotoEntity(_tmpId,_tmpTripId,_tmpJourneyId,_tmpPointId,_tmpFilePath,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
