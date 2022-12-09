package com.example.qrcodereader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.qrcodereader.databinding.ActivityMainBinding
import com.example.qrcodereader.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val result = intent.getStringExtra("msg") ?: "데이터가 존재하지 않습니다."

        setUI(result) // UI를 초기화
    }

    private fun setUI(result: String){
        // 넘어온 QR 코드 속 데이터를 텍스트뷰에 설정
        binding.tvContent.text = result
        binding.btnGoBack.setOnClickListener{
            finish() // 돌아가기 버튼을 눌렀을 때 ResultActivity를 종료
        }
    }
}