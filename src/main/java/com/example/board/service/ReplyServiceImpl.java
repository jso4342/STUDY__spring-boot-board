package com.example.board.service;

import com.example.board.domain.dao.BoardDAO;
import com.example.board.domain.dao.ReplyDAO;
import com.example.board.domain.vo.Criteria;
import com.example.board.domain.vo.ReplyDTO;
import com.example.board.domain.vo.ReplyVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReplyServiceImpl implements ReplyService{

    private final ReplyDAO replyDAO;
    private final BoardDAO boardDAO;

    @Override
    public boolean register(ReplyVO replyVO) {
        log.info("reply register......." + replyVO);
        boardDAO.updateReplyCount(replyVO.getBno(), 1);
        return replyDAO.register(replyVO) == 1;
    }

    @Override
    public ReplyVO read(Long rno) {
        log.info("reply read........" + rno);
        return replyDAO.read(rno);
    }

    @Override
    public boolean remove(Long rno) {
        log.info("reply remove........." + rno);
        boardDAO.updateReplyCount(replyDAO.read(rno).getBno(), -1);
        return replyDAO.remove(rno) == 1;
    }

    @Override
    public boolean modify(ReplyVO replyVO) {
        log.info("modify..........." + replyVO);
        return replyDAO.modify(replyVO) == 1;
    }

    @Override
    public ReplyDTO getList(Long bno, Criteria criteria) {
        log.info("reply get list.........." + bno);
        // read.html에서 replyCount, list를 동시에 받아야 하기 때문에
        // 선언해놓은 다오의 getTotal과 getList가 넣어진 ReplyDTO를 리턴해준다.
        return new ReplyDTO(replyDAO.getTotal(bno), replyDAO.getList(bno, criteria));
    }

    @Override
    public int getTotal(Long bno) {
        log.info("reply get total........." + bno);
        return replyDAO.getTotal(bno);
    }
}
