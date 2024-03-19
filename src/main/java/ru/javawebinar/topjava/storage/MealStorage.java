package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealStorage {
    void create(Meal m);
    Meal read(Integer uuid);
    List<Meal> readAll();
    void update(Meal m);
    void delete(Integer uuid);
    void clear();
    int size();
}
