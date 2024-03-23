package ru.javawebinar.topjava.storage;

import org.junit.jupiter.api.Test;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.uuidGenerator;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class MapStorageTest {
    private final MealStorage storage;
    static uuidGenerator generator = new uuidGenerator();
    private static final Integer UUID_1 = generator.nextId();
    private static final Integer UUID_2 = generator.nextId();
    private static final Integer UUID_3 = generator.nextId();
    private static final Integer UUID_4 = generator.nextId();

    private static final Meal MEAL_1;
    private static final Meal MEAL_2;
    private static final Meal MEAL_3;
    private static final Meal MEAL_4;

    static {
        MEAL_1 = new Meal(UUID_1, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500);
        MEAL_2 = new Meal(UUID_2, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000);
        MEAL_3 = new Meal(UUID_3, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500);
        MEAL_4 = new Meal(UUID_4, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100);
    }

    MapStorageTest() {
        this.storage = new InMemoryMealStorage();
    }

    @Test
    void create() {
        storage.create(MEAL_4);
        assertEquals(MEAL_4, storage.read(MEAL_4.getId()));
    }

    @Test
    void read() {
        storage.create(MEAL_3);
        storage.create(MEAL_2);
        storage.create(MEAL_1);
        assertEquals(MEAL_1, storage.read(MEAL_1.getId()));
        assertEquals(MEAL_2, storage.read(MEAL_2.getId()));
        assertEquals(MEAL_3, storage.read(MEAL_3.getId()));
    }

    @Test
    void update() {
        Meal updatedMEAL = new Meal(UUID_1, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100);
        storage.update(updatedMEAL);
        assertEquals(updatedMEAL, storage.read(updatedMEAL.getId()));
    }

    @Test
    void delete() {
        storage.delete(UUID_3);
        assertNull(storage.read(UUID_3));
    }

}