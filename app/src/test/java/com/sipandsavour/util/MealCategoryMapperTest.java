package com.sipandsavour.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class MealCategoryMapperTest {

    @Test
    public void mapBoeuf_shouldReturnBeef() {
        String result = MealCategoryMapper.mapToMealDBCategory("bœuf");
        assertEquals("Beef", result);
    }

    @Test
    public void mapAgneau_shouldReturnLamb() {
        String result = MealCategoryMapper.mapToMealDBCategory("agneau");
        assertEquals("Lamb", result);
    }

    @Test
    public void mapVolaille_shouldReturnChicken() {
        String result = MealCategoryMapper.mapToMealDBCategory("volaille");
        assertEquals("Chicken", result);
    }

    @Test
    public void mapSeafood_shouldReturnSeafood() {
        String result1 = MealCategoryMapper.mapToMealDBCategory("poisson blanc");
        String result2 = MealCategoryMapper.mapToMealDBCategory("fruits de mer");
        String result3 = MealCategoryMapper.mapToMealDBCategory("crustacés");

        assertEquals("Seafood", result1);
        assertEquals("Seafood", result2);
        assertEquals("Seafood", result3);
    }

    @Test
    public void mapVegetarian_shouldReturnVegetarian() {
        String result1 = MealCategoryMapper.mapToMealDBCategory("légumes grillés");
        String result2 = MealCategoryMapper.mapToMealDBCategory("salade");
        String result3 = MealCategoryMapper.mapToMealDBCategory("pâtes");

        assertEquals("Vegetarian", result1);
        assertEquals("Vegetarian", result2);
        assertEquals("Vegetarian", result3);
    }

    @Test
    public void mapNull_shouldReturnNull() {
        String result = MealCategoryMapper.mapToMealDBCategory(null);
        assertNull(result);
    }

    @Test
    public void mapUnknown_shouldReturnNull() {
        String result = MealCategoryMapper.mapToMealDBCategory("inconnu");
        assertNull(result);
    }

    @Test
    public void mapMainCategoryViande_shouldReturnBeef() {
        String result = MealCategoryMapper.mapMainCategoryToMealDB("viande");
        assertEquals("Beef", result);
    }

    @Test
    public void mapMainCategoryPoisson_shouldReturnSeafood() {
        String result = MealCategoryMapper.mapMainCategoryToMealDB("poisson");
        assertEquals("Seafood", result);
    }

    @Test
    public void mapMainCategoryVegetarien_shouldReturnVegetarian() {
        String result = MealCategoryMapper.mapMainCategoryToMealDB("végétarien");
        assertEquals("Vegetarian", result);
    }

    @Test
    public void mapMainCategoryNull_shouldReturnNull() {
        String result = MealCategoryMapper.mapMainCategoryToMealDB(null);
        assertNull(result);
    }

    @Test
    public void caseInsensitive_shouldWork() {
        String result1 = MealCategoryMapper.mapToMealDBCategory("BŒUF");
        String result2 = MealCategoryMapper.mapToMealDBCategory("BœUf");

        assertEquals("Beef", result1);
        assertEquals("Beef", result2);
    }
}
