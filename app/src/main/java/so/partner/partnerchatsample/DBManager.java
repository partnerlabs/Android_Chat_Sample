package so.partner.partnerchatsample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import so.partner.partnerchatsample.bean.TalkMessage;

public class DBManager extends SQLiteOpenHelper {

    private static final String TAG = DBManager.class.getSimpleName();

    private static final String DATABASE_NAME = "chat_example_db";
    private static final int DATABASE_VERSION = 1;

    // TABLE NAME & ATTRIBUTES
    private static final String TABLE_TALK_MESSAGE = "talk_message";
    private static final String TALK_MESSAGE_ATTRIBUTE_ID = "id";
    private static final String TALK_MESSAGE_ATTRIBUTE_TALK_ROOM_ID = "talk_room_id";
    private static final String TALK_MESSAGE_ATTRIBUTE_USER_ID = "user_id";
    private static final String TALK_MESSAGE_ATTRIBUTE_CONTENT = "content";
    private static final String TALK_MESSAGE_ATTRIBUTE_SEND_TIME = "send_time";

    private static final String TABLE_WAITTING_TALK_MESSAGE = "waitting_talk_message";
    private static final String WAITTING_TALK_MESSAGE_ATTRIBUTE_ID = "id";
    private static final String WAITTING_TALK_MESSAGE_ATTRIBUTE_TALK_ROOM_ID = "talk_room_id";
    private static final String WAITTING_TALK_MESSAGE_ATTRIBUTE_CONTENT = "content";

    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate");

        String SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_TALK_MESSAGE + " ("
                + TALK_MESSAGE_ATTRIBUTE_ID + " TEXT PRIMARY KEY NOT NULL, "
                + TALK_MESSAGE_ATTRIBUTE_TALK_ROOM_ID
                + " TEXT NOT NULL, " + TALK_MESSAGE_ATTRIBUTE_USER_ID
                + " TEXT NOT NULL, " + TALK_MESSAGE_ATTRIBUTE_CONTENT
                + " TEXT NOT NULL, " + TALK_MESSAGE_ATTRIBUTE_SEND_TIME
                + " LONG NOT NULL);";
        Log.d(TAG, "createTable: SQL=" + SQL);
        db.execSQL(SQL);

        SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_WAITTING_TALK_MESSAGE + " ("
                + WAITTING_TALK_MESSAGE_ATTRIBUTE_ID + " INTEGER PRIMARY KEY, "
                + WAITTING_TALK_MESSAGE_ATTRIBUTE_TALK_ROOM_ID
                + " TEXT NOT NULL, "
                + WAITTING_TALK_MESSAGE_ATTRIBUTE_CONTENT
                + " TEXT NOT NULL);";
        Log.d(TAG, "createTable: SQL=" + SQL);
        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade");
    }

    public boolean addTalkMessage(String id, String talkRoomId, String userId,
                                  String content, long sendTime) {
        boolean result = false;

        if (id == null) {
            return result;
        }

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + TABLE_TALK_MESSAGE + " WHERE " + TALK_MESSAGE_ATTRIBUTE_ID + "='" + id + "'", null);

            if (!c.moveToNext()) {
                ContentValues row = new ContentValues();

                row.put(TALK_MESSAGE_ATTRIBUTE_ID, id);
                row.put(TALK_MESSAGE_ATTRIBUTE_TALK_ROOM_ID, talkRoomId);
                row.put(TALK_MESSAGE_ATTRIBUTE_USER_ID, userId);
                row.put(TALK_MESSAGE_ATTRIBUTE_CONTENT, content);
                row.put(TALK_MESSAGE_ATTRIBUTE_SEND_TIME, sendTime);

                Log.d(TAG, "insertTuple: tableName=" + TABLE_TALK_MESSAGE + ", row=" + row.toString());
                db.insert(TABLE_TALK_MESSAGE, null, row);
                result = true;
            }
            c.close();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            result = false;
        } finally {
            db.endTransaction();
        }
        db.close();

        return result;
    }

    public int addWaittingTalkMessage(String talkRoomId, String content) {
        int result = -1;

        if (content == null || content.equals("")) {
            return result;
        }

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues row = new ContentValues();

            row.put(WAITTING_TALK_MESSAGE_ATTRIBUTE_TALK_ROOM_ID, talkRoomId);
            row.put(WAITTING_TALK_MESSAGE_ATTRIBUTE_CONTENT, content);

            Log.d(TAG, "insertTuple: tableName=" + TABLE_WAITTING_TALK_MESSAGE + ", row=" + row.toString());
            db.insert(TABLE_WAITTING_TALK_MESSAGE, null, row);

            Log.d(TAG, "getLastInsertKey");
            String SQL = "select last_insert_rowid();";
            Cursor c = db.rawQuery(SQL, null);
            if (c.moveToNext()) {
                result = c.getInt(0);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            result = -1;
        } finally {
            db.endTransaction();
        }
        db.close();

        return result;
    }

    public boolean endWaitting(int waittingMessageId, String id, long sendTime) {
        boolean result = false;

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            Cursor c = db.rawQuery("SELECT * FROM "
                    + TABLE_WAITTING_TALK_MESSAGE + " WHERE "
                    + WAITTING_TALK_MESSAGE_ATTRIBUTE_ID + "="
                    + waittingMessageId, null);

            if (c.moveToNext()) {
                String talkRoomId = c.getString(1);
                String content = c.getString(2);

                String SQL = "DELETE FROM " + TABLE_WAITTING_TALK_MESSAGE + " where " + WAITTING_TALK_MESSAGE_ATTRIBUTE_ID + "="
                        + waittingMessageId + ";";
                Log.d(TAG, "deleteTuple: SQL=" + SQL);
                db.execSQL(SQL);

                c.close();
                c = db.rawQuery("SELECT * FROM " + TABLE_TALK_MESSAGE
                        + " WHERE " + TALK_MESSAGE_ATTRIBUTE_ID + "='" + id
                        + "'", null);
                if (!c.moveToNext()) {
                    ContentValues row = new ContentValues();

                    row.put(TALK_MESSAGE_ATTRIBUTE_ID, id);
                    row.put(TALK_MESSAGE_ATTRIBUTE_TALK_ROOM_ID, talkRoomId);
                    row.put(TALK_MESSAGE_ATTRIBUTE_USER_ID,
                            ChatManager.getClientId());
                    row.put(TALK_MESSAGE_ATTRIBUTE_CONTENT, content);
                    row.put(TALK_MESSAGE_ATTRIBUTE_SEND_TIME, sendTime);

                    Log.d(TAG, "insertTuple: tableName=" + TABLE_TALK_MESSAGE + ", row=" + row.toString());
                    db.insert(TABLE_TALK_MESSAGE, null, row);
                    result = true;
                }
            }
            c.close();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            result = false;
        } finally {
            db.endTransaction();
        }
        db.close();

        return result;
    }

    public void updateMessage(String id, long sendTime) {
        if (id == null) {
            return;
        }

        SQLiteDatabase db = getWritableDatabase();

        String SQL = "UPDATE " + TABLE_TALK_MESSAGE + " SET " + TALK_MESSAGE_ATTRIBUTE_SEND_TIME + "='" + sendTime + "'" + " WHERE " + TALK_MESSAGE_ATTRIBUTE_ID + "='" + id + "';";
        Log.d(TAG, "updateTuple: SQL=" + SQL);

        db.execSQL(SQL);
    }

    public void removeTalkMessage(String id) {
        if (id == null) {
            return;
        }

        SQLiteDatabase db = getWritableDatabase();

        String SQL = "DELETE FROM " + TABLE_TALK_MESSAGE + " where " + TALK_MESSAGE_ATTRIBUTE_ID + "='" + id + "';";
        Log.d(TAG, "deleteTuple: SQL=" + SQL);

        db.execSQL(SQL);
        db.close();
    }

    public void removeWaittingTalkMessage(int id) {
        SQLiteDatabase db = getWritableDatabase();

        String SQL = "DELETE FROM " + TABLE_WAITTING_TALK_MESSAGE + " where " + WAITTING_TALK_MESSAGE_ATTRIBUTE_ID + "='" + id + "';";
        Log.d(TAG, "deleteTuple: SQL=" + SQL);
        db.close();
    }

    public void removeTalkMessages(String roomId) {
        SQLiteDatabase db = getWritableDatabase();

        String SQL = "DELETE FROM " + TABLE_TALK_MESSAGE + " where " + TALK_MESSAGE_ATTRIBUTE_TALK_ROOM_ID + "='" + roomId + "';";
        Log.d(TAG, "deleteTuple: SQL=" + SQL);
        SQL = "DELETE FROM " + TABLE_WAITTING_TALK_MESSAGE + " where " + WAITTING_TALK_MESSAGE_ATTRIBUTE_TALK_ROOM_ID + "='" + roomId + "';";
        Log.d(TAG, "deleteTuple: SQL=" + SQL);
        db.close();
    }

    public void clearTalkMessage() {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_TALK_MESSAGE);
        db.execSQL("DELETE FROM " + TABLE_WAITTING_TALK_MESSAGE);
        db.close();
    }

    public List<TalkMessage> getTalkMessageList(String roomId) {
        List<TalkMessage> result = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_TALK_MESSAGE
                + " WHERE " + TALK_MESSAGE_ATTRIBUTE_TALK_ROOM_ID + "='"
                + roomId + "'", null);
        while (c.moveToNext()) {
            TalkMessage talkMessage = new TalkMessage();

            talkMessage.id = c.getString(0);
            talkMessage.roomId = c.getString(1);
            talkMessage.userId = c.getString(2);
            talkMessage.content = c.getString(3);
            talkMessage.sendTime = c.getLong(4);

            result.add(talkMessage);
        }
        c.close();

        return result;
    }

    public ArrayList<TalkMessage> getWaittingTalkMessageList(String roomId) {
        ArrayList<TalkMessage> result = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_WAITTING_TALK_MESSAGE
                + " WHERE " + WAITTING_TALK_MESSAGE_ATTRIBUTE_TALK_ROOM_ID
                + "='" + roomId + "'", null);
        while (c.moveToNext()) {
            TalkMessage talkMessage = new TalkMessage();

            talkMessage.waittingId = c.getInt(0);
            talkMessage.roomId = c.getString(1);
            talkMessage.content = c.getString(2);

            result.add(talkMessage);
        }
        c.close();

        return result;
    }
}
