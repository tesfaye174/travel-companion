package com.example.travelcompanion.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.travelcompanion.data.db.entities.TripEntity;
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
public final class TripDao_Impl implements TripDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TripEntity> __insertionAdapterOfTripEntity;

  private final EntityDeletionOrUpdateAdapter<TripEntity> __deletionAdapterOfTripEntity;

  private final EntityDeletionOrUpdateAdapter<TripEntity> __updateAdapterOfTripEntity;

  public TripDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTripEntity = new EntityInsertionAdapter<TripEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `trips` (`id`,`destination`,`startDate`,`endDate`,`type`,`isCompleted`,`totalDistance`,`totalTime`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TripEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getDestination() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getDestination());
        }
        statement.bindLong(3, entity.getStartDate());
        if (entity.getEndDate() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getEndDate());
        }
        if (entity.getType() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getType());
        }
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindDouble(7, entity.getTotalDistance());
        statement.bindLong(8, entity.getTotalTime());
      }
    };
    this.__deletionAdapterOfTripEntity = new EntityDeletionOrUpdateAdapter<TripEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `trips` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TripEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfTripEntity = new EntityDeletionOrUpdateAdapter<TripEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `trips` SET `id` = ?,`destination` = ?,`startDate` = ?,`endDate` = ?,`type` = ?,`isCompleted` = ?,`totalDistance` = ?,`totalTime` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TripEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getDestination() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getDestination());
        }
        statement.bindLong(3, entity.getStartDate());
        if (entity.getEndDate() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getEndDate());
        }
        if (entity.getType() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getType());
        }
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindDouble(7, entity.getTotalDistance());
        statement.bindLong(8, entity.getTotalTime());
        statement.bindLong(9, entity.getId());
      }
    };
  }

  @Override
  public Object insertTrip(final TripEntity trip, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTripEntity.insertAndReturnId(trip);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTrip(final TripEntity trip, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfTripEntity.handle(trip);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTrip(final TripEntity trip, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTripEntity.handle(trip);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TripEntity>> getAllTrips() {
    final String _sql = "SELECT * FROM trips ORDER BY startDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"trips"}, new Callable<List<TripEntity>>() {
      @Override
      @NonNull
      public List<TripEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDestination = CursorUtil.getColumnIndexOrThrow(_cursor, "destination");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfTotalDistance = CursorUtil.getColumnIndexOrThrow(_cursor, "totalDistance");
          final int _cursorIndexOfTotalTime = CursorUtil.getColumnIndexOrThrow(_cursor, "totalTime");
          final List<TripEntity> _result = new ArrayList<TripEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TripEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDestination;
            if (_cursor.isNull(_cursorIndexOfDestination)) {
              _tmpDestination = null;
            } else {
              _tmpDestination = _cursor.getString(_cursorIndexOfDestination);
            }
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final Long _tmpEndDate;
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmpEndDate = null;
            } else {
              _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            }
            final String _tmpType;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _cursor.getString(_cursorIndexOfType);
            }
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final double _tmpTotalDistance;
            _tmpTotalDistance = _cursor.getDouble(_cursorIndexOfTotalDistance);
            final long _tmpTotalTime;
            _tmpTotalTime = _cursor.getLong(_cursorIndexOfTotalTime);
            _item = new TripEntity(_tmpId,_tmpDestination,_tmpStartDate,_tmpEndDate,_tmpType,_tmpIsCompleted,_tmpTotalDistance,_tmpTotalTime);
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
  public Object getTripById(final long tripId, final Continuation<? super TripEntity> $completion) {
    final String _sql = "SELECT * FROM trips WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, tripId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TripEntity>() {
      @Override
      @Nullable
      public TripEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDestination = CursorUtil.getColumnIndexOrThrow(_cursor, "destination");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfTotalDistance = CursorUtil.getColumnIndexOrThrow(_cursor, "totalDistance");
          final int _cursorIndexOfTotalTime = CursorUtil.getColumnIndexOrThrow(_cursor, "totalTime");
          final TripEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDestination;
            if (_cursor.isNull(_cursorIndexOfDestination)) {
              _tmpDestination = null;
            } else {
              _tmpDestination = _cursor.getString(_cursorIndexOfDestination);
            }
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final Long _tmpEndDate;
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmpEndDate = null;
            } else {
              _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            }
            final String _tmpType;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _cursor.getString(_cursorIndexOfType);
            }
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final double _tmpTotalDistance;
            _tmpTotalDistance = _cursor.getDouble(_cursorIndexOfTotalDistance);
            final long _tmpTotalTime;
            _tmpTotalTime = _cursor.getLong(_cursorIndexOfTotalTime);
            _result = new TripEntity(_tmpId,_tmpDestination,_tmpStartDate,_tmpEndDate,_tmpType,_tmpIsCompleted,_tmpTotalDistance,_tmpTotalTime);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
