package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.repository.inmemory.InMemoryUserRepository.ADMIN_ID;
import static ru.javawebinar.topjava.repository.inmemory.InMemoryUserRepository.USER_ID;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, InMemoryRepository<Meal>> repository = new ConcurrentHashMap<>();

    {
        MealsUtil.meals.forEach(meal -> save(meal, USER_ID));
        save(new Meal(LocalDateTime.of(2024, Month.APRIL, 12, 11, 10), "Завтрак (ADMIN)", 500), ADMIN_ID);
        save(new Meal(LocalDateTime.of(2024, Month.APRIL, 16, 11, 10), "Ужин (ADMIN)", 500), ADMIN_ID);
    }

    @Override
    public Meal save(Meal meal, int userId) {
        return repository.computeIfAbsent(userId, id -> new InMemoryRepository<>()).save(meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        InMemoryRepository<Meal> m = repository.get(userId);
        return m != null && m.delete(id);
    }

    @Override
    public Meal get(int id, int userId) {
        return repository.get(userId).get(id);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return filterWithPredicate(meal -> true, userId);
    }

    private List<Meal> filterWithPredicate(Predicate<Meal> filter, int userId) {
        InMemoryRepository<Meal> m = repository.get(userId);
        return m == null ? Collections.emptyList() :
                m.getCollection().stream()
                        .filter(filter)
                        .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                        .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return filterWithPredicate(meal -> isBetweenHafOpen(meal.getDateTime(), startDateTime, endDateTime), userId);
    }

    public static <T extends Comparable<T>> boolean isBetweenHafOpen(T value, @Nullable T start, @Nullable T end) {
        return (start == null || value.compareTo(start) >= 0 && (end == null || value.compareTo(end) < 0));
    }

}

