package cn.iwenjuan.storage.utils;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.util.Assert;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;

/**
 * @author lid
 * @date 2023/2/3 14:00
 */
@Slf4j
public class ImageUtils {

    /**
     * 图片压缩（不改变原有图片比例）
     *
     * @param inputStream   图片文件流
     * @param thumbnailSize 压缩目标大小，单位kb（压缩后图片大小小于等于该值）
     * @return
     */
    public static byte[] compress(InputStream inputStream, long thumbnailSize) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024 * 1024];
            int num;
            while ((num = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, num);
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return compress(outputStream.toByteArray(), thumbnailSize);
    }

    /**
     * 图片压缩（不改变原有图片比例）
     *
     * @param content       图片内容
     * @param thumbnailSize 压缩目标大小，单位kb（压缩后图片大小小于等于该值）
     * @return
     * @author ldan
     * @create 2020/3/10 17:40
     */
    public static byte[] compress(byte[] content, long thumbnailSize) {
        return compress(content, 0.8f, thumbnailSize);
    }

    /**
     * 图片压缩（不改变原有图片比例）
     *
     * @param content       图片内容
     * @param scale         压缩比例，建议小于0.9
     * @param thumbnailSize 压缩目标大小，单位kb（压缩后图片大小小于等于该值）
     * @return
     * @author ldan
     * @create 2020/3/10 17:40
     */
    public static byte[] compress(byte[] content, float scale, long thumbnailSize) {
        int fileSize = content.length / 1024;
        if (fileSize <= thumbnailSize) {
            return content;
        }
        InputStream inputStream = new ByteArrayInputStream(content);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            BufferedImage image = image(inputStream);
            int width = image.getWidth();
            int height = image.getHeight();
            int desWidth = new BigDecimal(width).multiply(new BigDecimal(scale)).intValue();
            int desHeight = new BigDecimal(height).multiply(new BigDecimal(scale)).intValue();
            log.info("【压缩前图片大小】：{}KB", content.length / 1024);
            Thumbnails.of(new ByteArrayInputStream(content)).size(desWidth, desHeight).outputQuality(scale).toOutputStream(out);
            content = out.toByteArray();
            fileSize = content.length / 1024;
            log.info("【压缩后图片大小】：{}KB", fileSize);
            if (fileSize > thumbnailSize) {
                // 压缩后图片大小超过目标值，继续压缩
                content = compress(content, scale, thumbnailSize);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return content;
    }

    /**
     * 创建文件
     *
     * @param path  文件路径
     * @return
     */
    public static File file(String path) {
        Assert.isTrue(StringUtils.isNotBlank(path), "File path is blank !");
        return new File(path);
    }

    /**
     * 判断文件是否为空
     *
     * @param file
     * @return
     */
    public static boolean isEmptyFile(File file) {
        if (file == null || false == file.exists()) {
            return true;
        }
        return file.length() == 0;
    }

    /**
     * 获取图片二进制流
     *
     * @param inputStream
     * @return
     */
    public static BufferedImage image(InputStream inputStream) {
        Assert.notNull(inputStream, "InputStream is null !");
        BufferedImage image = null;
        try {
            image = ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.notNull(image, "File not a image !");
        return image;
    }

    /**
     * 获取图片二进制流
     *
     * @param file
     * @return
     */
    public static BufferedImage image(File file) {
        Assert.isTrue(!isEmptyFile(file), "File not exist or empty !");
        Assert.isTrue(!file.isDirectory(), "File is a directory !");
        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.notNull(image, "File not a image !");
        return image;
    }

    /**
     * 获取图片相似度
     *
     * @param srcPath
     * @param destPath
     * @return
     */
    public static double similarity(String srcPath, String destPath) throws IOException {
        File srcFile = file(srcPath);
        File destFile = file(destPath);
        return similarity(srcFile, destFile);
    }

    /**
     * 获取图片相似度
     *
     * @param srcFile
     * @param destFile
     * @return
     */
    public static double similarity(File srcFile, File destFile) {
        Assert.isTrue(!isEmptyFile(srcFile), "Source File not exist or empty !");
        Assert.isTrue(!isEmptyFile(destFile), "Destination File not exist or empty !");
        Assert.isTrue(!srcFile.isDirectory(), "Source File is a directory !");
        Assert.isTrue(!destFile.isDirectory(), "Destination File is a directory !");
        int width = 32;
        int height = 32;
        // 缩小尺寸
        BufferedImage srcImage = resize(srcFile, width, height);
        BufferedImage destImage = resize(destFile, width, height);
        // 图像灰度化处理
        srcImage = gray(srcImage);
        destImage = gray(destImage);
        // 获取像素组
        int[] srcPixels = getPixels(srcImage);
        int[] destPixels = getPixels(destImage);
        // 获取平均灰度颜色
        int srcAverageColor = getAverageOfPixelArray(srcPixels);
        int destAverageColor = getAverageOfPixelArray(destPixels);
        // 获取灰度像素的比较数组（即图像指纹序列）
        srcPixels = getPixelDeviateWeightsArray(srcPixels, srcAverageColor);
        destPixels = getPixelDeviateWeightsArray(destPixels, destAverageColor);
        // 获取两个图的汉明距离
        int hammingDistance = getHammingDistance(srcPixels, destPixels);
        // 通过汉明距离计算相似度，取值范围 [0.0, 1.0]
        int length = width * height;
        double similarity = (length - hammingDistance) / (double) length;
        // 使用指数曲线调整相似度结果（平方计算）
        similarity = Math.pow(similarity, 2);
        return similarity;
    }

    /**
     * 修改图片尺寸
     *
     * @param file
     * @param width
     * @param height
     * @return
     */
    public static BufferedImage resize(File file, int width, int height) {
        BufferedImage image = image(file);
        return resize(image, width, height);
    }

    /**
     * 修改图片尺寸
     *
     * @param image
     * @param width
     * @param height
     * @return
     */
    public static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    /**
     * 获取像素
     *
     * @param image
     * @return
     */
    public static int[] getPixels(BufferedImage image) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);
        return pixels;
    }

    /**
     * 获取灰度图的平均像素颜色值
     *
     * @param pixels
     * @return
     */
    private static int getAverageOfPixelArray(int[] pixels) {
        Color color;
        long sumRed = 0;
        for (int i = 0; i < pixels.length; i++) {
            color = new Color(pixels[i], true);
            sumRed += color.getRed();
        }
        int averageRed = (int) (sumRed / pixels.length);
        return averageRed;
    }

    /**
     * 获取灰度图的像素比较数组（平均值的离差）
     *
     * @param pixels
     * @param averageColor
     * @return
     */
    private static int[] getPixelDeviateWeightsArray(int[] pixels, int averageColor) {
        Color color;
        int[] dest = new int[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            color = new Color(pixels[i], true);
            dest[i] = color.getRed() - averageColor > 0 ? 1 : 0;
        }
        return dest;
    }

    /**
     * 获取两个缩略图的平均像素比较数组的汉明距离（距离越大差异越大）
     *
     * @param a
     * @param b
     * @return
     */
    private static int getHammingDistance(int[] a, int[] b) {
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] == b[i] ? 0 : 1;
        }
        return sum;
    }

    /**
     * 图像灰度化处理
     *
     * @param path
     * @return
     */
    public static BufferedImage gray(String path) {
        return gray(file(path));
    }

    /**
     * 图像灰度化处理
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static BufferedImage gray(File file) {
        Assert.isTrue(!isEmptyFile(file), "File not exist or empty !");
        Assert.isTrue(!file.isDirectory(), "File is a directory !");
        return gray(image(file));
    }

    /**
     * 图像灰度化处理
     *
     * @param src
     * @return
     */
    public static BufferedImage gray(BufferedImage src) {
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage dest = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = src.getRGB(x, y);
                Color color = new Color(rgb);
                int gray = (int) (color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114);
                dest.setRGB(x, y, new Color(gray, gray, gray).getRGB());
            }
        }
        return dest;
    }

}
