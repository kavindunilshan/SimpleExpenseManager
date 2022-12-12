package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType.EXPENSE;

public class PersistentTransactionDAO implements TransactionDAO {
    private SqliteHelper sqliteHelper;
    private SQLiteDatabase sqLiteDatabase;

    private String[] columns = {"date", "accountNo", "expenseType", "amount"};

    public PersistentTransactionDAO(Context context) {
        this.sqliteHelper = new SqliteHelper(context);

    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        sqLiteDatabase = sqliteHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        values.put("date", format.format(date));
        values.put("accountNo", accountNo);
        values.put("expenseType", String.valueOf(expenseType));
        values.put("amount", amount);

        sqLiteDatabase.insert("transactions", null, values);

    }

    @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {
        List<Transaction> transactions = new ArrayList<>();

        sqLiteDatabase = sqliteHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query("transactions", columns, null, null, null, null, null);

        while (cursor.moveToNext()) {
            Date date = new SimpleDateFormat("dd-mm-yyyy").parse(cursor.getString(cursor.getColumnIndex("date")));
            String accountNo = cursor.getString(cursor.getColumnIndex("accountNo"));
            ExpenseType expenseType = ExpenseType.valueOf(cursor.getString(cursor.getColumnIndex("expenseType")));
            double amount = cursor.getDouble(cursor.getColumnIndex("amount"));

            Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
            transactions.add(transaction);
        }

        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {
        List<Transaction> transactions = new ArrayList<>();

        sqLiteDatabase = sqliteHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query("transactions", columns, null, null, null, null, null, String.valueOf(limit));

        while (cursor.moveToNext()) {
            Date date = new SimpleDateFormat("dd-mm-yyyy").parse(cursor.getString(cursor.getColumnIndex("date")));
            String accountNo = cursor.getString(cursor.getColumnIndex("accountNo"));
            String transactionType = cursor.getString(cursor.getColumnIndex("expenseType"));
            ExpenseType expenseType = ExpenseType.valueOf(transactionType);
            // System.out.println(".............................................................................................................." + cursor.getString(cursor.getColumnIndex("expenseType")) + "....");
            double amount = cursor.getDouble(cursor.getColumnIndex("amount"));

            Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
            transactions.add(transaction);
        }

        return transactions;
    }
}
