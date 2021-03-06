package com.rain.remynd

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentFactory
import com.rain.remynd.common.dependency
import com.rain.remynd.di.DaggerRemyndComponent
import com.rain.remynd.di.RemyndDependency
import com.rain.remynd.navigator.BackHandler
import com.rain.remynd.navigator.Navigator
import javax.inject.Inject

class RemyndActivity : AppCompatActivity() {
    @Inject
    internal lateinit var fragmentFactory: FragmentFactory
    @Inject
    internal lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        setUpDependency()
        supportFragmentManager.fragmentFactory = fragmentFactory
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remynd)
        if (savedInstanceState == null) {
            navigator.showRemindList()
        }
    }

    private fun setUpDependency() {
        DaggerRemyndComponent.factory()
            .create(this, applicationContext.dependency(RemyndDependency::class))
            .inject(this)
    }

    override fun onBackPressed() {
        val handler = supportFragmentManager.findFragmentById(R.id.main_container) as? BackHandler
        if (handler?.onBackPressed() == true) {
            return
        }

        super.onBackPressed()
    }
}
