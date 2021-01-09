package com.spring.springboard.board.controller;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.spring.springboard.board.service.BoardService;
import com.spring.springboard.board.vo.ArticleVO;
import com.spring.springboard.member.vo.MemberVO;

@Controller("boardController")
public class BoardControllerImpl implements BoardController{
	//이미지 저장 디렉토리 지정
	private static final String ARTICLE_IMAGE_REPO = "C:\\board\\article_image";
	@Autowired
	ArticleVO articleVO;
	@Autowired
	BoardService boardService;
	
	@Override	//전체 게시글 조회
	@RequestMapping(value="/board/listArticle.do", method=RequestMethod.GET)
	public ModelAndView listArticles(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String)request.getAttribute("viewName");		//Intercepter에서 request에 바인딩
		List articlesList = boardService.listArticles();
		ModelAndView mav = new ModelAndView(viewName);
		mav.addObject("articlesList", articlesList);
		return mav;
	}
	
	@Override		//새 게시글 등록
	@RequestMapping(value="/board/addNewArticle.do", method=RequestMethod.POST)
	@ResponseBody	//JSON이나 text 형식으로 데이터 전송, ResponseEntity: 상태코드 설정
	public ResponseEntity addNewArticle(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {
		multipartRequest.setCharacterEncoding("utf-8");
		Map<String, Object> articleMap = new HashMap<String, Object>();		//게시글 데이터 Map에 저장
		Enumeration enu = multipartRequest.getParameterNames();				//데이터의 명칭을 모두 가져옴
		
		while(enu.hasMoreElements()) {		//데이터의 명칭을 모두 불러올 때 까지 반복
			String name = (String)enu.nextElement();		//다음 명칭 선택
			String value = multipartRequest.getParameter(name);		//선택된 명칭을 이용해서 데이터 저장
			articleMap.put(name, value);							//불러온 데이터를 값으로, 명칭을 키로 ma에 저장
		}
		
		String imageFileName = upload(multipartRequest);		//이미지 파일 업로드 메소드 이용
		HttpSession session = multipartRequest.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("member");		//게시판 글쓴이정보
		String id = memberVO.getId();
		articleMap.put("parentNO", 0);		//기본적으로 답글이 아니면 parentNO=0
		articleMap.put("id", id);
		articleMap.put("imageFileName", imageFileName);
		
		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {	//새 게시글 추가
				int articleNO = boardService.addNewArticle(articleMap);
				if(imageFileName != null && imageFileName.length() != 0) {		//이미지가 있을 경우
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					FileUtils.moveFileToDirectory(srcFile, destDir, true); 		//임시 디렉토리에서 목적 디렉토리로 이동
				}
				
				//새글 추가 성공 알림 
				message = "<script>";
				message += " alert('New article has been added.');";
				message += " location.href='" + multipartRequest.getContextPath() + "/board/listArticles.do'; ";
				message += " </script>";
				
				resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		} catch (Exception e) {		//게시글 추가 오류 발생 시
			File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
			srcFile.delete();		//임시 파일 삭제
			
			//실패 알림
			message = " <script>";
			message += " alert('Error has been occured! Please retry.');";
			message += " location.href='" + multipartRequest.getContextPath() + "/board/articleForm.do'; ";	//게시글 추가창
			message += "</script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;
	}

	
	@Override		//특정 게시글 보기
	@RequestMapping(value="/board/viewArticle.do", method=RequestMethod.GET)
	//RequestParam을 이용해서 받아온 게시글 번호 바로 넣어줌(getParameter x)
	public ModelAndView viewArticle(@RequestParam("articleNO") int articleNO, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String viewName = (String)request.getAttribute("viewName");
		articleVO = boardService.viewArticle(articleNO);			//게시글 번호로 게시글 받아오기
		ModelAndView mav = new ModelAndView();
		mav.setViewName(viewName);
		mav.addObject("article", articleVO);		//view에 article 데이터 넘겨주기
		return mav;	
	}

	
	@Override		//게시글 수정
	@RequestMapping(value="/board/modArticle.do", method=RequestMethod.POST)
	@ResponseBody	//text or JSON 형태로 전송
	public ResponseEntity modArticle(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {
		multipartRequest.setCharacterEncoding("UTF-8");
		//새 게시글 등록과 비슷함
		Map<String,Object> articleMap = new HashMap<String,Object>();
		Enumeration enu=multipartRequest.getParameterNames();
		while(enu.hasMoreElements()){
			String name=(String)enu.nextElement();
			String value=multipartRequest.getParameter(name);
			articleMap.put(name,value);
		}
		
		String imageFileName= upload(multipartRequest);
		articleMap.put("imageFileName", imageFileName);
		
		String articleNO=(String)articleMap.get("articleNO");
		String message;
		ResponseEntity resEnt=null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		
		try {
		       boardService.modArticle(articleMap);
		       if(imageFileName!=null && imageFileName.length()!=0) {
		         File srcFile = new File(ARTICLE_IMAGE_REPO+"\\"+"temp"+"\\"+imageFileName);
		         File destDir = new File(ARTICLE_IMAGE_REPO+"\\"+articleNO);
		         FileUtils.moveFileToDirectory(srcFile, destDir, true);
		         
		         //새 게시글 등록과 다른점, 이전 이미지 파일은 삭제
		         String originalFileName = (String)articleMap.get("originalFileName");
		         File oldFile = new File(ARTICLE_IMAGE_REPO+"\\"+articleNO+"\\"+originalFileName);
		         oldFile.delete();
		       }	
		       message = "<script>";
			   message += " alert('글을 수정했습니다.');";
			   message += " location.href='"+multipartRequest.getContextPath()+"/board/viewArticle.do?articleNO="+articleNO+"';";
			   message +=" </script>";
		       resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		    }catch(Exception e) {
		      File srcFile = new File(ARTICLE_IMAGE_REPO+"\\"+"temp"+"\\"+imageFileName);
		      srcFile.delete();
		      message = "<script>";
			  message += " alert('오류가 발생했습니다.다시 수정해주세요');";
			  message += " location.href='"+multipartRequest.getContextPath()+"/board/viewArticle.do?articleNO="+articleNO+"';";
			  message +=" </script>";
		      resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		    }
		    return resEnt;
	}
	
	
	
	@Override		//게시글 삭제, 특정 게시글 보기처럼 '특정' 을 지정하기 위해 requestParam으로 articleNO 불러옴
	@RequestMapping(value="/board/removeArticle.do", method=RequestMethod.POST)
	public ResponseEntity removeArticle(@RequestParam("articleNO") int articleNO, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.setContentType("text/html; charset=utf-8");
		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		
		try {
			
			boardService.removeArticle(articleNO);
			File destDir = new File(ARTICLE_IMAGE_REPO+"\\"+articleNO);			//게시글 번호로 이미지 디렉토리 특정
			FileUtils.deleteDirectory(destDir);									//해당 디렉토리 삭제
			
			//글삭제 알림
			message = "<script>";
			message += " alert('글을 삭제했습니다.');";
			message += " location.href='"+request.getContextPath()+"/board/listArticles.do';";
			message +=" </script>";
			
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		} catch (Exception e) {		//오류 발생 알림
			message = "<script>";
			message += " alert('작업중 오류가 발생했습니다.다시 시도해 주세요.');";
			message += " location.href='"+request.getContextPath()+"/board/listArticles.do';";
			message +=" </script>";
		    resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		    e.printStackTrace();
		}
		return resEnt;
	}
	
	//Form 요청
	@RequestMapping(value="/board/*Form.do", method=RequestMethod.GET)
	private ModelAndView form(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String viewName = (String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView();
		mav.setViewName(viewName);
		return mav;
	}

	//이미지 업로드 메소드
	private String upload(MultipartHttpServletRequest multipartRequest) throws Exception{
		String imageFileName = null;
		Iterator<String> fileNames = multipartRequest.getFileNames();
		
		while(fileNames.hasNext()) {
			String fileName = fileNames.next();
			MultipartFile mFile = multipartRequest.getFile(fileName);		//가져온 파일 이름으로 데이터 접근
			imageFileName = mFile.getOriginalFilename();
			File file = new File(ARTICLE_IMAGE_REPO+"\\"+"temp"+"\\"+fileName);
			if(mFile.getSize()!=0){ //파일 존재여부 
				if(!file.exists()){ //디렉토리에 파일이 존재하지 않으면
					file.getParentFile().mkdirs();  //디렉토리 생성
					mFile.transferTo(new File(ARTICLE_IMAGE_REPO +"\\"+"temp"+ "\\"+imageFileName)); //임시 저장파일을 전달
				}
			}
		}
		return imageFileName;
	}
}
