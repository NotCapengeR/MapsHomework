package ru.netology.mapshomework.ui

import android.os.Bundle
import android.view.LayoutInflater
import com.yandex.mapkit.MapKitFactory
import ru.netology.mapshomework.base.BaseActivity
import ru.netology.mapshomework.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this)
    }
}