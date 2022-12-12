package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class SqliteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "200422A.sqlite";

    public SqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE accounts(accountNo TEXT PRIMARY KEY, bankName TEXT, accountHolderName TEXT, balance REAL)");

        sqLiteDatabase.execSQL("CREATE TABLE transactions(id INTEGER PRIMARY KEY AUTOINCREMENT, date" + " TEXT NOT NULL, expenseType TEXT NOT NULL,amount" + " REAL NOT NULL, accountNo" + " TEXT, FOREIGN KEY (accountNo) REFERENCES accounts (accountNo))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "accounts");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "transactions");

        // create new tables
        onCreate(sqLiteDatabase);
    }

}
