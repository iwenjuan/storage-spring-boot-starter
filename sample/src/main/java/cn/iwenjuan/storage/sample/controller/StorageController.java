package cn.iwenjuan.storage.sample.controller;

import cn.iwenjuan.storage.domain.UploadResponse;
import cn.iwenjuan.storage.service.IStorageService;
import cn.iwenjuan.storage.utils.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author li1244
 * @date 2023/3/27 11:51
 */
@RestController
@RequestMapping("storage")
public class StorageController {

    @Resource
    private IStorageService storageService;

    /**
     * 文件上传
     * @param request
     * @return
     */
    @PostMapping("upload")
    public UploadResponse upload(MultipartHttpServletRequest request) {

        MultipartFile multipartFile = request.getFile("file");
        UploadResponse uploadResponse = storageService.upload(multipartFile);
        return uploadResponse;
    }

    /**
     * 文件下载
     * @param fileUrl
     * @param request
     * @param response
     */
    @GetMapping("download")
    public void download(@RequestParam("fileUrl") String fileUrl, HttpServletRequest request, HttpServletResponse response) {

        String fileName = request.getParameter("fileName");
        if (StringUtils.isBlank(fileName)) {
            fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        }

        response.setCharacterEncoding("utf-8");
        response.setContentType("application/force-download");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);

        try {
            OutputStream outputStream = response.getOutputStream();
            storageService.download(outputStream, fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除已上传文件
     * @param fileUrl
     * @return
     */
    @PostMapping("delete")
    public String delete(@RequestParam("fileUrl") String fileUrl) {

        storageService.delete(fileUrl);
        return "success";
    }
}
