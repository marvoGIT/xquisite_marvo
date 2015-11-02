package no.ice_9.xquisite;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by HUMAN on 15.10.2015.
 */
public class Server {

    static String CODE_SERVER_PIN="3547";//SERVER PIN
    static String CODE_PACK_ID_PINC="23";//PACKET ID PIN
    static String CODE_PACK_ID_TASK="95";//PACKET ID TASK
    static String CODE_PACK_ID_DONE="124";//PACKET ID DONE

    static String CODE_CHECK_CONNECTION="0000";//check connection
    static String CODE_GET_LST_STRY_NDX="0003";//get last story ndx
    static String CODE_UPLOAD_STORY_PRT="0005";//upload part of the story
    static String CODE_RESRV_NDX_ON_SRV="0006";//reserve ndx for recording story
    static String CODE_COMPL_NDX_ON_SRV="0007";//complete reserved ndx

    String adress;
    int serverResponseCode = 0;

    Server(Context context)
    {

        String adr = context.getResources().getString(R.string.server_address);

        adress = adr;
        Log.d("SERVER", "got server address:" + adress);
    }

    //'PING' TO SERVER
    public boolean checkConnection()
    {
        String response=postToServer(CODE_CHECK_CONNECTION, null);

        boolean result=false;
        Log.d("SERVER","response"+response+";");
        if(response.matches("succ"))
        {
            Log.d("SERVER","got connection");
            result=true;
        }

        return result;
    }

    //RESERVE INDEX ON SERVER FOR CURRENTLY RECORDING STORY
    public int reserveNdx()
    {
        int result=-1;
        String response=postToServer(CODE_RESRV_NDX_ON_SRV, null);


        Log.d("SERVER","response"+response+";");
        if(!response.matches(""))
        {
            result=Integer.parseInt(response);

        }

        return result;
    }

    //COMPLETE INDEX ON SERVER FOR CURRENTLY RECORDING STORY
    public int completeNdx(int ndx)
    {
        int result=-1;
        String response=postToServer(CODE_COMPL_NDX_ON_SRV, String.valueOf(ndx));


        Log.d("SERVER","response"+response+";");
        if(!response.matches(""))
        {
            result=Integer.parseInt(response);

        }

        return result;
    }

    public boolean uploadPart(String fileUri,int storyPart, int ndx, int parent, int user)
    {
        //TODO:COMBINE ALL INPUTS and POST IT TO SERVER
        String response=uploadToServer(fileUri, String.valueOf(storyPart),String.valueOf(ndx),String.valueOf(parent),String.valueOf(user));

        boolean result=false;
        Log.d("SERVER","response"+response+";");
        if(response.matches("-1")){result=false;}
        else{result=true;}

        return result;
    }

    public String getLastStoryNdx()
    {
        String response=postToServer(CODE_GET_LST_STRY_NDX, null);

        String result="-1";
        Log.d("SERVER","response"+response+";");
        if(!response.matches("-1"))
        {
            result=response;
        }

        return result;
    }

