package com.nissa.loginsqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log; // Import untuk logging

public class DatabaseHelper extends SQLiteOpenHelper {

    // Konstanta untuk nama database dan versi
    private static final String DATABASE_NAME = "users_db";
    private static final int DATABASE_VERSION = 1;

    // Konstanta untuk nama tabel dan kolom
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    // Tag untuk logging
    private static final String TAG = "DatabaseHelper";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Query untuk membuat tabel users
        String createTableQuery = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT NOT NULL," + // Menambahkan NOT NULL
                COLUMN_EMAIL + " TEXT UNIQUE NOT NULL," + // Menambahkan UNIQUE dan NOT NULL
                COLUMN_PASSWORD + " TEXT NOT NULL)"; // Menambahkan NOT NULL
        try {
            db.execSQL(createTableQuery);
            Log.d(TAG, "Table " + TABLE_USERS + " created successfully.");
        } catch (Exception e) {
            Log.e(TAG, "Error creating table " + TABLE_USERS + ": " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Query untuk menghapus tabel jika sudah ada dan membuatnya kembali
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            Log.d(TAG, "Table " + TABLE_USERS + " dropped (if exists).");
            onCreate(db);
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading database: " + e.getMessage());
        }
    }

    /**
     * Mendaftarkan pengguna baru ke database.
     *
     * @param name Nama pengguna.
     * @param email Email pengguna (harus unik).
     * @param password Password pengguna.
     * @return true jika registrasi berhasil, false jika email sudah ada atau terjadi kesalahan.
     */
    public boolean registerUser(String name, String email, String password) {
        // Periksa apakah email sudah ada sebelum mencoba registrasi
        if (checkEmail(email)) {
            Log.d(TAG, "Registration failed: Email " + email + " already exists.");
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_EMAIL, email);
        cv.put(COLUMN_PASSWORD, password);

        long result = -1; // Default value for failure
        try {
            result = db.insert(TABLE_USERS, null, cv);
            if (result != -1) {
                Log.d(TAG, "User registered successfully: " + email);
            } else {
                Log.e(TAG, "Failed to register user: " + email);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error inserting user into database: " + e.getMessage());
        } finally {
            db.close(); // Pastikan database ditutup
        }
        return result != -1;
    }

    /**
     * Memeriksa apakah email sudah ada di database.
     *
     * @param email Email yang akan diperiksa.
     * @return true jika email sudah ada, false jika tidak.
     */
    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean exists = false;
        try {
            cursor = db.rawQuery("SELECT " + COLUMN_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=?", new String[]{email});
            exists = cursor.getCount() > 0;
            Log.d(TAG, "Check email " + email + ": " + (exists ? "exists" : "not exists"));
        } catch (Exception e) {
            Log.e(TAG, "Error checking email existence: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close(); // Pastikan cursor ditutup
            }
            db.close(); // Pastikan database ditutup
        }
        return exists;
    }

    /**
     * Memeriksa apakah kombinasi email dan password valid.
     *
     * @param email Email pengguna.
     * @param password Password pengguna.
     * @return true jika kombinasi email dan password cocok, false jika tidak.
     */
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean isValidUser = false;
        try {
            cursor = db.rawQuery("SELECT " + COLUMN_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?", new String[]{email, password});
            isValidUser = cursor.getCount() > 0;
            Log.d(TAG, "Check user " + email + ": " + (isValidUser ? "valid" : "invalid"));
        } catch (Exception e) {
            Log.e(TAG, "Error checking user credentials: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close(); // Pastikan cursor ditutup
            }
            db.close(); // Pastikan database ditutup
        }
        return isValidUser;
    }

    /**
     * Mengambil nama pengguna berdasarkan email.
     *
     * @param email Email pengguna.
     * @return Nama pengguna jika ditemukan, null jika tidak.
     */
    public String getNameByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String name = null;
        try {
            cursor = db.rawQuery("SELECT " + COLUMN_NAME + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=?", new String[]{email});
            if (cursor != null && cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)); // Menggunakan getColumnIndexOrThrow
                Log.d(TAG, "Name found for " + email + ": " + name);
            } else {
                Log.d(TAG, "No name found for email: " + email);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting name by email: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close(); // Pastikan cursor ditutup
            }
            db.close(); // Pastikan database ditutup
        }
        return name;
    }
}