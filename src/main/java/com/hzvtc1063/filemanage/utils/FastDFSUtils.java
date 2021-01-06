package com.hzvtc1063.filemanage.utils;

import org.csource.common.MyException;
import org.csource.fastdfs.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * @author hangzhi1063
 * @date 2020/12/9 9:36
 */
public class FastDFSUtils {

    public static  String[] upload(byte[] buffFile ,String fileExtName){
        TrackerServer ts =null;
        StorageServer ss =null;

        try {
            ClientGlobal.init("fdfs_client.conf");
            TrackerClient tc =new TrackerClient();
            ts=tc.getConnection();
            ss=tc.getStoreStorage(ts);
            StorageClient sc =new StorageClient(ts,ss);
            String[] result = sc.upload_file(buffFile, fileExtName, null);

            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        } finally {
            if (ts!=null){
                try {
                    ts.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (ss!=null){
                try {
                    ss.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static  String[] upload(){
        TrackerServer ts =null;
        StorageServer ss =null;

        try {
            ClientGlobal.init("fdfs_client.conf");
            TrackerClient tc =new TrackerClient();
            ts=tc.getConnection();
            ss=tc.getStoreStorage(ts);
            StorageClient sc =new StorageClient(ts,ss);
            String[] result = sc.upload_file("target/classes/static/test.txt", "txt", null);

            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        } finally {
            if (ts!=null){
                try {
                    ts.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (ss!=null){
                try {
                    ss.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    public static int delete(String groupName,String remoteFileName){
        TrackerServer ts =null;
        StorageServer ss =null;

        try {
            ClientGlobal.init("fdfs_client.conf");
            TrackerClient tc =new TrackerClient();
            ts=tc.getConnection();
            ss=tc.getStoreStorage(ts);
            StorageClient sc =new StorageClient(ts,ss);
            int result = sc.delete_file(groupName, remoteFileName);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        } finally {
            if (ts!=null){
                try {
                    ts.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (ss!=null){
                try {
                    ss.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return 1;

    }
    public static  byte[] download(String groupName ,String remoteFileName){
        TrackerServer ts =null;
        StorageServer ss =null;

        try {
            ClientGlobal.init("fdfs_client.conf");
            TrackerClient tc =new TrackerClient();
            ts=tc.getConnection();
            ss=tc.getStoreStorage(ts);
            StorageClient sc =new StorageClient(ts,ss);
            byte[] result = sc.download_file(groupName, remoteFileName);

            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        } finally {
            if (ts!=null){
                try {
                    ts.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (ss!=null){
                try {
                    ss.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
