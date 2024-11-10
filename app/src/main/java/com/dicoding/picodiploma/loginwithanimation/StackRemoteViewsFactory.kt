package com.dicoding.picodiploma.loginwithanimation

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.dicoding.picodiploma.loginwithanimation.data.StoriesRepository
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.ListStoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

internal class StackRemoteViewsFactory(
    private val mContext: Context,
    private val storiesRepository: StoriesRepository
) : RemoteViewsService.RemoteViewsFactory {
    private val mWidgetItems = ArrayList<Bitmap>()

    override fun onCreate() {
        // No initialization needed
    }

    override fun onDataSetChanged() {
        // Clear the current list
        mWidgetItems.clear()
        var imageResUrl: List<ListStoryItem?>? = listOf()

        runBlocking {
            try {
                imageResUrl = storiesRepository.getListStory()
            } catch (_: Exception) {

            }
        }

        runBlocking {
            val jobs = imageResUrl?.map { resUrl ->
                async(Dispatchers.IO) {
                    val bitmap = Glide.with(mContext)
                        .asBitmap()
                        .load(resUrl?.photoUrl)
                        .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get()
                    bitmap
                }
            }
            val bitmaps = jobs?.awaitAll() // Load all bitmaps concurrently
            if (bitmaps != null) {
                mWidgetItems.addAll(bitmaps)
            }
        }
    }

    override fun onDestroy() {
        mWidgetItems.clear()
    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.imageView, mWidgetItems[position])

        val extras = bundleOf(
            StoryBannerWidget.EXTRA_ITEM to position
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = i.toLong()

    override fun hasStableIds(): Boolean = true
}
