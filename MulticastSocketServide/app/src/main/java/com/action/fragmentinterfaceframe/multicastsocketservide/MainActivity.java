package com.action.fragmentinterfaceframe.multicastsocketservide;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    MulticastSocket s;
    DatagramPacket dgramPacket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onBrodacastSend();

    }

    /**
     * onBrodacastSend() 發送
     */
    private void onBrodacastSend() {
        try {

            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifi != null){
                WifiManager.MulticastLock lock = wifi.createMulticastLock("mylock");
                lock.acquire();
            }

            // send
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
            String msg = "{\"event\": \"" + time + "\"}";
            InetAddress group = InetAddress.getByName("239.5.6.7");

            s = new MulticastSocket(12333);
            s.joinGroup(group);
            dgramPacket = new DatagramPacket(msg.getBytes(), msg.length(), group, 12332);


            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {

                        try {
                            s.send(dgramPacket);
                            Log.e("socketdata", dgramPacket.getSocketAddress().toString());

                            // 每執行一次，休眠2s，然后繼續下一次任務
                            Thread.sleep(2000);

                        } catch (InterruptedException e) {
                            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }).start();
        } catch (UnknownHostException e) {
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
