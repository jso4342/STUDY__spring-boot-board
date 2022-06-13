package com.example.board.task;

import com.example.board.domain.dao.AttachDAO;
import com.example.board.domain.vo.AttachVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileCheckTask {

    private final AttachDAO attachDAO;

    /* [Spring cron 일정]
        0 * * * * * : 매 분 0 초 마다
        0/1 * * * * * : 매 1초 간격으로 (매 1초마다)
        0 0/1 * * * : 매 1분 간격
        0 0/5 * ? : 매 5분 간격
        0 0 0/1 * * : 매 1시간 간격
        0 0 0 * * ? : 매일 0시 마다
        0 0 0 1 * ? : 매월 1일 마다
        * 10-13 * * * * : 매 10, 11, 12, 13 분 마다 동작
        * * L * * ? : 마지막날에
        * * L-3 * * ? : 마지막날 3일 전부터 마지막날 까지
     */
    @Scheduled(cron = "0 * * * * *")
                    // 초 분 시 일 월 년
                    // 매 0 초 마다 진행
                    // 우리 프로젝트 할 때 매 0시 0분에 실행하면 될듯
    public void checkFiles() throws Exception{
        log.warn("File check Task run.............");
        log.warn("=================================");

        List<AttachVO> fileList = attachDAO.getOldFiles();
        // 모델 객체에 있는 uploadPath 만 가져오기 위해 map 을 사용한다
        List<Path> fileListPaths = fileList.stream()
                .map(file -> Paths.get("/Users/macintoshhd/Desktop/upload", file.getUploadPath(), file.getUuid(), "_", file.getFileName()))
                .collect(Collectors.toList());
                                        // Paths.get 은 , 를 붙이면 / 를 알아서 붙여줌
        // 이미지 파일은 썸네일이 있기 때문에 썸네일 경로도 추가해준다.
        fileList.stream().filter(file -> file.isImage())
                .map(file -> Paths.get("/Users/macintoshhd/Desktop/upload", file.getUploadPath(), file.getUuid(), "s_", file.getFileName()))
                .forEach(p -> fileListPaths.add(p));

        //DB 의 목록과 실제 경로의 목록을 비교하여 삭제 대상을 찾은 후 삭제
        File directory = Paths.get("/Users/macintoshhd/Desktop/upload/", getUploadPathYesterDay()).toFile();
        for(File file : directory.listFiles(file -> !fileListPaths.contains(file.toPath()))){
            log.info(file.getPath() + "deleted");
            file.delete();
        }
    }

    private String getUploadPathYesterDay(){ //어제의 업로드 패스를 리턴하는 메소드
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        return sdf.format(yesterday.getTime());
    }
}