    private String postToServer(String code,String data) {
        String result="-1";
        Socket sck=null;

        try
        {
            InetAddress serverAdrr = InetAddress.getByName(adress);

            sck = new Socket(serverAdrr,237);


            DataOutputStream out = new DataOutputStream(sck.getOutputStream());
            InputStream input =  new BufferedInputStream(sck.getInputStream());


            out.write(createPacket(Integer.parseInt(CODE_PACK_ID_PINC), 4, CODE_SERVER_PIN));

            out.write(createPacket(Integer.parseInt(CODE_PACK_ID_TASK), 4, code));

            out.write(createPacket(Integer.parseInt(CODE_PACK_ID_DONE), 4, CODE_SERVER_PIN));


            byte[] bbuf=new byte[4];
            input.read(bbuf, 0, 4);

            int size=bbuf[0]*16777216+bbuf[1]*65536+bbuf[2]*256+bbuf[3];

            bbuf=new byte[size];

            input.read(bbuf,0,size);
            Log.d("SERVER", "resp:" +new String(bbuf,"ASCII"));

            result=new String(bbuf,"ASCII");

            sck.close();
            Log.d("SERVER","closed socket");



        } catch(IOException ex)
        {
            ex.printStackTrace();
        }
        finally {
            if(sck!=null)
            {
                try {
                    sck.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        /*HttpURLConnection conn = null;
        String result="-1";

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        try
        {
            //URL url = new URL(adress+"comm/");
            URL url = new URL(adress);
            conn = (HttpURLConnection) url.openConnection();

            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy

            /*conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            //conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            */

            //DataOutputStream outSt = new DataOutputStream(conn.getOutputStream());

            /*outSt.writeBytes(twoHyphens + boundary + lineEnd);
            outSt.writeBytes("Content-Disposition: form-data; name='code'" + lineEnd + lineEnd);
            outSt.writeBytes(code + lineEnd);

            outSt.writeBytes(twoHyphens + boundary + lineEnd);
            outSt.writeBytes("Content-Disposition: form-data; name='data'" + lineEnd + lineEnd);
            outSt.writeBytes(data + lineEnd);*/

            //InputStream in = new BufferedInputStream(conn.getInputStream());

            //String inS="";
            /*char A;
            boolean done=false;
            int iter=0;
            while(!done)
            {
                iter++;
                A=(char)in.read();
                //Log.d("SERVER","reading: "+A);
                if(A==36 || iter>1000)
                {
                    done=true;
                }
                else
                {
                    inS=inS+A;
                }

                //inS=inS+in.read();

            }
            result=inS;

            serverResponseCode = conn.getResponseCode();

            String serverResponseMessage = conn.getResponseMessage();
            Log.d("SERVER", "resp code:" + serverResponseCode);
            Log.d("SERVER", "resp msg:" + serverResponseMessage);

        }
        catch (MalformedURLException ex)
        {
            ex.printStackTrace();
        }
        catch (Exception e)
        {

            Log.e("SERVER","FUCK");
            //dialog.dismiss();
            e.printStackTrace();



        }
        finally {
            if(conn!=null)
            conn.disconnect();}*/

        return result;
    }


    private String uploadToServer(String sourceFileUri,String part,String ndx, String parent,String user)
    {

        String result="-1";
        Socket sck=null;
        int bytesRead, bytesAvailable, bufferSize;
        File sourceFile = new File(sourceFileUri);
        int maxBufferSize = 1* 1024 * 1024;
        byte[] buffer;

        if(!sourceFile.isFile())
        {
            Log.e("uploadFile", "Source File not exist :"
                    + sourceFileUri);



            return "-1";
        }

        try
        {
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            InetAddress serverAdrr = InetAddress.getByName(adress);

            sck = new Socket(serverAdrr,237);


            DataOutputStream out = new DataOutputStream(sck.getOutputStream());
            InputStream input =  new BufferedInputStream(sck.getInputStream());

            out.writeBytes("3547");
            out.writeBytes(CODE_UPLOAD_STORY_PRT);

            out.writeBytes("0001");

            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            int tot=0;
            while (bytesRead > 0) {
                tot+=bufferSize;
                out.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            }

            Log.d("SERVER","SENT "+tot+" bytes");

            int a;
            boolean done=false;
            String buf="";
            while(!done)
            {
                Log.d("SERVER","geting buffer");
                a=input.read();
                if(a==-1 || a==36){done=true;}
                else{buf+=(char)a;}

            }
            if(buf!=""){result=buf;}
            Log.d("SERVER", "!!!" + result);
            sck.close();
            Log.d("SERVER","closed socket");



        } catch(IOException ex)
        {
            ex.printStackTrace();
        }
        finally {
            if(sck!=null)
            {
                try {
                    sck.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
        /*String fileName = sourceFileUri;
        HttpURLConnection conn = null;
        String result="-1";
        DataOutputStream outSt;

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1* 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if(!sourceFile.isFile())
        {
            Log.e("uploadFile", "Source File not exist :"
                    + sourceFileUri);



            return 0;
        }
        else
        {
            try
            {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(adress+"comm/");
                conn = (HttpURLConnection) url.openConnection();

                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                conn.setRequestProperty("uploaded_file", fileName);

                outSt = new DataOutputStream(conn.getOutputStream());

                outSt.writeBytes(twoHyphens + boundary + lineEnd);
                outSt.writeBytes("Content-Disposition: form-data; name='code'" + lineEnd + lineEnd);
                outSt.writeBytes(CODE_UPLOAD_STORY_PRT + lineEnd);

                outSt.writeBytes(twoHyphens + boundary + lineEnd);
                outSt.writeBytes("Content-Disposition: form-data; name='part'" + lineEnd + lineEnd);
                outSt.writeBytes(part + lineEnd);

                outSt.writeBytes(twoHyphens + boundary + lineEnd);
                outSt.writeBytes("Content-Disposition: form-data; name='ndx'" + lineEnd + lineEnd);
                outSt.writeBytes(ndx + lineEnd);

                outSt.writeBytes(twoHyphens + boundary + lineEnd);
                outSt.writeBytes("Content-Disposition: form-data; name='parent'" + lineEnd + lineEnd);
                outSt.writeBytes(parent + lineEnd);

                outSt.writeBytes(twoHyphens + boundary + lineEnd);
                outSt.writeBytes("Content-Disposition: form-data; name='user'" + lineEnd + lineEnd);
                outSt.writeBytes(user + lineEnd);

                outSt.writeBytes(twoHyphens + boundary + lineEnd);
                outSt.writeBytes("Content-Disposition: form-data; name='uploaded_file';filename=" + fileName + lineEnd);

                outSt.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    outSt.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                outSt.writeBytes(lineEnd);
                outSt.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){


                }

                InputStream in = new BufferedInputStream(conn.getInputStream());
                //readStream(in);

                String inS="";
                char A;
                boolean done=false;
                int iter=0;
                while(!done && iter<1000)
                {
                    iter++;
                    A=(char)in.read();



                    inS=inS+A;

                }
                Log.d("SERVER","reading: "+inS);

                //close the streams //
                fileInputStream.close();
                outSt.flush();
                outSt.close();

            }catch (FileNotFoundException e1)
            {
                e1.printStackTrace();
            }catch (MalformedURLException e2)
            {
                e2.printStackTrace();
            }catch (IOException e3)
            {
                e3.printStackTrace();
            }finally {
                if(conn!=null)
                    conn.disconnect();}
        }


        return serverResponseCode;*/
    }

    private byte[] createPacket(int id,int size, String cont)
    {
        Log.d("SERVER", "CHAECK2");
        int totsize=5+size;
        byte[] result=new byte[totsize];

        String packet=""+
                (char)id+
                (char)((((size/256)/256)/256)%256)+
                (char)(((size/256)/256)%256)+
                (char)((size/256)%256)+
                (char)(size%256);

        packet+=cont;


        result=packet.getBytes();



        return result;
    }


}
