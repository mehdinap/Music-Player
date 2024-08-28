package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private var audioList: ArrayList<AudioModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = AudioAdapter(audioList)

//        if (hasStoragePermission()) {
            loadAudioFiles()
//        } else {
//            requestStoragePermission()
//        }
    }

//    private fun hasStoragePermission(): Boolean {
//        return ContextCompat.checkSelfPermission(
//            this,
//            READ_EXTERNAL_STORAGE
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun requestStoragePermission() {
//        ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), 100)
//    }

    @SuppressLint("NotifyDataSetChanged")
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
                val audioModel = AudioModel(title, artist)
                audioList.add(audioModel)
            }
            cursor.close()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.adapter?.notifyDataSetChanged()
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == 100) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
//                loadAudioFiles()
//            } else {
//                Toast.makeText(this, "Permission Denied. Please allow storage permission to use this app.", Toast.LENGTH_SHORT).show()
//                finish()
//            }
//        }
//    }
}