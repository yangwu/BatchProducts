package tools;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 * 
 * FTPClient上传产品图片到服务器时， 本机需要关闭防火墙
 * 
 * */
public class FilesTool {

	public static final String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public final static String TXTFILENAME = "read.txt";
	public final static String IMAGE_EXTENTION = "jpg";
	public final static String IMAGE_PREFIX = "http://wishconsole.com/images/";

	public final static String MAIN_IMAGE = "1.jpg";
	public final static String EXTRA_IMAGE1 = "2.jpg";
	public final static String EXTRA_IMAGE2 = "3.jpg";
	public final static String EXTRA_IMAGE3 = "4.jpg";
	public final static String EXTRA_IMAGE4 = "5.jpg";
	public final static String EXTRA_IMAGE5 = "6.jpg";
	public final static String EXTRA_IMAGE6 = "7.jpg";

	/**
	 * 第一步： command = 1; 1,自动删除尺寸太小的图片(<300); 2,在每个文件夹下生成一个read.txt文件，描述产品信息；
	 * 用户可在此基础上继续修改该文件，完成产品的描述。
	 */

	public void parseDirectory(String fileName) {
		File products = new File(fileName);

		if (!products.exists()) {
			System.out.println("no this file: " + fileName);
			return;
		}
		File[] subFiles = products.listFiles();

		// 处理各个产品目录
		for (int k = 0; k < subFiles.length; k++) {
			File file = subFiles[k];
			if (file.isDirectory()) {
				File[] images = file.listFiles();
				for (int i = 0; i < images.length; i++) {// 处理每个产品中的图片文件
					File image = images[i];
					String imageName = image.getName();
					if (image.isFile()) {
						String ext = this.getExtension(imageName);
						if (ext != null
								&& ext.toLowerCase().equals(IMAGE_EXTENTION)) {
							try {
								BufferedImage bufferImage = ImageIO.read(image);
								int width = bufferImage.getWidth();
								int height = bufferImage.getHeight();
								if (width < 300 || height < 300) {
									bufferImage = null;
									if (!image.delete()) {
										System.out.println("delete file: "
												+ imageName + " failed");
									}
								}
							} catch (IOException e) {
								System.out.println("read file failed:"
										+ imageName + e.getMessage());
							}
						}
					}
				}

				// 创建txt文件
				String txtname = file.getAbsolutePath() + "\\" + TXTFILENAME;
				System.out.println("create new file:" + txtname);
				File txtFile = new File(txtname);
				// if (!txtFile.exists()) {
				try {
					txtFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("faild to create txt file,"
							+ e.getMessage());
				}
				// }

				FileOutputStream out;
				try {
					out = new FileOutputStream(txtFile);
					System.out.println("start to write:");

					String name = "Name:"
							+ BaiduTranslateDemo.translateToEn(file.getName())
							+ file.getName();
					out.write(name.getBytes("utf-8"));

					String sku = "Parent_sku:stk_"
							+ this.getLastBracketContent(file.getName()) + "\n";
					out.write((sku).getBytes("utf-8"));

					String desc = "Description:Fashion DIY wall sticker, size: 60 * 90 cm, you can compose it by yourself.\n";
					out.write(desc.getBytes("utf-8"));

					String tags = "Tags:sticker,wall sticker,home decor,vinyl wall sticker,wall decals&sticker,decals wall sticker,cartoon wall sticker,living room wall sticker,cute wall sticker,removable wall sticker \n";
					out.write(tags.getBytes("utf-8"));

					out.write(("Color:\n").getBytes("utf-8"));
					out.write(("Size:\n").getBytes("utf-8"));
					out.write(("Price:\n").getBytes("utf-8"));
					out.write(("IncrementPrice:\n").getBytes("utf-8"));
					out.write(("Quantity:10\n").getBytes("utf-8"));
					out.write(("shipping:\n").getBytes("utf-8"));
					out.write(("shippingtime:7-30\n").getBytes("utf-8"));
					out.write(("MSRP:\n").getBytes("utf-8"));
					out.write(("Brand:\n").getBytes("utf-8"));
					out.write(("UPC:\n").getBytes("utf-8"));
					out.write(("LandingPageURL:\n").getBytes("utf-8"));
					out.write(("SourceURL:" + products.getName() + "/"
							+ file.getName() + "\n").getBytes("utf-8"));
					out.write(("SchedultTime:\n").getBytes("utf-8"));

					out.flush();
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public String getExtension(String fileName) {
		int index = fileName.lastIndexOf(".");
		if (index != -1)
			return fileName.substring(index + 1);
		return null;
	}

	public String getLastBracketContent(String fileName) {
		int left = fileName.lastIndexOf("(");
		int right = fileName.lastIndexOf(")");
		System.out.println("fileName" + fileName + ",left=" + left
				+ ",right = " + right);
		if (left > 0 && right > 0) {
			return fileName.substring(left + 1, right);
		}
		return "";
	}

	public Map<String, String> uploadImagesToServer(File imageDirectory) {
		Map<String, String> resultMap = new HashMap<String, String>();
		FTPClient ftp = new FTPClient();
		int reply;
		try {
			ftp.connect("120.25.88.137");
			ftp.login("myftp", "yangwu");
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return null;
			}
			ftp.changeWorkingDirectory("./images");
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

			File[] images = imageDirectory.listFiles();
			for (int k = 0; k < images.length; k++) {
				File curFile = images[k];
				if (curFile.isFile()
						&& IMAGE_EXTENTION.equals(getExtension(
								curFile.getName()).toLowerCase())) {
					String newfilename = generateString(8) + "."
							+ this.getExtension(curFile.getName());

					boolean result = ftp.storeFile(newfilename,
							new FileInputStream(curFile));
					if (result) {
						System.out.println("upload file success" + newfilename);
						BufferedImage bufferImage = ImageIO.read(curFile);
						int width = bufferImage.getWidth();
						int height = bufferImage.getHeight();
						if (width > 800 || height > 800) {
							newfilename += "_800x800.jpg";
						}
						resultMap.put(curFile.getName(), newfilename);
					} else {
						System.out.println("upload file failed"
								+ curFile.getPath() + "/" + curFile.getName());
					}
				}
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultMap;
	}

	public String generateString(int length) {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
		}
		return sb.toString();
	}

	/**
	 * 第二步为用户手动处理read.txt文件和图片。 第三步： command=3; 读取
	 * read.txt文件，并把图片和信息保存到服务器上。服务器会自动定时上传产品。
	 * */
	public void parseDirectoryTxt(String fileName) {
		File products = new File(fileName);

		if (!products.exists()) {
			System.out.println("no this file: " + fileName);
			return;
		}
		File[] subFiles = products.listFiles();

		for (int k = 0; k < subFiles.length; k++) {
			File file = subFiles[k];
			if (file.isDirectory()) {
				File[] childFiles = file.listFiles();
				Map<String, String> productValue = new HashMap<String, String>();
				for (int i = 0; i < childFiles.length; i++) {
					File child = childFiles[i];
					String childName = child.getName();
					if (TXTFILENAME.equals(childName)) {
						FileInputStream fis = null;
						InputStreamReader isr = null;
						BufferedReader br = null;
						try {
							fis = new FileInputStream(child);
							isr = new InputStreamReader(fis);
							br = new BufferedReader(isr);
							String temp = null;
							while ((temp = br.readLine()) != null) {
								System.out.println("Line:" + temp);
								int index = temp.indexOf(":");
								productValue.put(temp.substring(0, index),
										temp.substring(index+1));
							}
							br.close();
							isr.close();
							fis.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							try {
								if (br != null)
									br.close();
								if (isr != null)
									isr.close();
								if (fis != null)
									fis.close();
							} catch (IOException e) {
								System.out.println("close file faild");
							}

						}
					}
				}

				Map<String, String> newImages = this.uploadImagesToServer(file);
				if (newImages.size() > 0) {
					if (newImages.get(MAIN_IMAGE) != null) {

						productValue.put("Main_image",
								IMAGE_PREFIX + newImages.get(MAIN_IMAGE));
					}

					String extraImages = "";

					extraImages += (newImages.get(EXTRA_IMAGE1) != null) ? IMAGE_PREFIX
							+ newImages.get(EXTRA_IMAGE1) + "|"
							: "";
					extraImages += (newImages.get(EXTRA_IMAGE2) != null) ? IMAGE_PREFIX
							+ newImages.get(EXTRA_IMAGE2) + "|"
							: "";
					extraImages += (newImages.get(EXTRA_IMAGE3) != null) ? IMAGE_PREFIX
							+ newImages.get(EXTRA_IMAGE3) + "|"
							: "";
					extraImages += (newImages.get(EXTRA_IMAGE4) != null) ? IMAGE_PREFIX
							+ newImages.get(EXTRA_IMAGE4) + "|"
							: "";
					extraImages += (newImages.get(EXTRA_IMAGE5) != null) ? IMAGE_PREFIX
							+ newImages.get(EXTRA_IMAGE5) + "|"
							: "";
					extraImages += (newImages.get(EXTRA_IMAGE6) != null) ? IMAGE_PREFIX
							+ newImages.get(EXTRA_IMAGE6)
							: "";

					productValue.put("Extra_images", extraImages);
				}

				insertdb(productValue);
			}
		}
	}

	private void insertdb(Map<String, String> productValue) {
		Iterator<String> it = productValue.keySet().iterator();
		System.out.println("insertdb:");
		while (it.hasNext()) {
			String key = it.next();
			System.out.println(key + ":" + productValue.get(key));
		}
	}
}
