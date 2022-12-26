package jp.co.internous.utopia.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import jp.co.internous.utopia.model.domain.MstDestination;
import jp.co.internous.utopia.model.mapper.MstDestinationMapper;
import jp.co.internous.utopia.model.mapper.TblCartMapper;
import jp.co.internous.utopia.model.mapper.TblPurchaseHistoryMapper;
import jp.co.internous.utopia.model.session.LoginSession;

@Controller
@RequestMapping("/utopia/settlement")
public class SettlementController {

	@Autowired
	LoginSession loginSession;
	
	@Autowired
	private MstDestinationMapper mstDestinationMapper;
	
	@Autowired
	private TblCartMapper tblCartMapper;
	
	@Autowired
	private TblPurchaseHistoryMapper tblPurchaseHistoryMapper;
	
	Gson gson = new Gson();

	@RequestMapping("/")
	public String index(Model m) {
		int userId = loginSession.getId();
		
		List<MstDestination> destinasion = mstDestinationMapper.findByUserId(userId);
		m.addAttribute("destination", destinasion);
		m.addAttribute("loginSession", loginSession);
		
			return "settlement";
	}
	
	@ResponseBody
	@RequestMapping("/complete")
	@SuppressWarnings("unchecked")
	public boolean complete(@RequestBody String destinationId) {
		Map<String, String> map = gson.fromJson(destinationId, Map.class);
		int id = Integer.parseInt(map.get("destinationId"));
		
		int userId = loginSession.getId();

		int insertCount = tblPurchaseHistoryMapper.insert(id, userId);
		
		int deleteCount = 0;
		if (insertCount > 0) {
			deleteCount = tblCartMapper.deleteByUserId(userId);
		}	
			return deleteCount == insertCount;
	}
}