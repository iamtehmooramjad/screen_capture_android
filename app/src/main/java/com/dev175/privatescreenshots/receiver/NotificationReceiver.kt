package com.dev175.privatescreenshots.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.dev175.privatescreenshots.R
import com.dev175.privatescreenshots.service.ScreenShotService
import com.dev175.privatescreenshots.service.ScreenShotService.Companion.isMediaProjectionRunning
import com.dev175.privatescreenshots.utils.Constants.ACTION_COUNT
import com.dev175.privatescreenshots.utils.Constants.ACTION_GALLERY
import com.dev175.privatescreenshots.utils.Constants.ACTION_START_STOP
import com.dev175.privatescreenshots.utils.Constants.ACTION_STOP
import com.dev175.privatescreenshots.utils.Constants.COUNT
import com.dev175.privatescreenshots.utils.NotificationUtils
import com.dev175.privatescreenshots.utils.showShortToast


class NotificationReceiver : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {

        when(intent?.action){

            ACTION_STOP->{
                //Stop Service
                context?.startService(ScreenShotService.getStopIntent(context))
            }
            ACTION_START_STOP->{
                if(isMediaProjectionRunning){
                    NotificationUtils.updateNotification(context,R.drawable.ic_start)
                    context?.startService(ScreenShotService.getStopProjection(context))

                }
                else {
                    NotificationUtils.updateNotification(context,R.drawable.ic_stop)
                    context?.startService(ScreenShotService.getStartProjection(context))
                }
            }
            ACTION_GALLERY->{
                context?.startService(ScreenShotService.getStopIntent(context))
            }
            ACTION_COUNT->{
                val count =  intent.extras?.get(COUNT) as Int
                NotificationUtils.updateNotificationCount(context,count)
            }

        }
    }
}