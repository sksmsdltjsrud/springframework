package com.spring.javagreenS;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.javagreenS.pagination.PageProcess;
import com.spring.javagreenS.pagination.PageVO;
import com.spring.javagreenS.service.BoardService;
import com.spring.javagreenS.service.MemberService;
import com.spring.javagreenS.vo.BoardReplyVO;
import com.spring.javagreenS.vo.BoardVO;
import com.spring.javagreenS.vo.MemberVO;

@Controller
@RequestMapping("/board")
public class BoardController {
	
	@Autowired
	BoardService boardService;
	
	@Autowired
	PageProcess pageProcess;
	
	@Autowired
	MemberService memberService;
	
	@RequestMapping(value = "/boList", method = RequestMethod.GET)
	public String boListGet(
			@RequestParam(name="pag", defaultValue = "1", required = false) int pag,
			@RequestParam(name="pageSize", defaultValue = "5", required = false) int pageSize,
			Model model) {
		PageVO pageVO = pageProcess.totRecCnt(pag, pageSize, "board", "", "");
		
		List<BoardVO> vos = boardService.getBoardList(pageVO.getStartIndexNo(), pageSize);
		model.addAttribute("vos", vos);
		model.addAttribute("pageVO",pageVO);
		
		return "board/boList";
	}
	
	@RequestMapping(value = "/boInput", method = RequestMethod.GET)
	public String boInputGet(Model model, HttpSession session) {
		String mid = (String) session.getAttribute("sMid");
		MemberVO vo = memberService.getMemIdCheck(mid);
		
		model.addAttribute("vo", vo);
		
		return "board/boInput";
	}
	
	@RequestMapping(value = "/boInput", method = RequestMethod.POST)
	public String boInputPost(BoardVO vo) {
		System.out.println("vo : " + vo);
		// 만약에 content에 이미지가 저장되어 있다면, 저장된 이미지만을 /resources/data/ckeditor/board/ 폴더에 저장시켜준다.
		boardService.imgCheck(vo.getContent());
		
		// 이미지 복사작업이 끝나면, board폴더에 실제로 저장된 파일명을 DB에 저장시켜준다.
		vo.setContent(vo.getContent().replace("/data/ckeditor/", "/data/ckeditor/board/"));
		boardService.setBoardInput(vo);
		
		return "redirect:/msg/boardInputOk";
	}
	
	@RequestMapping(value = "/boContent", method = RequestMethod.GET)
	public String boContentGet(int idx, int pag, int pageSize, Model model, HttpSession session) {
		// 조회수 증가(조회수 중복방지처리)
		ArrayList<String> contentIdx = (ArrayList) session.getAttribute("sContentIdx");
		if(contentIdx == null) contentIdx = new ArrayList<String>();
		
		String imsiContentIdx = "board" + idx;
		if(!contentIdx.contains(imsiContentIdx)) {
			boardService.setReadNum(idx);
			contentIdx.add(imsiContentIdx);
		}
		session.setAttribute("sContentIdx", contentIdx);
		
		// 원본글 가져오기
		BoardVO vo = boardService.getBoardContent(idx);
		
		// '이전/다음'글 가져오기
		ArrayList<BoardVO> pnVos = boardService.getPreNext(idx);
		int minIdx = boardService.getMinIdx();
		
		model.addAttribute("vo", vo);
		model.addAttribute("pnVos", pnVos);
		model.addAttribute("minIdx", minIdx);
		model.addAttribute("pag", pag);
		model.addAttribute("pageSize", pageSize);

		// 댓글 가져오기(replyVos)
		ArrayList<BoardReplyVO> replyVos = boardService.getBoardReply(idx);
		model.addAttribute("replyVos", replyVos);
		
		return "board/boContent";
	}
	
	@RequestMapping(value = "/boSearch", method = RequestMethod.POST)
	public String boSearchPost(
			@RequestParam(name="pag", defaultValue = "1", required = false) int pag,
			@RequestParam(name="pageSize", defaultValue = "5", required = false) int pageSize,
			String search,
			String searchString,
			Model model) {
		pageSize = 100;
		PageVO pageVO = pageProcess.totRecCnt(pag, pageSize, "board", search, searchString);
		
		List<BoardVO> vos = boardService.getBoardSearch(pageVO.getStartIndexNo(), pageSize, search, searchString);
		
		String searchTitle;
		if(search.equals("title")) searchTitle = "글제목";
		else if(search.equals("nickName")) searchTitle = "닉네임";
		else searchTitle = "글내용";
		
		model.addAttribute("vos", vos);
		model.addAttribute("pageVO",pageVO);
		model.addAttribute("searchTitle", searchTitle);
		model.addAttribute("searchString",searchString);
		
		return "board/boSearch";
	}
	
