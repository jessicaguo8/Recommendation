package mars;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


import mars.common.ResponseHelper;
import mars.entity.HistoryItem;
import mars.entity.Item;
import mars.service.TicketMasterService;


@Controller
public class ApiController {
	@Autowired
	private TicketMasterService service;

	
	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public ModelAndView greeting() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("msg", "First SpringMVC project");
		mv.setViewName("hello");
		return mv;
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public void search(@RequestParam(value = "user_id") String userId, @RequestParam(value = "lat") double lat,
			@RequestParam(value = "lon") double lon, @RequestParam(value = "term") Optional<String> term,
			HttpServletRequest request, HttpServletResponse response) {
		List<Item> itemList = service.search(lat, lon, term.orElse(""));
		ResponseHelper.createResponse(response, itemList);
	}

	@RequestMapping(value = "/history", method = RequestMethod.GET)
	public void getHistory(@RequestParam(value = "user_id") String userId, HttpServletRequest request,
			HttpServletResponse response) {
	}

	@RequestMapping(value = "/history", method = RequestMethod.POST)
	public void setUserHistory(@RequestBody HistoryItem historyItem, HttpServletRequest request,
			HttpServletResponse response) {
	}

	@RequestMapping(value = "/history", method = RequestMethod.DELETE)
	public void deleteUserHistory(@RequestBody HistoryItem historyItem, HttpServletRequest request,
			HttpServletResponse response) {
	}

	@RequestMapping(value = "/recommendation", method = RequestMethod.GET)
	public void getRecommendation(@RequestParam(value = "user_id") String userId,
			@RequestParam(value = "lat") double lat, @RequestParam(value = "lon") double lon,
			HttpServletRequest request, HttpServletResponse response) {
	}

}
