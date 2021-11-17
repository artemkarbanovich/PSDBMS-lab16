package karbanovich.fit.bstu.contactsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.time.DayOfWeek;
import java.util.ArrayList;

public class ViewContactsActivity extends AppCompatActivity {

    private EditText searchPhoneNumber;
    private ListView viewContacts;

    private ArrayList<Contact> contacts;
    private ContactsListAdapter contactsListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contacts);

        searchPhoneNumber = findViewById(R.id.edtSearchPhoneNumber);
        viewContacts = findViewById(R.id.listViewContacts);

        getContacts();
        contactsListAdapter = new ContactsListAdapter(this, contacts);
        viewContacts.setAdapter(contactsListAdapter);

        findViewById(R.id.btnSearchByPhoneNumber).setOnClickListener(view ->
                contactsListAdapter.searchContactsByPhoneNumber(getSearchContacts()));
    }

    private void getContacts() {
        contacts = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[] {
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                }, null, null, null);

        if(cursor.getCount() == 0) return;

        while(cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

            Contact contact = new Contact(Integer.parseInt(id), name, phoneNumber);
            contacts.add(contact);
        }
        cursor.close();
    }

    private ArrayList<Contact> getSearchContacts() {
        String searchStr = searchPhoneNumber.getText().toString()
                .replace(" ", "")
                .replace("(", "")
                .replace(")", "")
                .replace("-", "");

        ArrayList<Contact> searchContacts = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[] {
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                },
                "replace(replace(replace(replace(" + ContactsContract.CommonDataKinds.Phone.NUMBER + ", ' ',''), '(',''), ')',''), '-','')" +
                        " like '%" + searchStr + "%'",
                null, null);

        if(cursor.getCount() == 0) return searchContacts;

        while(cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

            Contact contact = new Contact(Integer.parseInt(id), name, phoneNumber);
            searchContacts.add(contact);
        }
        cursor.close();

        return searchContacts;
    }

    public class ContactsListAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<Contact> contacts;


        public ContactsListAdapter(Context context, ArrayList<Contact> contacts) {
            this.context = context;
            this.contacts = contacts;
        }

        @Override
        public int getCount() {
            return contacts.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public void searchContactsByPhoneNumber(ArrayList<Contact> contacts) {
            this.contacts.clear();
            this.contacts.addAll(contacts);
            this.notifyDataSetChanged();
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup viewGroup) {
            View view = getLayoutInflater().inflate(R.layout.contact_item, null);

            TextView fullName = view.findViewById(R.id.txtContactItemFullName);
            TextView phoneNumber = view.findViewById(R.id.txtContactItemPhoneNumber);

            fullName.setText(contacts.get(pos).getName());
            phoneNumber.setText(contacts.get(pos).getPhoneNumber());

            view.setOnClickListener(v -> {
                Intent intent = new Intent(ViewContactsActivity.this, SelectedContactActivity.class);
                intent.putExtra("contact", contacts.get(pos));
                startActivity(intent);
            });

            return view;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}