package karbanovich.fit.bstu.contactsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button addContactActivity;
    private Button viewContactsActivity;

    private static final int REQUEST_CODE_READ_CONTACTS = 1;
    private static boolean READ_WRITE_CONTACTS_GRANTED = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addContactActivity = findViewById(R.id.btnAddContactActivity);
        viewContactsActivity = findViewById(R.id.btnViewContactsActivity);

        addContactActivity.setOnClickListener(view ->
                startActivity(new Intent(this, AddContactActivity.class)));
        viewContactsActivity.setOnClickListener(view ->
                startActivity(new Intent(this, ViewContactsActivity.class)));

        setPermission();
    }

    private void setPermission() {
        int hasReadContactPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

        if(hasReadContactPermission == PackageManager.PERMISSION_GRANTED)
            READ_WRITE_CONTACTS_GRANTED = true;
        else
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS},
                    REQUEST_CODE_READ_CONTACTS);

        addContactActivity.setEnabled(READ_WRITE_CONTACTS_GRANTED);
        viewContactsActivity.setEnabled(READ_WRITE_CONTACTS_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                READ_WRITE_CONTACTS_GRANTED = true;
            addContactActivity.setEnabled(READ_WRITE_CONTACTS_GRANTED);
            viewContactsActivity.setEnabled(READ_WRITE_CONTACTS_GRANTED);
        }
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }
}