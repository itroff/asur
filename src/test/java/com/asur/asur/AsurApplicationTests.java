package com.asur.asur;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.asur.asur.models.BoolCmd;
import com.asur.asur.models.CargoCmd;
import com.asur.asur.models.CmdElem;
import com.asur.asur.models.FileMeta;
import com.asur.asur.models.PosCmd;
import com.asur.asur.models.Robot;
import com.asur.asur.services.AutoControlService;

@SpringBootTest
class AsurApplicationTests {

	@Autowired
    private WebApplicationContext context;

	@Autowired
	AutoControlService autoControlService;

    private MockMvc mvc;

	private FileMeta file;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
          .apply(springSecurity())
          .build();

		file = new FileMeta();
		file.setName("Test");
		file.setRepeated(false);
		file.setRobotId("001");
		List<CmdElem> list =  new ArrayList<>();
		CmdElem el = new CmdElem();
		el.setType("Boolean");
		el.setTopic("action/start/cmd");
		el.setResponseTopic("action/start/res");
		Robot rb = new Robot();
		rb.setTopicId("001");
		BoolCmd bcmd = new BoolCmd("action/start/cmd", rb , true);
		el.setBoolCmd(bcmd);

		CmdElem el2 = new CmdElem();
		el2.setType("PosCmd");
		el2.setTopic("action/pos/cmd");
		el2.setResponseTopic("action/pos/res");
		PosCmd pcmd = new PosCmd(true, 3, new float[0], rb);
		el2.setPosCmd(pcmd);

		CmdElem el3 = new CmdElem();
		el3.setType("CargoCmd");
		el3.setTopic("action/cargo/cmd");
		el3.setResponseTopic("action/cargo/res");
		CargoCmd ccmd = new CargoCmd(true, true, rb);
		el3.setCargoCmd(ccmd);
		List<CmdElem> els = List.of(el, el2, el3);
		file.setCmds(els);
    }
/* 
	@Test
	void contextLoads() {
	}

	@Test
	@WithAnonymousUser
	public void whenAnonymousAccessLogin_thenOk() throws Exception {
		mvc.perform(get("/login"))
				.andExpect(status().isOk());
	}

	@Test
	@WithAnonymousUser
	public void whenAnonymousAccessRestrictedEndpoint_thenIsUnauthorized() throws Exception {
		mvc.perform(get("/all"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithUserDetails()
	public void whenUserAccessUserSecuredEndpoint_thenOk() throws Exception {
		mvc.perform(get("/user"))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails()
	public void whenUserAccessRestrictedEndpoint_thenOk() throws Exception {
		mvc.perform(get("/all"))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails()
	public void whenUserAccessAdminSecuredEndpoint_thenIsForbidden() throws Exception {
		mvc.perform(get("/admin"))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithUserDetails(value = "admin")
	public void whenAdminAccessUserEndpoint_thenOk() throws Exception {
		mvc.perform(get("/user"))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails(value = "admin")
	public void whenAdminAccessAdminSecuredEndpoint_thenIsOk() throws Exception {
		mvc.perform(get("/admin"))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails(value = "admin")
	public void whenAdminAccessDeleteSecuredEndpoint_thenIsOk() throws Exception {
		mvc.perform(delete("/delete").content("{}"))
				.andExpect(status().isOk());
	}
*/

	@Test
	@WithUserDetails(value = "user")
	public void testSM() throws Exception {
		//autoControlService.injectNewMission(this.file);

	}

}
