package com.dicoding.picodiploma.loginwithanimation

import android.content.Intent
import android.widget.RemoteViewsService
import com.dicoding.picodiploma.loginwithanimation.di.Injection

class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        val storiesRepository = Injection.provideStoriesRepository(this.applicationContext)
        return StackRemoteViewsFactory(this.applicationContext, storiesRepository)
    }
}