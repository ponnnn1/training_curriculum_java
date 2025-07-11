package in.tech_camp.training_curriculum_java.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import in.tech_camp.training_curriculum_java.repository.PlanRepository;
import in.tech_camp.training_curriculum_java.form.PlanForm;
import in.tech_camp.training_curriculum_java.entity.PlanEntity;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class CalendarsController {

  private final PlanRepository planRepository;

  // 1週間のカレンダーと予定が表示されるページ
  @GetMapping("/")
  public String index(Model model) {
    model.addAttribute("planForm", new PlanForm());
    // List<Map<String, Object>> weekDays = get_week();
    List<Map<String, Object>> weekDays = getWeek();  //Issue2
    model.addAttribute("weekDays", weekDays);
    return "calendars/index";
  }

  // 予定の保存
  @PostMapping("/calendars")
  public String create(@ModelAttribute("planForm") @Validated PlanForm planForm, BindingResult result) {
    if (!result.hasErrors()) {
      PlanEntity newPlan = new PlanEntity();
      newPlan.setDate(planForm.getDate());
      newPlan.setPlan(planForm.getPlan());
      planRepository.insert(newPlan);
    }
    // return "redirect:/calendars";
    return "redirect:/";  //Issue4
  }

  // private List<Map<String, Object>> get_week() {
  private List<Map<String, Object>> getWeek() {  //Issue2
    List<Map<String, Object>> weekDays = new ArrayList<>();

    LocalDate todaysDate = LocalDate.now();
    List<PlanEntity> plans = planRepository.findByDateBetween(todaysDate, todaysDate.plusDays(6));

    String[] wdays = {"(日)", "(月)", "(火)", "(水)", "(木)", "(金)", "(土)"};

    for (int x = 0; x < 7; x++) {
      // Map<String, Object> day_map = new HashMap<String, Object>();
      // Map<String, Object> day_map = new HashMap<>();  //Issue1
      Map<String, Object> dayMap = new HashMap<>();  //Issue1,2
      LocalDate currentDate = todaysDate.plusDays(x);

      List<String> todayPlans = new ArrayList<>();
      for (PlanEntity plan : plans) {
          if (plan.getDate().equals(currentDate)) {
              todayPlans.add(plan.getPlan());
          }
      }

      // 曜日に対応する番号を取得  Issue6
      // 数値が7以上の場合を考慮
      int wdayNum = (todaysDate.getDayOfWeek().getValue() + x) % 7;

      // day_map.put("month", currentDate.getMonthValue());
      // day_map.put("date", currentDate.getDayOfMonth());
      // day_map.put("plans", todayPlans);

      // weekDays.add(day_map);
      dayMap.put("month", currentDate.getMonthValue());  //Issue2
      dayMap.put("date", currentDate.getDayOfMonth());  //Issue2
      dayMap.put("plans", todayPlans);  //Issue2
      dayMap.put("wday", wdays[wdayNum]);  //Issue6

      weekDays.add(dayMap);  //Issu2
    }

    return weekDays;
  }


}
