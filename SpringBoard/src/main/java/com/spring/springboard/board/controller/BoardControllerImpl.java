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
	//specify the directory for images
	private static final String ARTICLE_IMAGE_REPO = "C:\\board\\article_image";
	@Autowired
	ArticleVO articleVO;
	@Autowired
	BoardService boardService;
	
	@Override	//list all articles
	@RequestMapping(value="/board/listArticles.do", method=RequestMethod.GET)
	public ModelAndView listArticles(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String)request.getAttribute("viewName");		//Intercepter에서 request에 바인딩
		List articlesList = boardService.listArticles();
		ModelAndView mav = new ModelAndView(viewName);
		mav.addObject("articlesList", articlesList);
		return mav;
	}
	
	@Override		//add new articles
	@RequestMapping(value="/board/addNewArticle.do", method=RequestMethod.POST)
	@ResponseBody	//JSON이나 text 형식으로 데이터 전송, ResponseEntity: 상태코드 설정
	public ResponseEntity addNewArticle(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {
		multipartRequest.setCharacterEncoding("utf-8");
		Map<String, Object> articleMap = new HashMap<String, Object>();		//Save article data to Map
		Enumeration enu = multipartRequest.getParameterNames();				//bring all names of data
		
		while(enu.hasMoreElements()) {		//loop until all data names has been reviewed
			String name = (String)enu.nextElement();		//select the next name
			String value = multipartRequest.getParameter(name);		//bring data by selected names
			articleMap.put(name, value);							//save name as a key, and save data as a value
		}
		
		String imageFileName = upload(multipartRequest);		//Image file upload method(line 221)
		HttpSession session = multipartRequest.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("member");		//Writer information
		String id = memberVO.getId();
		articleMap.put("parentNO", 0);		//Basically, if it's not an answer, parentNO=0
		articleMap.put("id", id);
		articleMap.put("imageFileName", imageFileName);
		
		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {	//add new image
				int articleNO = boardService.addNewArticle(articleMap);
				if(imageFileName != null && imageFileName.length() != 0) {		//if there is the image selected
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					FileUtils.moveFileToDirectory(srcFile, destDir, true); 		//move to destination directory form temporary
				}
				
				//alert the success of regi
				message = "<script>";
				message += " alert('New article has been added.');";
				message += " location.href='" + multipartRequest.getContextPath() + "/board/listArticles.do'; ";
				message += " </script>";
				
				resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		} catch (Exception e) {		//if there is an error
			File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
			srcFile.delete();		//remove temporary file
			
			//alert the failure
			message = " <script>";
			message += " alert('Error has been occured! Please retry.');";
			message += " location.href='" + multipartRequest.getContextPath() + "/board/articleForm.do'; ";	//adding article page
			message += "</script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;
	}

	
	@Override		//view the specific article
	@RequestMapping(value="/board/viewArticle.do", method=RequestMethod.GET)
	//using RequestParam instead of request.getParameter
	public ModelAndView viewArticle(@RequestParam("articleNO") int articleNO, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String viewName = (String)request.getAttribute("viewName");
		articleVO = boardService.viewArticle(articleNO);			//bring the article data by using article number
		ModelAndView mav = new ModelAndView();
		mav.setViewName(viewName);
		mav.addObject("article", articleVO);		//Send article data to the view
		return mav;	
	}

	
	@Override		//update an article
	@RequestMapping(value="/board/modArticle.do", method=RequestMethod.POST)
	@ResponseBody	//Send data as a text or JSON form
	public ResponseEntity modArticle(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {
		multipartRequest.setCharacterEncoding("UTF-8");
		//similart to adding article
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
		         
		         //The difference of adding article, remove the previous image directory
		         String originalFileName = (String)articleMap.get("originalFileName");
		         File oldFile = new File(ARTICLE_IMAGE_REPO+"\\"+articleNO+"\\"+originalFileName);
		         oldFile.delete();
		       }	
		       message = "<script>";
			   message += " alert('Article has been updated.');";
			   message += " location.href='"+multipartRequest.getContextPath()+"/board/viewArticle.do?articleNO="+articleNO+"';";
			   message +=" </script>";
		       resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		    }catch(Exception e) {
		      File srcFile = new File(ARTICLE_IMAGE_REPO+"\\"+"temp"+"\\"+imageFileName);
		      srcFile.delete();
		      message = "<script>";
			  message += " alert('There is an error occured. Please retry.');";
			  message += " location.href='"+multipartRequest.getContextPath()+"/board/viewArticle.do?articleNO="+articleNO+"';";
			  message +=" </script>";
		      resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		    }
		    return resEnt;
	}
	
	
	
	@Override		//Remove a specific article by using article number
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
			File destDir = new File(ARTICLE_IMAGE_REPO+"\\"+articleNO);			//select an image directory by article number
			FileUtils.deleteDirectory(destDir);									//remove the directory
			
			//alert
			message = "<script>";
			message += " alert('Article has been successfully deleted.');";
			message += " location.href='"+request.getContextPath()+"/board/listArticles.do';";
			message +=" </script>";
			
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		} catch (Exception e) {		//alert error
			message = "<script>";
			message += " alert('There is an error occured. Please retry.');";
			message += " location.href='"+request.getContextPath()+"/board/listArticles.do';";
			message +=" </script>";
		    resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		    e.printStackTrace();
		}
		return resEnt;
	}
	
	//Request a form
	@RequestMapping(value="/board/*Form.do", method=RequestMethod.GET)
	private ModelAndView form(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String viewName = (String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView();
		mav.setViewName(viewName);
		return mav;
	}

	//Image upload method
	private String upload(MultipartHttpServletRequest multipartRequest) throws Exception{
		String imageFileName = null;
		Iterator<String> fileNames = multipartRequest.getFileNames();
		
		while(fileNames.hasNext()) {
			String fileName = fileNames.next();
			MultipartFile mFile = multipartRequest.getFile(fileName);		//access to data by using file names
			imageFileName = mFile.getOriginalFilename();
			File file = new File(ARTICLE_IMAGE_REPO+"\\"+"temp"+"\\"+fileName);
			if(mFile.getSize()!=0){ //if file is exist 
				if(!file.exists()){ //if there is no file in the directory
					file.getParentFile().mkdirs();  //create the directory
					mFile.transferTo(new File(ARTICLE_IMAGE_REPO +"\\"+"temp"+ "\\"+imageFileName)); //send temp file
				}
			}
		}
		return imageFileName;
	}
}
