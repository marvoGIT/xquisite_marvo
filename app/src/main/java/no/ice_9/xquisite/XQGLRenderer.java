package no.ice_9.xquisite;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;

import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by human on 16.03.16.
 */
public class XQGLRenderer implements GLSurfaceView.Renderer {

    private int infoStatus=2;
    private int infoTarget=2;
    private float infoHeight=1.0f;
    private float infoTop=0.0f;




    //DEBUG TIME MEASURE
    long mMesTime=0;
    long mLasTime=0;


    private AsciiTiles mAsciiTiles;
    private InfoTile mInfoTile;
    private TextLine[] mTextLine;
    private ButtonTile mContinueButton;

    public int[] textures = new int[3];
    public Context actContext;
    public boolean upAval;
    public boolean upVid;

    Bitmap mBitmap;

    public int asciicols;
    public int asciirows;

    public ASCIIscreen view;

    private float mAngle;

    public SurfaceTexture mSurface;

    private boolean clearDone=true;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mTranslationMatrix = new float[16];

    @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {

        mAngle=0f;

        mMesTime= Calendar.getInstance().getTimeInMillis();
        mLasTime=mMesTime;



        /*RENDER INIT*/
        GLES20.glClearColor(0.0f, 0.3f, 0.0f, 0.5f);

        /*TEXTURES INIT*/
        initTextures();

        int sx=asciicols;
        int sy=asciirows;

        mMesTime= Calendar.getInstance().getTimeInMillis();
        Log.d("TIME","  GLREND TX2 INIT "+(mMesTime-mLasTime)+"ms");
        mLasTime=mMesTime;

        /*ASCII TILE BUILD*/

        mAsciiTiles = new AsciiTiles(sx,sy,textures[0],textures[1],textures[2]);

        mTextLine=new TextLine[sy];

        mContinueButton = new ButtonTile();




        for(int j=0;j<sy;j++)
        {


            mTextLine[j] = new TextLine(j,textures[0]);
        }



        /*INFO TILE BUILD*/
        mInfoTile=new InfoTile();





    }




    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        //Random rnd=new Random();
        view.mReady=true;
        float[] scratch = new float[16];



        /*int l=0;
        for(int j=0;j<asciirows;j++)
        {
            for(int i=0;i<asciicols;i++)
            {
                int ndx = i+(j*asciicols);// (asciicols*(asciirows*(j-1))-i-1);
                mTile[ndx].putChar("helasdlo woasdasdfasdasfdafssdfgsdfsdfssdrld".charAt(j));
                l++;
            }
        }*/
        mAngle+=0.01f;
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        updateAval();
        animInfo();

        // Set the camera position (View matrix)
        //Matrix.setLookAtM(mViewMatrix, 0, 0, 1, -1f - mAngle, 0f, -1f, 0f, 0f, 1f, 0f);
        // Calculate the projection and view transformation
        //Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        //Matrix.translateM(mTranslationMatrix, 0, 0f, 0f, -2f);
        Matrix.setIdentityM(mTranslationMatrix,0);
        Matrix.translateM(mTranslationMatrix, 0, 0f, infoTop, 0f);
        scratch=mTranslationMatrix.clone();
        Matrix.scaleM(mTranslationMatrix, 0, 1.0f, infoHeight, 1.0f);

        //Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mTranslationMatrix, 0);

        //int sx=asciicols;
        int sy=asciirows;
        //int k=0;

        mAsciiTiles.draw();
        /*for(int j=0;j<sy;j++)

        {
            for(int i=0;i<sx;i++)
            {
                mTile[k].draw();
                k++;
            }
        }*/

        mInfoTile.draw(mTranslationMatrix);

