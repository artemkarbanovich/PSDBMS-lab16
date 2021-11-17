package karbanovich.fit.bstu.contactsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class SelectedContactActivity extends AppCompatActivity {

    private EditText fullName;
    private EditText phoneNumber;

    private Contact selectedContact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_contact);

        fullName = findViewById(R.id.edtSelectedContactFullName);
        phoneNumber = findViewById(R.id.edtSelectedContactPhoneNumber);

        Intent intent = getIntent();
        selectedContact = (Contact) intent.getSerializableExtra("contact");

        fullName.setText(selectedContact.getName());
        phoneNumber.setText(selectedContact.getPhoneNumber());

        findViewById(R.id.btnEditContact).setOnClickListener(view -> editContactAction());
        findViewById(R.id.btnDeleteContact).setOnClickListener(view -> deleteContactAction());
    }

    private void editContactAction() {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        ops.add(ContentProviderOperation
                .newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.CONTACT_ID + " = ? and " +
                        ContactsContract.Data.MIMETYPE + " = ?",
                        new String[] { String.valueOf(selectedContact.getId()), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE })
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, fullName.getText().toString())
                .withValue(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME, "")
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, "")
                .build());
        ops.add(ContentProviderOperation
                .newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.CONTACT_ID + " = ? and " +
                                ContactsContract.Data.MIMETYPE + " = ?",
                        new String[] { String.valueOf(selectedContact.getId()), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE })
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber.getText().toString())
                .build());

        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            Toast.makeText(this, "Контакт изменен", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка изменения", Toast.LENGTH_SHORT).show();
            Log.d("ERROR editing contact", e.getMessage());
        }
    }

    private void deleteContactAction() {
        try {
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);

            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                String lookupKey = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.Contacts.LOOKUP_KEY));
                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);

                if(selectedContact.getId() == Integer.parseInt(id)) {
                    cr.delete(uri, null, null);
                    break;
                }
            }
            cur.close();

            Toast.makeText(this, "Контакт удален", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ViewContactsActivity.class));
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
            Log.d("ERROR deleting contact", e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ViewContactsActivity.class));
    }
}