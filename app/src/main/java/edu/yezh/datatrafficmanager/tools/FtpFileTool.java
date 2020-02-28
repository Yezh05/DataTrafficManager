package edu.yezh.datatrafficmanager.tools;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.InputStream;

public class FtpFileTool {


  /**
   * Description: 向FTP服务器上传文件
   *
   * @param url
   *            FTP服务器hostname
   * @param port
   *            FTP服务器端口
   * @param username
   *            FTP登录账号
   * @param password
   *            FTP登录密码
   * @param path
   *            FTP服务器保存目录，是linux下的目录形式,如/photo/
   * @param filename
   *            上传到FTP服务器上的文件名,是自己定义的名字，
   * @param input
   *            输入流
   * @return 成功返回true，否则返回false
   */
  public static boolean uploadFile(String url, int port, String username,
                                   String password, String path, String filename, InputStream input) {
    boolean success = false;
    FTPClient ftp = new FTPClient();
    try {
      int reply;
      ftp.connect(url, port);// 连接FTP服务器
      // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
      ftp.enterLocalPassiveMode();
      ftp.setFileType(FTP.BINARY_FILE_TYPE);

      ftp.login(username, password);//登录
      reply = ftp.getReplyCode();
      if (!FTPReply.isPositiveCompletion(reply)) {
        ftp.disconnect();
        return success;
      }
      ftp.changeWorkingDirectory(path);
      ftp.storeFile(filename, input);
      input.close();
      ftp.logout();
      success = true;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (ftp.isConnected()) {
        try {
          ftp.disconnect();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    return success;
  }

       /* // 测试
        public  void amain() {
            FileInputStream in = null ;
            File dir = new File("G://pathnew");
            File files[] = dir.listFiles();
            if(dir.isDirectory()) {
                for(int i=0;i<files.length;i++) {
                    try {
                        in = new FileInputStream(files[i]);
                        boolean flag = uploadFile("17.8.119.77", 21, "android", "android",
                                "/photo/", "412424123412341234_20130715120334_" + i + ".jpg", in);
                        System.out.println(flag);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }*/

}