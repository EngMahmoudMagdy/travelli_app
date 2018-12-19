package com.magdy.travelli.Services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static com.magdy.travelli.Data.Constants.FILE;
import static com.magdy.travelli.Data.Constants.ID;
import static com.magdy.travelli.Data.Constants.NOTIFICATION;
import static com.magdy.travelli.Data.Constants.PROGRESS;
import static com.magdy.travelli.Data.Constants.RESULT;
import static com.magdy.travelli.Data.Constants.TYPE;
import static com.magdy.travelli.Data.Constants.TYPE_PROGRESS;
import static com.magdy.travelli.Data.Constants.TYPE_RESULT;
import static com.magdy.travelli.Data.Constants.URL;

public class VideoDownloadService extends IntentService {
    int prog;
    File outFile,dir;
    String url , id = "0";
    private int result = Activity.RESULT_CANCELED;

    public VideoDownloadService() {
        super("downloadVideo");
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        assert intent != null;
        id = intent.getStringExtra(ID);
        url = intent.getStringExtra(URL);
        dir = new File(getCacheDir(),"data");
        dir.mkdirs();
        try {
            outFile = new File(new URI(dir.getAbsolutePath()).toString(),id+".mp4");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        BufferedInputStream input = null;

            try {
                outFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out = new FileOutputStream(outFile,true);

                try {
                    playCycle();
                    URL url = new URL(this.url);

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        throw new RuntimeException("response is not http_ok");
                    }
                    long fileLength = connection.getContentLength();
                    if (outFile.exists() &&outFile.length()==fileLength) {
                        result = Activity.RESULT_OK;
                    }
                    else {
                        input = new BufferedInputStream(connection.getInputStream());
                        byte data[] = new byte[2048];
                        long readBytes = 0;
                        int len;
                        while ((len = input.read(data)) != -1) {
                            out.write(data, 0, len);
                            readBytes += len;
                            Log.w("download", (readBytes / 1024) + "kb of " + (fileLength / 1024) + "kb");
                            //publishProgress(readBytes,fileLength);
                            prog = (int) (readBytes * 100 / (float) fileLength);
                        }
                        result = Activity.RESULT_OK;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (outFile.exists() &&outFile.length()>0)
                    {
                        result = Activity.RESULT_OK;
                    }
                } finally {
                    out.flush();
                    out.close();
                    if(input != null)
                        input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        publishResults(outFile.getAbsolutePath());
    }
    Runnable runnable ;
    Handler handler = new Handler();
    void playCycle()
    {
        updateProgress();
        if(result==Activity.RESULT_OK) {
            handler.removeCallbacks(runnable);
            return;
        }
            runnable = new Runnable() {
                @Override
                public void run() {
                    playCycle();
                }
            };
            handler.postDelayed(runnable,1000);
    }
    private void updateProgress()
    {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(TYPE,TYPE_PROGRESS);
        intent.putExtra(PROGRESS,prog);
        sendBroadcast(intent);

    }
    private void publishResults(String absolutePath) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(TYPE,TYPE_RESULT);
        intent.putExtra(FILE, absolutePath);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

}
