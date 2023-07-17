package com.sdasda7777.endpointmonitor.layer2;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LocalDateTimeServiceTest
{
	@Test
	void testCurrentValueIsGiven()
	{
		LocalDateTime ldt1 = LocalDateTime.now();

		LocalDateTimeService ldts = new LocalDateTimeService();
		LocalDateTime ldt2 = ldts.now();

		LocalDateTime ldt3 = LocalDateTime.now();

		assert(ldt1.isBefore(ldt2) || ldt1.isEqual(ldt2));
		assert(ldt2.isBefore(ldt3) || ldt2.isEqual(ldt3));
	}
}