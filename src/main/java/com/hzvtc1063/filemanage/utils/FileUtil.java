package com.hzvtc1063.filemanage.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author hangzhi1063
 * @date 2020/12/20 16:48
 */
@Slf4j
public class FileUtil {

    private static final int BUFFER_SIZE = 2 * 1024;


    public static void downloadFile(HttpServletResponse response, File file) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            response.setCharacterEncoding("UTF-8");
            //response.setContentType("application/force-download");//应用程序强制下载
            //如果文件不存在
            if (file == null || !file.exists()) {
                String msg = "文件不存在!";
                System.out.println(msg);
                PrintWriter out = response.getWriter();
                out.write(msg);
                out.flush();
                out.close();
                return;
            }
            String simpleName = file.getName().substring(file.getName().lastIndexOf("/") + 1);
            String newFileName = new String(simpleName.getBytes(), "utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(newFileName, "UTF-8"));
            bis = new BufferedInputStream(
                    new FileInputStream(file));
            bos = new BufferedOutputStream(
                    response.getOutputStream());
            byte[] buffer = new byte[1024];
            int length;
            while ((length = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }
        } catch (ClientAbortException e) {
            log.info("---qq浏览器导致下载异常--");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (ClientAbortException e) {
                    log.info("---qq浏览器导致下载异常--");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (ClientAbortException e) {
                    log.info("---qq浏览器导致下载异常--");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static long getTotalSizeOfFilesInDir(File file) {
        if (file.isFile())
            return file.length();
        final File[] children = file.listFiles();
        long total = 0;
        if (children != null)
            for (File child : children)
                total += getTotalSizeOfFilesInDir(child);
        return total;
    }

    public static String getFileSize(Double size) {
        if (size / 1073741824 >= 1) {
            String format = String.format("%.2f", size / 1073741824);
            return format + "GB";
        }

        if (size / 1048576 >= 1) {
            String format = String.format("%.2f", size / 1048576);
            return format + "MB";
        }
        if (size / 1024 > 1) {
            String format = String.format("%.2f", size / 1024);
            return format + "KB";
        }

        return size + "B";
    }

    public static void toZip(String srcDir, String zipPath, boolean KeepDirStructure)
            throws RuntimeException, FileNotFoundException {
        File file = new File(zipPath);
        OutputStream out = new FileOutputStream(file);
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);

            compress(sourceFile, zos, sourceFile.getName(), KeepDirStructure);
            long end = System.currentTimeMillis();

            System.out.println("压缩完成，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static void compress(File sourceFile, ZipOutputStream zos, String name,
                                 boolean KeepDirStructure) throws Exception {
        byte[] buf = new byte[BUFFER_SIZE];
        if (sourceFile.isFile()) {
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if (KeepDirStructure) {
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }
            } else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (KeepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(), KeepDirStructure);
                    } else {
                        compress(file, zos, file.getName(), KeepDirStructure);
                    }

                }
            }
        }
    }
}
