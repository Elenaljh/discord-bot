package com.bot;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.Sheets.Builder;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class GoogleSheetsHelper {
  private static final String SPREADSHEET_ID = "1CBFNGCWr54M_3_TBOQdltk0L9jB4H5OjKI9wRt2Xgy0";
  private static final String SHEET = "'2024년'!";
  private static final String KEY_PATH = "/google-key.json";
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final Logger logger = Logger.getLogger(GoogleSheetsHelper.class.getName());

  private Sheets sheetsService;


  public GoogleSheetsHelper() throws IOException {
    try {
      //자격 증명 파일을 사용하여 Google Sheets API 클라이언트 초기화
      InputStream inputStream = GoogleSheetsHelper.class.getResourceAsStream(KEY_PATH);
      if (inputStream == null) {
        throw new FileNotFoundException("Resource not found: " + KEY_PATH);
      }
      GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream)
          .createScoped(Collections.singletonList("https://www.googleapis.com/auth/spreadsheets"));
      this.sheetsService = new Builder(new NetHttpTransport(), JSON_FACTORY,
          new HttpCredentialsAdapter(credentials))
          .setApplicationName("출석체크봇")
          .build();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      logger.severe("google-key.json 파일 경로가 올바르지 않음\n" + e.getMessage());
      throw new RuntimeException(e);
    }
    logger.info("구글시트 클라이언트 초기화 완료");
  }

  public int findRowByDate(String targetDate) throws IOException {
    logger.info("findRowByDate 진입");
    logger.info("targetDate: " + targetDate);
    String range = SHEET + "B2:B300";
    ValueRange response = sheetsService.spreadsheets().values()
        .get(SPREADSHEET_ID, range)
        .execute();
    List<List<Object>> values = response.getValues();

    if (values == null || values.isEmpty()) {
      logger.warning("행 못찾음 : values가 null이거나 empty임");
      return -1;
    }

    for (int i = 0; i < values.size(); i++) {
      List<Object> row = values.get(i);
      if (row.isEmpty()) return -1;
      String date = row.get(0).toString();
      logger.info("셀에 적힌 날짜: " + date);
      if (date.equals(targetDate)) {
        logger.info("행: "+ (i + 2));
        return i+2;
      }
    }

    logger.warning("행 못찾음 : 행 순회했는데 못찾음");
    return -1;
  }

  public String findColumnByNickname(String nickname) throws IOException {
    logger.info("findColumnByNickname 메서드 진입");
    logger.info("nickname: " + nickname);
    String range = SHEET + "C1:H1";
    ValueRange response = sheetsService.spreadsheets().values()
        .get(SPREADSHEET_ID, range)
        .execute();
    List<Object> values = response.getValues().get(0);

    if (values == null || values.isEmpty()) {
      logger.warning("열 못찾음");
      return null;
    }

    for (int i = 0; i < values.size(); i++) {
      String name = values.get(i).toString();
      if (name.equals(nickname)) {
        return Character.toString((char) (i + 2 + 'A'));
      }
    }

    logger.warning("열 못찾음 : 닉네임으로 못찾음");
    return null;
  }

  public void write(String range, boolean value) throws IOException {
    List<List<Object>> values = Arrays.asList(Arrays.asList(value));
    ValueRange body = new ValueRange().setValues(values);
    sheetsService.spreadsheets()
        .values()
        .update(SPREADSHEET_ID, range, body)
        .setValueInputOption("RAW")
        .execute();
    logger.info("출석부 작성 => 셀: "+range+", 값: "+value);
  }

}
