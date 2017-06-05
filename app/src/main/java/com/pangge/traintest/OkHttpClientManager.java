package com.pangge.traintest;



import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


import okhttp3.OkHttpClient;


/**
 * Created by iuuu on 17/3/16.
 */

public final class OkHttpClientManager {

    private SSLSocketFactory sslSocketFactory;
    private TrustManager[] trustManagers;
    public static OkHttpClient client = new OkHttpClient();



    public void setCertificates(InputStream... certificates){

        try{
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

            //Create a KeyStore containing our trusted CAs
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);

            int index = 0;
            for(InputStream certificate : certificates){
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try{
                    if(certificate != null)
                        certificate.close();
                }catch (IOException e){

                }

            }



            //Create a TrustManager that trusts the CAs in our KeyStore
            TrustManagerFactory tmf =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            trustManagers = tmf.getTrustManagers();

            //Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, null);

            sslSocketFactory = sslContext.getSocketFactory();



        }catch (Exception e){
            e.printStackTrace();
        }
        client = new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustManagers[0])
                .build();


    }
/*
    public void run() throws Exception{

    }*/
}
