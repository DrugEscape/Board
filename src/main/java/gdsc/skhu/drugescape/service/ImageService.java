package gdsc.skhu.drugescape.service;

import gdsc.skhu.drugescape.domain.model.Board;
import gdsc.skhu.drugescape.domain.model.Image;
import gdsc.skhu.drugescape.domain.repository.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {
    private final ImageRepository imageRepository;
    // 파일 저장 경로는 환경에 따라 설정해야 합니다.
    private final String fileDir = "/path/to/upload-images/";

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    // 실제 이미지 URL이 아닌, 파일 시스템에 저장된 이미지의 경로를 반환합니다.
    public Image saveImage(String imageUrl, Board board) throws IOException {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }

        String savedFilename = saveFileToSystem(imageUrl);
        Image image = Image.builder()
                .imageURL(savedFilename)
                .board(board)
                .build();

        return imageRepository.save(image);
    }

    private String saveFileToSystem(String imageUrl) throws IOException {
        // 여기서는 파일 이름을 예시로 생성합니다. 실제 구현에서는 파일 업로드 로직이 필요합니다.
        String savedFilename = UUID.randomUUID().toString() + ".jpg"; // 확장자는 예시입니다.
        Path path = Paths.get(fileDir + savedFilename);
        Files.createDirectories(path.getParent());
        Files.createFile(path);
        // 실제로는 imageUrl에서 파일을 다운로드하여 path에 저장하는 로직을 구현해야 합니다.
        return savedFilename;
    }

    @Transactional
    public void deleteImage(Image image) throws IOException {
        if (image != null) {
            Path path = Paths.get(fileDir + image.getImageURL());
            Files.deleteIfExists(path);
            imageRepository.delete(image);
        }
    }
}