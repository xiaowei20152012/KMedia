package com.k.todo.service.music


import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Message
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.util.Log
import android.view.KeyEvent

import com.k.todo.BuildConfig
import com.k.todo.service.MusicPlayService

/**
 * Used to control headset playback.
 * Single press: pause/resume
 * Double press: next track
 * Triple press: previous track
 */
class MediaButtonIntentReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (DEBUG) Log.v(TAG, "Received intent: " + intent)
        if (handleIntent(context, intent) && isOrderedBroadcast) {
            abortBroadcast()
        }
    }

    companion object {
        private val DEBUG = BuildConfig.DEBUG
        val TAG = MediaButtonIntentReceiver::class.java.simpleName

        private val MSG_HEADSET_DOUBLE_CLICK_TIMEOUT = 2

        private val DOUBLE_CLICK = 400

        private var mWakeLock: WakeLock? = null
        private var mClickCounter = 0
        private var mLastClickTime: Long = 0

        @SuppressLint("HandlerLeak") // false alarm, handler is already static
        private val mHandler = object : Handler() {

            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_HEADSET_DOUBLE_CLICK_TIMEOUT -> {
                        val clickCount = msg.arg1
                        val command: String?

                        if (DEBUG) Log.v(TAG, "Handling headset click, count = " + clickCount)
                        when (clickCount) {
                            1 -> command = MusicPlayService.ACTION_TOGGLE_PAUSE
                            2 -> command = MusicPlayService.ACTION_SKIP
                            3 -> command = MusicPlayService.ACTION_REWIND
                            else -> command = null
                        }

                        if (command != null) {
                            val context = msg.obj as Context
                            startService(context, command)
                        }
                    }
                }
                releaseWakeLockIfHandlerIdle()
            }
        }

        fun handleIntent(context: Context, intent: Intent): Boolean {
            val intentAction = intent.action
            if (Intent.ACTION_MEDIA_BUTTON == intentAction) {
                val event = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT) ?: return false

                val keycode = event.keyCode
                val action = event.action
                val eventTime = event.eventTime

                var command: String? = null
                when (keycode) {
                    KeyEvent.KEYCODE_MEDIA_STOP -> command = MusicPlayService.ACTION_STOP
                    KeyEvent.KEYCODE_HEADSETHOOK, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> command = MusicPlayService.ACTION_TOGGLE_PAUSE
                    KeyEvent.KEYCODE_MEDIA_NEXT -> command = MusicPlayService.ACTION_SKIP
                    KeyEvent.KEYCODE_MEDIA_PREVIOUS -> command = MusicPlayService.ACTION_REWIND
                    KeyEvent.KEYCODE_MEDIA_PAUSE -> command = MusicPlayService.ACTION_PAUSE
                    KeyEvent.KEYCODE_MEDIA_PLAY -> command = MusicPlayService.ACTION_PLAY
                }
                if (command != null) {
                    if (action == KeyEvent.ACTION_DOWN) {
                        if (event.repeatCount == 0) {
                            // Only consider the first event in a sequence, not the repeat events,
                            // so that we don't trigger in cases where the first event went to
                            // a different app (e.g. when the user ends a phone call by
                            // long pressing the headset button)

                            // The service may or may not be running, but we need to send it
                            // a command.
                            if (keycode == KeyEvent.KEYCODE_HEADSETHOOK || keycode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                                if (eventTime - mLastClickTime >= DOUBLE_CLICK) {
                                    mClickCounter = 0
                                }

                                mClickCounter++
                                if (DEBUG) Log.v(TAG, "Got headset click, count = " + mClickCounter)
                                mHandler.removeMessages(MSG_HEADSET_DOUBLE_CLICK_TIMEOUT)

                                val msg = mHandler.obtainMessage(
                                        MSG_HEADSET_DOUBLE_CLICK_TIMEOUT, mClickCounter, 0, context)

                                val delay = (if (mClickCounter < 3) DOUBLE_CLICK else 0).toLong()
                                if (mClickCounter >= 3) {
                                    mClickCounter = 0
                                }
                                mLastClickTime = eventTime
                                acquireWakeLockAndSendMessage(context, msg, delay)
                            } else {
                                startService(context, command)
                            }
                            return true
                        }
                    }
                }
            }
            return false
        }

        private fun startService(context: Context, command: String?) {
            val intent = Intent(context, MusicPlayService::class.java)
            intent.action = command
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        private fun acquireWakeLockAndSendMessage(context: Context, msg: Message, delay: Long) {
            if (mWakeLock == null) {
                val appContext = context.applicationContext
                val pm = appContext.getSystemService(Context.POWER_SERVICE) as PowerManager
                mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Phonograph headset button")
                mWakeLock!!.setReferenceCounted(false)
            }
            if (DEBUG) Log.v(TAG, "Acquiring wake lock and sending " + msg.what)
            // Make sure we don't indefinitely hold the wake lock under any circumstances
            mWakeLock!!.acquire(10000)

            mHandler.sendMessageDelayed(msg, delay)
        }

        private fun releaseWakeLockIfHandlerIdle() {
            if (mHandler.hasMessages(MSG_HEADSET_DOUBLE_CLICK_TIMEOUT)) {
                if (DEBUG) Log.v(TAG, "Handler still has messages pending, not releasing wake lock")
                return
            }

            if (mWakeLock != null) {
                if (DEBUG) Log.v(TAG, "Releasing wake lock")
                mWakeLock!!.release()
                mWakeLock = null
            }
        }
    }
}
