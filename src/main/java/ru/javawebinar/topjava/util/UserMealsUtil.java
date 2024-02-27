package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0),  "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        Objects.requireNonNull(mealsTo).forEach(System.out::println);

//        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> sumOfCaloriesByDay = new HashMap<>();
        List<UserMealWithExcess> filteredMeals = new ArrayList<>();
        for (UserMeal meal : meals) {
            LocalTime mealTime = meal.getDateTime().toLocalTime();
            LocalDate mealDate = meal.getDateTime().toLocalDate();
            if (mealTime.isAfter(startTime) && mealTime.isBefore(endTime)) {
                sumOfCaloriesByDay.merge(mealDate, meal.getCalories(), Integer::sum);
                boolean isExceed = sumOfCaloriesByDay.getOrDefault(mealDate, 0) > caloriesPerDay;
                filteredMeals.add(new UserMealWithExcess(meal.getDateTime(),meal.getDescription(),caloriesPerDay, isExceed));
        }
    }
        return filteredMeals;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> sumOfCaloriesByDay = meals.stream()
                .filter(meal -> meal.getDateTime().toLocalTime().isAfter(startTime) && meal.getDateTime().toLocalTime().isBefore(endTime))
                .collect(Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate(), Collectors.summingInt(UserMeal::getCalories)));

        return meals.stream()
                .filter(meal -> sumOfCaloriesByDay.getOrDefault(meal.getDateTime().toLocalDate(), 0) > caloriesPerDay)
                .map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), sumOfCaloriesByDay.get(meal.getDateTime().toLocalDate()), true))
                .collect(Collectors.toList());
}
}
