package com.ushahidi.swiftriver.core.api.dao;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@ContextConfiguration(locations={"file:src/main/webapp/WEB-INF/spring/root-context.xml", "file:src/main/webapp/WEB-INF/spring/web-context.xml"})
@ActiveProfiles(profiles = { "test" })
public abstract class AbstractDaoTest extends
		AbstractTransactionalJUnit4SpringContextTests {

}
