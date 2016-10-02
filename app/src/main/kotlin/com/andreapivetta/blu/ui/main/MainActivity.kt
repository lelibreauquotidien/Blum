package com.andreapivetta.blu.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.andreapivetta.blu.BuildConfig
import com.andreapivetta.blu.R
import com.andreapivetta.blu.common.settings.AppSettingsFactory
import com.andreapivetta.blu.common.utils.visible
import com.andreapivetta.blu.data.jobs.NotificationsJob
import com.andreapivetta.blu.data.model.Notification
import com.andreapivetta.blu.data.model.PrivateMessage
import com.andreapivetta.blu.data.storage.AppStorageFactory
import com.andreapivetta.blu.ui.base.custom.ThemedActivity
import com.andreapivetta.blu.ui.newtweet.NewTweetActivity
import com.andreapivetta.blu.ui.notifications.NotificationsActivity
import com.andreapivetta.blu.ui.privatemessages.PrivateMessagesActivity
import com.andreapivetta.blu.ui.search.SearchActivity
import com.andreapivetta.blu.ui.settings.SettingsActivity
import com.andreapivetta.blu.ui.setup.SetupActivity
import com.andreapivetta.blu.ui.timeline.TimelineFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu_messages.view.*
import kotlinx.android.synthetic.main.menu_notifications.view.*

class MainActivity : ThemedActivity(), MainMvpView {

    private var notificationsCount = 0L
    private var messagesCount = 0L
    private val receiver: NotificationUpdatesReceiver? by lazy { NotificationUpdatesReceiver() }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, IntentFilter(Notification.NEW_NOTIFICATION_INTENT))
        registerReceiver(receiver, IntentFilter(PrivateMessage.NEW_PRIVATE_MESSAGE_INTENT))

        val storage = AppStorageFactory.getAppStorage()
        messagesCount = storage.getUnreadPrivateMessagesCount()
        notificationsCount = storage.getUnreadNotificationsCount()

        invalidateOptionsMenu()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { newTweet() }

        if (savedInstanceState == null)
            pushFragment(TimelineFragment.newInstance())

        if (!AppSettingsFactory.getAppSettings(this).isUserDataDownloaded())
            SetupActivity.launch(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun pushFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container_frameLayout, fragment).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(R.menu.menu_main, menu)

        var item = menu?.findItem(R.id.action_notifications)
        MenuItemCompat.setActionView(item, R.layout.menu_notifications)
        var view = MenuItemCompat.getActionView(item)
        val notificationImageButton = view.findViewById(R.id.notificationImageButton)
        notificationImageButton.setOnClickListener {
            notificationsCount = 0
            invalidateOptionsMenu()
            openNotifications()
        }

        if (notificationsCount > 0) {
            val notificationsCountTextView = view.notificationCountTextView
            notificationsCountTextView.visible()
            notificationsCountTextView.text = "${notificationsCount}"
        }

        item = menu?.findItem(R.id.action_messages)
        MenuItemCompat.setActionView(item, R.layout.menu_messages)
        view = MenuItemCompat.getActionView(item)
        val messagesImageButton = view.findViewById(R.id.messagesImageButton)
        messagesImageButton.setOnClickListener { openMessages() }

        if (messagesCount > 0) {
            val messagesCountTextView = view.messagesCountTextView
            messagesCountTextView.visible()
            messagesCountTextView.text = "${messagesCount}"
        }

        val searchView = MenuItemCompat.
                getActionView(menu?.findItem(R.id.action_search)) as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                search(searchView.query.toString())
                return true
            }

            override fun onQueryTextChange(s: String) = false
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_settings -> openSettings()
            R.id.action_profile -> openProfile()
        }

        if (BuildConfig.DEBUG && R.id.action_check_notifications == item?.itemId)
            Thread() { NotificationsJob().checkNotifications(this) }.start()

        return super.onOptionsItemSelected(item)
    }

    override fun search(string: String) {
        SearchActivity.launch(this, string)
    }

    override fun openSettings() {
        SettingsActivity.launch(this)
    }

    override fun openNotifications() {
        NotificationsActivity.launch(this)
    }

    override fun openMessages() {
        PrivateMessagesActivity.launch(this)
    }

    override fun openProfile() {
        // TODO
    }

    override fun newTweet() {
        NewTweetActivity.launch(this)
    }

    inner class NotificationUpdatesReceiver() : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Notification.NEW_NOTIFICATION_INTENT -> {
                    notificationsCount++
                    invalidateOptionsMenu()
                }
                PrivateMessage.NEW_PRIVATE_MESSAGE_INTENT -> {
                    messagesCount++
                    invalidateOptionsMenu()
                }
            }
        }
    }

}
