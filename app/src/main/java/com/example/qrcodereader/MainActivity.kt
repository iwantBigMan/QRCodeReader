package com.example.qrcodereader

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.GnssAntennaInfo.Listener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.qrcodereader.databinding.ActivityMainBinding
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // 바인딩 변수 생성
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>

    private val PERMISSIONS_REQUEST_CODE = 1
    private val PERMISSIONS_REQUIRED = arrayOf(android.Manifest.permission.CAMERA)
    //카메라 권한 지정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩 설정
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (!hasPermissions(this)){
            // 카메라 권한을 요청
            requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
        }else{
            // 만약 이미 권한이 있다면 카메라 시작
            startCamera()
        }
    }

    private var isDetect = false

    override fun onResume() {
        super.onResume()
        isDetect = false
    }
    // 권한 유무 확인
    fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    // 권한 요청 콜백 함수
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // 권한 요청 조건문에서  requestPermissions의 인수로 넣은 PERMISSIONS_REQUEST_CODE와
        // 맞는지 확인
        if (requestCode == PERMISSIONS_REQUEST_CODE){
            if (PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()){
                Toast.makeText(this@MainActivity, "권한 요청이 승인되었습니다.",
                Toast.LENGTH_LONG).show()
                startCamera()
            }else {
                Toast.makeText(this@MainActivity, "권한 요청이 거부되었습니다.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    fun getImageAnalysis(): ImageAnalysis {
        val cameraExecutor : ExecutorService = Executors.newSingleThreadExecutor()
        val imageAnalysis = ImageAnalysis.Builder().build()

        imageAnalysis.setAnalyzer(cameraExecutor,
            QRCodeAnalyzer(object : OnDectectListener{
                override fun onDetect(msg: String) {
                   if (!isDetect){
                       isDetect = true // 데이터가 감지되었으므로 true로 바꿈

                       val intent = Intent(this@MainActivity,
                       ResultActivity::class.java)
                       intent.putExtra("msg", msg)
                       startActivity(intent)
                   }
                }
            }))
        return imageAnalysis
    }

    // 미리보기와 이미지 분석 시작
    fun startCamera(){
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)//
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()

            val preview = getPreview() // 미리보기 객체 가져오기
            val imageAnalysis = getImageAnalysis()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            //후면 카메라 선택

            cameraProvider.bindToLifecycle(this, cameraSelector, preview,
            imageAnalysis)
            //미리보기 기능 선택
        }, ContextCompat.getMainExecutor(this))


    }

    // 미리보기 객체 반환
    fun getPreview(): Preview{
        val preview : Preview = Preview.Builder().build() // Preview 객체 생성
        preview.setSurfaceProvider(binding.barcodePreview.surfaceProvider)
        return preview
    }


}