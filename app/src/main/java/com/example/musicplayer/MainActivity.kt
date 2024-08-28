package com.example.musicplayer

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.data.model.SongModel
import com.example.musicplayer.ui.songslist.SongAdapter

class MainActivity : AppCompatActivity() {

    private var audioList: ArrayList<SongModel> = ArrayList()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = SongAdapter(audioList)

        if (hasStoragePermission()) {
            loadAudioFiles()
        } else {
            requestStoragePermission()
        }
    }

    private fun hasStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
            Toast.makeText(
                this, "Storage permission is required to display audio files.", Toast.LENGTH_LONG
            ).show()
            ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), 100)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), 100)
        }
    }

    private fun loadAudioFiles() {
        val contentResolver: ContentResolver = contentResolver
        val audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media._ID
        )

        val cursor = contentResolver.query(audioUri, projection, null, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val title =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val artist =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                Log.i("test", "title : $title / artist : $artist")
                val audioModel = SongModel(title, artist)
                audioList.add(audioModel)
            }
            cursor.close()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                loadAudioFiles()
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        this, READ_EXTERNAL_STORAGE
                    )
                ) {
                    Toast.makeText(
                        this,
                        "Permission Denied Permanently. Please enable it in settings.",
                        Toast.LENGTH_LONG
                    ).show()
                    openAppSettings()
                } else {
                    Toast.makeText(
                        this,
                        "Permission Denied. Please allow storage permission to use this app.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}