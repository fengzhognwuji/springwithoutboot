package spring.hk.springcenter;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import spring.hk.springcenter.base.CompactDisc;

/**
 * @author I323691
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=SpringCenterApplication.class)
public class CompactDiscTest {
	
	@Autowired
	private CompactDisc dc;
	
	@Test
	public void dcShouldNotNull(){
		dc.play();
		assertNotNull(dc);
	}
}
