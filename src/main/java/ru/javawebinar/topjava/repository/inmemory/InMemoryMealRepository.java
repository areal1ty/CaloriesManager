package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, List<Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(this::save);
    }

    @Override
    public Meal save(Meal meal) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
        } else {
            repository.compute(meal.getUserId(), (userId, meals) -> {
                if (meals == null) {
                    meals = new CopyOnWriteArrayList<>();
                }
                meals.removeIf(existingMeal -> Objects.equals(existingMeal.getId(), meal.getId()));
                meals.add(meal);
                return meals;
            });
        }
        return meal;
    }

    @Override
    public boolean delete(int userId, int id) {
        List<Meal> l = repository.get(userId);
        Iterator<Meal> iterator = l.iterator();
        while (iterator.hasNext()) {
            Meal m = iterator.next();
            if (m.getUserId() == userId && m.getId() == id) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public Meal get(int id, int userId) {
        List<Meal> userMeals = repository.get(userId);
        return userMeals.get(id);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return repository.getOrDefault(userId, new CopyOnWriteArrayList<>())
                .stream()
                .sorted(Comparator.comparing(Meal::getDate)
                        .reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime dateTimeOfStart, LocalDateTime dateTimeOfEnd, int userId) {
        return repository.getOrDefault(userId, new CopyOnWriteArrayList<>())
                .stream()
                .filter(meal -> isBetweenHafOpen(meal.getDateTime(), dateTimeOfStart, dateTimeOfEnd))
                .sorted(Comparator.comparing(Meal::getDateTime)
                        .reversed())
                .collect(Collectors.toList());
    }

    public static <T extends Comparable<T>> boolean isBetweenHafOpen(T value, @Nullable T start, @Nullable T end) {
        return (start == null || value.compareTo(start) >= 0 && (end == null || value.compareTo(end) < 0));
    }

}