        for(int j=0;j<sy;j++)
        {
            if(!mTextLine[j].isEmpty())
            {
                Matrix.translateM(scratch, 0, 0f, -0.05f, 0f);
                mTextLine[j].draw(scratch);
            }

        }
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public void updateAval()
    {
        //mSurface.updateTexImage();
        //mSurface.
        if(upVid)
        {   float[] mtx = new float[16];
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[2]);
            mSurface.updateTexImage();
            mSurface.getTransformMatrix(mtx);

            //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 1);
        }
        if (upAval)
        {


            //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 1);

            //Log.d("GL","bpup"+mBitmap.getPixel(0,0));
            Log.d("GL", "upval");
            //GLES20.glGenTextures(1, textures, 1);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[1]);
            // Set filtering
            // GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            // GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            //GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, mBitmap);
            GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0,0,0, mBitmap);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 1);




            upAval=false;
        }
    }

    private void animInfo()
    {
        if(infoTarget!=infoStatus)
        {
            switch(infoTarget)
            {
                case 1:
                    infoHeight=infoHeight+(0.3f-infoHeight)/10.0f;
                    infoTop=infoTop+(-0.85f-infoTop)/10.0f;
                    if(infoHeight==0.3f){infoStatus=infoTarget;}
                    break;

            }
        }
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    private void initTextures()
    {
        upAval=true;
        upAval=false;

        //TEXTURE 0
        mBitmap = Bitmap.createBitmap(asciicols,asciirows, Bitmap.Config.ARGB_8888 );


        int textGridTex=loadTexture(actContext,R.drawable.textgrid);
        textures[0]=textGridTex;

        mMesTime= Calendar.getInstance().getTimeInMillis();
        Log.d("TIME","  GLREND TX0 INIT "+(mMesTime-mLasTime)+"ms");
        mLasTime=mMesTime;

        Log.d("ASCII", "tex" + textGridTex);



        //GLES20.glGenTextures(1, textures, 1);
        //mBitmap = Bitmap.createBitmap(asciicols,asciirows, Bitmap.Config.ARGB_8888 );

        //GLES20.glGenTextures(2, textures, 2);

        Random r= new Random();

        for(int i=0;i<asciicols;i++)
        {
            for(int j=0;j<asciirows;j++)
            {
                //mBitmap.setPixel(i,j, Color.argb(r.nextInt(256), r.nextInt(256),r.nextInt(256),r.nextInt(256)));
                mBitmap.setPixel(i, j, Color.argb(0, 0, 0, 0));
                //Log.d("GL","PX:"+mBitmap.getPixel(i,j));
            }
        }



        //TEXTURE 1
        GLES20.glGenTextures(1, textures, 1);


        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[1]);
        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);

        //bitmap.recycle();

        int screenTileValue=textures[1];

       //TEXTURE 2
        GLES20.glGenTextures(1, textures, 2);
        //mSurface.setUseExternalTextureID();
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[2]);

        // Set filtering
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        //GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);




        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_REPEAT);



        mSurface = new SurfaceTexture(textures[2]);
        mSurface.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                //surfaceTexture.updateTexImage();


                upVid = true;
            }
        });

        int videoTex=textures[2];
    }

    public static int loadTexture(Context context,final int resourceId)
    {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }

    public void putImage(Bitmap bitmap)
    {
        Random r = new Random();
        for(int i=0;i<asciicols;i++)
        {
            for(int j=0;j<asciirows;j++)
            {
                //mBitmap.setPixel(i, j, Color.argb(r.nextInt(), 0, 0, 255));
                mBitmap.setPixel(i, j, bitmap.getPixel(i, j));
            }
        }
        //mBitmap=bitmap.copy(Bitmap.Config.ARGB_8888,true);

        upAval=true;
    }

    public void putString(String str, int row, int pos)
    {
        mTextLine[row].set(str);
        if(row<asciirows &&/* pos+str.length()<asciicols &&*/ view.mReady)
        {

            //GLES20.glGenTextures(1, textures, 1);
            //Bitmap bitmap = Bitmap.createBitmap(asciicols,asciirows, Bitmap.Config.ARGB_8888 );

            Random r= new Random();

            //Log.d("ASCII", "AS" + row + " :" + pos + "__" + str.length());
            for(int i=0;i<str.length();i++)
            {
                int ndx = ((asciicols*(row))+(pos))+i;
                //Log.d("ASCII","CC"+ndx+ ":"+mTile.length);



                if(ndx>0 && ((pos+i)%asciicols)>=0)
                {

                    mBitmap.setPixel((pos+i)%asciicols, row, Color.argb(str.charAt(i), 0, 0, 255));
                    //mBitmap.setPixel(1, 0, Color.argb(r.nextInt(256), r.nextInt(256), r.nextInt(256), 255));
                    //Log.d("ASCII", "CC" + mTile[ndx] + ":" + mTile.length);
                    //mTile[ndx].putChar(str.charAt(i));
                }


            }
            //Log.d("GL","bp"+mBitmap.getPixel(0,0));
            upAval=true;
            //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[1]);
            // Set filtering
            // GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            // GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            //GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, bitmap);


            //int screenTileValue=textures[1];
            //bitmap.recycle();
        }
    }

    public void clearAscii()
    {
        clearDone=false;
        TimerTask clearTrhead=new TimerTask() {
            @Override
            public void run() {
                clearDone=true;
                for(int i=0;i<asciicols;i++)
                {
                    for(int j=0;j<asciirows;j++)
                    {
                        int px=mBitmap.getPixel(i,j);
                        if (px>0){clearDone=false;}
                        mBitmap.setPixel(i,j,px/2);
                    }
                }
                if(clearDone){this.cancel();}
                else{upAval=true;}

            }
        };
        new Timer().scheduleAtFixedRate(clearTrhead, 0, 5);

    }

    public void minimizeInfo()
    {
        infoTarget=1;
    }

    public void maximizeInfo()
    {
        infoTarget=2;
    }


}