	// 게시글 삭제처리
	@RequestMapping(value = "/boDeleteOk", method = RequestMethod.GET)
	public String boDeleteOkGet(int idx, int pag, int pageSize, Model model) {
		// 게시글에 사진이 존재한다면 서버에 존재하는 사진파일을 먼저 삭제처리한다.
		BoardVO vo = boardService.getBoardContent(idx);
		if(vo.getContent().indexOf("src=\"/") != -1) boardService.imgDelete(vo.getContent());
		
		// DB에서 실제 게시글을 삭제처리한다.
		boardService.setBoardDelete(idx);
		
		model.addAttribute("flag", "?pag="+pag+"&pageSize="+pageSize);
		return "redirect:/msg/boardDeleteOk";
	}
	
	// 게시글 수정하기위해서 폼화면 불러오기
	@RequestMapping(value = "/boUpdate", method = RequestMethod.GET)
	public String boUpdateGet(int idx, int pag, int pageSize, Model model) {
		// 수정창으로 들어올때 원본파일에 그림파일이 존재한다면, 현재폴더(board)의 그림파일을 ckeditor폴더로 복사시켜둔다.
		BoardVO vo = boardService.getBoardContent(idx);
		if(vo.getContent().indexOf("src=\"/") != -1) boardService.imgCheckUpdate(vo.getContent());
		
		model.addAttribute("vo", vo);
		model.addAttribute("pag", pag);
		model.addAttribute("pageSize", pageSize);
		
		return "board/boUpdate";
	}
	
	// 게시글 수정하기위해서 폼화면 불러오기
	@RequestMapping(value = "/boUpdate", method = RequestMethod.POST)
	public String boUpdatePost(BoardVO vo, int pag, int pageSize, Model model) {
		// 수정창으로 들어올때 원본파일에 그림파일이 존재한다면, 현재폴더(board)의 그림파일을 ckeditor폴더로 복사시켜둔다.
		BoardVO oriVo = boardService.getBoardContent(vo.getIdx());
		
		// content안에서 내용의 수정이 없을시는 아래작업을 처리할 필요가 없다.
		if(!oriVo.getContent().equals(vo.getContent()))	{
			System.out.println("내용 수정하였음...");
			// 수정버튼을 클릭하고 post 호출시에는 기존의 board폴더의 사진파일들을 모두 삭제처리한다.
			if(oriVo.getContent().indexOf("src=\"/") != -1) boardService.imgDelete(oriVo.getContent());
			
			// 파일복사전에 원본파일의 위치가 'ckeditor/board'폴더였던것을 'ckeditor'폴더로 변경시켜두어야 한다.
			vo.setContent(vo.getContent().replace("/data/ckeditor/board/", "/data/ckeditor/"));
			
			// 앞의 준비작업이 완료되면, 수정된 그림(복사된그림)을 다시 board폴더에 속사처리한다.(/data/ckeditor/ -> /data/ckeditor/board/)
			// 이 작업은 처음 게시글을 올릴때의 파일복사 작업과 동일한 작업이다.
			boardService.imgCheck(vo.getContent());
			
			// 다시 ckeditor에 있는 그림파일의 경로를 ckeditor/board폴더로 변경시켜준다.
			vo.setContent(vo.getContent().replace("/data/ckeditor/", "/data/ckeditor/board/"));
		}
		
		// 잘 정비된 vo를 DB에 저장시켜준다.
		boardService.setBoardUpdate(vo);
		
		model.addAttribute("flag", "?pag="+pag+"&pageSize="+pageSize);
		
		return "redirect:/msg/boUpdateOk";
	}
	
	@ResponseBody
	@RequestMapping(value = "/boardReplyInput", method = RequestMethod.POST)
	public String boardReplyInputPost(BoardReplyVO replyVo) {
		//int level = 0;
		int levelOrder = 0;
		
		String strLevelOrder = boardService.maxLevelOrder(replyVo.getBoardIdx());
		if(strLevelOrder != null) levelOrder =  Integer.parseInt(strLevelOrder) + 1;
		replyVo.setLevelOrder(levelOrder);
		
		boardService.setBoardReplyInput(replyVo);
		
		return "1";
	}
	
	// 대댓글의 경우 나중에 쓴 댓글이 먼저 나오게 처리한것
	@ResponseBody
	@RequestMapping(value = "/boardReplyInput2", method = RequestMethod.POST)
	public String boardReplyInput2Post(BoardReplyVO replyVo) {
		boardService.levelOrderPlusUpdate(replyVo);				// 부모댓글의 levelOrder값보다 큰 모든 댓글의 levelOrder값을 +1 시켜준다(update)
		replyVo.setLevel(replyVo.getLevel()+1);  					// 자신의 level은 부모 level 보다 +1 시켜준다.
		replyVo.setLevelOrder(replyVo.getLevelOrder()+1);	// 자신의 levelOrder은 부모의 levelOrder보다 +1 시켜준다.
		
		boardService.setBoardReplyInput2(replyVo);
		
		return "";
	}
	
	@ResponseBody
	@RequestMapping(value = "/boReplyDeleteOk", method = RequestMethod.POST)
	public String boReplyDeleteOkPost(int idx) {
		boardService.setBoardReplyDeleteOk(idx);
		return "";
	}
}
