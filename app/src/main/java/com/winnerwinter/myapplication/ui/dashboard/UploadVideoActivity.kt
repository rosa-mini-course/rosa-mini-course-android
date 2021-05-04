package com.winnerwinter.myapplication.ui.dashboard


import android.R.attr
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.winnerwinter.myapplication.databinding.ActivityUploadVideoBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.io.InputStream


class UploadVideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadVideoBinding
    lateinit var courseId: String

    lateinit var file: File
    lateinit var inputStream: InputStream
    lateinit var somebody: ContentUriRequestBody

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadVideoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        courseId = intent.getStringExtra("courseId").toString()
        binding.filenameTv.text = courseId

        binding.uploadBtn.setOnClickListener {
            if (binding.filenameEt.text == null || binding.filenameEt.text.toString() == "") {
                runOnUiThread {
                    Toast.makeText(this@UploadVideoActivity, "文件名不能为空", Toast.LENGTH_SHORT).show()
                }
                return@setOnClickListener
            }
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("courseId", courseId)
                .addFormDataPart(
                    "file",
//                    file.name,
                    binding.filenameEt.text.toString() + ".mp4",
//                    file.asRequestBody("video/mp4".toMediaTypeOrNull())
                    somebody
                )
                .build()
            val okHttpClient = OkHttpClient()
            val request = Request.Builder()
                .url("http://192.168.1.138:4000/upload")
                .post(requestBody)
                .build()
            okHttpClient.newCall(request)
                .enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        runOnUiThread {
                            Toast.makeText(
                                this@UploadVideoActivity,
                                response.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Log.e(SUCCESS, response.toString())
                        this@UploadVideoActivity.finish()
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        Log.e(FAILURE, e.toString())
                        runOnUiThread {
                            Toast.makeText(
                                this@UploadVideoActivity,
                                e.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
        }

        binding.selectFileBtn.setOnClickListener {
            val myFileIntent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            myFileIntent.type = "video/*"
            startActivityForResult(myFileIntent, 10)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10) {
            if (resultCode == RESULT_OK || data?.data != null) {
                val uri = data!!.data
                val path = uri?.path
    //                inputStream = contentResolver.openInputStream(uri!!)!!
//                file = File(path)
                binding.filenameTv.text = path
                binding.uploadBtn.isEnabled = true
                somebody = ContentUriRequestBody(contentResolver, uri!!)
            }
        }
    }


}