package com.puj.taller02_permission.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.puj.taller02_permission.R;
import com.puj.taller02_permission.adapters.ContactsAdapter;
import com.puj.taller02_permission.databinding.ActivityContactosBinding;
import com.puj.taller02_permission.utils.AlertUtils;
import com.puj.taller02_permission.utils.PermissionHelper;

public class ContactosActivity extends AppCompatActivity {

    private final int PERMISSIONS_REQUEST_READ_CONTACTS = 2002;
    private static String TAG = ContactosActivity.class.getName();
    ActivityContactosBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        requestPermission(this, Manifest.permission.READ_CONTACTS,"Permiso para leer contactos", PERMISSIONS_REQUEST_READ_CONTACTS);
    }

    private void requestPermission(Activity context, String permission,String justification, int id) {
        // Verificar si no hay permisos
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                    Manifest.permission.READ_CONTACTS)) {
                AlertUtils.shortSimpleSnackbar(binding.getRoot(), justification);
            }
        }
            ActivityCompat.requestPermissions(context, new String[]{permission}, id);
    }

    private void initView(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED){
            fillConstacts();
        }else {
            Log.i(TAG, "initView: No se pudo obtener el permiso :)");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSIONS_REQUEST_READ_CONTACTS){
            initView();
        }
    }

    private void fillConstacts(){
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, ContactsAdapter.PROYECTION, null, null, null);
        ContactsAdapter adapter = new ContactsAdapter(this, cursor, false);
        binding.listaContactos.setAdapter(adapter);
    }
}