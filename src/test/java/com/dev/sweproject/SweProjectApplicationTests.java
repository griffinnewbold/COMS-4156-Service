package com.dev.sweproject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SweProjectApplicationTests {

	@Test
	public void basicTest(){
		Assertions.assertEquals(2 + 2, 4);
	}

}
