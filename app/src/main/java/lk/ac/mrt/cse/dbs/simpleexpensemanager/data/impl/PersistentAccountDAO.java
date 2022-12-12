package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType.EXPENSE;

public class PersistentAccountDAO implements AccountDAO {
    private SqliteHelper sqliteHelper;
    private SQLiteDatabase sqLiteDatabase;

    private String[] columns = {"accountNo", "bankName", "accountHolderName", "balance"};

    public PersistentAccountDAO(Context context) {
        this.sqliteHelper = new SqliteHelper(context);

    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> accountNumbers = new ArrayList<>();

        sqLiteDatabase = sqliteHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query("accounts", columns, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String accountNumber = cursor.getString(
                    cursor.getColumnIndexOrThrow("accountNo"));
            accountNumbers.add(accountNumber);
        }

        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accounts = new ArrayList<>();

        sqLiteDatabase = sqliteHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query("accounts", columns, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String accountNo = cursor.getString(cursor.getColumnIndex("accountNo"));
            String bankName = cursor.getString(cursor.getColumnIndex("bankName"));
            String accountHolderName = cursor.getString(cursor.getColumnIndex("accountHolderName"));
            double balance = cursor.getDouble(cursor.getColumnIndex("balance"));

            Account account = new Account(accountNo, bankName, accountHolderName, balance);

            accounts.add(account);

        }

        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        sqLiteDatabase = sqliteHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query("accounts", columns, "accountNo" + " = ?", new String[]{accountNo}, null, null, null);

        if (cursor == null){
            String msg = "Account " + accountNo + " is getAccount.";
            throw new InvalidAccountException(msg);
        }
        else {
            cursor.moveToFirst();

            Account account = new Account(accountNo,
                    cursor.getString(cursor.getColumnIndex("bankName")),
                    cursor.getString(cursor.getColumnIndex("accountHolderName")),
                    cursor.getDouble(cursor.getColumnIndex("balance")));
            return account;
        }



    }

    @Override
    public void addAccount(Account account) {
        sqLiteDatabase = sqliteHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("accountNo", account.getAccountNo());
        values.put("bankName", account.getBankName());
        values.put("accountHolderName", account.getAccountHolderName());
        values.put("balance", account.getBalance());

        sqLiteDatabase.insert("accounts", null, values);

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        sqLiteDatabase = sqliteHelper.getWritableDatabase();


        sqLiteDatabase.delete("accounts", "accountNo" + " = ?", new String[]{accountNo});


    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        List<String> accounts = getAccountNumbersList();

        sqLiteDatabase = sqliteHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query("accounts", new String[]{"balance"}, "accountNo" + " = ?", new String[]{accountNo}, null, null, null);

        double balance;
        if (cursor.moveToFirst())
            balance = cursor.getDouble(0);
        else {
            String msg = "Account " + accountNo + " is update.";
            throw new InvalidAccountException(msg);
        }

        if (expenseType == EXPENSE) {
            balance -= amount;
        } else {
            balance += amount;
        }

        ContentValues values = new ContentValues();
        values.put("balance", String.valueOf(balance));

        sqLiteDatabase.update("accounts", values, accountNo + " = ?", new String[]{accountNo});

    }
}
