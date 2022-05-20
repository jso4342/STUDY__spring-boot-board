package com.example.board.mapper;

import com.example.board.domain.vo.AttachVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class AttachMapperTests {
    @Autowired
    private AttachMapper attachMapper;

    @Test
    public void testInsert(){
        AttachVO attachVO = new AttachVO();
        attachVO.setFileName("day05.png");
        attachVO.setUuid("몰라");
        attachVO.setUploadPath("2022/04/19");
        attachVO.setImage(true);
        attachVO.setBno(1012L);
        attachMapper.insert(attachVO);
    }

    @Test
    public void testSelect(){
        attachMapper.select(1012L).stream().map(AttachVO::toString).forEach(log::info);
    }

    @Test
    public void testDelete(){
        attachMapper.delete("몰라");
    }
}