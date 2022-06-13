package com.example.board.service;

import com.example.board.domain.dao.AttachDAO;
import com.example.board.domain.dao.BoardDAO;
import com.example.board.domain.vo.AttachVO;
import com.example.board.domain.vo.BoardVO;
import com.example.board.domain.vo.Criteria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

    private final BoardDAO boardDAO;
    private final AttachDAO attachDAO;

    @Override
    public List<BoardVO> getList(Criteria criteria) {
        return boardDAO.getList(criteria);
    }

    //적어놓은 익셉션이 발생할때에만 롤백이 된다.
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(BoardVO boardVO) {
        //롤백에 익셉션 정해주지 않을때는 이렇게 따로 적어줘야한다.
        /* if(boardVO.getAttachVOList()==null){throw new RuntimeException();}*/
        boardDAO.register(boardVO);
        if(boardVO.getAttachVOList() != null) {
            boardVO.getAttachVOList().forEach(attachVO -> {
                attachVO.setBno(boardVO.getBno());
                attachDAO.register(attachVO);
            });
        }
    }

    @Transactional
    @Override
    public boolean modify(BoardVO boardVO) {
        attachDAO.removeAll(boardVO.getBno()); // 삭제 했다가
        boolean check = boardDAO.modify(boardVO);
        if (check){
            boardVO.getAttachVOList().forEach(attach -> {
                attach.setBno(boardVO.getBno()); //bno 만 저장해서
                attachDAO.register(attach); // 새로 insert
            });
        }
        return check;
    }

    @Transactional
    @Override
    public boolean remove(Long bno) {
        //해당 게시글의 첨부파일들은 공통된 UUID 가 있으며
        //UUID 로 첨부파일을 삭제하면 DBMS 쪽에서 처리가 되기 때문에
        //JAVA 단에서는 반복을 돌릴 필요가 없다
       // attachDAO.removeAll(bno); //RDB 에서 ON DELETE CASCADE 명령어를 썼기 때문에 직접 처리할 필요가 없다.
        return boardDAO.remove(bno);
    }

    @Override
    public BoardVO get(Long bno) {
        return boardDAO.get(bno);
    }

    @Override
    public int getTotal(Criteria criteria) {
        return boardDAO.getTotal(criteria);
    }

    @Override
    public List<AttachVO> getAttach(Long bno){
        return attachDAO.selectAllByBno(bno);
    }
}
