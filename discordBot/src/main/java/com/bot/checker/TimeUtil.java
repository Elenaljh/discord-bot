package com.bot.checker;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import net.dv8tion.jda.api.entities.Message;

public class TimeUtil {
  public String getDate(Message message) {
    ZonedDateTime createdTime = message.getTimeCreated().atZoneSameInstant(ZoneId.of("Asia/Seoul"));
    ZonedDateTime aDayBefore = createdTime.minusDays(1L);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. M. d");
    int hour = createdTime.getHour();
    if (hour > 3) {
      return createdTime.format(formatter);
    } else {
      return aDayBefore.format(formatter);
    }
  }

}
