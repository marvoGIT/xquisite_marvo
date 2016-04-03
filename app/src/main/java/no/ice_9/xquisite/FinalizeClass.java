package no.ice_9.xquisite;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.TimerTask;

/**
 * Created by human on 03.04.16.
 */
public class FinalizeClass extends SubAct{

    ASCIIscreen mAscii;
    Server mServer;
    int mServerConnection=0;
    int mReconnectTime=-1;
    boolean mScreenSaver=false;

    int mTime;
    public boolean mInitDone=false;


    //@Override
    public FinalizeClass(Activity activity,ASCIIscreen ascii,Server server)
    {


        mServer=server;
        mAscii = ascii;
        mTime=0;

    }

    @Override
    public int action()
    {
        System.gc();
        return 1;
    }

    @Override
    public TimerTask getTimerTask()
    {
        return new TimerTask() {
            @Override
            public void run() {
                //mAscii.fillTrash();

                //mAscii.fillTrash();

                if(mAscii.mReady)
                {

                    mAscii.mGLView.mRenderer.setProgress(0.0f);
                    mAscii.modLine("ALL FINISHED PUSH BUTTON TO RESTART",0,0);
                }

            }
        };
    }

    @Override
    public void destroy()
    {

    }

}