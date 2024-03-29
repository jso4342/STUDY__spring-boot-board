package com.example.board.controller;

import com.example.board.aspect.annotation.LogStatus;
import com.example.board.domain.vo.AttachVO;
import com.example.board.domain.vo.BoardVO;
import com.example.board.domain.vo.Criteria;
import com.example.board.domain.vo.PageDTO;
import com.example.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
/*
 *   Task        URL             Method          Parameter       Form        URL이동
 *   전체 목록    /board/list     GET             없음             없음         없음
 *   등록 처리    /board/register POST            모든 항목         필요         이동
 *   조회        /board/read      GET            bno              필요         없음
 *   삭제 처리    /board/remove   GET             bno              필요         이동
 *   수정 처리    /board/modify   POST            모든 항목         필요         이동
 * */

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/board/*")
public class BoardController {
    private final BoardService boardService;

    @LogStatus
    @GetMapping("/list")
    public void list(Criteria criteria, Model model){
        //pageDTO를 전달
        model.addAttribute("list", boardService.getList(criteria));
        model.addAttribute("pageDTO", new PageDTO(criteria, boardService.getTotal(criteria)));
    }

    @GetMapping("/register") public void register(){}

    @LogStatus
    @PostMapping("/register")
    public RedirectView register(BoardVO boardVO, RedirectAttributes rttr){
        if(boardVO.getAttachVOList() != null){
            boardVO.getAttachVOList().forEach(attach -> log.info(attach.toString()));
        }

        boardService.register(boardVO);

//        쿼리 스트링 (targetRequestParams)
//        rttr.addAttribute("bno", boardVO.getBno());

//        Session 사용 (FlashMap-attributes)
        rttr.addFlashAttribute("bno", boardVO.getBno());

//        RedirectView를 사용하면 redirect방식으로 전송이 가능하다.
//        HandlerMapping으로 이동하여 다른 페이지를 가야 한다면, Redirect방식으로 이동한다.
//        이 때 같은 컨트롤러라면, 전체 URL 경로가 아닌 메소드의 Mapping URL만 작성해준다.
        return new RedirectView("list");
    }

    @LogStatus
    @GetMapping({"/read", "/modify"})
    public void read(Long bno, Criteria criteria, HttpServletRequest request, Model model){
        model.addAttribute("board", boardService.get(bno));
        model.addAttribute("criteria", criteria);
    }

    @LogStatus
    @GetMapping("/remove")
    public RedirectView remove(Long bno, Criteria criteria, RedirectAttributes rttr){
        String result = null;
       // List<AttachVO> attachList = boardService.getAttach(bno);

        if(boardService.remove(bno)){
         //   if (attachList != null || attachList.size() !=0){ // attachList 가 있다면

            //   deleteFiles(boardService.getAttach(bno));
           // }
            result = "success";
        }else {
            result = "failure";
        }

        rttr.addFlashAttribute("result", result);
        rttr.addAttribute("pageNum", criteria.getPageNum());
        rttr.addAttribute("amount", criteria.getAmount());
        rttr.addAttribute("type", criteria.getType());
        rttr.addAttribute("keyword", criteria.getKeyword());
        return new RedirectView("list");
    }

    @LogStatus
    @GetMapping("/getAttach")
    @ResponseBody
    public ResponseEntity<List<AttachVO>> getAttach(Long bno){
        return new ResponseEntity<>(boardService.getAttach(bno), HttpStatus.OK);
    }

    @LogStatus
    @PostMapping("/modify")
    public RedirectView modify(BoardVO boardVO, Criteria criteria, RedirectAttributes rttr){
        String result = null;

//        Redirect로 전송 시
//        addAttribute()를 사용하면 컨트롤러에 파라미터가 전달되고 그걸 통해서 화면으로 간다.
//        addFlashAttribute()를 사용하면 컨트롤러에 전달되지 않고 바로 화면으로 간다.
        rttr.addAttribute("result", boardService.modify(boardVO) ? "success" : "failure");
        rttr.addAttribute("pageNum", criteria.getPageNum());
        rttr.addAttribute("amount", criteria.getAmount());
        rttr.addAttribute("type", criteria.getType());
        rttr.addAttribute("keyword", criteria.getKeyword());
        return new RedirectView("list");
    }

    // 은닉화 , 캡슐화
    @LogStatus
    private void deleteFiles(List<AttachVO> attachList){
        attachList.forEach(attach -> {
            try {
                Path file = Paths.get("/Users/macintoshhd/Desktop/upload/" + attach.getUploadPath() +"/" + attach.getUuid() +"_" + attach.getFileName());
                Files.delete(file);
                if(Files.probeContentType(file).startsWith("image")){
                    Path thumbnail = Paths.get("/Users/macintoshhd/Desktop/upload/" + attach.getUploadPath() +"/s_" + attach.getUuid() +"_" + attach.getFileName());
                    Files.delete(thumbnail);
                }
            }catch (Exception e){

            }
        });
    }
}

