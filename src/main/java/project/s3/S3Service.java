package project.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    public S3Service(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
        return upload(uploadFile, dirName);
    }

    public List<String> multiUpload(List<MultipartFile> files, String dirName) throws IOException {
        List<File> convertFiles = multiConvert(files);
        List<String> fileList = new ArrayList<>();
        for (File convertFile : convertFiles) {
            fileList.add(upload(convertFile, dirName));
        }
        return fileList;
    }

    private String upload(File uploadFile, String dirName) {
        String fileName = dirName + "/" + uploadFile.getName();
        putS3(uploadFile, fileName);

        removeNewFile(uploadFile);
        return fileName;
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        targetFile.delete();
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(file.getOriginalFilename());
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    private List<File> multiConvert(List<MultipartFile> multipartFiles) throws IOException {
        List<File> convertFiles = new ArrayList<>();
        for (MultipartFile file : multipartFiles) {
            File convertFile = new File(file.getOriginalFilename());
            if (convertFile.createNewFile()) {
                try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                    fos.write(file.getBytes());
                }
            }
            convertFiles.add(convertFile);
        }
        return convertFiles;
    }

    public void fileDelete(String key) {
        amazonS3Client.deleteObject(bucket, key);
    }

    public void fileListDelete(List<String> keys) {
        keys.forEach(key -> amazonS3Client.deleteObject(bucket, key));
    }

}