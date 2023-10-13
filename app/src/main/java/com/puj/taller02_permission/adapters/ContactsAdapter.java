package com.puj.taller02_permission.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.puj.taller02_permission.databinding.ContactsAdapterBinding;

public class ContactsAdapter extends CursorAdapter {

    private final static int CONTACT_ID_INDEX = 0;
    public static final String[] PROYECTION = new String[]{
            ContactsContract.Profile._ID,
            ContactsContract.Contacts.PHOTO_URI,
            ContactsContract.Contacts.DISPLAY_NAME
    };

    public ContactsAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return ContactsAdapterBinding.inflate(LayoutInflater.from(context), viewGroup, false).getRoot();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ContactsAdapterBinding binding =  ContactsAdapterBinding.bind(view);
        if(cursor.getString(1) != null)
            binding.fotoContacto.setImageURI(Uri.parse(cursor.getString(1)));
        binding.idContacto.setText(String.valueOf(cursor.getPosition()+1));
        binding.nombreContacto.setText(cursor.getString(2));
    }
}
