package zerobase.weather.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional; // javax - jakarta 로 하면 read only 옵션을 선택할 수 없음
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

@Service
@Transactional(readOnly = true)
public class DiaryService {

  private final DiaryRepository diaryRepository;
  private final DateWeatherRepository dateWeatherRepository;

  @Value("${openweathermap.key}")
  private String apiKey;

  public DiaryService(DiaryRepository diaryRepository, DateWeatherRepository dateWeatherRepository) {
    this.diaryRepository = diaryRepository;
    this.dateWeatherRepository = dateWeatherRepository;
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public void createDiary(LocalDate date, String text){
    DateWeather dateWeather = getDateWeather(date);
    Diary nowDiary = new Diary();

    nowDiary.setDateWeather(dateWeather);
    nowDiary.setText(text);
    diaryRepository.save(nowDiary);

  }

  private DateWeather getDateWeather(LocalDate date) {
    List<DateWeather> dateWeatherList = dateWeatherRepository.findAllByDate(date);
    if(dateWeatherList.size() == 0){
      return getWeatherFromApi();
    }else{
      return dateWeatherList.get(0);
    }
  }

  private String getWeatherString(){
    String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;

    try{
      URL url = new URL(apiUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      int responseCode = connection.getResponseCode();
      BufferedReader br;

      if(responseCode == 200){
        br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      } else{
        br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
      }

      String inputLine;
      StringBuilder response = new StringBuilder();

      while((inputLine = br.readLine()) != null){
        response.append(inputLine);
      }
      br.close();

      return response.toString();
    } catch(Exception e){
      return "failed to get response";
    }
  }

  private Map<String, Object> parseWeather(String jsonString){
    JSONParser jsonParser = new JSONParser();
    JSONObject jsonObject;

    try{
      jsonObject = (JSONObject) jsonParser.parse(jsonString);
    } catch (ParseException e){
      throw new RuntimeException(e);
    }

    Map<String, Object> resultMap = new HashMap<>();

    JSONObject mainData = (JSONObject) jsonObject.get("main");
    resultMap.put("temp", mainData.get("temp"));
    JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
    JSONObject weatherData = (JSONObject) weatherArray.get(0);
    resultMap.put("main", weatherData.get("main"));
    resultMap.put("icon", weatherData.get("icon"));

    return resultMap;
  }

  @Transactional(readOnly = true)
  public List<Diary> readDiary(LocalDate date) {
    return diaryRepository.findAllByDate(date);
  }

  public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
    return diaryRepository.findAllByDateBetween(startDate, endDate);
  }

  public void updateDiary(LocalDate date, String text) {
    Diary nowDiary = diaryRepository.getFirstByDate(date);
    nowDiary.setText(text);
    diaryRepository.save(nowDiary);
  }

  @Transactional
  public void deleteDiary(LocalDate date) {
    diaryRepository.deleteAllByDate(date);
  }

  @Transactional
  @Scheduled(cron = "0 0 1 * * *")
  public void saveWeatherDate(){
    dateWeatherRepository.save(getWeatherFromApi());
  }

  private DateWeather getWeatherFromApi(){
    String weatherData = getWeatherString();
    Map<String, Object> parsedWeather = parseWeather(weatherData);

    DateWeather dateWeather = new DateWeather();
    dateWeather.setDate(LocalDate.now());
    dateWeather.setWeather(parsedWeather.get("main").toString());
    dateWeather.setIcon(parsedWeather.get("icon").toString());
    dateWeather.setTemperature((Double) parsedWeather.get("temp"));

    return dateWeather;
  }

}